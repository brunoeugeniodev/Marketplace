package io.github.brunoeugeniodev.marketplace.util;

import io.github.brunoeugeniodev.marketplace.dto.*;
import io.github.brunoeugeniodev.marketplace.models.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MapperUtil {

    private final ModelMapper modelMapper;

    // Usuario
    // No método toUsuarioEntity do MapperUtil
    public Usuario toUsuarioEntity(UsuarioRegisterDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));

        // Mapear telefone se existir
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            usuario.setTelefone(dto.getTelefone().replaceAll("[^0-9]", ""));
        }

        usuario.setSenha(dto.getSenha());
        usuario.setAtivo(true);
        return usuario;
    }

    // No método toUsuarioDTO do MapperUtil
    public UsuarioDTO toUsuarioDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .telefone(usuario.getTelefone()) // ADICIONE ESTA LINHA
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .build();
    }

    public Usuario toUsuarioEntity(UsuarioUpdateDTO dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        return usuario;
    }

    // Produto
    public ProdutoDTO toProdutoDTO(Produto produto) {
        ProdutoDTO dto = modelMapper.map(produto, ProdutoDTO.class);
        dto.setLojaId(produto.getLoja() != null ? produto.getLoja().getId() : null);
        dto.setLojaNome(produto.getLoja() != null ? produto.getLoja().getNome() : null);
        dto.setLojaCnpj(produto.getLoja() != null ? produto.getLoja().getCnpj() : null);
        return dto;
    }

    public Produto toProdutoEntity(ProdutoDTO dto) {
        return modelMapper.map(dto, Produto.class);
    }

    public Produto toProdutoEntity(ProdutoCreateDTO dto) {
        return modelMapper.map(dto, Produto.class);
    }

    public Produto toProdutoEntity(ProdutoUpdateDTO dto) {
        return modelMapper.map(dto, Produto.class);
    }


    public Loja toLojaEntity(LojaDTO dto) {
        return modelMapper.map(dto, Loja.class);
    }

    // No arquivo MapperUtil.java
// Adicione este método para mapear LojaCreateDTO para Loja
    public Loja toLojaEntity(LojaCreateDTO dto) {
        Loja loja = modelMapper.map(dto, Loja.class);

        // Mapear os campos de endereço manualmente se necessário
        loja.setLojaCep(dto.getCep());
        loja.setLojaRua(dto.getLogradouro());
        loja.setLojaNumerao(dto.getNumero());
        loja.setLojaComplemento(dto.getComplemento());
        loja.setLojaBairro(dto.getBairro());
        loja.setLojaCidade(dto.getCidade());
        loja.setLojaEstado(dto.getEstado());

        // A categoria já será mapeada automaticamente pelo ModelMapper
        // pois temos o campo com o mesmo nome em LojaCreateDTO e Loja

        return loja;
    }

    public LojaDTO toLojaDTO(Loja loja) {
        LojaDTO dto = modelMapper.map(loja, LojaDTO.class);
        if (loja.getUsuario() != null) {
            dto.setProprietarioId(loja.getUsuario().getId());
            dto.setProprietarioNome(loja.getUsuario().getNome());
        }

        // A categoria será mapeada automaticamente

        return dto;
    }

    public Loja toLojaEntity(LojaUpdateDTO dto) {
        return modelMapper.map(dto, Loja.class);
    }

    // Endereco (do Usuario)
    public EnderecoDTO toEnderecoDTO(Endereco endereco) {
        EnderecoDTO dto = modelMapper.map(endereco, EnderecoDTO.class);
        dto.setUsuarioId(endereco.getUsuario() != null ? endereco.getUsuario().getId() : null);
        return dto;
    }

    public Endereco toEnderecoEntity(EnderecoDTO dto) {
        return modelMapper.map(dto, Endereco.class);
    }

    public Endereco toEnderecoEntity(EnderecoCreateDTO dto) {
        return modelMapper.map(dto, Endereco.class);
    }

    public Endereco toEnderecoEntity(EnderecoUpdateDTO dto) {
        return modelMapper.map(dto, Endereco.class);
    }

    // Carrinho
    public CarrinhoDTO mapCarrinhoToDTO(Carrinho carrinho) {
        CarrinhoDTO dto = modelMapper.map(carrinho, CarrinhoDTO.class);
        if (carrinho.getUsuario() != null) {
            dto.setUsuarioId(carrinho.getUsuario().getId());
            dto.setUsuarioNome(carrinho.getUsuario().getNome());
            dto.setUsuarioEmail(carrinho.getUsuario().getEmail());
        }

        if (carrinho.getItens() != null && !carrinho.getItens().isEmpty()) {
            List<ItemCarrinhoDTO> itensDTO = carrinho.getItens().stream()
                    .map(this::mapItemCarrinhoToDTO)
                    .collect(Collectors.toList());
            dto.setItens(itensDTO);
        }

        return dto;
    }

    public ItemCarrinhoDTO mapItemCarrinhoToDTO(ItemCarrinho item) {
        ItemCarrinhoDTO dto = modelMapper.map(item, ItemCarrinhoDTO.class);
        if (item.getProduto() != null) {
            dto.setProdutoId(item.getProduto().getId());
            dto.setProdutoNome(item.getProduto().getNome());
            dto.setProdutoDescricao(item.getProduto().getDescricao());
            dto.setProdutoPreco(item.getProduto().getPreco());
            dto.setProdutoFotoUrl(item.getProduto().getFotoUrl());
            dto.setProdutoQuantidadeDisponivel(item.getProduto().getQuantidade());
            dto.setProdutoDisponivel(item.getProduto().isDisponivel());
        }
        return dto;
    }

    public ItemCarrinho toItemCarrinhoEntity(ItemCarrinhoRequestDTO dto, Produto produto) {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(produto.getPreco());
        return item;
    }


    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    // Adicione estes métodos na classe MapperUtil:

    public ProdutoUpdateDTO toProdutoUpdateDTO(Produto produto) {
        return modelMapper.map(produto, ProdutoUpdateDTO.class);
    }

    public <T> T toProdutoEntity(Produto produto, Class<T> targetClass) {
        return modelMapper.map(produto, targetClass);
    }

    // E atualize o método configureModelMapper para incluir configurações de produto:
    public void configureModelMapper() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        // Configuração para Produto
        modelMapper.createTypeMap(ProdutoCreateDTO.class, Produto.class)
                .addMappings(mapper -> {
                    mapper.skip(Produto::setId);
                    mapper.skip(Produto::setLoja);
                    mapper.skip(Produto::setAtivo);
                    mapper.skip(Produto::setDataCriacao);
                    mapper.skip(Produto::setDataAtualizacao);
                    mapper.skip(Produto::setAvaliacaoMedia);
                    mapper.skip(Produto::setTotalVendas);
                });

        modelMapper.createTypeMap(ProdutoUpdateDTO.class, Produto.class)
                .addMappings(mapper -> {
                    mapper.skip(Produto::setId);
                    mapper.skip(Produto::setLoja);
                    mapper.skip(Produto::setAtivo);
                    mapper.skip(Produto::setDataCriacao);
                    mapper.skip(Produto::setDataAtualizacao);
                    mapper.skip(Produto::setAvaliacaoMedia);
                    mapper.skip(Produto::setTotalVendas);
                });
    }
}