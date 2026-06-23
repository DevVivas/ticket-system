CREATE TABLE promotores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(255) NOT NULL,
    porcentaje_comision DOUBLE NOT NULL,
    estado VARCHAR(255) NOT NULL,
    creado_en DATETIME,
    actualizado_en DATETIME
);

CREATE TABLE comisiones_promotor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    promotor_id BIGINT NOT NULL,
    venta_id BIGINT NOT NULL,
    monto_venta DOUBLE NOT NULL,
    porcentaje_aplicado DOUBLE NOT NULL,
    monto_comision DOUBLE NOT NULL,
    estado_comision VARCHAR(255) NOT NULL,
    creado_en DATETIME,
    pagado_en DATETIME,
    CONSTRAINT fk_comision_promotor FOREIGN KEY (promotor_id) REFERENCES promotores(id)
);
