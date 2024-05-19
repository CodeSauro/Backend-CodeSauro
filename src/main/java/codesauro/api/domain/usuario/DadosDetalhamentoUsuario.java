package codesauro.api.domain.usuario;

public record DadosDetalhamentoUsuario(
        Long id,
        Boolean Ativo,
        String nome,
        String apelido,
        String email,
        String telefone,
        String senha
    ) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getAtivo(), usuario.getNome(), usuario.getApelido(), usuario.getEmail(), usuario.getTelefone(), usuario.getSenha());
    }
}
