package codesauro.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean ativo;
    private String nome;
    private String apelido;
    private String email;
    private String telefone;
    private String data_de_nascimento;
    private String senha;

    public Usuario(DadosCadastroUsuario dados) {
        this.ativo = true;
        this.nome = dados.nome();
        this.apelido = dados.apelido();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.data_de_nascimento = dados.data_de_nascimento();
        this.senha = dados.senha();
    }

    public void atualizarInformacoes(DadosAtualizarUsuario dados) {
        if (dados.ativo() != null) {
            this.ativo = dados.ativo();
        }
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.apelido() != null) {
            this.apelido = dados.apelido();
        }
        if (dados.email() != null) {
            this.email = dados.email();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if (dados.data_de_nascimento() != null) {
            this.data_de_nascimento = dados.data_de_nascimento();
        }
        if (dados.senha() != null) {
            this.senha = dados.senha();
        }
    }

    public void excluir() {
        this.ativo = false;
    }

}
