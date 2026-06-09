-- ============================================================
-- GR06 — Cuenta MASTER del banco (para transferencias)
-- Idempotente. Cuenta puente con saldo alto; pertenece al
-- cliente especial 00900 "BANCO EUREKA MASTER".
-- Moneda 02 = Dólares (moneda preferente del banco).
-- ============================================================

INSERT INTO `cliente` (chr_cliecodigo, vch_cliepaterno, vch_cliematerno, vch_clienombre, chr_cliedni, vch_clieciudad, vch_cliedireccion, vch_clietelefono, vch_clieemail) VALUES
 ('00900','EUREKA','MASTER','BANCO','00009000','QUITO','MATRIZ','022000000','master@eurekabank.ec')
ON DUPLICATE KEY UPDATE vch_clienombre = VALUES(vch_clienombre);

INSERT INTO `cuenta` (chr_cuencodigo, chr_monecodigo, chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo, dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, int_cuencontmov, chr_cuenclave) VALUES
 ('00900000','02','001','0001','00900',1000000.00,CURDATE(),'ACTIVO',0,'123456')
ON DUPLICATE KEY UPDATE
  dec_cuensaldo = 1000000.00,
  vch_cuenestado = 'ACTIVO';
