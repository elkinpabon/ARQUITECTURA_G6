-- ============================================================
-- GR06 — Conversión de moneda
-- Tabla de tasas editable + columnas de detalle en `movimiento`.
-- Tasa de referencia: 1 USD (02) = 3.75 PEN (01).
-- ============================================================

-- 1. Tabla de tasas (editable por SQL)
CREATE TABLE IF NOT EXISTS `tasacambio` (
  `chr_origen`  char(2)        NOT NULL,
  `chr_destino` char(2)        NOT NULL,
  `dec_tasa`    decimal(18,6)  NOT NULL,
  PRIMARY KEY (`chr_origen`, `chr_destino`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `tasacambio` (chr_origen, chr_destino, dec_tasa) VALUES
 ('01','01',1.000000),
 ('02','02',1.000000),
 ('02','01',3.750000),   -- 1 Dólar  -> 3.75 Soles
 ('01','02',0.266667)    -- 1 Sol    -> 0.266667 Dólares
ON DUPLICATE KEY UPDATE dec_tasa = VALUES(dec_tasa);

-- 2. Columnas de detalle de conversión en `movimiento` (NULL = sin conversión)
SET @c1 := (SELECT COUNT(*) FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='movimiento'
              AND COLUMN_NAME='chr_movimonori');
SET @s := IF(@c1=0,'ALTER TABLE `movimiento` ADD COLUMN `chr_movimonori` char(2) NULL','SELECT 1');
PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;

SET @c2 := (SELECT COUNT(*) FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='movimiento'
              AND COLUMN_NAME='dec_moviimporteori');
SET @s := IF(@c2=0,'ALTER TABLE `movimiento` ADD COLUMN `dec_moviimporteori` decimal(12,2) NULL','SELECT 1');
PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;

SET @c3 := (SELECT COUNT(*) FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='movimiento'
              AND COLUMN_NAME='dec_movitasa');
SET @s := IF(@c3=0,'ALTER TABLE `movimiento` ADD COLUMN `dec_movitasa` decimal(18,6) NULL','SELECT 1');
PREPARE st FROM @s; EXECUTE st; DEALLOCATE PREPARE st;
