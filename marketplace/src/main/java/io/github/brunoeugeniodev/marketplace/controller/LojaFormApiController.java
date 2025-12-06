package io.github.brunoeugeniodev.marketplace.controller;

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

            // Normaliza e seta categoria
            if (categoria != null && !categoria.trim().isEmpty()) {
                loja.setCategoria(categoria.trim().toLowerCase());
            }

            // Salvar foto se existir
            if (foto != null && !foto.isEmpty()) {
                try {
                    String uploadsDir = System.getProperty("user.dir") + "/uploads/lojas/";
                    java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadsDir);
                    if (!java.nio.file.Files.exists(uploadPath)) {
                        java.nio.file.Files.createDirectories(uploadPath);
                    }

                    String originalFilename = java.nio.file.Paths.get(foto.getOriginalFilename()).getFileName().toString();
                    String filename = System.currentTimeMillis() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
                    java.nio.file.Path filePath = uploadPath.resolve(filename);

                    // Salva arquivo no filesystem
                    try (java.io.InputStream is = foto.getInputStream()) {
                        java.nio.file.Files.copy(is, filePath);
                    }

                    // Define a URL pública para acessar o upload
                    String fotoUrl = "/uploads/lojas/" + filename;
                    loja.setFotoUrl(fotoUrl);

                } catch (Exception ex) {
                    // Se falhar ao salvar a foto, registra e continua sem foto
                    ex.printStackTrace();
                }
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