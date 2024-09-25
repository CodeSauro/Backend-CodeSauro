package codesauro.api.domain.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findAllByAtivoTrue(Pageable paginacao);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByResetToken(String token);
    Optional<Usuario> findByConfirmacaoToken(String token);
    Optional<Usuario> findByApelido(String apelido);


}

