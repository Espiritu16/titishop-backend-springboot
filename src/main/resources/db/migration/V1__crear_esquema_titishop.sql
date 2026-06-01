CREATE TABLE usuarios (
  id BINARY(16) NOT NULL,
  nombre_completo VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol VARCHAR(30) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL,
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_usuarios_email UNIQUE (email),
  CONSTRAINT chk_usuarios_rol CHECK (rol IN ('ADMINISTRADOR', 'ALMACENERO', 'SUPERVISOR')),
  CONSTRAINT chk_usuarios_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
  CONSTRAINT fk_usuarios_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_usuarios_actualizado_por FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_usuarios_inactivado_por FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);

CREATE TABLE categorias (
  id BINARY(16) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL,
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_categorias_nombre UNIQUE (nombre),
  CONSTRAINT chk_categorias_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
  CONSTRAINT fk_categorias_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_categorias_actualizado_por FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_categorias_inactivado_por FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_categorias_estado ON categorias(estado);

CREATE TABLE marcas (
  id BINARY(16) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL,
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_marcas_nombre UNIQUE (nombre),
  CONSTRAINT chk_marcas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
  CONSTRAINT fk_marcas_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_marcas_actualizado_por FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_marcas_inactivado_por FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_marcas_estado ON marcas(estado);

CREATE TABLE productos (
  id BINARY(16) NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  sku VARCHAR(40) NOT NULL,
  descripcion TEXT NULL,
  imagen_url VARCHAR(500) NULL,
  categoria_id BINARY(16) NOT NULL,
  marca_id BINARY(16) NOT NULL,
  precio_compra DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
  precio_venta DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL,
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_productos_sku UNIQUE (sku),
  CONSTRAINT chk_productos_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
  CONSTRAINT chk_productos_precio_compra CHECK (precio_compra >= 0),
  CONSTRAINT chk_productos_precio_venta CHECK (precio_venta >= 0),
  CONSTRAINT chk_productos_margen CHECK (precio_venta >= precio_compra),
  CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
  CONSTRAINT fk_productos_marca FOREIGN KEY (marca_id) REFERENCES marcas(id),
  CONSTRAINT fk_productos_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_productos_actualizado_por FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_productos_inactivado_por FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_productos_nombre ON productos(nombre);
CREATE INDEX idx_productos_estado ON productos(estado);
CREATE INDEX idx_productos_categoria ON productos(categoria_id);
CREATE INDEX idx_productos_marca ON productos(marca_id);

CREATE TABLE proveedores (
  id BINARY(16) NOT NULL,
  razon_social VARCHAR(120) NOT NULL,
  ruc CHAR(11) NOT NULL,
  celular CHAR(9) NOT NULL,
  telefono VARCHAR(9) NOT NULL,
  email VARCHAR(160) NOT NULL,
  direccion VARCHAR(160) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL,
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_proveedores_ruc UNIQUE (ruc),
  CONSTRAINT uk_proveedores_email UNIQUE (email),
  CONSTRAINT chk_proveedores_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
  CONSTRAINT fk_proveedores_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_proveedores_actualizado_por FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_proveedores_inactivado_por FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_proveedores_estado ON proveedores(estado);

CREATE TABLE inventario (
  id BINARY(16) NOT NULL,
  producto_id BINARY(16) NOT NULL,
  stock_actual INT NOT NULL DEFAULT 0,
  stock_minimo INT NOT NULL DEFAULT 0,
  ubicacion VARCHAR(40) NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL,
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_inventario_producto UNIQUE (producto_id),
  CONSTRAINT chk_inventario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
  CONSTRAINT chk_inventario_stock_actual CHECK (stock_actual >= 0),
  CONSTRAINT chk_inventario_stock_minimo CHECK (stock_minimo >= 0),
  CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
  CONSTRAINT fk_inventario_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_inventario_actualizado_por FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_inventario_inactivado_por FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_inventario_estado ON inventario(estado);
CREATE INDEX idx_inventario_stock_minimo ON inventario(stock_actual, stock_minimo);

CREATE TABLE movimientos (
  id BINARY(16) NOT NULL,
  producto_id BINARY(16) NOT NULL,
  proveedor_id BINARY(16) NULL,
  tipo VARCHAR(20) NOT NULL,
  cantidad INT NOT NULL,
  motivo VARCHAR(255) NOT NULL,
  stock_antes INT NOT NULL,
  stock_despues INT NOT NULL,
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NOT NULL,
  anulado_en TIMESTAMP(6) NULL,
  anulado_por_id BINARY(16) NULL,
  motivo_anulacion VARCHAR(255) NULL,
  PRIMARY KEY (id),
  CONSTRAINT chk_movimientos_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA', 'AJUSTE')),
  CONSTRAINT chk_movimientos_cantidad CHECK (cantidad > 0),
  CONSTRAINT chk_movimientos_stock_antes CHECK (stock_antes >= 0),
  CONSTRAINT chk_movimientos_stock_despues CHECK (stock_despues >= 0),
  CONSTRAINT fk_movimientos_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
  CONSTRAINT fk_movimientos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
  CONSTRAINT fk_movimientos_creado_por FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_movimientos_anulado_por FOREIGN KEY (anulado_por_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_movimientos_producto_fecha ON movimientos(producto_id, creado_en);
CREATE INDEX idx_movimientos_proveedor ON movimientos(proveedor_id);
CREATE INDEX idx_movimientos_tipo ON movimientos(tipo);
CREATE INDEX idx_movimientos_creado_en ON movimientos(creado_en);
