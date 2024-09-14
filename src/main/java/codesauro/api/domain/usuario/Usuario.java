package codesauro.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

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

    @Column(name = "ultima_atualizacao_vidas")
    private LocalDateTime ultimaAtualizacaoVidas = LocalDateTime.now();

    public Usuario(String nome, String apelido, String email, String telefone, String senha) {
        this.ativo = true;
        this.nome = nome;
        this.apelido = apelido;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.estrelas = 0;
        this.vidas = 5;
        this.ultimaAtualizacaoVidas = LocalDateTime.now();
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

    // Método para regenerar vidas
    public void regenerarVidas() {
        if (this.vidas < 5) {
            LocalDateTime agora = LocalDateTime.now();
            Duration duracao = Duration.between(this.ultimaAtualizacaoVidas, agora);

            // Tempo total em minutos que se passou desde a última atualização
            long minutosPassados = duracao.toMinutes();
            int vidasParaAdicionar = (int) Math.min(minutosPassados, 5 - this.vidas);

            if (vidasParaAdicionar > 0) {
                this.vidas += vidasParaAdicionar;
                // Atualiza a última atualização das vidas apenas se vidas forem regeneradas
                this.ultimaAtualizacaoVidas = agora.minusMinutes(minutosPassados - vidasParaAdicionar);
            }
        }
    }

    // Método para perder vida
    public void perderVida() {
        if (this.vidas > 0) {
            this.vidas -= 1;
            // Se as vidas estavam em 5, atualize a hora da primeira perda
            if (this.vidas == 4) {
                this.ultimaAtualizacaoVidas = LocalDateTime.now();
            }
        }
    }

    // Método para obter o tempo restante até todas as vidas serem regeneradas
    public Duration getTempoParaTodasVidas() {
        if (this.vidas < 5) {
            LocalDateTime agora = LocalDateTime.now();
            Duration duracao = Duration.between(this.ultimaAtualizacaoVidas, agora);

            // Calcula o tempo total restante para regenerar todas as vidas faltantes
            int vidasFaltantes = 5 - this.vidas;

            // Tempo total em segundos para regenerar todas as vidas faltantes
            long tempoTotalRestanteSegundos = (vidasFaltantes * 60) - duracao.getSeconds();

            // Garantir que o tempo não seja negativo
            return Duration.ofSeconds(tempoTotalRestanteSegundos > 0 ? tempoTotalRestanteSegundos : 0);
        } else {
            return Duration.ZERO; // Se o jogador já tem 5 vidas, não há regeneração pendente
        }
    }

}
