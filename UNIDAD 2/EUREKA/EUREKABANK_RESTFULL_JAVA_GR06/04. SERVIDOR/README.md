# EUREKABANK GR06 — Servidor RESTful (Java / Jakarta JAX-RS)

Misma funcionalidad que el servidor SOAP; **solo cambia la arquitectura** (REST/JSON
en vez de SOAP). Capas `modelo`, `persistencia`, `servicio` **idénticas** al SOAP;
se reemplazó `controlador` (@WebService) por **recursos JAX-RS**.

- Proyecto: `eurekabank_restful_java_gr06` (WAR, Payara 6, Jakarta EE 10).
- Base de datos: **`eurekabank_rest`** (clon completo de `eurekabank`: estructura +
  datos + scripts GR06 04-07). Independiente del SOAP. Conexión en
  `persistencia/ConexionBD.java`.
- Base URL: `http://localhost:8080/eurekabank_restful_java_gr06/api`

## Endpoints

| Método | Ruta | Equivale a (SOAP) |
|---|---|---|
| POST | `/api/login` `{usuario,clave}` | iniciarSesion |
| GET  | `/api/login/cliente/{usuario}` | clienteDeUsuario |
| GET  | `/api/clientes` | listarClientes |
| POST | `/api/clientes` `{paterno,materno,nombre,dni,ciudad,direccion,telefono,email}` | registrarCliente |
| GET  | `/api/cuentas?cliente=00001` | listarCuentasPorCliente |
| GET  | `/api/cuentas/{cuenta}/saldo` | consultarSaldo |
| POST | `/api/cuentas/{cuenta}/deposito` `{monto,moneda}` | depositar |
| POST | `/api/cuentas/{cuenta}/retiro` `{monto,moneda}` | retirar |
| POST | `/api/cuentas` `{cliente,moneda}` | registrarCuenta |
| DELETE | `/api/cuentas/{cuenta}` | eliminarCuenta |
| POST | `/api/transferencias` `{origen,destino,monto,moneda}` | transferir |
| GET  | `/api/movimientos?cuenta=00200002` | listarMovimientos (orden fecha desc) |

Reglas idénticas al SOAP: rol admin/cliente, depósito solo admin, conversión de
moneda (tabla `tasacambio`, 1 USD = 3.75 S/), CRÉDITO/DÉBITO, movimientos desc.

## Construir y desplegar

```
mvn clean package
asadmin deploy --force=true --contextroot /eurekabank_restful_java_gr06 \
  --name eurekabank_restful_java_gr06 target/eurekabank_restful_java_gr06.war
```

Usuarios de prueba: ver `09. DOCUMENTACION` del proyecto SOAP (misma data clonada:
`monster/monster9` ADMIN, `jmarin/demo123` cliente 00021, etc.).

> Clientes (web/consola/escritorio/móvil) REST: pendientes para siguientes iteraciones.
