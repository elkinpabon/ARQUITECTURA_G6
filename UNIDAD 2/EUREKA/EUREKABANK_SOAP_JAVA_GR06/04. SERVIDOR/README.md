# SERVIDOR — eurekabank_soap_java_gr06

Servidor SOAP JAX-WS (Jakarta EE 10, Java 17), MVC limpio.

## Capas (MVC)

| Paquete | Rol |
|---|---|
| `ec.edu.monster.ws` | Controlador: fachadas `@WebService` |
| `ec.edu.monster.servicio` | Lógica de negocio (transacciones, validaciones, SHA1) |
| `ec.edu.monster.persistencia` | DAOs + `ConexionBD` (ÚNICA clase de conexión) |
| `ec.edu.monster.modelo` | DTOs/POJOs |

`ConexionBD.conectar()` / `desconectar()` centraliza toda conexión. Lecturas
abren/cierran su conexión; depósito/retiro corren en **una transacción atómica**
(`setAutoCommit(false)` + `commit`/`rollback`, `SELECT ... FOR UPDATE`).

## Operaciones SOAP

| Servicio | Operación | Parámetros | Retorno |
|---|---|---|---|
| WSLogin | `iniciarSesion` | usuario, clave | boolean |
| WSCuenta | `depositar` | cuenta, monto | Resultado |
| WSCuenta | `retirar` | cuenta, monto | Resultado |
| WSCuenta | `consultarSaldo` | cuenta | Resultado |
| WSMovimiento | `listarMovimientos` | cuenta | List\<MovimientoModel\> |

`Resultado = { exito, mensaje, saldo }`

## Build y despliegue

```bash
mvn clean package          # genera target/eurekabank_soap_java_gr06.war
```

Desplegar el WAR en GlassFish/Payara (en NetBeans: Run sobre el proyecto).
Context root: `/eurekabank_soap_java_gr06`. WSDLs:

- http://localhost:8080/eurekabank_soap_java_gr06/WSLogin?wsdl
- http://localhost:8080/eurekabank_soap_java_gr06/WSCuenta?wsdl
- http://localhost:8080/eurekabank_soap_java_gr06/WSMovimiento?wsdl

## Credenciales de prueba

| Usuario | Clave | Estado |
|---|---|---|
| monster | monster9 | ACTIVO |
| internet | admin2002 | ACTIVO |

## Mejoras aplicadas vs. proyecto original

1. Login real contra tabla `usuario` (SHA1 + estado ACTIVO). Antes: quemado.
2. Depósito/retiro **atómicos** con rollback. Antes: 2 conexiones sin transacción.
3. Validación de saldo en retiro y de cuenta ACTIVA. Antes: inexistente.
4. Nº de movimiento atómico (`MAX+1` dentro de la tx). Antes: traía todo a memoria.
5. `ConexionBD` única con conectar/desconectar. Antes: credenciales y cierres dispersos.
6. Operaciones SOAP semánticas. Antes: `hello`, `cuenta`.
7. `Resultado` con mensaje de negocio. Antes: solo boolean.
8. pom Java 17, mysql-connector-j 9.1.0. Antes: Java 11 inconsistente.
