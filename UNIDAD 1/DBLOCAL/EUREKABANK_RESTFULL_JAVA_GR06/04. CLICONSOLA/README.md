# EUREKABANK GR06 — Cliente Consola RESTful

Misma funcionalidad y MVC que la consola SOAP; **solo cambia la arquitectura**:
consume el servidor **REST/JSON** por HTTP. `controlador`, `vista`, `util`,
`Sesion` **idénticos** al SOAP.

- Proyecto: `eurekabank_restful_java_con_gr06` (JAR ejecutable).
- Banner: "EUREKABANK GR06 — Banca RESTFULL · Cliente Consola".

## Conexión al servidor (archivo aparte)

`src/main/resources/servidor.properties`:

```
servidor.base=http://localhost:8080/eurekabank_restful_java_gr06/api
```

Solo se edita esa línea. La lee `ec.edu.monster.config.ServidorConfig`.

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
mvn clean package
mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy-dependencies -DoutputDirectory=target/lib
java -cp "target/classes:target/lib/*" ec.edu.monster.vista.ConsolaApp
```

Requiere el servidor REST desplegado. Usuarios: `monster/monster9`,
`jmarin/demo123`, … (BD `eurekabank_rest`).
