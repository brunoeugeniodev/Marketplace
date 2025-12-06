package io.github.brunoeugeniodev.marketplace.controller;

import io.github.brunoeugeniodev.marketplace.dto.UsuarioRegisterDTO;
import io.github.brunoeugeniodev.marketplace.models.Loja;
import io.github.brunoeugeniodev.marketplace.models.Produto;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.service.AuthService;
import io.github.brunoeugeniodev.marketplace.service.LojaService;
import io.github.brunoeugeniodev.marketplace.service.ProdutoService;
import io.github.brunoeugeniodev.marketplace.service.UsuarioService;
import io.github.brunoeugeniodev.marketplace.util.MapperUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class PaginaController {

    private final LojaService lojaService;
    private final ProdutoService produtoService;
    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final MapperUtil mapperUtil;

    public PaginaController(LojaService lojaService, ProdutoService produtoService, AuthService authService, UsuarioService usuarioService, MapperUtil mapperUtil) {
        this.lojaService = lojaService;
        this.produtoService = produtoService;
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.mapperUtil = mapperUtil;
    }

    private void addLoginStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("usuarioLogado", userDetails.getUsername());
        } else {
            model.addAttribute("usuarioLogado", null);
        }
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Loja> lojas = lojaService.listarLojasAtivas();
        List<Produto> produtosDestaque = produtoService.listarProdutosAtivos();

        model.addAttribute("lojas", lojas);
        model.addAttribute("produtosDestaque", produtosDestaque);
        addLoginStatus(model);
        return "index";
    }

    @GetMapping("/busca-resultado")
    public String buscaResultado(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("termoBusca", q);
        addLoginStatus(model);
        return "busca-resultado";
    }

    @GetMapping("/loja/{id}")
    public String loja(@PathVariable Long id, Model model) {
        Optional<Loja> optionalLoja = lojaService.buscarPorIdAtiva(id);
        if (optionalLoja.isPresent()) {
            Loja loja = optionalLoja.get();
            List<Produto> produtos = produtoService.listarProdutosPorLoja(id);
            model.addAttribute("loja", loja);
            model.addAttribute("produtos", produtos);
        } else {
            model.addAttribute("loja", null);
            model.addAttribute("produtos", Collections.emptyList());
        }
        addLoginStatus(model); // Adiciona o estado do login
        return "loja";
    }

    @GetMapping("/ofertas")
    public String ofertas(Model model) {
        addLoginStatus(model);
        return "ofertas";
    }

    @GetMapping("/lancamentos")
    public String lancamentos(Model model) {
        addLoginStatus(model);
        return "lancamentos";
    }

    @GetMapping("/destaques")
    public String destaques(Model model) {
        List<Produto> produtosDestaque = produtoService.listarProdutosAtivos();
        model.addAttribute("produtosDestaque", produtosDestaque);
        addLoginStatus(model);
        return "destaques";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        addLoginStatus(model);
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute UsuarioRegisterDTO registerDTO,
                                   RedirectAttributes redirectAttributes) {
        try {
            // 1. Validação de senha
            if (!registerDTO.getSenha().equals(registerDTO.getConfirmarSenha())) {
                redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
                return "redirect:/registro";
            }

            // 2. Verificar se email já existe
            usuarioService.buscarPorEmail(registerDTO.getEmail())
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Este email já está cadastrado.");
                    });

            // 3. Limpar máscaras dos campos
            String cpfLimpo = registerDTO.getCpf().replaceAll("[^0-9]", "");

            // Telefone pode ser opcional - limpar se existir
            String telefoneLimpo = null;
            if (registerDTO.getTelefone() != null && !registerDTO.getTelefone().isBlank()) {
                telefoneLimpo = registerDTO.getTelefone().replaceAll("[^0-9]", "");

                // Validar telefone limpo (10 ou 11 dígitos)
                if (telefoneLimpo.length() < 10) {
                    redirectAttributes.addFlashAttribute("error",
                            "Telefone deve ter pelo menos 10 dígitos.");
                    return "redirect:/registro";
                }
            }

            // 4. Validar CPF limpo
            if (cpfLimpo.length() != 11) {
                redirectAttributes.addFlashAttribute("error", "CPF deve ter 11 dígitos.");
                return "redirect:/registro";
            }

            // 5. Atualizar DTO com valores limpos
            registerDTO.setCpf(cpfLimpo);
            if (telefoneLimpo != null) {
                registerDTO.setTelefone(telefoneLimpo);
            }

            // 6. Converter e salvar usuário
            Usuario usuario = mapperUtil.toUsuarioEntity(registerDTO);
            usuario.setAtivo(true);

            // Log para debug
            System.out.println("Salvando usuário:");
            System.out.println("CPF: " + usuario.getCpf());
            System.out.println("Telefone: " + usuario.getTelefone());
            System.out.println("Email: " + usuario.getEmail());

            // 7. Garantir que a senha seja criptografada
            usuario = authService.register(usuario);

            System.out.println("Usuário salvo com ID: " + usuario.getId());

            redirectAttributes.addFlashAttribute("success",
                    "Cadastro realizado com sucesso! Você pode fazer login agora.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Erro ao cadastrar. Por favor, tente novamente.");
            return "redirect:/registro";
        }
    }

    @GetMapping("/minha-conta")
    public String minhaConta(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername());
        model.addAttribute("usuario", usuario);
        addLoginStatus(model);
        return "minha-conta";
    }

    @PostMapping("/usuario/atualizar")
    public String atualizarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogado = usuarioService.loadUsuarioByUsername(userDetails.getUsername());
        
        usuarioService.atualizarUsuario(usuarioLogado.getId(), usuario);
        
        redirectAttributes.addFlashAttribute("success", "Dados atualizados com sucesso!");
        return "redirect:/minha-conta";
    }

    @GetMapping("/minha-loja")
    public String minhaLoja(Model model) {
        System.out.println("DEBUG: Acessando /minha-loja");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            System.out.println("DEBUG: Usuário não autenticado, redirecionando para login");
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername());

        // Adiciona o usuário ao modelo (IMPORTANTE!)
        model.addAttribute("usuarioLogado", usuario.getEmail()); // Ou usuario.getNome() se preferir

        // Buscar loja do usuário
        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);

        if (!lojas.isEmpty()) {
            Loja loja = lojas.get(0);
            model.addAttribute("loja", loja);
            model.addAttribute("lojaId", loja.getId());
            System.out.println("DEBUG: Loja encontrada: " + loja.getNome());

            // Busca produtos da loja
            List<Produto> produtos = produtoService.listarProdutosPorLoja(loja.getId());
            model.addAttribute("produtos", produtos);
        } else {
            model.addAttribute("loja", null);
            model.addAttribute("produtos", Collections.emptyList());
            System.out.println("DEBUG: Usuário não tem loja");
        }

        return "minha-loja";
    }

    // No arquivo PaginaController.java
    @GetMapping("/loja/editar")
    public String editarLoja(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login?redirect=/loja/editar";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername());

        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);
        if (lojas.isEmpty()) {
            return "redirect:/cadastro-loja";
        }

        Loja loja = lojas.get(0);

        // Adiciona dados específicos para edição
        model.addAttribute("loja", loja);
        model.addAttribute("modo", "edicao");
        model.addAttribute("tituloPagina", "Editar Minha Loja");
        model.addAttribute("usuarioLogado", usuario.getEmail());
        model.addAttribute("acao", "atualizar");

        return "editar-loja"; // Template específico para edição
    }

    // Modifique também o método cadastroLoja para ser mais simples
    @GetMapping("/cadastro-loja")
    public String cadastroLoja(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login?redirect=/cadastro-loja";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername());

        // Verificar se usuário já tem loja
        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);
        if (!lojas.isEmpty()) {
            return "redirect:/minha-loja";
        }

        model.addAttribute("loja", new Loja());
        model.addAttribute("modo", "cadastro");
        model.addAttribute("tituloPagina", "Cadastrar Nova Loja");
        model.addAttribute("usuarioLogado", usuario.getEmail());
        model.addAttribute("acao", "criar");

        return "cadastro-loja";
    }

    @GetMapping("/minha-loja-redirect")
    public String minhaLojaRedirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            return "redirect:/minha-loja";
        }
        return "redirect:/login";
    }

    // Adicione este método ao PaginaController:

    @GetMapping("/produtos")
    public String produtos(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername());

        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);
        if (lojas.isEmpty()) {
            return "redirect:/cadastro-loja";
        }

        Loja loja = lojas.get(0);
        List<Produto> produtos = produtoService.listarProdutosPorLoja(loja.getId());

        model.addAttribute("loja", loja);
        model.addAttribute("produtos", produtos);
        model.addAttribute("usuarioLogado", usuario.getEmail());

        return "produtos/gerenciar";
    }

    @GetMapping("/carrinho")
    public String carrinho(Model model) {
        addLoginStatus(model);
        return "carrinho";
    }

    @GetMapping("/lojas")
    public String listarLojas(@RequestParam(required = false) String categoria, Model model) {
        List<Loja> lojas;
        if (categoria != null && !categoria.trim().isEmpty()) {
            lojas = lojaService.listarPorCategoria(categoria);
        } else {
            lojas = lojaService.listarLojasAtivas();
        }

        model.addAttribute("lojas", lojas);
        addLoginStatus(model);
        return "lojas";
    }
}

