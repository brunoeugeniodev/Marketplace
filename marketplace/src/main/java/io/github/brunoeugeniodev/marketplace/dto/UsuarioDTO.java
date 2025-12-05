package io.github.brunoeugeniodev.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone; // ADICIONE ESTA LINHA
    private List<String> roles;
    private Boolean ativo;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAtualizacao;

    // Método para mascarar CPF
    public String getCpf() {
        if (cpf != null && cpf.length() == 11) {
            return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
        }
        return cpf;
    }

    // Método para mascarar telefone (opcional)
    public String getTelefoneFormatado() {
        if (telefone != null && telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " +
                    telefone.substring(2, 7) + "-" +
                    telefone.substring(7);
        } else if (telefone != null && telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " +
                    telefone.substring(2, 6) + "-" +
                    telefone.substring(6);
        }
        return telefone;
    }

    // Método para verificar se é admin
    public Boolean isAdmin() {
        return roles != null && roles.contains("ROLE_ADMIN");
    }
}