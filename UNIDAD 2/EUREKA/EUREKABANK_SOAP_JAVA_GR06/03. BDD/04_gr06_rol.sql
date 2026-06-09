-- ============================================================
-- GR06 — Rol de acceso: mapeo usuario -> cliente
-- 'monster' es ADMIN (no se mapea): puede buscar cualquier cliente.
-- Un usuario mapeado solo ve/opera las cuentas de SU cliente.
-- ============================================================

-- 1. Columna de relación usuario -> cliente (NULL = no es cliente)
SET @col := (SELECT COUNT(*) FROM information_schema.COLUMNS
             WHERE TABLE_SCHEMA = DATABASE()
               AND TABLE_NAME = 'usuario'
               AND COLUMN_NAME = 'chr_cliecodigo');
SET @sql := IF(@col = 0,
  'ALTER TABLE `usuario` ADD COLUMN `chr_cliecodigo` char(5) NULL AFTER `vch_emplestado`',
  'SELECT 1');
PREPARE st FROM @sql; EXECUTE st; DEALLOCATE PREPARE st;

-- 2. Clave foránea hacia cliente (se ignora si ya existe)
SET @fk := (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
            WHERE CONSTRAINT_SCHEMA = DATABASE()
              AND TABLE_NAME = 'usuario'
              AND CONSTRAINT_NAME = 'fk_usuario_cliente');
SET @sql := IF(@fk = 0,
  'ALTER TABLE `usuario` ADD CONSTRAINT `fk_usuario_cliente`
     FOREIGN KEY (`chr_cliecodigo`) REFERENCES `cliente` (`chr_cliecodigo`)
     ON DELETE RESTRICT ON UPDATE RESTRICT',
  'SELECT 1');
PREPARE st FROM @sql; EXECUTE st; DEALLOCATE PREPARE st;

-- 3. Mapeo de demostración
UPDATE `usuario` SET `chr_cliecodigo` = NULL    WHERE `vch_emplusuario` = 'monster';   -- ADMIN
UPDATE `usuario` SET `chr_cliecodigo` = '00001' WHERE `vch_emplusuario` = 'cromero';   -- ERIC GUSTAVO CORONEL
UPDATE `usuario` SET `chr_cliecodigo` = '00005' WHERE `vch_emplusuario` = 'cvalencia'; -- ALAN ALBERTO ARANDA
