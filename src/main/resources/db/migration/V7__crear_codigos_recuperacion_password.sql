CREATE TABLE password_reset_codes (
  id BINARY(16) NOT NULL,
  usuario_id BINARY(16) NOT NULL,
  codigo_hash VARCHAR(255) NOT NULL,
  reset_token_hash VARCHAR(255) NULL,
  expira_en TIMESTAMP(6) NOT NULL,
  validado_en TIMESTAMP(6) NULL,
  usado_en TIMESTAMP(6) NULL,
  intentos INT NOT NULL DEFAULT 0,
  creado_en TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_password_reset_codes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_password_reset_codes_usuario_estado ON password_reset_codes(usuario_id, usado_en, creado_en);
CREATE INDEX idx_password_reset_codes_expira_en ON password_reset_codes(expira_en);
