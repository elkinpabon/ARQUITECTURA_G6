# Guía de despliegue — EUREKABANK_SOAP_JAVA_GR06

## Orden OBLIGATORIO de arranque

Los clientes de consola y escritorio generan sus stubs con `wsimport`
**leyendo el WSDL del servidor en ejecución**. Por eso el orden es:

1. **MySQL** corriendo con la base `eurekabank` cargada.
2. **Servidor** desplegado en GlassFish/Payara (WSDLs accesibles).
3. Recién entonces, compilar/ejecutar los clientes.

## 1. Base de datos

```bash
cd "03. BDD"
mysql -u root -padmin2002 < 01_estructura.sql
mysql -u root -padmin2002 < 02_datos.sql
mysql -u root -padmin2002 < 03_gr06_seed.sql
```

Credenciales de prueba: `monster` / `monster9`  ·  `internet` / `admin2002`

## 2. Servidor (08. SERVIDOR/eurekabank_soap_java_gr06)

Abrir en NetBeans y **Run** (despliega en GlassFish/Payara), o:

```bash
mvn clean package      # genera target/eurekabank_soap_java_gr06.war
```

Verificar WSDLs:
- http://localhost:8080/eurekabank_soap_java_gr06/WSLogin?wsdl
- http://localhost:8080/eurekabank_soap_java_gr06/WSCuenta?wsdl
- http://localhost:8080/eurekabank_soap_java_gr06/WSMovimiento?wsdl

## 3. Clientes

| Cliente | Carpeta | Cómo ejecutar |
|---|---|---|
| Consola | 04. CLICONSOLA/eurekabank_soap_java_con_gr06 | NetBeans Run, o `mvn -q compile exec:java` |
| Escritorio | 05. CLIESCRITORIO/eurekabank_soap_java_cliesc_gr06 | NetBeans Run (Swing) |
| Web | 06. CLIWEB/eurekabank_soap_java_cliweb_gr06 | `npm install` y `npm start` (http://localhost:3000) |
| Móvil | 07. CLIMOVIL/eurekabank_soap_java_climov_gr06 | Android Studio, emulador (usa `http://10.0.2.2:8080`) |

> Consola/Escritorio: si cambia el WSDL, borrar `target/` y recompilar para
> regenerar stubs. El servidor debe estar arriba durante la compilación.

## Contrato SOAP nuevo (vs. proyecto original)

| Servicio | Operación | Parámetros | Retorno |
|---|---|---|---|
| WSLogin | `iniciarSesion` | usuario, clave | boolean |
| WSCuenta | `depositar` | cuenta, monto | Resultado |
| WSCuenta | `retirar` | cuenta, monto | Resultado |
| WSCuenta | `consultarSaldo` | cuenta | Resultado |
| WSMovimiento | `listarMovimientos` | cuenta | List\<MovimientoModel\> |

`Resultado = { exito, mensaje, saldo }`. La clave viaja en **texto plano**;
el servidor aplica **SHA1** contra la tabla `usuario` (estado ACTIVO).

## Smoke test ya verificado (servidor)

- Login real OK (monster/monster9 ✓, clave mala ✗, usuario ANULADO ✗)
- Depósito atómico ✓, Retiro ✓
- Saldo insuficiente rechazado ✓
- Cuenta no ACTIVA rechazada ✓
- Rollback ante error ✓
