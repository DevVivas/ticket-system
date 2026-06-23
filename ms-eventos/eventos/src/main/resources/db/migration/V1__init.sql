CREATE TABLE eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo VARCHAR(255),
    fecha_evento DATETIME NOT NULL,
    lugar VARCHAR(255),
    capacidad_total INT,
    descripcion VARCHAR(255),
    estado VARCHAR(255) NOT NULL
);
