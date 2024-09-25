package codesauro.api.domain.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacao(String email, String token) {
        String subject = "Recuperação de Senha";
        String message = "Para redefinir sua senha, use o seguinte código: " + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    public void enviarEmailConfirmacao(String email, String token) {
        String subject = "Confirmação de Cadastro";

        String confirmUrl = "http://localhost:8080/usuarios/confirmar?token=" + token;

        String message = "<p>Para confirmar seu cadastro, clique no botão abaixo:</p>" +
                "<a href=\"" + confirmUrl + "\" style=\"padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none;\">Confirmar Cadastro</a>";

        MimeMessage mailMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message, true);
        } catch (MessagingException e) {
            throw new IllegalStateException("Erro ao enviar email", e);
        }

        mailSender.send(mailMessage);
    }

}
