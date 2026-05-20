-- =============================================================================
--  Base de datos: ticketpremiumDB
--  Examen Practica Primera Unidad - TicketPremium - GR06
--  Motor: MySQL 9.x (local)  Usuario: root  Password: admin2002
--  Equipo:  Josue Marin / Mikaela Salcedo / Elkin Pabon
-- =============================================================================

DROP DATABASE IF EXISTS ticketpremiumDB;
CREATE DATABASE ticketpremiumDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE ticketpremiumDB;

-- -----------------------------------------------------------------------------
--  Tabla USUARIO  (autenticacion + rol)
--    Rol ADMIN  ->  master "monster" puede ver reportes y todas las facturas
--    Rol CLIENTE -> usuarios finales que compran boletos
-- -----------------------------------------------------------------------------
CREATE TABLE USUARIO (
    ID_USUARIO  INT          NOT NULL AUTO_INCREMENT,
    USUARIO     VARCHAR(50)  NOT NULL,
    CONTRASENA  VARCHAR(100) NOT NULL,
    NOMBRE      VARCHAR(120) NOT NULL,
    ROL         VARCHAR(20)  NOT NULL,   -- ADMIN | CLIENTE
    PRIMARY KEY (ID_USUARIO),
    UNIQUE KEY uk_usuario (USUARIO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  Tabla PARTIDO_FUTBOL
-- -----------------------------------------------------------------------------
CREATE TABLE PARTIDO_FUTBOL (
    CODIGO         INT          NOT NULL AUTO_INCREMENT,
    EQUIPO_LOCAL   VARCHAR(100) NOT NULL,
    EQUIPO_VISITA  VARCHAR(100) NOT NULL,
    FECHA          DATETIME     NOT NULL,
    LUGAR          VARCHAR(150) NOT NULL,
    PRIMARY KEY (CODIGO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  Tabla LOCALIDAD_PARTIDO
-- -----------------------------------------------------------------------------
CREATE TABLE LOCALIDAD_PARTIDO (
    ID                INT           NOT NULL AUTO_INCREMENT,
    CODIGO_PARTIDO    INT           NOT NULL,
    CODIGO_LOCALIDAD  VARCHAR(50)   NOT NULL,
    DISPONIBILIDAD    INT           NOT NULL DEFAULT 0,
    PRECIO            DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT fk_locpar_partido
        FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  Tabla FACTURA  (cabecera de la venta de boletos) - asociada al usuario comprador
-- -----------------------------------------------------------------------------
CREATE TABLE FACTURA (
    ID_FACTURA  INT           NOT NULL AUTO_INCREMENT,
    ID_USUARIO  INT           NOT NULL,
    FECHA       DATETIME      NOT NULL,
    SUBTOTAL    DECIMAL(10,2) NOT NULL,
    IVA         DECIMAL(10,2) NOT NULL,
    TOTAL       DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ID_FACTURA),
    CONSTRAINT fk_factura_usuario
        FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  Tabla DETALLE_FACTURA  (lineas de la factura)
-- -----------------------------------------------------------------------------
CREATE TABLE DETALLE_FACTURA (
    ID_DETALLE       INT           NOT NULL AUTO_INCREMENT,
    ID_FACTURA       INT           NOT NULL,
    CODIGO_PARTIDO   INT           NOT NULL,
    LOCALIDAD        VARCHAR(50)   NOT NULL,
    CANTIDAD         INT           NOT NULL,
    PRECIO_UNITARIO  DECIMAL(10,2) NOT NULL,
    TOTAL            DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ID_DETALLE),
    CONSTRAINT fk_det_factura
        FOREIGN KEY (ID_FACTURA)     REFERENCES FACTURA(ID_FACTURA),
    CONSTRAINT fk_det_partido
        FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO)
) ENGINE=InnoDB;

-- =============================================================================
--  DATOS DE PRUEBA
-- =============================================================================

-- 4 usuarios (1 admin "monster" + 3 clientes de prueba)
INSERT INTO USUARIO (USUARIO, CONTRASENA, NOMBRE, ROL) VALUES
 ('monster',  'monster9',   'Administrador TicketPremium', 'ADMIN'),
 ('josue',    'josue123',   'Josue Marin',                 'CLIENTE'),
 ('mikaela',  'mikaela123', 'Mikaela Salcedo',             'CLIENTE'),
 ('elkin',    'elkin123',   'Elkin Pabon',                 'CLIENTE');

-- 5 partidos  (todos con fecha > 2026-05-19)
INSERT INTO PARTIDO_FUTBOL (EQUIPO_LOCAL, EQUIPO_VISITA, FECHA, LUGAR) VALUES
 ('LDU Quito',              'Barcelona SC', '2026-06-01 19:00:00', 'Estadio Casa Blanca, Quito'),
 ('Emelec',                 'Barcelona SC', '2026-06-08 20:00:00', 'Estadio George Capwell, Guayaquil'),
 ('Independiente del Valle','LDU Quito',    '2026-06-15 18:00:00', 'Estadio Banco Pichincha, Sangolqui'),
 ('Aucas',                  'El Nacional',  '2026-06-22 19:30:00', 'Estadio Gonzalo Pozo Ripalda, Quito'),
 ('Universidad Catolica',   'Macara',       '2026-06-29 17:00:00', 'Estadio Olimpico Atahualpa, Quito');

-- 20 localidades  (4 por cada partido: GENERAL, TRIBUNA, PALCO, PREFERENCIA)
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES
 (1, 'GENERAL',     3000,  8.00),
 (1, 'TRIBUNA',     1500, 15.00),
 (1, 'PALCO',        200, 30.00),
 (1, 'PREFERENCIA',  800, 20.00),
 (2, 'GENERAL',     3500,  8.00),
 (2, 'TRIBUNA',     1800, 15.00),
 (2, 'PALCO',        250, 30.00),
 (2, 'PREFERENCIA',  900, 20.00),
 (3, 'GENERAL',     2500,  7.50),
 (3, 'TRIBUNA',     1200, 14.00),
 (3, 'PALCO',        150, 28.00),
 (3, 'PREFERENCIA',  700, 18.00),
 (4, 'GENERAL',     2000,  6.00),
 (4, 'TRIBUNA',     1000, 12.00),
 (4, 'PALCO',        100, 25.00),
 (4, 'PREFERENCIA',  500, 16.00),
 (5, 'GENERAL',     2200,  6.50),
 (5, 'TRIBUNA',     1100, 13.00),
 (5, 'PALCO',        120, 26.00),
 (5, 'PREFERENCIA',  600, 17.00);

-- =============================================================================
--  VERIFICACION
-- =============================================================================
SELECT 'USUARIO'          AS tabla, COUNT(*) AS filas FROM USUARIO
UNION SELECT 'PARTIDO_FUTBOL',    COUNT(*) FROM PARTIDO_FUTBOL
UNION SELECT 'LOCALIDAD_PARTIDO', COUNT(*) FROM LOCALIDAD_PARTIDO
UNION SELECT 'FACTURA',           COUNT(*) FROM FACTURA
UNION SELECT 'DETALLE_FACTURA',   COUNT(*) FROM DETALLE_FACTURA;
