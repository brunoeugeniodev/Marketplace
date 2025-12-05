package io.github.brunoeugeniodev.marketplace.service;

import io.github.brunoeugeniodev.marketplace.exception.ResourceNotFoundException;
import io.github.brunoeugeniodev.marketplace.models.Carrinho;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.repository.CarrinhoRepository;
import io.github.brunoeugeniodev.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService { // Removido implements UserDetailsService

    private final UsuarioRepository usuarioRepository;
    private final CarrinhoRepository carrinhoRepository;

    // Removido @Override e UserDetails
    public Usuario loadUsuarioByUsername(String email) {
        return buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existsByCpf(String cpf) {
        return usuarioRepository.existsByCpf(cpf);
    }

    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        criarCarrinhoParaUsuario(usuarioSalvo);
        return usuarioSalvo;
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNome(usuarioAtualizado.getNome());
                    usuario.setEmail(usuarioAtualizado.getEmail());

                    // Atualizar telefone se fornecido
                    if (usuarioAtualizado.getTelefone() != null) {
                        usuario.setTelefone(usuarioAtualizado.getTelefone());
                    }

                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public void criarCarrinhoParaUsuario(Usuario usuario) {
        if (!carrinhoRepository.existsByUsuarioId(usuario.getId())) {
            Carrinho carrinho = Carrinho.builder().usuario(usuario).build();
            carrinhoRepository.save(carrinho);
        }
    }
    
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public void deletarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public void desativarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(user -> {
            user.setAtivo(false);
            usuarioRepository.save(user);
        });
    }

    public void ativarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(user -> {
            user.setAtivo(true);
            usuarioRepository.save(user);
        });
    }
}