package codesauro.api.controller;

import codesauro.api.domain.autenticacao.Autenticacao;
import codesauro.api.domain.autenticacao.AutenticacaoRepository;
import codesauro.api.domain.usuario.Usuario;
import codesauro.api.domain.usuario.UsuarioRepository;
import codesauro.api.domain.usuario.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(dados.nome(), dados.apelido(), dados.email(), dados.telefone(), senhaCriptografada);
        repository.save(usuario);

        var autenticacao = new Autenticacao();
        autenticacao.setLogin(dados.apelido());
        autenticacao.setSenha(senhaCriptografada);
        autenticacaoRepository.save(autenticacao);

        var uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoUsuario(usuario));
    }

    @GetMapping
    public ResponseEntity<Page<Usuario>> listar(Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var usuario = repository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario));
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizarUsuario dados) {
        var usuario = repository.getReferenceById(dados.id());
        usuario.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var usuario = repository.getReferenceById(id);
        usuario.excluir();

        return ResponseEntity.noContent().build();
    }

}
