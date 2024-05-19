package codesauro.api.infra.security;

import codesauro.api.domain.autenticacao.Autenticacao;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String gerarToken(Autenticacao autenticacao) {
        try {
            var algoritmo = getAlgorithm();
            return JWT.create()
                    .withIssuer("BackEnd da aplicação CodeSauro")
                    .withSubject(autenticacao.getLogin())
                    .withClaim("id", autenticacao.getId())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = getAlgorithm();
            return JWT.require(algoritmo)
                    .withIssuer("BackEnd da aplicação CodeSauro")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!", exception);
        }
    }

    public boolean validateToken(String tokenJWT) {
        try {
            getSubject(tokenJWT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
