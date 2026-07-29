# EUREKABANK GR06 — Cliente Consola RESTful

Misma funcionalidad y MVC que la consola SOAP; **solo cambia la arquitectura**:
consume el servidor **REST/JSON** por HTTP. `controlador`, `vista`, `util`,
`Sesion` **idénticos** al SOAP.

- Proyecto: `eurekabank_restful_java_con_gr06` (JAR ejecutable).
- Banner: "EUREKABANK GR06 - Banca RESTFUL - Cliente Consola".

## Conexión a microservicios

`src/microservices.properties` define `ms.autenticacion`, `ms.consulta` y
`ms.transacciones`. Cada clave
admite propiedad de sistema o variable de entorno en mayúsculas, con esa
prioridad sobre el archivo empaquetado.

```
ms.autenticacion=http://localhost:8080/ms-rest-autenticacion/api
ms.consulta=http://localhost:8080/ms-rest-consulta/api
ms.transacciones=http://localhost:8080/ms-rest-transacciones/api
```

La lee `ec.edu.monster.config.ServidorConfig`.

## Capas

| Paquete | Contenido |
|---|---|
| `config` | `ServidorConfig` (URL del API REST) |
| `rest` | `Rest` — HTTP (`java.net.http`) + JSON (`jakarta.json`/Parsson) |
| `ws` | POJOs `Resultado`, `CuentaResumen`, `ClienteResumen`, `MovimientoModel` |
| `servicio` | `LoginClient`, `CuentaClient`, `MovimientoClient`, `Sesion` |
| `controlador` | `BancoController` (mismas reglas que SOAP/web) |
| `vista` | `ConsolaApp` |
| `util` | `Moneda`, `ExportHtml` (export estado de cuenta a HTML) |

Funcionalidad igual que el resto: rol admin/cliente, cuentas/saldo, depósito
solo admin, retiro, transferencia con conversión, movimientos CRÉDITO/DÉBITO
desc, export HTML, admin registrar/eliminar.

## Ejecutar

```
ant jar
```

Requiere los tres microservicios REST desplegados. Usuarios: `monster/monster9`,
`jmarin/demo123`, etc.
