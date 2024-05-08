create table usuarios(
    id bigint not null auto_increment,
    ativo boolean not null,
    nome varchar(100) not null,
    apelido varchar(100) not null unique,
    email varchar(100) not null unique,
    telefone varchar(11) not null unique,
    data_de_nascimento varchar(8) not null,
    senha varchar(100) not null,

    primary key (id)
);