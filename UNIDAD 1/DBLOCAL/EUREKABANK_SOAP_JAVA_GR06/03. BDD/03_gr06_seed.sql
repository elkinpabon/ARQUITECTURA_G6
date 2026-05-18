-- EUREKABANK_SOAP_JAVA_GR06 - Seed de pruebas GR06
USE eurekabank;

-- Usuario de prueba GR06: usuario=monster  clave=monster9
-- (SHA1('monster9') = ce545786ea39ba4527f8fd50b7957ead139dc125)
-- Reutiliza el empleado 0012 (Mendoza Jara Monica) que no tenia usuario.
INSERT INTO usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado)
VALUES ('0012', 'monster', 'ce545786ea39ba4527f8fd50b7957ead139dc125', 'ACTIVO')
ON DUPLICATE KEY UPDATE
    vch_emplusuario = VALUES(vch_emplusuario),
    vch_emplclave   = VALUES(vch_emplclave),
    vch_emplestado  = VALUES(vch_emplestado);

-- Todos los DEMAS usuarios (no 'monster') tienen clave = admin2002
-- (SHA1('admin2002') = 0552126c8956bb24dfbc141e8507306142f0eb01)
UPDATE usuario
SET vch_emplclave = '0552126c8956bb24dfbc141e8507306142f0eb01'
WHERE vch_emplusuario <> 'monster';

-- Credenciales utiles para pruebas:
--   monster  / monster9     (ACTIVO)
--   internet / admin2002    (ACTIVO)
--   cualquier otro usuario ACTIVO (cromero, lcastro, ...) / admin2002
--   creyes, ediaz, csarmiento estan ANULADOS (login debe fallar)
