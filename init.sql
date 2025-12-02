-- Tabela: usuario
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Tabela: partida_historico
CREATE TABLE partida_historico (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES usuario(id),
    adversario VARCHAR(100),
    resultado VARCHAR(20),
    data_hora TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    movimentos INTEGER,
    nivel VARCHAR(20),
    duracao_segundos INTEGER
);

-- Tabela: torneio
CREATE TABLE torneio (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES usuario(id),
    posicao_final INTEGER
);

-- Tabela: partida do torneio
CREATE TABLE partida (
    id SERIAL PRIMARY KEY,
    torneio_id INTEGER REFERENCES torneio(id),
    vencedor VARCHAR(100),
    data_partida TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    nivelia VARCHAR(255)
);
