# EUREKABANK GR06 — Cliente Web RESTful

Misma UI/funcionalidad que el cliente web SOAP; **solo cambia la arquitectura**:
la capa `servicio` consume el servidor **REST/JSON** por HTTP en vez de SOAP.
Servlets, JSP, CSS e imágenes son **idénticos** al SOAP.

- Proyecto: `eurekabank_restful_java_cliweb_gr06` (WAR, Payara 6, Jakarta EE 10).
- URL: `http://localhost:8080/eurekabank_restful_java_cliweb_gr06/`
- Consume: `http://localhost:8080/eurekabank_restful_java_gr06/api`

## Conexión al servidor (archivo aparte)

`ec.edu.monster.cliweb.config.ServidorConfig` → constante `BASE`
(sobrescribible con `-Deurekabank.rest.base=...`).

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

```
mvn clean package
asadmin deploy --force=true --contextroot /eurekabank_restful_java_cliweb_gr06 \
  --name eurekabank_restful_java_cliweb_gr06 target/eurekabank_restful_java_cliweb_gr06.war
```

Requiere el **servidor REST** desplegado. Usuarios: `monster/monster9`,
`jmarin/demo123`, etc. (BD `eurekabank_rest`, datos clonados del SOAP).
