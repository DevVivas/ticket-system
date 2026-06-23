CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_unico VARCHAR(255) NOT NULL UNIQUE,
    evento_id BIGINT NOT NULL,
    sector_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    precio DOUBLE NOT NULL,
    estado VARCHAR(255),
    codigo_qr VARCHAR(255)
);
