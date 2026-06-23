CREATE TABLE sesiones_validacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    nombre_portero VARCHAR(255) NOT NULL,
    puesto_acceso VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    total_escaneados INT NOT NULL,
    iniciada_en DATETIME,
    cerrada_en DATETIME
);

CREATE TABLE validaciones_ticket (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sesion_id BIGINT NOT NULL,
    codigo_qr VARCHAR(255) NOT NULL,
    ticket_id BIGINT NOT NULL,
    resultado VARCHAR(255) NOT NULL,
    detalle_resultado VARCHAR(255),
    escaneado_en DATETIME,
    CONSTRAINT fk_validacion_sesion FOREIGN KEY (sesion_id) REFERENCES sesiones_validacion(id)
);
