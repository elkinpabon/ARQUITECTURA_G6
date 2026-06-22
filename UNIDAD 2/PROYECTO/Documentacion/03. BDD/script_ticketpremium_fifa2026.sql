-- =============================================================================
--  Base de datos: ticketpremiumDB   (FIFA World Cup 2026 - datos REALES)
--  Proyecto TicketPremium - Arquitectura de Software GR06 (ESPE) - UNIDAD 2
--  Motor: MySQL 9.x  Â·  Instancia: Amazon  3.239.254.34:3306  (user admin)
--  Equipo: Josue Marin / Mikaela Salcedo / Elkin Pabon
--
--  REDISENO UNIDAD 2 (3 mejoras):
--    1) ESTADIO como ENTIDAD propia (16 sedes reales del Mundial 2026).
--    2) Localidades por CATEGORIA oficial FIFA Cat 1-4 con precios en USD.
--    3) Modelo de SECCION/fila/asientos estilo StubHub para selecciones.
--    + SELECCION como entidad (48 selecciones reales en 12 grupos A-L).
--
--  DATOS: 16 estadios, 48 selecciones, 72 partidos de la FASE DE GRUPOS
--         (11-27 jun 2026). Matchups = round-robin real de cada grupo;
--         fechas/sedes segun calendario oficial reconciliado.
--  NOTA: los horarios son representativos; el partido B SUI-BIH (codigo 10)
--        tiene sede/fecha reconstruida (no estaba en la fuente consultada).
--
--  Crea SOLO ticketpremiumDB. NO toca eurekasopajava / eurekarestjava / api_paises.
-- =============================================================================

DROP DATABASE IF EXISTS ticketpremiumDB;
CREATE DATABASE ticketpremiumDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE ticketpremiumDB;

