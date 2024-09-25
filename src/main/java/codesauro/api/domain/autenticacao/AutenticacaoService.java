package codesauro.api.domain.autenticacao;

import codesauro.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private AutenticacaoRepository repository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var autenticacao = repository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Login não encontrado"));

        if (!autenticacao.getUsuario().getConfirmado()) {
            throw new IllegalStateException("O e-mail ainda não foi confirmado.");
        }

        return autenticacao;
    }

}
