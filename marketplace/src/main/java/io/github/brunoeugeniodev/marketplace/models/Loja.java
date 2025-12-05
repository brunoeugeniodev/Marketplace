// No arquivo Loja.java (models)
package io.github.brunoeugeniodev.marketplace.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lojas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;

    private String site;

    @Column(name = "foto_url")
    private String fotoUrl;

    // NOVO CAMPO: Categoria da loja
    @Column(name = "categoria")
    private String categoria;

    // Endereço
    @Column(name = "loja_rua")
    private String lojaRua;

    @Column(name = "loja_numero")
    private String lojaNumerao;

    @Column(name = "loja_complemento")
    private String lojaComplemento;

    @Column(name = "loja_bairro")
    private String lojaBairro;

    @Column(name = "loja_cep")
    private String lojaCep;

    @Column(name = "loja_cidade")
    private String lojaCidade;

    @Column(name = "loja_estado")
    private String lojaEstado;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(name = "avaliacao_media")
    private Double avaliacaoMedia = 0.0;

    @Column(name = "total_avaliacoes")
    private Integer totalAvaliacoes = 0;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}