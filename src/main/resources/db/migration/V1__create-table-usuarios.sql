CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ativo BOOLEAN NOT NULL,
    nome VARCHAR(100) NOT NULL,
    apelido VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(11) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);