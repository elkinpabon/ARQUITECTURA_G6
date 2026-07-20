-- EUREKABANK_RESTFULL_JAVA_GR06 - Auditoria de operaciones ejecutadas desde Bonita.
USE eurekarestjava;

CREATE TABLE IF NOT EXISTS auditoria_bpm (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  operacion VARCHAR(20) NOT NULL,
  cuenta_origen VARCHAR(8) NOT NULL,
  cuenta_destino VARCHAR(8) NOT NULL DEFAULT 'N/A',
  monto DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  usuario_bpm VARCHAR(50) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_auditoria_bpm_fecha (fecha),
  KEY idx_auditoria_bpm_origen (cuenta_origen)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
