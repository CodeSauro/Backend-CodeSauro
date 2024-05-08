package codesauro.api.domain.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface AutenticacaoRepository extends JpaRepository<Autenticacao, Long> {
    UserDetails findByLogin(String login);
}
