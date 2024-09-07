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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean ativo;
    private String nome;
    private String apelido;
    private String email;
    private String telefone;
    private String senha;
    private int estrelas = 0;
    private int vidas = 5;

    public Usuario(String nome, String apelido, String email, String telefone, String senha) {
        this.ativo = true;
        this.nome = nome;
        this.apelido = apelido;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.estrelas = 0;
        this.vidas = 5;
    }

    public void atualizarInformacoes(DadosAtualizarUsuario dados) {
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
        if (dados.senha() != null) {
            this.senha = dados.senha();
        }
        if (dados.estrelas() != null) {
            this.estrelas = dados.estrelas();
        }
        if (dados.vidas() != null && dados.vidas() >= 0 && dados.vidas() <= 5) {
            this.vidas = dados.vidas();
        } else if (dados.vidas() != null) {
            throw new IllegalArgumentException("O número de vidas deve estar entre 0 e 5.");
        }
    }



    public void excluir() {
        this.ativo = false;
    }
}
