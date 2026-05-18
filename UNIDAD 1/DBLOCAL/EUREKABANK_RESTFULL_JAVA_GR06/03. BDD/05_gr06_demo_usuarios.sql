-- ============================================================
-- GR06 — Usuarios de DEMOSTRACIÓN (integrantes + Juan Pérez)
-- Idempotente: se puede re-ejecutar; reinicia saldos a 5000.
-- Clave de todos: demo123   (SHA1 = cbdbe4936ce8be63184d9f2e13fc249234371b9a)
-- 'monster' sigue siendo el único ADMIN (no se toca aquí).
-- ============================================================

-- 1. empleado (usuario.chr_emplcodigo exige FK a empleado)
INSERT INTO `empleado` (chr_emplcodigo, vch_emplpaterno, vch_emplmaterno, vch_emplnombre, vch_emplciudad) VALUES
 ('0015','MARIN','LOPEZ','JOSUE','QUITO'),
 ('0016','SALCEDO','RUIZ','MIKAELA','QUITO'),
 ('0017','PABON','TORRES','ELKIN','QUITO'),
 ('0018','PEREZ','GOMEZ','JUAN','QUITO')
ON DUPLICATE KEY UPDATE vch_emplnombre = VALUES(vch_emplnombre);

-- 2. cliente
INSERT INTO `cliente` (chr_cliecodigo, vch_cliepaterno, vch_cliematerno, vch_clienombre, chr_cliedni, vch_clieciudad, vch_cliedireccion, vch_clietelefono, vch_clieemail) VALUES
 ('00021','MARIN','LOPEZ','JOSUE','17000021','QUITO','ESPE','0990000021','jmarin@espe.edu.ec'),
 ('00022','SALCEDO','RUIZ','MIKAELA','17000022','QUITO','ESPE','0990000022','msalcedo@espe.edu.ec'),
 ('00023','PABON','TORRES','ELKIN','17000023','QUITO','ESPE','0990000023','epabon@espe.edu.ec'),
 ('00024','PEREZ','GOMEZ','JUAN','17000024','QUITO','ESPE','0990000024','jperez@espe.edu.ec')
ON DUPLICATE KEY UPDATE vch_clienombre = VALUES(vch_clienombre);

-- 3. usuario (mapeado a SU cliente -> rol restringido)
INSERT INTO `usuario` (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo) VALUES
 ('0015','jmarin',  'cbdbe4936ce8be63184d9f2e13fc249234371b9a','ACTIVO','00021'),
 ('0016','msalcedo','cbdbe4936ce8be63184d9f2e13fc249234371b9a','ACTIVO','00022'),
 ('0017','epabon',  'cbdbe4936ce8be63184d9f2e13fc249234371b9a','ACTIVO','00023'),
 ('0018','jperez',  'cbdbe4936ce8be63184d9f2e13fc249234371b9a','ACTIVO','00024')
ON DUPLICATE KEY UPDATE
  vch_emplclave = VALUES(vch_emplclave),
  vch_emplestado = 'ACTIVO',
  chr_cliecodigo = VALUES(chr_cliecodigo);

-- 4. cuenta (una por cliente, Dólares (02), saldo 5000 para demos)
INSERT INTO `cuenta` (chr_cuencodigo, chr_monecodigo, chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo, dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, int_cuencontmov, chr_cuenclave) VALUES
 ('00900021','02','001','0001','00021',5000.00,CURDATE(),'ACTIVO',0,'123456'),
 ('00900022','02','001','0001','00022',5000.00,CURDATE(),'ACTIVO',0,'123456'),
 ('00900023','02','001','0001','00023',5000.00,CURDATE(),'ACTIVO',0,'123456'),
 ('00900024','02','001','0001','00024',5000.00,CURDATE(),'ACTIVO',0,'123456')
ON DUPLICATE KEY UPDATE
  chr_monecodigo = '02',
  dec_cuensaldo = 5000.00,
  vch_cuenestado = 'ACTIVO';

-- 4b. Segunda cuenta por cliente en Soles (01) -> el usuario puede ESCOGER cuenta
INSERT INTO `cuenta` (chr_cuencodigo, chr_monecodigo, chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo, dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, int_cuencontmov, chr_cuenclave) VALUES
 ('00900031','01','001','0001','00021',3000.00,CURDATE(),'ACTIVO',0,'123456'),
 ('00900032','01','001','0001','00022',3000.00,CURDATE(),'ACTIVO',0,'123456'),
 ('00900033','01','001','0001','00023',3000.00,CURDATE(),'ACTIVO',0,'123456'),
 ('00900034','01','001','0001','00024',3000.00,CURDATE(),'ACTIVO',0,'123456')
ON DUPLICATE KEY UPDATE
  chr_monecodigo = '01',
  dec_cuensaldo = 3000.00,
  vch_cuenestado = 'ACTIVO';
