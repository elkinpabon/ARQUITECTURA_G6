# EUREKABANK GR06 — Cliente Web RESTful

Misma UI/funcionalidad que el cliente web SOAP; **solo cambia la arquitectura**:
la capa `servicio` consume el servidor **REST/JSON** por HTTP en vez de SOAP.
Servlets, JSP, CSS e imágenes son **idénticos** al SOAP.

- Proyecto: `eurekabank_restful_java_cliweb_gr06` (WAR, Payara 6, Jakarta EE 10).
- URL: `http://localhost:8080/eurekabank_restful_java_cliweb_gr06/`
- Consume las tres bases configurables `ms.*` de los microservicios REST.

## Conexión a microservicios

`src/java/microservices.properties` contiene las bases `ms.autenticacion`,
`ms.consulta` y `ms.transacciones`. Cada clave se puede sobrescribir con propiedad de sistema
o variable de entorno en mayúsculas, en ese orden de precedencia.

## Capas

| Paquete | Contenido |
|---|---|
| `config` | `ServidorConfig` (URL del API REST) |
| `rest` | `Rest` — HTTP (`java.net.http`) + JSON (`jakarta.json`) |
| `ws` | POJOs `Resultado`, `CuentaResumen`, `ClienteResumen`, `MovimientoModel` (mapeo del JSON; mismo nombre de paquete que el SOAP para no tocar servlets/JSP) |
| `servicio` | `LoginClient`, `CuentaClient`, `MovimientoClient` — llaman los endpoints REST |
| `controlador` | Servlets (sin cambios respecto al SOAP) |
| `util` | `Moneda` |

## Funcionalidad (igual que el web SOAP, verificado)

Login con rol (`monster`=ADMIN combo de clientes; cliente ve solo lo suyo),
panel de cuentas + saldo, consultar saldo, **depósito solo admin**, retiro,
**transferencia con conversión de moneda**, movimientos **CRÉDITO/DÉBITO**
ordenados por fecha desc + detalle de conversión, imprimir/PDF, y admin:
registrar cliente, registrar/eliminar cuenta.

## Construir y desplegar

```powershell
mvn clean package
asadmin deploy --name eurekabank_restful_java_cliweb_gr06 --contextroot eurekabank_restful_java_cliweb_gr06 target/eurekabank_restful_java_cliweb_gr06.war
```

Requiere los tres microservicios REST desplegados. Usuarios: `monster/monster9`,
`jmarin/demo123`, etc.
