package codesauro.api.domain.progresso;

import codesauro.api.domain.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "progresso_fases")
@Entity(name = "ProgressoFase")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProgressoFase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private int faseId;
    private boolean bloqueada = true;
    private int estrelas = 0;

    public ProgressoFase(Usuario usuario, int faseId) {
        this.usuario = usuario;
        this.faseId = faseId;
    }

    public void atualizarProgresso(int estrelas, boolean desbloquearProxima) {
        this.estrelas = estrelas;
        if (desbloquearProxima) {
            this.bloqueada = false;
        }
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
    }
}
