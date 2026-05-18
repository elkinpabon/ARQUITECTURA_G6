# EUREKABANK GR06 — Cliente Consola

Cliente de **consola** (Java + JAX-WS), **misma funcionalidad que el cliente web**,
patrón **MVC** y conexión al servidor en **archivo aparte**.

## Conexión al servidor (archivo aparte)

`src/main/resources/servidor.properties`:

```
servidor.base=http://localhost:8080/eurekabank_soap_java_gr06
```

Solo se edita esa línea para apuntar a otro servidor. La lee
`ec.edu.monster.config.ServidorConfig`; `WsFactory` fija el endpoint en cada
puerto SOAP (no se recompila la lógica).

## Estructura MVC

| Capa | Paquete | Contenido |
|---|---|---|
| Config | `config` | `ServidorConfig` |
| Servicio | `servicio` | `WsFactory`, `LoginClient`, `CuentaClient`, `MovimientoClient`, `Sesion` |
| Controlador | `controlador` | `BancoController` (rol admin/cliente, guard de cuenta propia, depósito solo admin, conversión) |
| Vista | `vista` | `ConsolaApp` (menús de texto) |
| Util | `util` | `Moneda` |

## Funcionalidad (igual que CLIWEB)

- Login con rol: `monster` = ADMIN; resto = cliente (ve solo sus cuentas).
- Ver cuentas y saldo total; Actualizar saldos.
- Consultar saldo, **Depositar (solo admin)**, Retirar, **Transferir** con
  conversión de moneda (Dólares preferente; 1 USD = 3.75 S/).
- Movimientos con ingresos/egresos y **detalle de conversión**.
- Admin: registrar cliente, registrar cuenta, eliminar cuenta.

## Ejecutar

```
mvn clean package    # requiere el SERVIDOR desplegado (wsimport)
mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy-dependencies -DoutputDirectory=target/lib
java -cp "target/classes:target/lib/*" ec.edu.monster.vista.ConsolaApp
```

Usuarios de prueba: `09. DOCUMENTACION/USUARIOS_PRUEBA.md`.
