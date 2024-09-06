CREATE TABLE progresso_fases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    fase_id INT NOT NULL,
    bloqueada BOOLEAN NOT NULL DEFAULT TRUE,
    estrelas INT NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
