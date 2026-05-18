-- EUREKABANK_SOAP_DOTNET_GR06 - Estructura SQL Server
-- DB: .\SQLEXPRESS | User: sa | Password: admin123

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'EurekaBank')
BEGIN
    CREATE DATABASE EurekaBank;
END
GO

USE EurekaBank;
GO

-- Tabla: empleado
IF OBJECT_ID('dbo.empleado', 'U') IS NOT NULL DROP TABLE dbo.empleado;
CREATE TABLE dbo.empleado (
    chr_emplcodigo CHAR(4) NOT NULL PRIMARY KEY,
    vch_emplpaterno VARCHAR(25) NOT NULL,
    vch_emplmaterno VARCHAR(25) NOT NULL,
    vch_emplnombre VARCHAR(30) NOT NULL,
    vch_emplciudad VARCHAR(30) NOT NULL,
    vch_empldireccion VARCHAR(50) NULL
);
GO

-- Tabla: sucursal
IF OBJECT_ID('dbo.sucursal', 'U') IS NOT NULL DROP TABLE dbo.sucursal;
CREATE TABLE dbo.sucursal (
    chr_sucucodigo CHAR(3) NOT NULL PRIMARY KEY,
    vch_sucunombre VARCHAR(50) NOT NULL,
    vch_sucuciudad VARCHAR(30) NOT NULL,
    vch_sucudireccion VARCHAR(50) NULL,
    int_sucucontcuenta INT NOT NULL DEFAULT 0
);
GO

-- Tabla: moneda
IF OBJECT_ID('dbo.moneda', 'U') IS NOT NULL DROP TABLE dbo.moneda;
CREATE TABLE dbo.moneda (
    chr_monecodigo CHAR(2) NOT NULL PRIMARY KEY,
    vch_monedescripcion VARCHAR(20) NOT NULL
);
GO

-- Tabla: tipomovimiento
IF OBJECT_ID('dbo.tipomovimiento', 'U') IS NOT NULL DROP TABLE dbo.tipomovimiento;
CREATE TABLE dbo.tipomovimiento (
    chr_tipocodigo CHAR(3) NOT NULL PRIMARY KEY,
    vch_tipodescripcion VARCHAR(40) NOT NULL,
    vch_tipoaccion VARCHAR(10) NOT NULL CHECK (vch_tipoaccion IN ('INGRESO', 'SALIDA')),
    vch_tipoestado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_tipoestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
);
GO

-- Tabla: cliente
IF OBJECT_ID('dbo.cliente', 'U') IS NOT NULL DROP TABLE dbo.cliente;
CREATE TABLE dbo.cliente (
    chr_cliecodigo CHAR(5) NOT NULL PRIMARY KEY,
    vch_cliepaterno VARCHAR(25) NOT NULL,
    vch_cliematerno VARCHAR(25) NOT NULL,
    vch_clienombre VARCHAR(30) NOT NULL,
    chr_cliedni CHAR(8) NOT NULL,
    vch_clieciudad VARCHAR(30) NOT NULL,
    vch_cliedireccion VARCHAR(50) NOT NULL,
    vch_clietelefono VARCHAR(20) NULL,
    vch_clieemail VARCHAR(50) NULL
);
GO

-- Tabla: cuenta
IF OBJECT_ID('dbo.cuenta', 'U') IS NOT NULL DROP TABLE dbo.cuenta;
CREATE TABLE dbo.cuenta (
    chr_cuencodigo CHAR(8) NOT NULL PRIMARY KEY,
    chr_monecodigo CHAR(2) NOT NULL,
    chr_sucucodigo CHAR(3) NOT NULL,
    chr_emplcreacuenta CHAR(4) NOT NULL,
    chr_cliecodigo CHAR(5) NOT NULL,
    dec_cuensaldo DECIMAL(12,2) NOT NULL,
    dtt_cuenfechacreacion DATE NOT NULL,
    vch_cuenestado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_cuenestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    int_cuencontmov INT NOT NULL DEFAULT 0,
    chr_cuenclave CHAR(6) NOT NULL,
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (chr_cliecodigo) REFERENCES dbo.cliente(chr_cliecodigo),
    CONSTRAINT fk_cuenta_moneda FOREIGN KEY (chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo),
    CONSTRAINT fk_cuenta_sucursal FOREIGN KEY (chr_sucucodigo) REFERENCES dbo.sucursal(chr_sucucodigo),
    CONSTRAINT fk_cuenta_empleado FOREIGN KEY (chr_emplcreacuenta) REFERENCES dbo.empleado(chr_emplcodigo)
);
GO

