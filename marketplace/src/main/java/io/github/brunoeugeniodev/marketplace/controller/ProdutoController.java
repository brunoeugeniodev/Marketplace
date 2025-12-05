package io.github.brunoeugeniodev.marketplace.controller;

import io.github.brunoeugeniodev.marketplace.dto.ProdutoCreateDTO;
import io.github.brunoeugeniodev.marketplace.dto.ProdutoDTO;
import io.github.brunoeugeniodev.marketplace.dto.ProdutoUpdateDTO;
import io.github.brunoeugeniodev.marketplace.models.Loja;
import io.github.brunoeugeniodev.marketplace.models.Produto;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.service.LojaService;
import io.github.brunoeugeniodev.marketplace.service.ProdutoService;
import io.github.brunoeugeniodev.marketplace.service.UsuarioService;
import io.github.brunoeugeniodev.marketplace.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final LojaService lojaService;
    private final UsuarioService usuarioService;
    private final MapperUtil mapperUtil;

    // ========== ENDPOINTS WEB (Thymeleaf) ==========

    @GetMapping("/gerenciar")
    public String gerenciarProdutos(@RequestParam(required = false) Long lojaId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);
        if (lojas.isEmpty()) {
            return "redirect:/cadastro-loja";
        }

        // Se lojaId foi passado, use-o, senão use a primeira loja do usuário
        Loja loja;
        if (lojaId != null) {
            Optional<Loja> lojaOpt = lojaService.buscarPorId(lojaId);
            if (lojaOpt.isPresent()) {
                loja = lojaOpt.get();
                // Verificar se a loja pertence ao usuário
                if (!loja.getUsuario().getId().equals(usuario.getId())) {
                    // Se não pertence, usar a primeira loja do usuário
                    loja = lojas.get(0);
                }
            } else {
                loja = lojas.get(0);
            }
        } else {
            loja = lojas.get(0);
        }

        List<Produto> produtos = produtoService.listarProdutosPorLoja(loja.getId());

        model.addAttribute("loja", loja);
        model.addAttribute("produtos", produtos);
        model.addAttribute("usuarioLogado", usuario.getEmail());

        return "produtos/gerenciar";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovoProduto(@RequestParam(required = false) Long lojaId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);
        if (lojas.isEmpty()) {
            return "redirect:/cadastro-loja";
        }

        // Selecionar a loja correta (mesma lógica do método acima)
        Loja loja;
        if (lojaId != null) {
            Optional<Loja> lojaOpt = lojaService.buscarPorId(lojaId);
            if (lojaOpt.isPresent()) {
                loja = lojaOpt.get();
                // Verificar se a loja pertence ao usuário
                if (!loja.getUsuario().getId().equals(usuario.getId())) {
                    loja = lojas.get(0);
                }
            } else {
                loja = lojas.get(0);
            }
        } else {
            loja = lojas.get(0);
        }

        if (!model.containsAttribute("produto")) {
            ProdutoCreateDTO produtoDTO = new ProdutoCreateDTO();
            produtoDTO.setDestaque(false);
            produtoDTO.setQuantidade(0L);
            produtoDTO.setLojaId(loja.getId()); // Adiciona o ID da loja ao DTO
            model.addAttribute("produto", produtoDTO);
        }

        model.addAttribute("loja", loja);
        model.addAttribute("modo", "cadastro");
        model.addAttribute("usuarioLogado", usuario.getEmail());

        return "produtos/form";
    }

    @PostMapping("/novo")
    public String criarProduto(@Valid @ModelAttribute("produto") ProdutoCreateDTO produtoDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.produto", result);
            redirectAttributes.addFlashAttribute("produto", produtoDTO);
            return "redirect:/produtos/novo?lojaId=" + produtoDTO.getLojaId();
        }

        try {
            // Buscar a loja pelo ID no DTO
            Optional<Loja> lojaOpt = lojaService.buscarPorId(produtoDTO.getLojaId());
            if (lojaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Loja não encontrada");
                return "redirect:/produtos/novo?lojaId=" + produtoDTO.getLojaId();
            }

            Loja loja = lojaOpt.get();

            // Verificar se a loja pertence ao usuário
            if (!loja.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "Você não tem permissão para adicionar produtos a esta loja");
                return "redirect:/produtos/novo?lojaId=" + produtoDTO.getLojaId();
            }

            Produto produto = mapperUtil.toProdutoEntity(produtoDTO);
            produto.setLoja(loja); // Associar a loja ao produto
            produto.setAtivo(true); // Produto começa ativo

            produtoService.criarProduto(produto, loja, usuario);

            redirectAttributes.addFlashAttribute("success", "Produto cadastrado com sucesso!");
            return "redirect:/produtos/gerenciar?lojaId=" + loja.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar produto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("produto", produtoDTO);
            return "redirect:/produtos/novo?lojaId=" + (produtoDTO.getLojaId() != null ? produtoDTO.getLojaId() : "");
        }
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEditarProduto(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
        if (produtoOpt.isEmpty()) {
            return "redirect:/produtos/gerenciar";
        }

        Produto produto = produtoOpt.get();

        // Verificar se o usuário é dono da loja
        if (!produto.getLoja().getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/produtos/gerenciar";
        }

        ProdutoUpdateDTO produtoDTO = mapperUtil.toProdutoEntity(produto, ProdutoUpdateDTO.class);
        model.addAttribute("produto", produtoDTO);
        model.addAttribute("modo", "edicao");
        model.addAttribute("produtoId", id);
        model.addAttribute("loja", produto.getLoja());
        model.addAttribute("usuarioLogado", usuario.getEmail());

        return "produtos/form";
    }

    @PostMapping("/editar/{id}")
    public String atualizarProduto(@PathVariable Long id,
                                   @Valid @ModelAttribute("produto") ProdutoUpdateDTO produtoDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.produto", result);
            redirectAttributes.addFlashAttribute("produto", produtoDTO);
            return "redirect:/produtos/editar/" + id;
        }

        try {
            // Buscar produto atual para obter a loja
            Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
            if (produtoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Produto não encontrado");
                return "redirect:/produtos/gerenciar";
            }

            Produto produtoExistente = produtoOpt.get();

            // Verificar se o usuário é dono da loja
            if (!produtoExistente.getLoja().getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "Você não tem permissão para editar este produto");
                return "redirect:/produtos/gerenciar";
            }

            Produto produtoAtualizado = mapperUtil.toProdutoEntity(produtoDTO);
            produtoAtualizado.setId(id);
            produtoAtualizado.setLoja(produtoExistente.getLoja());

            produtoService.atualizarProduto(id, produtoAtualizado, usuario);

            redirectAttributes.addFlashAttribute("success", "Produto atualizado com sucesso!");
            return "redirect:/produtos/gerenciar?lojaId=" + produtoExistente.getLoja().getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar produto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("produto", produtoDTO);
            return "redirect:/produtos/editar/" + id;
        }
    }

    @GetMapping("/desativar/{id}")
    public String desativarProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        try {
            produtoService.desativarProduto(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Produto desativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao desativar produto: " + e.getMessage());
        }

        return "redirect:/produtos/gerenciar";
    }

    @GetMapping("/reativar/{id}")
    public String reativarProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        try {
            Produto produto = produtoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Verificar se o usuário é dono da loja
            if (!produto.getLoja().getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "Você não tem permissão para reativar este produto");
                return "redirect:/produtos/gerenciar";
            }

            produto.setAtivo(true);
            produtoService.atualizarProduto(id, produto, usuario);

            redirectAttributes.addFlashAttribute("success", "Produto reativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao reativar produto: " + e.getMessage());
        }

        return "redirect:/produtos/gerenciar";
    }

    // ========== ENDPOINTS REST API ==========

    @GetMapping("/api")
    public ResponseEntity<List<ProdutoDTO>> listarProdutos() {
        List<Produto> produtos = produtoService.listarProdutosAtivos();
        List<ProdutoDTO> produtosDTO = mapperUtil.mapList(produtos, ProdutoDTO.class);
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/api/{id}")
    public ResponseEntity<ProdutoDTO> buscarProduto(@PathVariable Long id) {
        Optional<Produto> produtoOpt = produtoService.buscarProdutoAtivoPorId(id);
        if (produtoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProdutoDTO produtoDTO = mapperUtil.toProdutoDTO(produtoOpt.get());
        return ResponseEntity.ok(produtoDTO);
    }

    @PostMapping("/api")
    public ResponseEntity<ProdutoDTO> criarProdutoApi(@Valid @RequestBody ProdutoCreateDTO produtoDTO,
                                                      Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        List<Loja> lojas = lojaService.listarLojasDoUsuario(usuario);
        if (lojas.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Loja loja = lojas.get(0);
        Produto produto = mapperUtil.toProdutoEntity(produtoDTO);

        Produto produtoCriado = produtoService.criarProduto(produto, loja, usuario);
        ProdutoDTO responseDTO = mapperUtil.toProdutoDTO(produtoCriado);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/api/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProdutoApi(@PathVariable Long id,
                                                          @Valid @RequestBody ProdutoUpdateDTO produtoDTO,
                                                          Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        Produto produtoAtualizado = mapperUtil.toProdutoEntity(produtoDTO);
        Produto produto = produtoService.atualizarProduto(id, produtoAtualizado, usuario);

        ProdutoDTO responseDTO = mapperUtil.toProdutoDTO(produto);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/api/{id}")
    public ResponseEntity<Void> deletarProdutoApi(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername()); // ← CORRETO

        produtoService.deletarProduto(id, usuario);
        return ResponseEntity.noContent().build();
    }
}