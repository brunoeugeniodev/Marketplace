package io.github.brunoeugeniodev.marketplace.service;

import io.github.brunoeugeniodev.marketplace.exception.ResourceNotFoundException;
import io.github.brunoeugeniodev.marketplace.models.Loja;
import io.github.brunoeugeniodev.marketplace.models.Usuario;
import io.github.brunoeugeniodev.marketplace.repository.LojaRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LojaService {

    private final LojaRepository lojaRepository;
    private final ProdutoService produtoService;

    @Transactional
    public Loja criarLoja(Loja loja, Usuario usuario) {
        validarLoja(loja);
        loja.setUsuario(usuario);
        loja.setAtivo(true);
        return lojaRepository.save(loja);
    }

    @Transactional
    public Loja atualizarLoja(Loja loja) { // Removido os parâmetros id e usuario, pois a loja já vem com ID
        if (loja.getId() == null) {
            throw new ValidationException("ID da loja é obrigatório para atualização");
        }

        // Busca a loja existente
        Loja lojaExistente = lojaRepository.findById(loja.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));

        // Mantém alguns campos originais
        loja.setUsuario(lojaExistente.getUsuario());
        loja.setAtivo(lojaExistente.getAtivo());
        loja.setAvaliacaoMedia(lojaExistente.getAvaliacaoMedia());
        loja.setTotalAvaliacoes(lojaExistente.getTotalAvaliacoes());
        loja.setDataCriacao(lojaExistente.getDataCriacao());

        // Se está mantendo a mesma foto
        if (loja.getFotoUrl() == null) {
            loja.setFotoUrl(lojaExistente.getFotoUrl());
        }

        validarLoja(loja);
        return lojaRepository.save(loja);
    }

    // Mantenha também o método atualizarLoja com ID (sobrecarga)
    @Transactional
    public Loja atualizarLoja(Long id, Loja lojaAtualizada, Usuario usuario) {
        return lojaRepository.findById(id)
                .map(loja -> {
                    if (!loja.getUsuario().getId().equals(usuario.getId())) {
                        throw new ValidationException("Você não tem permissão para editar esta loja");
                    }

                    validarLoja(lojaAtualizada);

                    if (!loja.getCnpj().equals(lojaAtualizada.getCnpj()) &&
                            lojaRepository.existsByCnpjAndIdNot(lojaAtualizada.getCnpj(), id)) {
                        throw new ValidationException("CNPJ já está em uso");
                    }

                    // Atualiza campos básicos
                    loja.setNome(lojaAtualizada.getNome());
                    loja.setCnpj(lojaAtualizada.getCnpj());
                    loja.setDescricao(lojaAtualizada.getDescricao());
                    loja.setTelefone(lojaAtualizada.getTelefone());
                    loja.setEmail(lojaAtualizada.getEmail());
                    loja.setSite(lojaAtualizada.getSite());

                    // Atualiza campos de endereço - CORRIGINDO OS NOMES DOS CAMPOS
                    if (lojaAtualizada.getLojaRua() != null) {
                        loja.setLojaRua(lojaAtualizada.getLojaRua());
                    }
                    if (lojaAtualizada.getLojaNumerao() != null) { // Corrigido: era "LojaNumerao"
                        loja.setLojaNumerao(lojaAtualizada.getLojaNumerao());
                    }
                    if (lojaAtualizada.getLojaComplemento() != null) {
                        loja.setLojaComplemento(lojaAtualizada.getLojaComplemento());
                    }
                    if (lojaAtualizada.getLojaBairro() != null) {
                        loja.setLojaBairro(lojaAtualizada.getLojaBairro());
                    }
                    if (lojaAtualizada.getLojaCep() != null) {
                        loja.setLojaCep(lojaAtualizada.getLojaCep());
                    }
                    if (lojaAtualizada.getLojaCidade() != null) {
                        loja.setLojaCidade(lojaAtualizada.getLojaCidade());
                    }
                    if (lojaAtualizada.getLojaEstado() != null) {
                        loja.setLojaEstado(lojaAtualizada.getLojaEstado());
                    }

                    return lojaRepository.save(loja);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));
    }

    @Transactional
    public Loja atualizarFotoLoja(Long id, String fotoUrl, Usuario usuario) {
        return lojaRepository.findById(id)
                .map(loja -> {
                    if (!loja.getUsuario().getId().equals(usuario.getId())) {
                        throw new ValidationException("Você não tem permissão para editar esta loja");
                    }

                    loja.setFotoUrl(fotoUrl);
                    return lojaRepository.save(loja);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));
    }

    public List<Loja> listarLojasAtivas() {
        return lojaRepository.findByAtivoTrue();
    }

    public Page<Loja> listarLojasRecomendadas(Pageable pageable) {
        return lojaRepository.findLojasRecomendadas(pageable);
    }

    public Optional<Loja> buscarPorId(Long id) {
        return lojaRepository.findById(id);
    }

    public Optional<Loja> buscarPorIdAtiva(Long id) {
        return lojaRepository.findById(id)
                .filter(Loja::getAtivo);
    }

    public List<Loja> buscarPorNome(String nome) {
        return lojaRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Loja> buscarPorTermo(String termo) {
        return lojaRepository.buscarPorTermo(termo);
    }

    public List<Loja> listarLojasDoUsuario(Usuario usuario) {
        return lojaRepository.findByUsuarioId(usuario.getId());
    }

    public List<Loja> listarLojasPorUsuarioEmail(String email) {
        return lojaRepository.findByUsuarioEmail(email);
    }

    @Transactional
    public Loja desativarLoja(Long id, Usuario usuario) {
        return lojaRepository.findById(id)
                .map(loja -> {
                    if (!loja.getUsuario().getId().equals(usuario.getId())) {
                        throw new ValidationException("Você não tem permissão para desativar esta loja");
                    }
                    loja.setAtivo(false);
                    return lojaRepository.save(loja);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));
    }

    @Transactional
    public void deletarLoja(Long id, Usuario usuario) {
        Loja loja = lojaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));

        if (!loja.getUsuario().getId().equals(usuario.getId())) {
            throw new ValidationException("Você não tem permissão para deletar esta loja");
        }

        // Verifica se loja tem produtos ativos
        if (produtoService.contarProdutosAtivosPorLoja(loja.getId()) > 0) {
            throw new ValidationException("Não é possível deletar uma loja com produtos cadastrados");
        }

        lojaRepository.delete(loja);
    }

    public Long contarProdutosAtivosPorLoja(Long lojaId) {
        return produtoService.contarProdutosAtivosPorLoja(lojaId);
    }

    @Transactional
    public Loja salvar(Loja loja) {
        validarLoja(loja);
        return lojaRepository.save(loja);
    }


    private void validarLoja(Loja loja) {
        if (loja.getNome() == null || loja.getNome().trim().isEmpty()) {
            throw new ValidationException("Nome da loja é obrigatório");
        }

        if (loja.getCnpj() == null || loja.getCnpj().trim().isEmpty()) {
            throw new ValidationException("CNPJ é obrigatório");
        }

        // Remove espaços e caracteres especiais
        String cnpjLimpo = loja.getCnpj().replaceAll("\\D", "");

        if (cnpjLimpo.length() != 14) {
            throw new ValidationException("CNPJ deve ter 14 dígitos");
        }

        loja.setCnpj(cnpjLimpo); // Salva o CNPJ limpo

        // Validação do CNPJ: se a loja já tem ID (é edição), permite o mesmo CNPJ
        if (loja.getId() == null) {
            // Modo criação: não pode existir
            if (lojaRepository.existsByCnpj(cnpjLimpo)) {
                throw new ValidationException("CNPJ já cadastrado");
            }
        } else {
            // Modo edição: só não permite se o CNPJ está sendo usado por OUTRA loja
            if (lojaRepository.existsByCnpjAndIdNot(cnpjLimpo, loja.getId())) {
                throw new ValidationException("CNPJ já está em uso por outra loja");
            }
        }

        // Restante das validações permanecem...
        if (loja.getNome() != null && loja.getNome().length() > 100) {
            throw new ValidationException("Nome deve ter no máximo 100 caracteres");
        }

        if (loja.getDescricao() != null && loja.getDescricao().length() > 500) {
            throw new ValidationException("Descrição deve ter no máximo 500 caracteres");
        }

        if (loja.getTelefone() != null && loja.getTelefone().length() > 20) {
            throw new ValidationException("Telefone deve ter no máximo 20 caracteres");
        }

        if (loja.getEmail() != null && loja.getEmail().length() > 100) {
            throw new ValidationException("Email deve ter no máximo 100 caracteres");
        }

        if (loja.getSite() != null && loja.getSite().length() > 200) {
            throw new ValidationException("Site deve ter no máximo 200 caracteres");
        }

        if (loja.getFotoUrl() != null && loja.getFotoUrl().length() > 500) {
            throw new ValidationException("URL da foto deve ter no máximo 500 caracteres");
        }

        // Validações de endereço (opcionais, mas recomendadas)
        if (loja.getLojaCep() != null && !loja.getLojaCep().matches("\\d{8}")) {
            throw new ValidationException("CEP deve ter 8 dígitos");
        }

        if (loja.getLojaEstado() != null && loja.getLojaEstado().length() != 2) {
            throw new ValidationException("Estado deve ter 2 caracteres (sigla)");
        }
    }


}