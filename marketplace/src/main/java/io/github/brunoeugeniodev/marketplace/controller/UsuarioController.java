package io.github.brunoeugeniodev.marketplace.controller;

import io.github.brunoeugeniodev.marketplace.dto.UsuarioDTO;
import io.github.brunoeugeniodev.marketplace.dto.UsuarioUpdateDTO;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.service.UsuarioService;
import io.github.brunoeugeniodev.marketplace.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MapperUtil mapperUtil;

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return usuarioService.buscarPorEmail(userDetails.getUsername())
                .map(mapperUtil::toUsuarioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDTO> atualizarMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        
        return usuarioService.buscarPorEmail(userDetails.getUsername())
                .map(usuarioExistente -> {
                    Usuario usuarioAtualizado = mapperUtil.toUsuarioEntity(usuarioUpdateDTO);
                    // Garante que o ID não seja perdido no mapeamento
                    usuarioAtualizado.setId(usuarioExistente.getId()); 
                    Usuario usuarioSalvo = usuarioService.atualizarUsuario(usuarioExistente.getId(), usuarioAtualizado);
                    return ResponseEntity.ok(mapperUtil.toUsuarioDTO(usuarioSalvo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: A segurança deste endpoint foi removida. Reavaliar a necessidade de proteção.
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(mapperUtil.mapList(usuarios, UsuarioDTO.class));
    }

    // TODO: A segurança deste endpoint foi removida. Reavaliar a necessidade de proteção.
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(mapperUtil::toUsuarioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: A segurança deste endpoint foi removida. Reavaliar a necessidade de proteção.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // TODO: A segurança deste endpoint foi removida. Reavaliar a necessidade de proteção.
    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarUsuario(@PathVariable Long id) {
        usuarioService.desativarUsuario(id);
        return ResponseEntity.ok().build();
    }

    // TODO: A segurança deste endpoint foi removida. Reavaliar a necessidade de proteção.
    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativarUsuario(@PathVariable Long id) {
        usuarioService.ativarUsuario(id);
        return ResponseEntity.ok().build();
    }
}