-- -----------------------------------------------------------------------------
--  USUARIO  (autenticacion + rol)
-- -----------------------------------------------------------------------------
CREATE TABLE USUARIO (
    ID_USUARIO  INT          NOT NULL AUTO_INCREMENT,
    USUARIO     VARCHAR(50)  NOT NULL,
    CONTRASENA  VARCHAR(100) NOT NULL,
    NOMBRE      VARCHAR(120) NOT NULL,
    ROL         VARCHAR(20)  NOT NULL,            -- ADMIN | CLIENTE
    PRIMARY KEY (ID_USUARIO),
    UNIQUE KEY uk_usuario (USUARIO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  ESTADIO  (mejora 3: sede como entidad propia)
-- -----------------------------------------------------------------------------
CREATE TABLE ESTADIO (
    ID_ESTADIO     INT          NOT NULL AUTO_INCREMENT,
    NOMBRE_OFICIAL VARCHAR(120) NOT NULL,         -- ej. Estadio Azteca
    NOMBRE_FIFA    VARCHAR(120) NOT NULL,         -- ej. Mexico City Stadium
    CIUDAD         VARCHAR(100) NOT NULL,
    PAIS           VARCHAR(60)  NOT NULL,         -- Mexico | Estados Unidos | Canada
    CAPACIDAD      INT          NOT NULL,
    PRIMARY KEY (ID_ESTADIO),
    UNIQUE KEY uk_estadio (NOMBRE_OFICIAL)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  SELECCION  (48 selecciones reales agrupadas A-L)
-- -----------------------------------------------------------------------------
CREATE TABLE SELECCION (
    ID_SELECCION INT          NOT NULL AUTO_INCREMENT,
    NOMBRE       VARCHAR(80)  NOT NULL,
    GRUPO        CHAR(1)      NOT NULL,           -- A..L
    PRIMARY KEY (ID_SELECCION),
    UNIQUE KEY uk_seleccion (NOMBRE)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  PARTIDO_FUTBOL  (FK a ESTADIO y a SELECCION local/visita)
-- -----------------------------------------------------------------------------
CREATE TABLE PARTIDO_FUTBOL (
    CODIGO         INT          NOT NULL AUTO_INCREMENT,
    ID_LOCAL       INT          NOT NULL,
    ID_VISITA      INT          NOT NULL,
    ID_ESTADIO     INT          NOT NULL,
    FECHA          DATETIME     NOT NULL,
    FASE           VARCHAR(30)  NOT NULL DEFAULT 'GRUPOS',
    GRUPO          CHAR(1)      NOT NULL,
    PRIMARY KEY (CODIGO),
    CONSTRAINT fk_partido_local   FOREIGN KEY (ID_LOCAL)   REFERENCES SELECCION(ID_SELECCION),
    CONSTRAINT fk_partido_visita  FOREIGN KEY (ID_VISITA)  REFERENCES SELECCION(ID_SELECCION),
    CONSTRAINT fk_partido_estadio FOREIGN KEY (ID_ESTADIO) REFERENCES ESTADIO(ID_ESTADIO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  LOCALIDAD_PARTIDO  (mejora 2: categoria oficial Cat 1-4 + precio USD + stock)
-- -----------------------------------------------------------------------------
CREATE TABLE LOCALIDAD_PARTIDO (
    ID                INT           NOT NULL AUTO_INCREMENT,
    CODIGO_PARTIDO    INT           NOT NULL,
    CATEGORIA         VARCHAR(20)   NOT NULL,     -- CAT1 | CAT2 | CAT3 | CAT4
    PRECIO            DECIMAL(10,2) NOT NULL,     -- en USD
    DISPONIBILIDAD    INT           NOT NULL DEFAULT 0,
    PRIMARY KEY (ID),
    UNIQUE KEY uk_localidad (CODIGO_PARTIDO, CATEGORIA),
    CONSTRAINT fk_locpar_partido
        FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  SECCION  (mejora 2/StubHub: secciones fisicas de cada categoria con mapa de asientos)
-- -----------------------------------------------------------------------------
CREATE TABLE SECCION (
    ID_SECCION        INT          NOT NULL AUTO_INCREMENT,
    ID_LOCALIDAD      INT          NOT NULL,
    CODIGO_SECCION    VARCHAR(20)  NOT NULL,      -- ej. CAT1-S1
    NUM_FILAS         INT          NOT NULL,
    ASIENTOS_POR_FILA INT          NOT NULL,
    PRIMARY KEY (ID_SECCION),
    UNIQUE KEY uk_seccion (ID_LOCALIDAD, CODIGO_SECCION),
    CONSTRAINT fk_seccion_localidad
        FOREIGN KEY (ID_LOCALIDAD) REFERENCES LOCALIDAD_PARTIDO(ID)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  FACTURA  (cabecera de la compra)  -  una factura = un CARRITO (N detalles)
--  Pago a CONTADO o a CREDITO; si es credito genera tabla de AMORTIZACION.
-- -----------------------------------------------------------------------------
CREATE TABLE FACTURA (
    ID_FACTURA       INT           NOT NULL AUTO_INCREMENT,
    ID_USUARIO       INT           NOT NULL,
    FECHA            DATETIME      NOT NULL,
    SUBTOTAL         DECIMAL(10,2) NOT NULL,
    IVA              DECIMAL(10,2) NOT NULL,        -- 15%
    TOTAL            DECIMAL(10,2) NOT NULL,
    MONEDA           VARCHAR(5)    NOT NULL DEFAULT 'USD',
    TIPO_PAGO        VARCHAR(10)   NOT NULL DEFAULT 'CONTADO',  -- CONTADO | CREDITO
    ENTRADA          DECIMAL(10,2) NOT NULL DEFAULT 0.00,       -- abono inicial (credito)
    MONTO_FINANCIADO DECIMAL(10,2) NOT NULL DEFAULT 0.00,       -- total - entrada (credito)
    NUM_CUOTAS       INT           NOT NULL DEFAULT 0,          -- nro de cuotas (credito)
    TASA_INTERES     DECIMAL(6,4)  NOT NULL DEFAULT 0.0000,     -- interes mensual, ej 0.0200 = 2%
    PRIMARY KEY (ID_FACTURA),
    CONSTRAINT fk_factura_usuario
        FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  DETALLE_FACTURA  (linea de compra estilo StubHub: seccion + fila + asientos)
-- -----------------------------------------------------------------------------
CREATE TABLE DETALLE_FACTURA (
    ID_DETALLE       INT           NOT NULL AUTO_INCREMENT,
    ID_FACTURA       INT           NOT NULL,
    CODIGO_PARTIDO   INT           NOT NULL,
    ID_SECCION       INT           NOT NULL,
    CATEGORIA        VARCHAR(20)   NOT NULL,
    FILA             VARCHAR(10)   NOT NULL,       -- ej. F12
    ASIENTOS         VARCHAR(60)   NOT NULL,       -- ej. "12,13"
    CANTIDAD         INT           NOT NULL,
    PRECIO_UNITARIO  DECIMAL(10,2) NOT NULL,
    TOTAL            DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ID_DETALLE),
    CONSTRAINT fk_det_factura  FOREIGN KEY (ID_FACTURA)     REFERENCES FACTURA(ID_FACTURA),
    CONSTRAINT fk_det_partido  FOREIGN KEY (CODIGO_PARTIDO) REFERENCES PARTIDO_FUTBOL(CODIGO),
    CONSTRAINT fk_det_seccion  FOREIGN KEY (ID_SECCION)     REFERENCES SECCION(ID_SECCION)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  AMORTIZACION  (tabla de amortizacion para facturas a CREDITO - sistema frances)
--    Una fila por cuota: saldo inicial, cuota fija, interes, abono capital, saldo final.
-- -----------------------------------------------------------------------------
CREATE TABLE AMORTIZACION (
    ID_AMORTIZACION   INT           NOT NULL AUTO_INCREMENT,
    ID_FACTURA        INT           NOT NULL,
    NUM_CUOTA         INT           NOT NULL,
    FECHA_VENCIMIENTO DATE          NOT NULL,
    SALDO_INICIAL     DECIMAL(10,2) NOT NULL,
    CUOTA             DECIMAL(10,2) NOT NULL,
    INTERES           DECIMAL(10,2) NOT NULL,
    ABONO_CAPITAL     DECIMAL(10,2) NOT NULL,
    SALDO_FINAL       DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (ID_AMORTIZACION),
    UNIQUE KEY uk_amort (ID_FACTURA, NUM_CUOTA),
    CONSTRAINT fk_amort_factura
        FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID_FACTURA)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  RESERVA_ASIENTO  (estado por asiento en tiempo real)
--    Solo guarda asientos NO libres. Sin fila aqui => el asiento esta LIBRE.
--    ESTADO: RESERVADO (en carrito, expira a los 10 min) | OCUPADO (pagado).
-- -----------------------------------------------------------------------------
CREATE TABLE RESERVA_ASIENTO (
    ID_RESERVA  INT          NOT NULL AUTO_INCREMENT,
    ID_SECCION  INT          NOT NULL,
    FILA        VARCHAR(10)  NOT NULL,
    ASIENTO     VARCHAR(10)  NOT NULL,
    ESTADO      VARCHAR(12)  NOT NULL,            -- RESERVADO | OCUPADO
    ID_USUARIO  INT          NOT NULL,
    ID_FACTURA  INT          NULL,
    CREADO      DATETIME     NOT NULL,
    PRIMARY KEY (ID_RESERVA),
    UNIQUE KEY uk_asiento (ID_SECCION, FILA, ASIENTO),
    CONSTRAINT fk_res_seccion FOREIGN KEY (ID_SECCION) REFERENCES SECCION(ID_SECCION),
    CONSTRAINT fk_res_usuario FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO),
    CONSTRAINT fk_res_factura FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID_FACTURA)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
--  CUENTA / MOVIMIENTO  (parte "core bancario" dentro del mismo esquema)
--    Cada usuario tiene una cuenta; las compras generan un movimiento.
--    Las compras a CREDITO ademas suman el monto financiado al SALDO (deuda).
-- -----------------------------------------------------------------------------
CREATE TABLE CUENTA (
    ID_CUENTA  INT           NOT NULL AUTO_INCREMENT,
    ID_USUARIO INT           NOT NULL,
    NUMERO     VARCHAR(20)   NOT NULL,
    SALDO      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (ID_CUENTA),
    UNIQUE KEY uk_cuenta_usuario (ID_USUARIO),
    UNIQUE KEY uk_cuenta_numero (NUMERO),
    CONSTRAINT fk_cuenta_usuario FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)
) ENGINE=InnoDB;

CREATE TABLE MOVIMIENTO (
    ID_MOVIMIENTO INT           NOT NULL AUTO_INCREMENT,
    ID_CUENTA     INT           NOT NULL,
    FECHA         DATETIME      NOT NULL,
    TIPO          VARCHAR(20)   NOT NULL,           -- COMPRA_CONTADO | CREDITO
    MONTO         DECIMAL(12,2) NOT NULL,
    DESCRIPCION   VARCHAR(200)  NOT NULL,
    ID_FACTURA    INT           NULL,
    PRIMARY KEY (ID_MOVIMIENTO),
    CONSTRAINT fk_mov_cuenta  FOREIGN KEY (ID_CUENTA)  REFERENCES CUENTA(ID_CUENTA),
    CONSTRAINT fk_mov_factura FOREIGN KEY (ID_FACTURA) REFERENCES FACTURA(ID_FACTURA)
) ENGINE=InnoDB;

-- =============================================================================
--  SEMILLA DE DATOS REALES
-- =============================================================================

-- USUARIO (admin "monster"/monster9; clientes admin2002) ----------------------
INSERT INTO USUARIO (USUARIO, CONTRASENA, NOMBRE, ROL) VALUES
 ('monster',  'monster9',  'Administrador TicketPremium', 'ADMIN'),
 ('josue',    'admin2002', 'Josue Marin',                 'CLIENTE'),
 ('mikaela',  'admin2002', 'Mikaela Salcedo',             'CLIENTE'),
 ('elkin',    'admin2002', 'Elkin Pabon',                 'CLIENTE');

-- ESTADIO (16 sedes reales del Mundial 2026) ----------------------------------
INSERT INTO ESTADIO (ID_ESTADIO, NOMBRE_OFICIAL, NOMBRE_FIFA, CIUDAD, PAIS, CAPACIDAD) VALUES
 ( 1,'Estadio Azteca',          'Mexico City Stadium',            'Ciudad de Mexico', 'Mexico',          93000),
 ( 2,'Estadio Akron',           'Estadio Guadalajara',            'Zapopan',          'Mexico',          52000),
 ( 3,'Estadio BBVA',            'Estadio Monterrey',              'Guadalupe',        'Mexico',          53500),
 ( 4,'Mercedes-Benz Stadium',   'Atlanta Stadium',                'Atlanta',          'Estados Unidos',  75000),
 ( 5,'SoFi Stadium',            'Los Angeles Stadium',            'Inglewood',        'Estados Unidos',  70000),
 ( 6,'BMO Field',               'Toronto Stadium',                'Toronto',          'Canada',          45000),
 ( 7,'Levi''s Stadium',         'San Francisco Bay Area Stadium', 'Santa Clara',      'Estados Unidos',  71000),
 ( 8,'BC Place',                'Vancouver Stadium',              'Vancouver',        'Canada',          54000),
 ( 9,'Gillette Stadium',        'Boston Stadium',                 'Foxborough',       'Estados Unidos',  65000),
 (10,'Lumen Field',             'Seattle Stadium',                'Seattle',          'Estados Unidos',  69000),
 (11,'MetLife Stadium',         'New York New Jersey Stadium',    'East Rutherford',  'Estados Unidos',  82500),
 (12,'Lincoln Financial Field', 'Philadelphia Stadium',           'Philadelphia',     'Estados Unidos',  69000),
 (13,'Hard Rock Stadium',       'Miami Stadium',                  'Miami Gardens',    'Estados Unidos',  65000),
 (14,'NRG Stadium',             'Houston Stadium',                'Houston',          'Estados Unidos',  72000),
 (15,'Arrowhead Stadium',       'Kansas City Stadium',            'Kansas City',      'Estados Unidos',  73000),
 (16,'AT&T Stadium',            'Dallas Stadium',                 'Arlington',        'Estados Unidos',  94000);

-- SELECCION (48 selecciones reales, grupos A-L) -------------------------------
INSERT INTO SELECCION (ID_SELECCION, NOMBRE, GRUPO) VALUES
 ( 1,'Mexico','A'),       ( 2,'Sudafrica','A'),    ( 3,'Corea del Sur','A'), ( 4,'Chequia','A'),
 ( 5,'Canada','B'),       ( 6,'Suiza','B'),        ( 7,'Qatar','B'),         ( 8,'Bosnia y Herzegovina','B'),
 ( 9,'Brasil','C'),       (10,'Marruecos','C'),    (11,'Haiti','C'),         (12,'Escocia','C'),
 (13,'Estados Unidos','D'),(14,'Paraguay','D'),    (15,'Australia','D'),     (16,'Turquia','D'),
 (17,'Alemania','E'),     (18,'Curazao','E'),      (19,'Costa de Marfil','E'),(20,'Ecuador','E'),
 (21,'Paises Bajos','F'), (22,'Japon','F'),        (23,'Tunez','F'),         (24,'Suecia','F'),
 (25,'Belgica','G'),      (26,'Egipto','G'),       (27,'Iran','G'),          (28,'Nueva Zelanda','G'),
 (29,'Espana','H'),       (30,'Cabo Verde','H'),   (31,'Arabia Saudi','H'),  (32,'Uruguay','H'),
 (33,'Francia','I'),      (34,'Senegal','I'),      (35,'Noruega','I'),       (36,'Irak','I'),
 (37,'Argentina','J'),    (38,'Argelia','J'),      (39,'Austria','J'),       (40,'Jordania','J'),
 (41,'Portugal','K'),     (42,'Uzbekistan','K'),   (43,'Colombia','K'),      (44,'Republica Democratica del Congo','K'),
 (45,'Inglaterra','L'),   (46,'Croacia','L'),      (47,'Ghana','L'),         (48,'Panama','L');

-- PARTIDO_FUTBOL (72 partidos de fase de grupos) ------------------------------
-- ID_LOCAL / ID_VISITA referencian SELECCION; ID_ESTADIO referencia ESTADIO.
INSERT INTO PARTIDO_FUTBOL (CODIGO, ID_LOCAL, ID_VISITA, ID_ESTADIO, FECHA, GRUPO) VALUES
 -- Grupo A
 ( 1, 1, 2, 1,'2026-06-11 19:00:00','A'),   ( 2, 3, 4, 2,'2026-06-11 16:00:00','A'),
 ( 3, 4, 2, 4,'2026-06-18 13:00:00','A'),   ( 4, 1, 3, 2,'2026-06-18 19:00:00','A'),
 ( 5, 2, 3, 3,'2026-06-24 13:00:00','A'),   ( 6, 4, 1, 1,'2026-06-24 19:00:00','A'),
 -- Grupo B  (codigo 10 = SUI-BIH reconstruido)
 ( 7, 5, 8, 6,'2026-06-12 18:00:00','B'),   ( 8, 7, 6, 7,'2026-06-13 13:00:00','B'),
 ( 9, 5, 7, 8,'2026-06-18 18:00:00','B'),   (10, 6, 8, 5,'2026-06-18 16:00:00','B'),  -- 10: SUI-BIH en SoFi (verificado Wikipedia 2026-06-10)
 (11, 6, 5, 8,'2026-06-24 18:00:00','B'),   (12, 8, 7,10,'2026-06-24 16:00:00','B'),
 -- Grupo C
 (13, 9,10,11,'2026-06-13 18:00:00','C'),   (14,11,12, 9,'2026-06-13 15:00:00','C'),
 (15,12,10, 9,'2026-06-19 15:00:00','C'),   (16, 9,11,12,'2026-06-19 18:00:00','C'),
 (17,12, 9,13,'2026-06-24 21:00:00','C'),   (18,10,11, 4,'2026-06-24 18:00:00','C'),
 -- Grupo D
 (19,13,14, 5,'2026-06-12 19:00:00','D'),   (20,15,16, 8,'2026-06-13 16:00:00','D'),
 (21,13,15,10,'2026-06-19 19:00:00','D'),   (22,16,14, 7,'2026-06-19 16:00:00','D'),
 (23,16,13, 5,'2026-06-25 19:00:00','D'),   (24,14,15, 7,'2026-06-25 16:00:00','D'),
 -- Grupo E
 (25,17,18,14,'2026-06-14 18:00:00','E'),   (26,19,20,12,'2026-06-14 15:00:00','E'),
 (27,17,19, 6,'2026-06-20 15:00:00','E'),   (28,20,18,15,'2026-06-20 18:00:00','E'),
 (29,20,17,11,'2026-06-25 21:00:00','E'),   (30,18,19,12,'2026-06-25 18:00:00','E'),
 -- Grupo F
 (31,21,22,16,'2026-06-14 21:00:00','F'),   (32,24,23, 3,'2026-06-14 12:00:00','F'),
 (33,21,24,14,'2026-06-20 18:00:00','F'),   (34,23,22, 3,'2026-06-20 12:00:00','F'),
 (35,22,24,16,'2026-06-25 21:00:00','F'),   (36,23,21,15,'2026-06-25 18:00:00','F'),
 -- Grupo G
 (37,25,26,10,'2026-06-15 18:00:00','G'),   (38,27,28, 5,'2026-06-15 16:00:00','G'),
 (39,25,27, 5,'2026-06-21 16:00:00','G'),   (40,28,26, 8,'2026-06-21 18:00:00','G'),
 (41,26,27,10,'2026-06-26 18:00:00','G'),   (42,28,25, 8,'2026-06-26 16:00:00','G'),
 -- Grupo H
 (43,29,30, 4,'2026-06-15 15:00:00','H'),   (44,31,32,13,'2026-06-15 21:00:00','H'),
 (45,29,31, 4,'2026-06-21 15:00:00','H'),   (46,32,30,13,'2026-06-21 21:00:00','H'),
 (47,30,31,14,'2026-06-26 18:00:00','H'),   (48,32,29, 2,'2026-06-26 21:00:00','H'),
 -- Grupo I
 (49,33,34,11,'2026-06-16 18:00:00','I'),   (50,36,35, 9,'2026-06-16 15:00:00','I'),
 (51,33,36,12,'2026-06-22 15:00:00','I'),   (52,35,34,11,'2026-06-22 18:00:00','I'),
 (53,35,33, 9,'2026-06-26 15:00:00','I'),   (54,34,36, 6,'2026-06-26 18:00:00','I'),
 -- Grupo J
 (55,37,38,15,'2026-06-16 21:00:00','J'),   (56,39,40, 7,'2026-06-16 16:00:00','J'),
 (57,37,39,16,'2026-06-22 21:00:00','J'),   (58,40,38, 7,'2026-06-22 16:00:00','J'),
 (59,38,39,15,'2026-06-27 16:00:00','J'),   (60,40,37,16,'2026-06-27 21:00:00','J'),
 -- Grupo K
 (61,41,44,14,'2026-06-17 18:00:00','K'),   (62,42,43, 1,'2026-06-17 16:00:00','K'),
 (63,41,42,14,'2026-06-23 18:00:00','K'),   (64,43,44, 2,'2026-06-23 16:00:00','K'),
 (65,43,41,13,'2026-06-27 18:00:00','K'),   (66,44,42, 4,'2026-06-27 15:00:00','K'),
 -- Grupo L
 (67,45,46,16,'2026-06-17 21:00:00','L'),   (68,47,48, 6,'2026-06-17 16:00:00','L'),
 (69,45,47, 9,'2026-06-23 15:00:00','L'),   (70,48,46, 6,'2026-06-23 16:00:00','L'),
 (71,48,45,11,'2026-06-27 21:00:00','L'),   (72,46,47,12,'2026-06-27 18:00:00','L');

-- LOCALIDAD_PARTIDO: 4 categorias (Cat 1-4) por cada partido.
--   Precio USD por categoria; stock proporcional a la capacidad del estadio.
INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CATEGORIA, PRECIO, DISPONIBILIDAD)
SELECT p.CODIGO, c.CATEGORIA, c.PRECIO, FLOOR(e.CAPACIDAD * c.FRAC)
FROM PARTIDO_FUTBOL p
JOIN ESTADIO e ON e.ID_ESTADIO = p.ID_ESTADIO
JOIN (
    SELECT 'CAT1' AS CATEGORIA, 185.00 AS PRECIO, 0.15 AS FRAC
    UNION ALL SELECT 'CAT2', 135.00, 0.25
    UNION ALL SELECT 'CAT3',  90.00, 0.30
    UNION ALL SELECT 'CAT4',  60.00, 0.30
) c
ORDER BY p.CODIGO, c.PRECIO DESC;

-- SECCION: 6 secciones fisicas por cada categoria (zonas del estadio / mapa StubHub).
--   S1-S2 mas grandes (30x25), S3-S6 medianas (18x20).
INSERT INTO SECCION (ID_LOCALIDAD, CODIGO_SECCION, NUM_FILAS, ASIENTOS_POR_FILA)
SELECT lp.ID, CONCAT(lp.CATEGORIA,'-S', n.N), 30, 25
FROM LOCALIDAD_PARTIDO lp
JOIN (SELECT 1 AS N UNION ALL SELECT 2) n;
INSERT INTO SECCION (ID_LOCALIDAD, CODIGO_SECCION, NUM_FILAS, ASIENTOS_POR_FILA)
SELECT lp.ID, CONCAT(lp.CATEGORIA,'-S', n.N), 18, 20
FROM LOCALIDAD_PARTIDO lp
JOIN (SELECT 3 AS N UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) n;

-- CUENTA: una por usuario (parte bancaria del mismo esquema)
INSERT INTO CUENTA (ID_USUARIO, NUMERO, SALDO) VALUES
 (1,'CTA-0001',0.00),(2,'CTA-0002',0.00),(3,'CTA-0003',0.00),(4,'CTA-0004',0.00);

-- =============================================================================
--  COMPRAS DEMO (para poblar FACTURA / DETALLE_FACTURA)  -  IVA 15%
-- =============================================================================
-- Factura 1: josue compra 2 boletos CAT2 para el partido inaugural (Mexico-Sudafrica)
INSERT INTO FACTURA (ID_FACTURA, ID_USUARIO, FECHA, SUBTOTAL, IVA, TOTAL, MONEDA) VALUES
 (1, 2, '2026-06-01 10:30:00', 270.00, 40.50, 310.50, 'USD');
INSERT INTO DETALLE_FACTURA (ID_FACTURA, CODIGO_PARTIDO, ID_SECCION, CATEGORIA, FILA, ASIENTOS, CANTIDAD, PRECIO_UNITARIO, TOTAL)
SELECT 1, 1, s.ID_SECCION, 'CAT2', 'F12', '12,13', 2, 135.00, 270.00
FROM SECCION s JOIN LOCALIDAD_PARTIDO lp ON lp.ID = s.ID_LOCALIDAD
WHERE lp.CODIGO_PARTIDO = 1 AND lp.CATEGORIA = 'CAT2'
ORDER BY s.ID_SECCION LIMIT 1;

-- Factura 2: mikaela compra 1 boleto CAT1 para USA-Paraguay
INSERT INTO FACTURA (ID_FACTURA, ID_USUARIO, FECHA, SUBTOTAL, IVA, TOTAL, MONEDA) VALUES
 (2, 3, '2026-06-02 14:05:00', 185.00, 27.75, 212.75, 'USD');
INSERT INTO DETALLE_FACTURA (ID_FACTURA, CODIGO_PARTIDO, ID_SECCION, CATEGORIA, FILA, ASIENTOS, CANTIDAD, PRECIO_UNITARIO, TOTAL)
SELECT 2, 19, s.ID_SECCION, 'CAT1', 'F03', '07', 1, 185.00, 185.00
FROM SECCION s JOIN LOCALIDAD_PARTIDO lp ON lp.ID = s.ID_LOCALIDAD
WHERE lp.CODIGO_PARTIDO = 19 AND lp.CATEGORIA = 'CAT1'
ORDER BY s.ID_SECCION LIMIT 1;

-- =============================================================================
--  VERIFICACION
-- =============================================================================
SELECT 'USUARIO'           AS tabla, COUNT(*) AS filas FROM USUARIO
UNION ALL SELECT 'ESTADIO',           COUNT(*) FROM ESTADIO
UNION ALL SELECT 'SELECCION',         COUNT(*) FROM SELECCION
UNION ALL SELECT 'PARTIDO_FUTBOL',    COUNT(*) FROM PARTIDO_FUTBOL
UNION ALL SELECT 'LOCALIDAD_PARTIDO', COUNT(*) FROM LOCALIDAD_PARTIDO
UNION ALL SELECT 'SECCION',           COUNT(*) FROM SECCION
UNION ALL SELECT 'FACTURA',           COUNT(*) FROM FACTURA
UNION ALL SELECT 'DETALLE_FACTURA',   COUNT(*) FROM DETALLE_FACTURA
UNION ALL SELECT 'AMORTIZACION',      COUNT(*) FROM AMORTIZACION
UNION ALL SELECT 'RESERVA_ASIENTO',   COUNT(*) FROM RESERVA_ASIENTO
UNION ALL SELECT 'CUENTA',            COUNT(*) FROM CUENTA
UNION ALL SELECT 'MOVIMIENTO',        COUNT(*) FROM MOVIMIENTO;

