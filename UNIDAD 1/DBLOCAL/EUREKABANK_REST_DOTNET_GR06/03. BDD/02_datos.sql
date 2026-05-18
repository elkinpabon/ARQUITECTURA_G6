-- EUREKABANK_SOAP_DOTNET_GR06 - Datos SQL Server
USE EurekaBank;
GO

-- Empleado
INSERT INTO dbo.empleado VALUES ('0001','Romero','Castillo','Carlos Alberto','Trujillo','Call1 1 Nro. 456');
INSERT INTO dbo.empleado VALUES ('0002','Castro','Vargas','Lidia','Lima','Federico Villarreal 456 - SMP');
INSERT INTO dbo.empleado VALUES ('0003','Reyes','Ortiz','Claudia','Lima','Av. Aviacion 3456 - San Borja');
INSERT INTO dbo.empleado VALUES ('0004','Ramos','Garibay','Angelica','Chiclayo','Calle Barcelona 345');
INSERT INTO dbo.empleado VALUES ('0005','Ruiz','Zabaleta','Claudia','Cusco','Calle Cruz Verde 364');
INSERT INTO dbo.empleado VALUES ('0006','Cruz','Tarazona','Ricardo','Areguipa','Calle La Gruta 304');
INSERT INTO dbo.empleado VALUES ('0007','Diaz','Flores','Edith','Lima','Av. Pardo 546');
INSERT INTO dbo.empleado VALUES ('0008','Sarmiento','Bellido','Claudia Rocio','Areguipa','Calle Alfonso Ugarte 1567');
INSERT INTO dbo.empleado VALUES ('0009','Pachas','Sifuentes','Luis Alberto','Trujillo','Francisco Pizarro 1263');
INSERT INTO dbo.empleado VALUES ('0010','Tello','Alarcon','Hugo Valentin','Cusco','Los Angeles 865');
INSERT INTO dbo.empleado VALUES ('0011','Carrasco','Vargas','Pedro Hugo','Chiclayo','Av. Balta 1265');
INSERT INTO dbo.empleado VALUES ('0012','Mendoza','Jara','Monica Valeria','Lima','Calle Las Toronjas 450');
INSERT INTO dbo.empleado VALUES ('0013','Espinoza','Melgar','Victor Eduardo','Huancayo','Av. San Martin 6734 Dpto. 508');
INSERT INTO dbo.empleado VALUES ('0014','Hidalgo','Sandoval','Milagros Leonor','Chiclayo','Av. Luis Gonzales 1230');
INSERT INTO dbo.empleado VALUES ('9999','Internet','Internet','internet','Internet','internet');
GO

-- Sucursal
INSERT INTO dbo.sucursal VALUES ('001','Sipan','Chiclayo','Av. Balta 1456',2);
INSERT INTO dbo.sucursal VALUES ('002','Chan Chan','Trujillo','Jr. Independencia 456',3);
INSERT INTO dbo.sucursal VALUES ('003','Los Olivos','Lima','Av. Central 1234',0);
INSERT INTO dbo.sucursal VALUES ('004','Pardo','Lima','Av. Pardo 345 - Miraflores',0);
INSERT INTO dbo.sucursal VALUES ('005','Misti','Arequipa','Bolivar 546',0);
INSERT INTO dbo.sucursal VALUES ('006','Machupicchu','Cusco','Calle El Sol 534',0);
INSERT INTO dbo.sucursal VALUES ('007','Grau','Piura','Av. Grau 1528',0);
GO

-- Moneda
INSERT INTO dbo.moneda VALUES ('01','Soles');
INSERT INTO dbo.moneda VALUES ('02','Dolares');
GO

