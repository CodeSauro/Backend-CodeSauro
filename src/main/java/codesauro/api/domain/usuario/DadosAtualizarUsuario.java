package codesauro.api.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosAtualizarUsuario(
        Long id,
        String nome,
        String apelido,
        String email,
        String telefone,
        String senha,
        Integer estrelas,
        Integer vidas
) {
}
