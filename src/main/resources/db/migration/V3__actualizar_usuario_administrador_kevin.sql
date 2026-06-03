UPDATE usuarios
SET
  nombre_completo = 'Kevin',
  email = 'kevin@gmail.com',
  password_hash = '$2a$10$KKa5HvGcKi7U2IDTMXPFoOh8d84LSlNHmYrPJ91SfDdZF7MVH..AG',
  rol = 'ADMINISTRADOR',
  estado = 'ACTIVO'
WHERE id = X'00000000000000000000000000000001';
