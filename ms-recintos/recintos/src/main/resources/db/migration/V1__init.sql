CREATE TABLE recintos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    direccion VARCHAR(255),
    capacidad_maxima INT
);

CREATE TABLE sectores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    capacidad INT,
    precio_base DOUBLE,
    recinto_id BIGINT NOT NULL,
    CONSTRAINT fk_sector_recinto FOREIGN KEY (recinto_id) REFERENCES recintos(id)
);
