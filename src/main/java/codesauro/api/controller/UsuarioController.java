package codesauro.api.controller;

import codesauro.api.domain.autenticacao.Autenticacao;
import codesauro.api.domain.autenticacao.AutenticacaoRepository;
import codesauro.api.domain.email.DadosRecuperacaoSenha;
import codesauro.api.domain.email.DadosRedefinicaoSenha;
import codesauro.api.domain.email.EmailService;
import codesauro.api.domain.progresso.ProgressoFase;
import codesauro.api.domain.progresso.ProgressoFaseRepository;
import codesauro.api.domain.usuario.Usuario;
import codesauro.api.domain.usuario.UsuarioRepository;
import codesauro.api.domain.usuario.DadosCadastroUsuario;
import codesauro.api.domain.usuario.DadosAtualizarUsuario;
import codesauro.api.domain.usuario.DadosDetalhamentoUsuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AutenticacaoRepository autenticacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProgressoFaseRepository progressoFaseRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        var usuario = new Usuario(dados.nome(), dados.apelido(), dados.email(), dados.telefone(), senhaCriptografada);
        repository.save(usuario);

        for (int i = 1; i <= 40; i++) {
            boolean primeiraFase = (i == 1);
            var progressoFase = new ProgressoFase(usuario, i);
            if (primeiraFase) {
                progressoFase.atualizarProgresso(0, true);
            }
            progressoFaseRepository.save(progressoFase);
        }

        var autenticacao = new Autenticacao();
        autenticacao.setUsuario(usuario);
        autenticacao.setLogin(dados.apelido());
        autenticacao.setSenha(senhaCriptografada);
        autenticacaoRepository.save(autenticacao);

        emailService.enviarEmailConfirmacao(usuario.getEmail(), usuario.getConfirmacaoToken());

        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body("Cadastro realizado com sucesso. Verifique seu email para confirmar o cadastro.");
    }

    @GetMapping
    public ResponseEntity<Page<Usuario>> listar(Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var usuario = repository.getReferenceById(id);
        usuario.regenerarVidas();
        repository.save(usuario);
        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario, usuario.calcularTempoFormatado(usuario)));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizarUsuario dados) {
        var usuario = repository.getReferenceById(dados.id());
        usuario.atualizarInformacoes(dados);
        repository.save(usuario);

        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario, usuario.calcularTempoFormatado(usuario)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var usuario = repository.getReferenceById(id);
        usuario.excluir();
        repository.save(usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progresso")
    public ResponseEntity<List<ProgressoFase>> listarProgresso(@PathVariable Long id) {
        var progressoFases = progressoFaseRepository.findByUsuarioId(id);
        return ResponseEntity.ok(progressoFases);
    }

    @PutMapping("/{id}/progresso/{faseId}")
    @Transactional
    public ResponseEntity atualizarProgresso(
            @PathVariable Long id,
            @PathVariable int faseId,
            @RequestParam int estrelas) {

        var progressoFase = progressoFaseRepository.findByUsuarioId(id).stream()
                .filter(pf -> pf.getFaseId() == faseId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fase não encontrada para este usuário"));

        if (estrelas > progressoFase.getEstrelas()) {
            progressoFase.atualizarProgresso(estrelas, true);
            progressoFaseRepository.save(progressoFase);
        }

        if (estrelas > 0 && faseId < 40) {
            var proximaFase = progressoFaseRepository.findByUsuarioId(id).stream()
                    .filter(pf -> pf.getFaseId() == faseId + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Próxima fase não encontrada"));

            proximaFase.setBloqueada(false);
            progressoFaseRepository.save(proximaFase);
        }

        int totalEstrelas = progressoFaseRepository.findByUsuarioId(id).stream()
                .mapToInt(ProgressoFase::getEstrelas)
                .sum();

        var usuario = repository.getReferenceById(id);
        usuario.atualizarInformacoes(new DadosAtualizarUsuario(id, null, null, null, null, null, totalEstrelas, null));
        repository.save(usuario);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/vidas")
    @Transactional
    public ResponseEntity atualizarVidas(
            @PathVariable Long id,
            @RequestParam boolean respostaCorreta) {

        var usuario = repository.getReferenceById(id);

        if (!respostaCorreta) {
            usuario.perderVida();
            repository.save(usuario);
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/pausar-regeneracao")
    @Transactional
    public ResponseEntity<Void> pausarRegeneracao(@PathVariable Long id) {
        var usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        usuario.pausarRegeneracao();
        repository.save(usuario);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/retomar-regeneracao")
    @Transactional
    public ResponseEntity<Void> retomarRegeneracao(@PathVariable Long id) {
        var usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        usuario.retomarRegeneracao();
        repository.save(usuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/solicitar-recuperacao")
    @Transactional
    public ResponseEntity<Void> solicitarRecuperacaoSenha(@RequestBody DadosRecuperacaoSenha dados) {
        var usuario = repository.findByEmail(dados.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String token = String.format("%06d", new Random().nextInt(999999));
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusHours(1));
        repository.save(usuario);

        emailService.enviarEmailRecuperacao(usuario.getEmail(), token);

        return ResponseEntity.noContent().build();
    }


    @PutMapping("/redefinir-senha")
    @Transactional
    public ResponseEntity<Void> redefinirSenha(@RequestBody DadosRedefinicaoSenha dados) {

        var usuario = repository.findByResetToken(dados.getToken())
                .filter(u -> u.getTokenExpiration().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido ou expirado"));

        String senhaCriptografada = passwordEncoder.encode(dados.getNovaSenha());
        usuario.setSenha(senhaCriptografada);
        usuario.setResetToken(null);
        usuario.setTokenExpiration(null);
        repository.save(usuario);

        var autenticacao = autenticacaoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado na tabela de autenticação"));
        autenticacao.setSenha(senhaCriptografada);
        autenticacaoRepository.save(autenticacao);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/confirmar")
    @Transactional
    public ResponseEntity<Void> confirmarEmail(@RequestParam("token") String token) {
        var usuario = repository.findByConfirmacaoToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido"));

        usuario.setAtivo(true);
        usuario.setConfirmado(true);
        usuario.setConfirmacaoToken(null);
        repository.save(usuario);

        return ResponseEntity.ok().build();
    }

}
