CREATE TABLE codigos_beneficio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    tipo VARCHAR(255) NOT NULL,
    porcentaje_descuento DOUBLE NOT NULL,
    uso_maximo INT NOT NULL,
    uso_actual INT,
    fecha_expiracion DATETIME,
    evento_id BIGINT,
    activo BIT
);
