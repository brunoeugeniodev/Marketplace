package io.github.brunoeugeniodev.marketplace.controller.api;

import io.github.brunoeugeniodev.marketplace.models.Loja;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.service.LojaService;
import io.github.brunoeugeniodev.marketplace.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lojas-form")
public class LojaFormApiController {

    private final LojaService lojaService;
    private final UsuarioService usuarioService;

    public LojaFormApiController(LojaService lojaService, UsuarioService usuarioService) {
        this.lojaService = lojaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> cadastrarLoja(
            @RequestParam("nome") String nome,
            @RequestParam("cnpj") String cnpj,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam("cep") String cep,
            @RequestParam("logradouro") String logradouro,
            @RequestParam("numero") String numero,
            @RequestParam(value = "complemento", required = false) String complemento,
            @RequestParam("bairro") String bairro,
            @RequestParam("cidade") String cidade,
            @RequestParam("estado") String estado,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "site", required = false) String site,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar autenticação
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                response.put("success", false);
                response.put("message", "Usuário não autenticado");
                return ResponseEntity.status(401).body(response);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuario = usuarioService.loadUsuarioByUsername(userDetails.getUsername());

            // Verificar se usuário já tem loja
            if (!lojaService.listarLojasDoUsuario(usuario).isEmpty()) {
                response.put("success", false);
                response.put("message", "Você já possui uma loja cadastrada");
                return ResponseEntity.badRequest().body(response);
            }

            // Criar nova loja
            Loja loja = new Loja();
            loja.setNome(nome);
            loja.setCnpj(cnpj);
            loja.setDescricao(descricao);
            loja.setLojaCep(cep);
            loja.setLojaRua(logradouro);
            loja.setLojaNumerao(numero);
            loja.setLojaComplemento(complemento);
            loja.setLojaBairro(bairro);
            loja.setLojaCidade(cidade);
            loja.setLojaEstado(estado);
            loja.setTelefone(telefone);
            loja.setSite(site);
            loja.setUsuario(usuario);
            loja.setAtivo(true);

            // Salvar foto se existir
            if (foto != null && !foto.isEmpty()) {
                // Aqui você pode implementar o upload da foto
                String fotoUrl = "/uploads/lojas/" + System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                loja.setFotoUrl(fotoUrl);
                // Salvar o arquivo no sistema de arquivos (implementar se necessário)
            }

            // Salvar loja
            Loja lojaSalva = lojaService.salvar(loja);

            // Preparar resposta de sucesso
            response.put("success", true);
            response.put("message", "Loja cadastrada com sucesso!");
            response.put("lojaId", lojaSalva.getId());
            response.put("redirect", "/minha-loja");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao cadastrar loja: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}