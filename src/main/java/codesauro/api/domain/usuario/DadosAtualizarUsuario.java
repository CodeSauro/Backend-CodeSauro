package codesauro.api.domain.usuario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarUsuario(
        @NotNull
        Long id,
        Boolean ativo,
        String nome,
        String apelido,
        String email,
        String telefone,
        String senha
    ) {
}
