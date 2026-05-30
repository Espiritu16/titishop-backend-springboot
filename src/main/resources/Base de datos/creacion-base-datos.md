# Base de datos - TitiShop

Este archivo mantiene el borrador SQL en formato Markdown para evitar que Spring Boot o Flyway lo ejecuten automaticamente.

Convenciones:
- Motor objetivo: MySQL 8.
- IDs tecnicos: `BINARY(16)` para UUID.
- Fechas de auditoria: `TIMESTAMP(6)`.
- Estados de negocio: `ACTIVO` / `INACTIVO` cuando la tabla permite desactivacion.
- Auditoria comun en tablas modificables: `creado_por_id`, `actualizado_por_id`, `inactivado_por_id`.
- Movimientos de inventario son registros historicos; no se editan ni se inactivan, se anulan si fuera necesario.

```sql
CREATE DATABASE IF NOT EXISTS titishop
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE titishop;

CREATE TABLE usuarios (
  id BINARY(16) NOT NULL,
  nombre_completo VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol ENUM('ADMINISTRADOR', 'ALMACENERO', 'SUPERVISOR') NOT NULL,
  estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_email (email),
  KEY idx_usuarios_rol (rol),
  KEY idx_usuarios_estado (estado),
  CONSTRAINT fk_usuarios_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_usuarios_actualizado_por
    FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_usuarios_inactivado_por
    FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE categorias (
  id BINARY(16) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_categorias_nombre (nombre),
  KEY idx_categorias_estado (estado),
  CONSTRAINT fk_categorias_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_categorias_actualizado_por
    FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_categorias_inactivado_por
    FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE marcas (
  id BINARY(16) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_marcas_nombre (nombre),
  KEY idx_marcas_estado (estado),
  CONSTRAINT fk_marcas_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_marcas_actualizado_por
    FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_marcas_inactivado_por
    FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

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
  estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_productos_sku (sku),
  KEY idx_productos_nombre (nombre),
  KEY idx_productos_estado (estado),
  KEY idx_productos_categoria (categoria_id),
  KEY idx_productos_marca (marca_id),
  CONSTRAINT fk_productos_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
  CONSTRAINT fk_productos_marca
    FOREIGN KEY (marca_id) REFERENCES marcas(id),
  CONSTRAINT fk_productos_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_productos_actualizado_por
    FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_productos_inactivado_por
    FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE proveedores (
  id BINARY(16) NOT NULL,
  razon_social VARCHAR(120) NOT NULL,
  ruc CHAR(11) NOT NULL,
  celular CHAR(9) NOT NULL,
  telefono VARCHAR(9) NOT NULL,
  email VARCHAR(160) NOT NULL,
  direccion VARCHAR(160) NOT NULL,
  estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_proveedores_ruc (ruc),
  UNIQUE KEY uk_proveedores_email (email),
  KEY idx_proveedores_estado (estado),
  CONSTRAINT fk_proveedores_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_proveedores_actualizado_por
    FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_proveedores_inactivado_por
    FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE inventario (
  id BINARY(16) NOT NULL,
  producto_id BINARY(16) NOT NULL,
  stock_actual INT NOT NULL DEFAULT 0,
  stock_minimo INT NOT NULL DEFAULT 0,
  ubicacion VARCHAR(40) NOT NULL,
  estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  creado_por_id BINARY(16) NULL,
  actualizado_en TIMESTAMP(6) NULL ON UPDATE CURRENT_TIMESTAMP(6),
  actualizado_por_id BINARY(16) NULL,
  inactivado_en TIMESTAMP(6) NULL,
  inactivado_por_id BINARY(16) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_inventario_producto (producto_id),
  KEY idx_inventario_estado (estado),
  KEY idx_inventario_stock_minimo (stock_actual, stock_minimo),
  CONSTRAINT fk_inventario_producto
    FOREIGN KEY (producto_id) REFERENCES productos(id),
  CONSTRAINT fk_inventario_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_inventario_actualizado_por
    FOREIGN KEY (actualizado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_inventario_inactivado_por
    FOREIGN KEY (inactivado_por_id) REFERENCES usuarios(id),
  CONSTRAINT chk_inventario_stock_actual
    CHECK (stock_actual >= 0),
  CONSTRAINT chk_inventario_stock_minimo
    CHECK (stock_minimo >= 0)
) ENGINE=InnoDB;

CREATE TABLE movimientos (
  id BINARY(16) NOT NULL,
  producto_id BINARY(16) NOT NULL,
  proveedor_id BINARY(16) NULL,
  tipo ENUM('ENTRADA', 'SALIDA', 'AJUSTE') NOT NULL,
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
  KEY idx_movimientos_producto_fecha (producto_id, creado_en),
  KEY idx_movimientos_proveedor (proveedor_id),
  KEY idx_movimientos_tipo (tipo),
  KEY idx_movimientos_creado_en (creado_en),
  CONSTRAINT fk_movimientos_producto
    FOREIGN KEY (producto_id) REFERENCES productos(id),
  CONSTRAINT fk_movimientos_proveedor
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
  CONSTRAINT fk_movimientos_creado_por
    FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  CONSTRAINT fk_movimientos_anulado_por
    FOREIGN KEY (anulado_por_id) REFERENCES usuarios(id),
  CONSTRAINT chk_movimientos_cantidad
    CHECK (cantidad > 0),
  CONSTRAINT chk_movimientos_stock_antes
    CHECK (stock_antes >= 0),
  CONSTRAINT chk_movimientos_stock_despues
    CHECK (stock_despues >= 0)
) ENGINE=InnoDB;
```

Notas pendientes:
- Convertir este borrador a una migracion Flyway cuando se apruebe el modelo final.
- Definir si `imagen_url` sera una URL externa, ruta interna o referencia a un servicio de archivos.
- Evaluar si `categorias` y `marcas` se administraran como catalogos independientes o desde productos.
- Agregar datos semilla en una migracion separada, no en este archivo.