-- TipoMovimiento
INSERT INTO dbo.tipomovimiento VALUES ('001','Apertura de Cuenta','INGRESO','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('002','Cancelar Cuenta','SALIDA','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('003','Deposito','INGRESO','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('004','Retiro','SALIDA','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('005','Interes','INGRESO','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('006','Mantenimiento','SALIDA','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('007','ITF','SALIDA','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('008','Transferencia','INGRESO','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('009','Transferencia','SALIDA','ACTIVO');
INSERT INTO dbo.tipomovimiento VALUES ('010','Cargo por Movimiento','SALIDA','ACTIVO');
GO

-- Cliente
INSERT INTO dbo.cliente VALUES ('00001','CORONEL','CASTILLO','ERIC GUSTAVO','06914897','LIMA','LOS OLIVOS','996-664-457','gcoronelc@gmail.com');
INSERT INTO dbo.cliente VALUES ('00002','VALENCIA','MORALES','PEDRO HUGO','01576173','LIMA','MAGDALENA','924-7834','pvalencia@terra.com.pe');
INSERT INTO dbo.cliente VALUES ('00003','MARCELO','VILLALOBOS','RICARDO','10762367','LIMA','LINCE','993-62966','ricardomarcelo@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00004','ROMERO','CASTILLO','CARLOS ALBERTO','06531983','LIMA','LOS OLIVOS','865-84762','c.romero@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00005','ARANDA','LUNA','ALAN ALBERTO','10875611','LIMA','SAN ISIDRO','834-67125','a.aranda@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00006','AYALA','PAZ','JORGE LUIS','10679245','LIMA','SAN BORJA','963-34769','j.ayala@yahoo.com');
INSERT INTO dbo.cliente VALUES ('00007','CHAVEZ','CANALES','EDGAR RAFAEL','10145693','LIMA','MIRAFLORES','999-96673','e.chavez@gmail.com');
INSERT INTO dbo.cliente VALUES ('00008','FLORES','CHAFLOQUE','ROSA LIZET','10773456','LIMA','LA MOLINA','966-87567','r.florez@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00009','FLORES','CASTILLO','CRISTIAN RAFAEL','10346723','LIMA','LOS OLIVOS','978-43768','c.flores@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00010','GONZALES','GARCIA','GABRIEL ALEJANDRO','10192376','LIMA','SAN MIGUEL','945-56782','g.gonzales@yahoo.es');
INSERT INTO dbo.cliente VALUES ('00011','LAY','VALLEJOS','JUAN CARLOS','10942287','LIMA','LINCE','956-12657','j.lay@peru.com');
INSERT INTO dbo.cliente VALUES ('00012','MONTALVO','SOTO','DEYSI LIDIA','10612376','LIMA','SURCO','965-67235','d.montalvo@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00013','RICALDE','RAMIREZ','ROSARIO ESMERALDA','10761324','LIMA','MIRAFLORES','991-23546','r.ricalde@gmail.com');
INSERT INTO dbo.cliente VALUES ('00014','RODRIGUEZ','FLORES','ENRIQUE MANUEL','10773345','LIMA','LINCE','976-82838','e.rodriguez@gmail.com');
INSERT INTO dbo.cliente VALUES ('00015','ROJAS','OSCANOA','FELIX NINO','10238943','LIMA','LIMA','962-32158','f.rojas@yahoo.com');
INSERT INTO dbo.cliente VALUES ('00016','TEJADA','DEL AGUILA','TANIA LORENA','10446791','LIMA','PUEBLO LIBRE','966-23854','t.tejada@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00017','VALDEVIESO','LEYVA','LIDIA ROXANA','10452682','LIMA','SURCO','956-78951','r.valdivieso@terra.com.pe');
INSERT INTO dbo.cliente VALUES ('00018','VALENTIN','COTRINA','JUAN DIEGO','10398247','LIMA','LA MOLINA','921-12456','j.valentin@terra.com.pe');
INSERT INTO dbo.cliente VALUES ('00019','VARGAS','MENDOZA','MARIA ELENA','10873456','LIMA','SAN BORJA','987-65432','m.vargas@hotmail.com');
INSERT INTO dbo.cliente VALUES ('00020','SALAZAR','ROJAS','JULIO CESAR','10456789','LIMA','MIRAFLORES','912-34567','j.salazar@gmail.com');
GO

-- Cuenta
INSERT INTO dbo.cuenta VALUES ('00100001','01','001','0004','00005',7404.00,'2008-01-06','ACTIVO',20,'123456');
INSERT INTO dbo.cuenta VALUES ('00100002','02','001','0004','00005',5002.97,'2008-01-08','ACTIVO',10,'123456');
INSERT INTO dbo.cuenta VALUES ('00200001','01','002','0001','00008',7000.00,'2008-01-05','ACTIVO',15,'123456');
INSERT INTO dbo.cuenta VALUES ('00200002','01','002','0001','00001',6800.00,'2008-01-09','ACTIVO',3,'123456');
INSERT INTO dbo.cuenta VALUES ('00200003','02','002','0001','00007',6000.00,'2008-01-11','ACTIVO',6,'123456');
INSERT INTO dbo.cuenta VALUES ('00300001','01','003','0002','00010',0.00,'2008-01-07','CANCELADO',3,'123456');
GO

-- Usuario (con hashes SHA1 - monster/monster9, resto admin2002)
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0001','cromero','1c0108a5c6cda33ab8dca984c29c7fe86b5ccbf9','ACTIVO','00001');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0002','lcastro','d96cc35458a2bf33dd6f0d3500b11724164a60a9','ACTIVO','00002');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0003','creyes','32b3491336522e073489725b5daf298cd749007a','ANULADO','00003');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0004','aramos','3ab9380fa2521f2d0d94fe931b79a1e1eaa91890','ACTIVO','00004');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0005','cvalencia','c8a50f632c3c4baf27fc05facb1883104e1d16ef','ACTIVO','00005');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0006','rcruz','59bf039c49eeb64a476a8ae2b6c1eea0932e4cc6','ACTIVO','00006');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0007','ediaz','3718e00ac45cec21633e2211af9b77cd0a193698','ANULADO','00007');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0008','csarmiento','8a9f9f887cc6b44a5e298683afd1ebe7b740278d','ANULADO','00008');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0009','lpachas','8f39c63d50478f69b087a9696546e72e50cd1967','ACTIVO','00009');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0010','htello','6c8143102247cfe67e7c34d70bfb01c596c62bd1','ACTIVO','00010');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('0011','pcarrasco','d7df40c5af57e533bc25c340d1b3f3ba793efed4','ACTIVO','00011');
INSERT INTO dbo.usuario (chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo)
VALUES ('9999','internet','4d0fb475b242228032cbdf6d53924d2538df037b','ACTIVO',NULL);
GO

