CREATE TABLE artistas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    genero VARCHAR(255) NOT NULL,
    nacionalidad VARCHAR(255) NOT NULL,
    biografia TEXT,
    imagen_url VARCHAR(300),
    sitio_web VARCHAR(300),
    estado VARCHAR(255) NOT NULL,
    creado_en DATETIME,
    actualizado_en DATETIME
);

CREATE TABLE agenda_artistas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artista_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    nombre_evento VARCHAR(255) NOT NULL,
    fecha_presentacion DATETIME NOT NULL,
    lugar VARCHAR(255) NOT NULL,
    estado_agenda VARCHAR(255) NOT NULL,
    notas TEXT,
    creado_en DATETIME,
    CONSTRAINT fk_agenda_artista FOREIGN KEY (artista_id) REFERENCES artistas(id)
);
