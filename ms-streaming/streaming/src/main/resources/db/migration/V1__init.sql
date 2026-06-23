CREATE TABLE streamings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL UNIQUE,
    nombre_stream VARCHAR(255) NOT NULL,
    url_stream VARCHAR(255) NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NOT NULL,
    capacidad_maxima INT NOT NULL,
    capacidad_disponible INT NOT NULL,
    estado VARCHAR(255) NOT NULL,
    descripcion TEXT,
    creado_en DATETIME,
    actualizado_en DATETIME
);

CREATE TABLE accesos_streaming (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    streaming_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    nombre_espectador VARCHAR(255) NOT NULL,
    email_espectador VARCHAR(255) NOT NULL,
    codigo_acceso VARCHAR(255) NOT NULL UNIQUE,
    estado_acceso VARCHAR(255) NOT NULL,
    creado_en DATETIME,
    usado_en DATETIME,
    CONSTRAINT fk_acceso_streaming FOREIGN KEY (streaming_id) REFERENCES streamings(id)
);
