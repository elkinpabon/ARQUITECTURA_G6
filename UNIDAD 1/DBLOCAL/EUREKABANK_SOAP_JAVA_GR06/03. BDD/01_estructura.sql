-- EUREKABANK_SOAP_JAVA_GR06 - Estructura
CREATE DATABASE IF NOT EXISTS eurekabank CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE eurekabank;

-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: eurekabank
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `asignado`
--

DROP TABLE IF EXISTS `asignado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asignado` (
  `chr_asigcodigo` char(6) NOT NULL,
  `chr_sucucodigo` char(3) NOT NULL,
  `chr_emplcodigo` char(4) NOT NULL,
  `dtt_asigfechaalta` date NOT NULL,
  `dtt_asigfechabaja` date DEFAULT NULL,
  PRIMARY KEY (`chr_asigcodigo`),
  KEY `idx_asignado01` (`chr_emplcodigo`),
  KEY `idx_asignado02` (`chr_sucucodigo`),
  CONSTRAINT `fk_asignado_empleado` FOREIGN KEY (`chr_emplcodigo`) REFERENCES `empleado` (`chr_emplcodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_asignado_sucursal` FOREIGN KEY (`chr_sucucodigo`) REFERENCES `sucursal` (`chr_sucucodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cargomantenimiento`
--

DROP TABLE IF EXISTS `cargomantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargomantenimiento` (
  `chr_monecodigo` char(2) NOT NULL,
  `dec_cargMontoMaximo` decimal(12,2) NOT NULL,
  `dec_cargImporte` decimal(12,2) NOT NULL,
  PRIMARY KEY (`chr_monecodigo`),
  KEY `idx_cargomantenimiento01` (`chr_monecodigo`),
  CONSTRAINT `fk_cargomantenimiento_moneda` FOREIGN KEY (`chr_monecodigo`) REFERENCES `moneda` (`chr_monecodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `chr_cliecodigo` char(5) NOT NULL,
  `vch_cliepaterno` varchar(25) NOT NULL,
  `vch_cliematerno` varchar(25) NOT NULL,
  `vch_clienombre` varchar(30) NOT NULL,
  `chr_cliedni` char(8) NOT NULL,
  `vch_clieciudad` varchar(30) NOT NULL,
  `vch_cliedireccion` varchar(50) NOT NULL,
  `vch_clietelefono` varchar(20) DEFAULT NULL,
  `vch_clieemail` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`chr_cliecodigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contador`
--

DROP TABLE IF EXISTS `contador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contador` (
  `vch_conttabla` varchar(30) NOT NULL,
  `int_contitem` int NOT NULL,
  `int_contlongitud` int NOT NULL,
  PRIMARY KEY (`vch_conttabla`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `costomovimiento`
--

DROP TABLE IF EXISTS `costomovimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `costomovimiento` (
  `chr_monecodigo` char(2) NOT NULL,
  `dec_costimporte` decimal(12,2) NOT NULL,
  PRIMARY KEY (`chr_monecodigo`),
  KEY `idx_costomovimiento` (`chr_monecodigo`),
  CONSTRAINT `fk_costomovimiento_moneda` FOREIGN KEY (`chr_monecodigo`) REFERENCES `moneda` (`chr_monecodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuenta`
--

DROP TABLE IF EXISTS `cuenta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta` (
  `chr_cuencodigo` char(8) NOT NULL,
  `chr_monecodigo` char(2) NOT NULL,
  `chr_sucucodigo` char(3) NOT NULL,
  `chr_emplcreacuenta` char(4) NOT NULL,
  `chr_cliecodigo` char(5) NOT NULL,
  `dec_cuensaldo` decimal(12,2) NOT NULL,
  `dtt_cuenfechacreacion` date NOT NULL,
  `vch_cuenestado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  `int_cuencontmov` int NOT NULL,
  `chr_cuenclave` char(6) NOT NULL,
  PRIMARY KEY (`chr_cuencodigo`),
  KEY `idx_cuenta01` (`chr_cliecodigo`),
  KEY `idx_cuenta02` (`chr_emplcreacuenta`),
  KEY `idx_cuenta03` (`chr_sucucodigo`),
  KEY `idx_cuenta04` (`chr_monecodigo`),
  CONSTRAINT `fk_cuenta_cliente` FOREIGN KEY (`chr_cliecodigo`) REFERENCES `cliente` (`chr_cliecodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cuenta_moneda` FOREIGN KEY (`chr_monecodigo`) REFERENCES `moneda` (`chr_monecodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cuenta_sucursal` FOREIGN KEY (`chr_sucucodigo`) REFERENCES `sucursal` (`chr_sucucodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cuente_empleado` FOREIGN KEY (`chr_emplcreacuenta`) REFERENCES `empleado` (`chr_emplcodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_cuenta_chr_cuenestado` CHECK ((`vch_cuenestado` in (_utf8mb3'ACTIVO',_utf8mb3'ANULADO',_utf8mb3'CANCELADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `empleado`
--

DROP TABLE IF EXISTS `empleado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleado` (
  `chr_emplcodigo` char(4) NOT NULL,
  `vch_emplpaterno` varchar(25) NOT NULL,
  `vch_emplmaterno` varchar(25) NOT NULL,
  `vch_emplnombre` varchar(30) NOT NULL,
  `vch_emplciudad` varchar(30) NOT NULL,
  `vch_empldireccion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`chr_emplcodigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interesmensual`
--

DROP TABLE IF EXISTS `interesmensual`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interesmensual` (
  `chr_monecodigo` char(2) NOT NULL,
  `dec_inteimporte` decimal(12,2) NOT NULL,
  PRIMARY KEY (`chr_monecodigo`),
  KEY `idx_interesmensual01` (`chr_monecodigo`),
  CONSTRAINT `fk_interesmensual_moneda` FOREIGN KEY (`chr_monecodigo`) REFERENCES `moneda` (`chr_monecodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logsession`
--

DROP TABLE IF EXISTS `logsession`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logsession` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `chr_emplcodigo` char(4) NOT NULL,
  `fec_ingreso` datetime NOT NULL,
  `fec_salida` datetime DEFAULT NULL,
  `ip` varchar(20) NOT NULL DEFAULT 'NONE',
  `hostname` varchar(100) NOT NULL DEFAULT 'NONE',
  PRIMARY KEY (`ID`),
  KEY `fk_log_session_empleado` (`chr_emplcodigo`),
  CONSTRAINT `fk_log_session_empleado` FOREIGN KEY (`chr_emplcodigo`) REFERENCES `empleado` (`chr_emplcodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `modulo`
--

DROP TABLE IF EXISTS `modulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modulo` (
  `int_moducodigo` int NOT NULL,
  `vch_modunombre` varchar(50) DEFAULT NULL,
  `vch_moduestado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`int_moducodigo`),
  CONSTRAINT `modulo_chk_1` CHECK ((`vch_moduestado` in (_utf8mb3'ACTIVO',_utf8mb3'ANULADO',_utf8mb3'CANCELADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `moneda`
--

DROP TABLE IF EXISTS `moneda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `moneda` (
  `chr_monecodigo` char(2) NOT NULL,
  `vch_monedescripcion` varchar(20) NOT NULL,
  PRIMARY KEY (`chr_monecodigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimiento`
--

DROP TABLE IF EXISTS `movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento` (
  `chr_cuencodigo` char(8) NOT NULL,
  `int_movinumero` int NOT NULL,
  `dtt_movifecha` date NOT NULL,
  `chr_emplcodigo` char(4) NOT NULL,
  `chr_tipocodigo` char(3) NOT NULL,
  `dec_moviimporte` decimal(12,2) NOT NULL,
  `chr_cuenreferencia` char(8) DEFAULT NULL,
  PRIMARY KEY (`chr_cuencodigo`,`int_movinumero`),
  KEY `idx_movimiento01` (`chr_tipocodigo`),
  KEY `idx_movimiento02` (`chr_emplcodigo`),
  KEY `idx_movimiento03` (`chr_cuencodigo`),
  CONSTRAINT `fk_movimiento_cuenta` FOREIGN KEY (`chr_cuencodigo`) REFERENCES `cuenta` (`chr_cuencodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_movimiento_empleado` FOREIGN KEY (`chr_emplcodigo`) REFERENCES `empleado` (`chr_emplcodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_movimiento_tipomovimiento` FOREIGN KEY (`chr_tipocodigo`) REFERENCES `tipomovimiento` (`chr_tipocodigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_Movimiento_importe4` CHECK ((`dec_moviimporte` >= 0.0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `parametro`
--

DROP TABLE IF EXISTS `parametro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parametro` (
  `chr_paracodigo` char(3) NOT NULL,
  `vch_paradescripcion` varchar(50) NOT NULL,
  `vch_paravalor` varchar(70) NOT NULL,
  `vch_paraestado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`chr_paracodigo`),
  CONSTRAINT `chk_parametro_vch_paraestado` CHECK ((`vch_paraestado` in (_utf8mb3'ACTIVO',_utf8mb3'ANULADO',_utf8mb3'CANCELADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permiso`
--

DROP TABLE IF EXISTS `permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permiso` (
  `chr_emplcodigo` char(4) NOT NULL,
  `int_moducodigo` int NOT NULL,
  `vch_permestado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`chr_emplcodigo`,`int_moducodigo`),
  KEY `FK_Permiso_Modulo` (`int_moducodigo`),
  CONSTRAINT `permiso_ibfk_1` FOREIGN KEY (`int_moducodigo`) REFERENCES `modulo` (`int_moducodigo`),
  CONSTRAINT `permiso_ibfk_2` FOREIGN KEY (`chr_emplcodigo`) REFERENCES `usuario` (`chr_emplcodigo`),
  CONSTRAINT `permiso_chk_1` CHECK ((`vch_permestado` in (_utf8mb3'ACTIVO',_utf8mb3'ANULADO',_utf8mb3'CANCELADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sucursal`
--

DROP TABLE IF EXISTS `sucursal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sucursal` (
  `chr_sucucodigo` char(3) NOT NULL,
  `vch_sucunombre` varchar(50) NOT NULL,
  `vch_sucuciudad` varchar(30) NOT NULL,
  `vch_sucudireccion` varchar(50) DEFAULT NULL,
  `int_sucucontcuenta` int NOT NULL,
  PRIMARY KEY (`chr_sucucodigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tipomovimiento`
--

DROP TABLE IF EXISTS `tipomovimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipomovimiento` (
  `chr_tipocodigo` char(3) NOT NULL,
  `vch_tipodescripcion` varchar(40) NOT NULL,
  `vch_tipoaccion` varchar(10) NOT NULL,
  `vch_tipoestado` varchar(15) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`chr_tipocodigo`),
  CONSTRAINT `chk_tipomovimiento_vch_tipoaccion` CHECK ((`vch_tipoaccion` in (_utf8mb3'INGRESO',_utf8mb3'SALIDA'))),
  CONSTRAINT `chk_tipomovimiento_vch_tipoestado` CHECK ((`vch_tipoestado` in (_utf8mb3'ACTIVO',_utf8mb3'ANULADO',_utf8mb3'CANCELADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `chr_emplcodigo` char(4) NOT NULL,
  `vch_emplusuario` varchar(20) NOT NULL,
  `vch_emplclave` varchar(50) NOT NULL,
  `vch_emplestado` varchar(15) DEFAULT 'ACTIVO',
  PRIMARY KEY (`chr_emplcodigo`),
  UNIQUE KEY `U_Usuario_vch_emplusuario` (`vch_emplusuario`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`chr_emplcodigo`) REFERENCES `empleado` (`chr_emplcodigo`),
  CONSTRAINT `usuario_chk_1` CHECK ((`vch_emplestado` in (_utf8mb3'ACTIVO',_utf8mb3'ANULADO',_utf8mb3'CANCELADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-09 10:42:49
