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

    @Column(name = "regeneracao_pausada")
    private Boolean regeneracaoPausada = false;

    @Column(name = "tempo_restante_pausado")
    private Long tempoRestantePausado;

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
        this.regeneracaoPausada = false;
        this.tempoRestantePausado = null;
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

    public void regenerarVidas() {
        if (this.regeneracaoPausada) {
            return;
        }
        if (this.vidas < 5) {
            LocalDateTime agora = LocalDateTime.now();
            Duration duracao = Duration.between(this.ultimaAtualizacaoVidas, agora);

            long minutosPassados = duracao.toMinutes();
            int vidasParaAdicionar = (int) Math.min(minutosPassados, 5 - this.vidas);

            if (vidasParaAdicionar > 0) {
                this.vidas += vidasParaAdicionar;
                this.ultimaAtualizacaoVidas = agora.minusMinutes(minutosPassados - vidasParaAdicionar);
            }
        }
    }

    public void perderVida() {
        if (this.vidas > 0) {
            this.vidas -= 1;
            LocalDateTime agora = LocalDateTime.now();

            if (this.regeneracaoPausada) {
                this.tempoRestantePausado += 60;
            } else {
                if (this.vidas < 5) {
                    this.ultimaAtualizacaoVidas = agora;
                }
            }
        }
    }


    public Duration getTempoParaTodasVidas() {
        if (this.regeneracaoPausada && this.tempoRestantePausado != null) {
            return Duration.ofSeconds(this.tempoRestantePausado);
        }

        if (this.vidas < 5) {
            LocalDateTime agora = LocalDateTime.now();
            Duration duracao = Duration.between(this.ultimaAtualizacaoVidas, agora);

            int vidasFaltantes = 5 - this.vidas;
            long tempoTotalRestanteSegundos = (vidasFaltantes * 60) - duracao.getSeconds();

            return Duration.ofSeconds(tempoTotalRestanteSegundos > 0 ? tempoTotalRestanteSegundos : 0);
        } else {
            return Duration.ZERO;
        }
    }

    public void pausarRegeneracao() {
        if (!this.regeneracaoPausada) {
            this.regeneracaoPausada = true;
            this.tempoRestantePausado = getTempoParaTodasVidas().getSeconds();
        }
    }

    public void retomarRegeneracao() {
        if (this.regeneracaoPausada) {
            this.regeneracaoPausada = false;
            this.ultimaAtualizacaoVidas = LocalDateTime.now().minusSeconds(60 * (5 - this.vidas) - this.tempoRestantePausado);
            this.tempoRestantePausado = null;
        }
    }

    public String calcularTempoFormatado(Usuario usuario) {
        var tempoRestante = usuario.getTempoParaTodasVidas();
        long minutos = tempoRestante.toMinutes();
        long segundos = tempoRestante.minusMinutes(minutos).getSeconds();
        return String.format("%02d:%02d", minutos, segundos);
    }
}
