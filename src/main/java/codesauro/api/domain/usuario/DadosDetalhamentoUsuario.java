package codesauro.api.domain.usuario;

public record DadosDetalhamentoUsuario(
        Long id,
        Boolean ativo,
        String nome,
        String apelido,
        String email,
        String telefone,
        String senha,
        int estrelas,
        int vidas
) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getAtivo(),
                usuario.getNome(),
                usuario.getApelido(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getSenha(),
                usuario.getEstrelas(),
                usuario.getVidas()
        );
    }
}

