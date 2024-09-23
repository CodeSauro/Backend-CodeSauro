package codesauro.api.domain.autenticacao;

import codesauro.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface AutenticacaoRepository extends JpaRepository<Autenticacao, Long> {
    UserDetails findByLogin(String login);
    Optional<Autenticacao> findByUsuario(Usuario usuario);

}