-- Asignado
INSERT INTO dbo.asignado VALUES ('000001','001','0004','2007-11-15',NULL);
INSERT INTO dbo.asignado VALUES ('000002','002','0001','2007-11-20',NULL);
INSERT INTO dbo.asignado VALUES ('000003','003','0002','2007-11-28',NULL);
INSERT INTO dbo.asignado VALUES ('000004','004','0003','2007-12-12','2008-03-25');
INSERT INTO dbo.asignado VALUES ('000005','005','0006','2007-12-20',NULL);
INSERT INTO dbo.asignado VALUES ('000006','006','0005','2008-01-05',NULL);
INSERT INTO dbo.asignado VALUES ('000007','004','0007','2008-01-07',NULL);
INSERT INTO dbo.asignado VALUES ('000008','005','0008','2008-01-07',NULL);
INSERT INTO dbo.asignado VALUES ('000009','001','0011','2008-01-08',NULL);
INSERT INTO dbo.asignado VALUES ('000010','002','0009','2008-01-08',NULL);
INSERT INTO dbo.asignado VALUES ('000011','006','0010','2008-01-08',NULL);
GO

-- Contador
INSERT INTO dbo.contador VALUES ('Asignado',11,6);
INSERT INTO dbo.contador VALUES ('Cliente',20,5);
INSERT INTO dbo.contador VALUES ('Empleado',14,4);
INSERT INTO dbo.contador VALUES ('Moneda',2,2);
INSERT INTO dbo.contador VALUES ('Parametro',2,3);
INSERT INTO dbo.contador VALUES ('Sucursal',7,3);
INSERT INTO dbo.contador VALUES ('TipoMovimiento',10,3);
GO

-- Modulo
INSERT INTO dbo.modulo VALUES (1,'Procesos','ACTIVO');
INSERT INTO dbo.modulo VALUES (2,'Tablas','ACTIVO');
INSERT INTO dbo.modulo VALUES (3,'Consultas','ACTIVO');
INSERT INTO dbo.modulo VALUES (4,'Reportes','ACTIVO');
INSERT INTO dbo.modulo VALUES (5,'Util','ACTIVO');
INSERT INTO dbo.modulo VALUES (6,'Seguridad','ACTIVO');
GO

-- Parametro
INSERT INTO dbo.parametro VALUES ('001','ITF - Impuesto a la Transacciones Financieras','0.08','ACTIVO');
INSERT INTO dbo.parametro VALUES ('002','Numero de Operaciones Sin Costo','15','ACTIVO');
GO

-- Cargomantenimiento
INSERT INTO dbo.cargomantenimiento VALUES ('01',3500.00,7.00);
INSERT INTO dbo.cargomantenimiento VALUES ('02',1200.00,2.50);
GO

-- Costomovimiento
INSERT INTO dbo.costomovimiento VALUES ('01',2.00);
INSERT INTO dbo.costomovimiento VALUES ('02',0.60);
GO

-- Interesmensual
INSERT INTO dbo.interesmensual VALUES ('01',0.70);
INSERT INTO dbo.interesmensual VALUES ('02',0.60);
GO

PRINT 'Datos basicos insertados exitosamente.';
GO
