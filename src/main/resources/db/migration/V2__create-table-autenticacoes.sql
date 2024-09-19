CREATE TABLE autenticacoes (
    id BIGINT NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);