CREATE INDEX idx_cuenta_cliente ON dbo.cuenta(chr_cliecodigo);
CREATE INDEX idx_cuenta_empleado ON dbo.cuenta(chr_emplcreacuenta);
CREATE INDEX idx_cuenta_sucursal ON dbo.cuenta(chr_sucucodigo);
CREATE INDEX idx_cuenta_moneda ON dbo.cuenta(chr_monecodigo);
GO

-- Tabla: movimiento (con columnas GR06 para conversion de moneda)
IF OBJECT_ID('dbo.movimiento', 'U') IS NOT NULL DROP TABLE dbo.movimiento;
CREATE TABLE dbo.movimiento (
    chr_cuencodigo CHAR(8) NOT NULL,
    int_movinumero INT NOT NULL,
    dtt_movifecha DATE NOT NULL,
    chr_emplcodigo CHAR(4) NOT NULL,
    chr_tipocodigo CHAR(3) NOT NULL,
    dec_moviimporte DECIMAL(12,2) NOT NULL CHECK (dec_moviimporte >= 0.0),
    chr_cuenreferencia CHAR(8) NULL,
    chr_movimonori CHAR(2) NULL,
    dec_moviimporteori DECIMAL(12,2) NULL,
    dec_movitasa DECIMAL(18,6) NULL,
    PRIMARY KEY (chr_cuencodigo, int_movinumero),
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (chr_cuencodigo) REFERENCES dbo.cuenta(chr_cuencodigo),
    CONSTRAINT fk_movimiento_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo),
    CONSTRAINT fk_movimiento_tipomovimiento FOREIGN KEY (chr_tipocodigo) REFERENCES dbo.tipomovimiento(chr_tipocodigo)
);
GO

CREATE INDEX idx_movimiento_tipo ON dbo.movimiento(chr_tipocodigo);
CREATE INDEX idx_movimiento_empleado ON dbo.movimiento(chr_emplcodigo);
GO

-- Tabla: usuario (con columna GR06 chr_cliecodigo)
IF OBJECT_ID('dbo.usuario', 'U') IS NOT NULL DROP TABLE dbo.usuario;
CREATE TABLE dbo.usuario (
    chr_emplcodigo CHAR(4) NOT NULL PRIMARY KEY,
    vch_emplusuario VARCHAR(20) NOT NULL UNIQUE,
    vch_emplclave VARCHAR(50) NOT NULL,
    vch_emplestado VARCHAR(15) DEFAULT 'ACTIVO' CHECK (vch_emplestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    chr_cliecodigo CHAR(5) NULL,
    CONSTRAINT fk_usuario_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo)
);
GO

-- Tabla: asignado
IF OBJECT_ID('dbo.asignado', 'U') IS NOT NULL DROP TABLE dbo.asignado;
CREATE TABLE dbo.asignado (
    chr_asigcodigo CHAR(6) NOT NULL PRIMARY KEY,
    chr_sucucodigo CHAR(3) NOT NULL,
    chr_emplcodigo CHAR(4) NOT NULL,
    dtt_asigfechaalta DATE NOT NULL,
    dtt_asigfechabaja DATE NULL,
    CONSTRAINT fk_asignado_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo),
    CONSTRAINT fk_asignado_sucursal FOREIGN KEY (chr_sucucodigo) REFERENCES dbo.sucursal(chr_sucucodigo)
);
GO

CREATE INDEX idx_asignado_empleado ON dbo.asignado(chr_emplcodigo);
CREATE INDEX idx_asignado_sucursal ON dbo.asignado(chr_sucucodigo);
GO

-- Tabla: parametro
IF OBJECT_ID('dbo.parametro', 'U') IS NOT NULL DROP TABLE dbo.parametro;
CREATE TABLE dbo.parametro (
    chr_paracodigo CHAR(3) NOT NULL PRIMARY KEY,
    vch_paradescripcion VARCHAR(50) NOT NULL,
    vch_paravalor VARCHAR(70) NOT NULL,
    vch_paraestado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_paraestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
);
GO

