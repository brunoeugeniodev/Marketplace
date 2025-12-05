package io.github.brunoeugeniodev.marketplace.controller;

import io.github.brunoeugeniodev.marketplace.dto.LojaCreateDTO;
import io.github.brunoeugeniodev.marketplace.dto.LojaUpdateDTO;
import io.github.brunoeugeniodev.marketplace.models.Loja;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.service.LojaService;
import io.github.brunoeugeniodev.marketplace.service.UsuarioService;
import io.github.brunoeugeniodev.marketplace.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/lojas")
@RequiredArgsConstructor
public class LojaController {

    private final LojaService lojaService;
    private final UsuarioService usuarioService;
    private final MapperUtil mapperUtil;

    @PostMapping
    public ResponseEntity<?> criarOuAtualizarLoja(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute LojaCreateDTO lojaCreateDTO,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam(value = "lojaId", required = false) Long lojaId) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();

        try {
            Loja loja = mapperUtil.toLojaEntity(lojaCreateDTO);

            // Se tiver lojaId, é uma atualização
            if (lojaId != null && lojaId > 0) {
                // Busca a loja existente para verificar permissão
                Optional<Loja> lojaExistenteOpt = lojaService.buscarPorId(lojaId);

                if (lojaExistenteOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Loja não encontrada.");
                }

                Loja lojaExistente = lojaExistenteOpt.get();

                // Verifica se o usuário é dono da loja
                if (!lojaExistente.getUsuario().getId().equals(usuario.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Você não tem permissão para editar esta loja.");
                }

                // Atualiza o ID na loja
                loja.setId(lojaId);

                // TODO: Lidar com upload/atualização da foto
                lojaService.atualizarLoja(loja);

                return ResponseEntity.ok("Loja atualizada com sucesso!");
            } else {
                // Modo criação
                // TODO: Lidar com upload da foto
                lojaService.criarLoja(loja, usuario);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Loja cadastrada com sucesso!");
            }

        } catch (Exception e) {
            log.error("Erro ao criar/atualizar loja: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar loja: " + e.getMessage());
        }
    }

    @GetMapping("/minha-loja")
    public ResponseEntity<?> buscarMinhaLoja(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(userDetails.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        try {
            List<Loja> lojas = lojaService.listarLojasDoUsuario(usuarioOpt.get());
            if (lojas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma loja encontrada.");
            }

            return ResponseEntity.ok(lojas.get(0)); // Retorna a primeira loja

        } catch (Exception e) {
            log.error("Erro ao buscar loja: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar loja: " + e.getMessage());
        }
    }
}