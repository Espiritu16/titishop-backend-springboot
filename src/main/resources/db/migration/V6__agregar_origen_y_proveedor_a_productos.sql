ALTER TABLE productos ADD COLUMN proveedor_id BINARY(16) NULL;
ALTER TABLE productos ADD COLUMN pais_origen VARCHAR(80) NULL;

UPDATE productos p
SET proveedor_id = (
  SELECT m.proveedor_id
  FROM movimientos m
  WHERE m.producto_id = p.id
    AND m.proveedor_id IS NOT NULL
  ORDER BY m.creado_en ASC
  LIMIT 1
)
WHERE proveedor_id IS NULL;

UPDATE productos
SET proveedor_id = (
  SELECT pr.id
  FROM proveedores pr
  ORDER BY pr.creado_en ASC
  LIMIT 1
)
WHERE proveedor_id IS NULL;

UPDATE productos
SET pais_origen = 'No especificado'
WHERE pais_origen IS NULL OR TRIM(pais_origen) = '';

ALTER TABLE productos MODIFY COLUMN proveedor_id BINARY(16) NOT NULL;
ALTER TABLE productos MODIFY COLUMN pais_origen VARCHAR(80) NOT NULL;

ALTER TABLE productos
  ADD CONSTRAINT fk_productos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id);

CREATE INDEX idx_productos_proveedor ON productos(proveedor_id);
CREATE INDEX idx_productos_pais_origen ON productos(pais_origen);
