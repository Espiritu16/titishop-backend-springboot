INSERT INTO usuarios (
  id,
  nombre_completo,
  email,
  password_hash,
  rol,
  estado,
  creado_en
) VALUES (
  X'00000000000000000000000000000001',
  'Administrador TitiShop',
  'admin@titishop.pe',
  '$2a$10$cbSRDkblT8f743O9bTU3p.M7lur5HUkzV2n9DU1/wlkosKeSsqnGy',
  'ADMINISTRADOR',
  'ACTIVO',
  CURRENT_TIMESTAMP(6)
);
