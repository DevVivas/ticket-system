CREATE TABLE devoluciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    motivo TEXT NOT NULL,
    monto_devolucion DOUBLE NOT NULL,
    estado VARCHAR(255) NOT NULL,
    tipo_devolucion VARCHAR(255) NOT NULL,
    creado_en DATETIME,
    actualizado_en DATETIME
);

CREATE TABLE reembolsos_devolucion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    devolucion_id BIGINT NOT NULL,
    monto_reembolso DOUBLE NOT NULL,
    metodo_reembolso VARCHAR(255) NOT NULL,
    estado_reembolso VARCHAR(255) NOT NULL,
    referencia_bancaria VARCHAR(255),
    creado_en DATETIME,
    procesado_en DATETIME,
    CONSTRAINT fk_reembolso_devolucion FOREIGN KEY (devolucion_id) REFERENCES devoluciones(id)
);
