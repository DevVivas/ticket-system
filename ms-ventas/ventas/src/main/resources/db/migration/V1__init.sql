CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comprador_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    fecha_venta DATETIME,
    monto_total DOUBLE NOT NULL,
    metodo_pago VARCHAR(255) NOT NULL,
    estado VARCHAR(255)
);

CREATE TABLE items_venta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    cantidad INT NOT NULL,
    CONSTRAINT fk_item_venta FOREIGN KEY (venta_id) REFERENCES ventas(id)
);
