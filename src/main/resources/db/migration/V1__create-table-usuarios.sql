CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ativo BOOLEAN NOT NULL,
    nome VARCHAR(100) NOT NULL,
    apelido VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(11) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    estrelas INT NOT NULL DEFAULT 0,
    vidas INT NOT NULL DEFAULT 5,
    ultima_atualizacao_vidas TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    regeneracao_pausada BOOLEAN DEFAULT FALSE,
    tempo_restante_pausado BIGINT DEFAULT NULL,
    reset_token VARCHAR(255),
    token_expiration TIMESTAMP,
    confirmado BOOLEAN DEFAULT FALSE,
    confirmacao_token VARCHAR(255),

    PRIMARY KEY (id)
);