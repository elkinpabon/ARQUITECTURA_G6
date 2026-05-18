-- EUREKABANK_SOAP_DOTNET_GR06 - Seed GR06 + Demo Users SQL Server
USE EurekaBank;
GO

-- Usuario de prueba GR06: usuario=monster clave=monster9
-- SHA1('monster9') = ce545786ea39ba4527f8fd50b7957ead139dc125
-- Reutiliza el empleado 0012 (Mendoza Jara Monica) - admin sin cliente
IF EXISTS (SELECT 1 FROM dbo.usuario WHERE chr_emplcodigo = '0012')
    UPDATE dbo.usuario SET vch_emplusuario = 'monster', vch_emplclave = 'ce545786ea39ba4527f8fd50b7957ead139dc125', vch_emplestado = 'ACTIVO', chr_cliecodigo = NULL WHERE chr_emplcodigo = '0012';
ELSE
    INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
    VALUES ('0012', 'monster', 'ce545786ea39ba4527f8fd50b7957ead139dc125', 'ACTIVO', NULL);
GO

-- Todos los demas usuarios (no 'monster') tienen clave = admin2002
-- SHA1('admin2002') = 0552126c8956bb24dfbc141e8507306142f0eb01
UPDATE dbo.usuario SET vch_emplclave = '0552126c8956bb24dfbc141e8507306142f0eb01' WHERE vch_emplusuario <> 'monster';
GO

-- Demo usuarios GR06 (clientes nuevos con sus cuentas)
-- jmarin / demo123 -> cliente 00021
IF NOT EXISTS (SELECT 1 FROM dbo.cliente WHERE chr_cliecodigo = '00021')
BEGIN
    INSERT INTO dbo.cliente VALUES ('00021','MARIN','SALCEDO','JORGE LUIS','10987654','LIMA','SAN MIGUEL','987-12345','jmarin@email.com');
    INSERT INTO dbo.empleado VALUES ('8001','Marin','Salcedo','Jorge Luis','Lima','San Miguel 123');
    INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
    VALUES ('8001', 'jmarin', '0552126c8956bb24dfbc141e8507306142f0eb01', 'ACTIVO', '00021');
END
GO

-- msalcedo / demo123 -> cliente 00022
IF NOT EXISTS (SELECT 1 FROM dbo.cliente WHERE chr_cliecodigo = '00022')
BEGIN
    INSERT INTO dbo.cliente VALUES ('00022','SALCEDO','RUIZ','MARIA ELENA','10876543','LIMA','SURCO','987-23456','msalcedo@email.com');
    INSERT INTO dbo.empleado VALUES ('8002','Salcedo','Ruiz','Maria Elena','Lima','Surco 456');
    INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
    VALUES ('8002', 'msalcedo', '0552126c8956bb24dfbc141e8507306142f0eb01', 'ACTIVO', '00022');
END
GO

-- epabon / demo123 -> cliente 00023
IF NOT EXISTS (SELECT 1 FROM dbo.cliente WHERE chr_cliecodigo = '00023')
BEGIN
    INSERT INTO dbo.cliente VALUES ('00023','PABON','TORRES','EDUARDO ALBERTO','10765432','LIMA','LINCE','987-34567','epabon@email.com');
    INSERT INTO dbo.empleado VALUES ('8003','Pabon','Torres','Eduardo Alberto','Lima','Lince 789');
    INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
    VALUES ('8003', 'epabon', '0552126c8956bb24dfbc141e8507306142f0eb01', 'ACTIVO', '00023');
END
GO

-- jperez / demo123 -> cliente 00024
IF NOT EXISTS (SELECT 1 FROM dbo.cliente WHERE chr_cliecodigo = '00024')
BEGIN
    INSERT INTO dbo.cliente VALUES ('00024','PEREZ','LOPEZ','JUAN CARLOS','10654321','LIMA','MIRAFLORES','987-45678','jperez@email.com');
    INSERT INTO dbo.empleado VALUES ('8004','Perez','Lopez','Juan Carlos','Lima','Miraflores 012');
    INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
    VALUES ('8004', 'jperez', '0552126c8956bb24dfbc141e8507306142f0eb01', 'ACTIVO', '00024');
END
GO

-- Cuenta master BANCO EUREKA (00900000) - no se puede eliminar
IF NOT EXISTS (SELECT 1 FROM dbo.cuenta WHERE chr_cuencodigo = '00900000')
BEGIN
    INSERT INTO dbo.cuenta VALUES ('00900000','01','001','9999','00001',1000000.00,'2024-01-01','ACTIVO',0,'000000');
END
GO

-- Tasas de cambio (GR06)
IF NOT EXISTS (SELECT 1 FROM dbo.tasacambio WHERE chr_origen = '01' AND chr_destino = '02')
    INSERT INTO dbo.tasacambio VALUES ('01','02',0.2667);
IF NOT EXISTS (SELECT 1 FROM dbo.tasacambio WHERE chr_origen = '02' AND chr_destino = '01')
    INSERT INTO dbo.tasacambio VALUES ('02','01',3.75);
IF NOT EXISTS (SELECT 1 FROM dbo.tasacambio WHERE chr_origen = '01' AND chr_destino = '01')
    INSERT INTO dbo.tasacambio VALUES ('01','01',1.0);
IF NOT EXISTS (SELECT 1 FROM dbo.tasacambio WHERE chr_origen = '02' AND chr_destino = '02')
    INSERT INTO dbo.tasacambio VALUES ('02','02',1.0);
GO

PRINT 'Seed GR06 + Demo Users insertados exitosamente.';
GO