-- Tabla: modulo
IF OBJECT_ID('dbo.modulo', 'U') IS NOT NULL DROP TABLE dbo.modulo;
CREATE TABLE dbo.modulo (
    int_moducodigo INT NOT NULL PRIMARY KEY,
    vch_modunombre VARCHAR(50) NULL,
    vch_moduestado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_moduestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
);
GO

-- Tabla: permiso
IF OBJECT_ID('dbo.permiso', 'U') IS NOT NULL DROP TABLE dbo.permiso;
CREATE TABLE dbo.permiso (
    chr_emplcodigo CHAR(4) NOT NULL,
    int_moducodigo INT NOT NULL,
    vch_permestado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO' CHECK (vch_permestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
    PRIMARY KEY (chr_emplcodigo, int_moducodigo),
    CONSTRAINT fk_permiso_modulo FOREIGN KEY (int_moducodigo) REFERENCES dbo.modulo(int_moducodigo),
    CONSTRAINT fk_permiso_usuario FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.usuario(chr_emplcodigo)
);
GO

-- Tabla: contador
IF OBJECT_ID('dbo.contador', 'U') IS NOT NULL DROP TABLE dbo.contador;
CREATE TABLE dbo.contador (
    vch_conttabla VARCHAR(30) NOT NULL PRIMARY KEY,
    int_contitem INT NOT NULL,
    int_contlongitud INT NOT NULL
);
GO

-- Tabla: logsession
IF OBJECT_ID('dbo.logsession', 'U') IS NOT NULL DROP TABLE dbo.logsession;
CREATE TABLE dbo.logsession (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    chr_emplcodigo CHAR(4) NOT NULL,
    fec_ingreso DATETIME NOT NULL,
    fec_salida DATETIME NULL,
    ip VARCHAR(20) NOT NULL DEFAULT 'NONE',
    hostname VARCHAR(100) NOT NULL DEFAULT 'NONE',
    CONSTRAINT fk_logsession_empleado FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.empleado(chr_emplcodigo)
);
GO

-- Tabla: cargomantenimiento
IF OBJECT_ID('dbo.cargomantenimiento', 'U') IS NOT NULL DROP TABLE dbo.cargomantenimiento;
CREATE TABLE dbo.cargomantenimiento (
    chr_monecodigo CHAR(2) NOT NULL PRIMARY KEY,
    dec_cargMontoMaximo DECIMAL(12,2) NOT NULL,
    dec_cargImporte DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_cargomantenimiento_moneda FOREIGN KEY (chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo)
);
GO

-- Tabla: costomovimiento
IF OBJECT_ID('dbo.costomovimiento', 'U') IS NOT NULL DROP TABLE dbo.costomovimiento;
CREATE TABLE dbo.costomovimiento (
    chr_monecodigo CHAR(2) NOT NULL PRIMARY KEY,
    dec_costimporte DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_costomovimiento_moneda FOREIGN KEY (chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo)
);
GO

-- Tabla: interesmensual
IF OBJECT_ID('dbo.interesmensual', 'U') IS NOT NULL DROP TABLE dbo.interesmensual;
CREATE TABLE dbo.interesmensual (
    chr_monecodigo CHAR(2) NOT NULL PRIMARY KEY,
    dec_inteimporte DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_interesmensual_moneda FOREIGN KEY (chr_monecodigo) REFERENCES dbo.moneda(chr_monecodigo)
);
GO

-- Tabla: tasacambio (GR06)
IF OBJECT_ID('dbo.tasacambio', 'U') IS NOT NULL DROP TABLE dbo.tasacambio;
CREATE TABLE dbo.tasacambio (
    chr_origen CHAR(2) NOT NULL,
    chr_destino CHAR(2) NOT NULL,
    dec_tasa DECIMAL(18,6) NOT NULL,
    PRIMARY KEY (chr_origen, chr_destino),
    CONSTRAINT fk_tasacambio_origen FOREIGN KEY (chr_origen) REFERENCES dbo.moneda(chr_monecodigo),
    CONSTRAINT fk_tasacambio_destino FOREIGN KEY (chr_destino) REFERENCES dbo.moneda(chr_monecodigo)
);
GO

PRINT 'Estructura de base de datos creada exitosamente.';
GO
