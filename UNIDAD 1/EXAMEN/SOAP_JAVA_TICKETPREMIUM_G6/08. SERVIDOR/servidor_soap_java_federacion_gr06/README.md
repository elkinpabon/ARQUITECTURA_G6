# `servidor_soap_java_federacion_gr06` — Federación de Fútbol (Servidor SOAP)

> Servidor central del ecosistema **TicketPremium**. Expone vía SOAP/JAX-WS las operaciones que consumen los 4 clientes (Consola, Escritorio, Web, Móvil).
> **Lee esto antes de empezar a programar tu cliente.**

---

## 1. Stack y dependencias

| Componente | Versión | Para qué |
|---|---|---|
| Java | 17 | Lenguaje |
| Jakarta EE | 10.0.0 | API estándar (`jakarta.jws.*`) — provided |
| Metro / JAX-WS | 2.3 | Runtime SOAP — provided por Payara |
| MySQL Connector/J | 9.1.0 | Driver JDBC — bundled en el WAR |
| Maven | 3.9+ | Build (`mvn clean package`) |
| Payara / GlassFish | 6.x | Servidor de aplicaciones (Jakarta EE 10) |

Dependencias declaradas en [`pom.xml`](pom.xml). No hay que descargar nada a mano: Maven (NetBeans) las baja la primera vez que compilas.

## 2. Arquitectura por capas

```
       ┌──────────────────────────────────────────────────────────┐
       │  CLIENTE SOAP (Consola / Swing / JSP / Android)          │
       └──────────────────────────────────────────────────────────┘
                              │  SOAP/XML  vía  WSDL
                              ▼
       ┌──────────────────────────────────────────────────────────┐
       │  controlador.WSFederacion          (@WebService)         │  ← Fachada SOAP
       └──────────────────────────────────────────────────────────┘
                              │
                              ▼
       ┌──────────────────────────────────────────────────────────┐
       │  servicio.*Service                  (lógica de negocio)  │  ← Validaciones, IVA, etc.
       └──────────────────────────────────────────────────────────┘
                              │
                              ▼
       ┌──────────────────────────────────────────────────────────┐
       │  persistencia.*DAO  +  ConexionBD   (JDBC plano)         │  ← SQL preparado
       └──────────────────────────────────────────────────────────┘
                              │
                              ▼
       ┌──────────────────────────────────────────────────────────┐
       │  MySQL  →  ticketpremiumDB                               │
       └──────────────────────────────────────────────────────────┘
```

**Regla**: ninguna capa salta a otra que no sea la inmediata inferior. `WSFederacion` **nunca** habla con JDBC directo; el `*Service` **nunca** llama a otro `@WebMethod`.

## 3. Estructura del proyecto

```
servidor_soap_java_federacion_gr06/
├── pom.xml
└── src/main/
    ├── java/ec/edu/monster/
    │   ├── controlador/
    │   │   └── WSFederacion.java          ← @WebService (única fachada SOAP)
    │   ├── servicio/
    │   │   ├── SesionService.java         ← login
    │   │   ├── PartidoService.java        ← listar partidos disponibles
    │   │   ├── LocalidadService.java      ← listar localidades por partido
    │   │   ├── VentaService.java          ← registrar venta + historial
    │   │   └── ReporteService.java        ← resumen ventas por partido
    │   ├── modelo/                        ← DTOs Serializable (viajan en el WSDL)
    │   │   ├── Usuario.java
    │   │   ├── SesionResultado.java
    │   │   ├── Partido.java
    │   │   ├── Localidad.java
    │   │   ├── Factura.java
    │   │   ├── DetalleFactura.java
    │   │   ├── ResumenLocalidad.java
    │   │   └── Resultado.java
    │   ├── persistencia/
    │   │   ├── ConexionBD.java            ← apertura/cierre + config portable
    │   │   ├── BootstrapBD.java           ← @WebListener (lo invoca Payara al deploy)
    │   │   ├── BootstrapEngine.java       ← lógica idempotente (CREATE IF NOT EXISTS + INSERT IGNORE)
    │   │   ├── UsuarioDAO.java
    │   │   ├── PartidoDAO.java
    │   │   ├── LocalidadDAO.java
    │   │   ├── FacturaDAO.java            ← transacción FACTURA + DETALLE + UPDATE stock
    │   │   └── ReporteDAO.java
    │   └── pruebas/                       ← programas main() de validación local
    │       ├── PruebaConexion.java
    │       ├── PruebaSesion.java
    │       ├── PruebaPartido.java
    │       ├── PruebaLocalidad.java
    │       ├── PruebaVenta.java
    │       ├── PruebaReporte.java
    │       └── EjecutarTodas.java         ← runner que ejecuta las 6 en orden
    ├── resources/
    │   ├── db.properties                  ← config externa (host, user, pass...)
    │   └── bootstrap.sql                  ← script idempotente que ejecuta BootstrapBD al deploy
    └── webapp/
        ├── index.html
        └── WEB-INF/
            ├── web.xml
            └── beans.xml
```

## 4. Modelo de datos

```
USUARIO  (1 admin "monster" + 3 clientes seed)
   │ 1
   │
   │ N
FACTURA  ───<  DETALLE_FACTURA  >───  PARTIDO_FUTBOL  ───<  LOCALIDAD_PARTIDO
```

| Tabla | PK | Comentario |
|---|---|---|
| `USUARIO` | `ID_USUARIO` | `USUARIO` UNIQUE. `ROL ∈ {ADMIN, CLIENTE}`. Contraseña en claro (didáctico). |
| `PARTIDO_FUTBOL` | `CODIGO` | `FECHA` se usa para filtrar disponibles (`>= NOW()`). |
| `LOCALIDAD_PARTIDO` | `ID` | `(CODIGO_PARTIDO, CODIGO_LOCALIDAD)` único por convención. `DISPONIBILIDAD` se decrementa en cada venta. |
| `FACTURA` | `ID_FACTURA` | FK `ID_USUARIO` para historial. Guarda `SUBTOTAL`, `IVA`, `TOTAL` ya calculados. |
| `DETALLE_FACTURA` | `ID_DETALLE` | FK `ID_FACTURA` + FK `CODIGO_PARTIDO`. `LOCALIDAD` se guarda como string (snapshot). |

Script DDL + seed: [`../../02. MER/03. FISICO/script_ticketpremium.sql`](../../02.%20MER/03.%20FISICO/script_ticketpremium.sql).

## 5. Operaciones SOAP expuestas

WSDL: **`http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl`**
TargetNamespace: `http://ws.monster.edu.ec/`
Service: `WSFederacion`
Port: `WSFederacionPort`

### 5.1 `iniciarSesion(usuario, contrasena) → SesionResultado`

Login del cliente. **Llamar primero** antes de cualquier compra.

```
Request  : usuario="monster", contrasena="monster9"
Response : { exito=true, mensaje="Bienvenido Administrador TicketPremium",
             usuario={ idUsuario=1, usuario="monster", nombre="Administrador TicketPremium", rol="ADMIN" } }

Request  : usuario="monster", contrasena="xxx"
Response : { exito=false, mensaje="Credenciales invalidas", usuario=null }
```

Reglas:
- `usuario` y `contrasena` no pueden ser vacíos.
- El cliente debe guardar `idUsuario` para usarlo en `registrarVenta` y `misFacturas`.
- El rol `ADMIN` habilita pantallas de reporte; `CLIENTE` solo compra y ve sus facturas.

### 5.2 `listarPartidosDisponibles() → List<Partido>`

Partidos cuya `FECHA >= NOW()`, ordenados por fecha ascendente.

```
Request  : (sin parámetros)
Response : [
   { codigo=1, equipoLocal="LDU Quito", equipoVisita="Barcelona SC",
     fecha="2026-06-01 19:00:00.0", lugar="Estadio Casa Blanca, Quito" },
   ...
]
```

### 5.3 `listarLocalidadesPorPartido(codigoPartido) → List<Localidad>`

Localidades del partido **con `DISPONIBILIDAD > 0`**, ordenadas por precio ascendente.

```
Request  : codigoPartido=1
Response : [
   { id=1, codigoPartido=1, codigoLocalidad="GENERAL",     disponibilidad=3000, precio=8.00 },
   { id=2, codigoPartido=1, codigoLocalidad="TRIBUNA",     disponibilidad=1500, precio=15.00 },
   { id=4, codigoPartido=1, codigoLocalidad="PREFERENCIA", disponibilidad=800,  precio=20.00 },
   { id=3, codigoPartido=1, codigoLocalidad="PALCO",       disponibilidad=200,  precio=30.00 }
]
```

### 5.4 `registrarVenta(idUsuario, codigoPartido, codigoLocalidad, cantidad) → Resultado`

**Operación transaccional.** Hace en un solo commit:

1. Verifica que el usuario exista.
2. Verifica que el partido exista y **aún no se haya jugado** (`FECHA >= NOW()`).
3. Verifica que la localidad exista para ese partido.
4. Verifica `DISPONIBILIDAD >= cantidad`.
5. Calcula:
   - `subtotal = precio × cantidad`
   - `iva = subtotal × 0.15` (redondeo HALF_UP a 2 decimales)
   - `total = subtotal + iva`
6. `INSERT FACTURA` (con `ID_USUARIO`) → obtiene `id_factura`.
7. `INSERT DETALLE_FACTURA`.
8. `UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - cantidad WHERE … AND DISPONIBILIDAD >= cantidad` (el filtro defensivo evita over-selling concurrente).
9. **Commit** o **rollback** si algo falla.

```
Request  : idUsuario=2, codigoPartido=1, codigoLocalidad="GENERAL", cantidad=2
Response : { exito=true, mensaje="Venta registrada. Factura #1",
             factura={ idFactura=1, idUsuario=2, fecha="2026-05-19 18:42:11.0",
                       subtotal=16.00, iva=2.40, total=18.40 } }
```

Mensajes de error posibles (`exito=false`):

| Causa | Mensaje |
|---|---|
| `cantidad <= 0` | "La cantidad debe ser mayor a cero." |
| Usuario inexistente | "Usuario no autenticado o inexistente." |
| Partido inexistente | "El partido N no existe." |
| Partido ya jugado | "El partido ya se jugo. No se pueden vender mas boletos." |
| Localidad inválida | "La localidad X no esta definida para el partido N." |
| Sin stock | "Disponibilidad insuficiente. Quedan M boletos en X." |
| Fallo BD | "No se pudo completar la venta (conflicto de disponibilidad o error de BD)." |

### 5.5 `resumenVentasPartido(codigoPartido) → List<ResumenLocalidad>`

Reporte de la rúbrica. Agrupa `DETALLE_FACTURA` por localidad para un partido.

```
Request  : codigoPartido=1
Response : [
   { localidad="GENERAL", vendidos=1456, totalRecaudado=8500.00 },
   { localidad="PALCO",   vendidos=50,   totalRecaudado=1500.00 },
   { localidad="TRIBUNA", vendidos=300,  totalRecaudado=6000.00 }
]
```

### 5.6 `misFacturas(idUsuario) → List<Factura>`

Historial de compras del usuario, ordenado por fecha descendente.

```
Request  : idUsuario=2
Response : [
   { idFactura=12, idUsuario=2, fecha="2026-05-20 ...", subtotal=…, iva=…, total=… },
   { idFactura=8,  idUsuario=2, fecha="2026-05-19 ...", subtotal=…, iva=…, total=… }
]
```

## 6. Configuración portable

`ConexionBD` resuelve la config en este orden (lo primero que esté definido gana):

| Prioridad | Origen | Llave |
|---|---|---|
| 1 | Variables de entorno del servidor de aplicaciones | `TICKETPREMIUM_DB_HOST`, `_PORT`, `_NAME`, `_USER`, `_PASSWORD` |
| 2 | `src/main/resources/db.properties` (dentro del WAR) | `db.host`, `db.port`, `db.name`, `db.user`, `db.password` |
| 3 | Defaults hardcodeados | `localhost:3306 / ticketpremiumDB / root / admin123` |

> **No editar el código** para cambiar de máquina: cambia las variables de entorno del Payara o el `db.properties` y reempaqueta.

### 6.4 Configuración para los clientes (dónde está el servidor)

Los clientes (consola/escritorio/web/móvil) necesitan saber **dónde encontrar este servidor**. Para que cualquier equipo pueda apuntar sus clientes a su propio Payara sin recompilar, el servidor publica una "ficha de conexión" reutilizable:

```
08. SERVIDOR/conexion-clientes/
├── server.properties        ← plantilla de host/puerto/contexto/servicio
├── ServidorConfig.java      ← helper Java que resuelve la URL en runtime
└── README.md                ← cómo lo usa cada cliente
```

Cada cliente copia ambos archivos a su propio `src/main/resources/` (el `.properties`) y `src/main/java/ec/edu/monster/config/` (el `.java`). En runtime resuelve la URL así: **env vars → server.properties → defaults**.

| Variable de entorno | Default |
|---|---|
| `TICKETPREMIUM_SERVER_PROTOCOL` | `http` |
| `TICKETPREMIUM_SERVER_HOST` | `localhost` |
| `TICKETPREMIUM_SERVER_PORT` | `8080` |
| `TICKETPREMIUM_SERVER_CONTEXT` | `servidor_soap_java_federacion_gr06` |
| `TICKETPREMIUM_SERVER_SERVICE` | `WSFederacion` |

Ver el [README de conexión](../conexion-clientes/README.md) para los detalles.

## 6.5 Bootstrap automático de la BD

Al desplegar el WAR, Payara invoca `BootstrapBD` (un `ServletContextListener`) **antes** de aceptar requests. Esto garantiza que la base esté siempre disponible aunque alguien la haya borrado.

```
Payara start ──► BootstrapBD.contextInitialized()
                       │
                       ▼
                BootstrapEngine.ejecutar()
                       │
                       ├── leer classpath:/bootstrap.sql
                       ├── conectar SIN base de datos
                       ├── CREATE DATABASE IF NOT EXISTS ticketpremiumDB
                       ├── conectar CON ticketpremiumDB
                       ├── CREATE TABLE IF NOT EXISTS x5
                       ├── INSERT IGNORE  (4 usuarios + 5 partidos + 20 localidades)
                       └── sembrarDatosDemoSiVacio()
                              └── si FACTURA está vacía → inserta 5 facturas demo
                                  (con detalles + descuento de stock, en transacción)
```

### Datos demo sembrados (solo si `FACTURA` está vacía)

| # | Usuario | Partido | Localidad | Cant | Total |
|---|---|---|---|---|---|
| 1 | josue (id=2) | LDU vs Barcelona (1) | GENERAL | 5 | $46.00 |
| 2 | mikaela (id=3) | LDU vs Barcelona (1) | PALCO | 2 | $69.00 |
| 3 | josue (id=2) | Emelec vs Barcelona (2) | TRIBUNA | 3 | $51.75 |
| 4 | elkin (id=4) | IDV vs LDU (3) | PREFERENCIA | 1 | $20.70 |
| 5 | mikaela (id=3) | Universidad Católica vs Macará (5) | GENERAL | 4 | $29.90 |

Con esto, recién desplegado puedes probar:
- `misFacturas(2)` → 2 facturas de josue
- `resumenVentasPartido(1)` → GENERAL=5/$40 y PALCO=2/$60
- `listarLocalidadesPorPartido(1)` → GENERAL ya muestra 2995 (no 3000) porque vendió 5

### Garantías

| Escenario inicial | Resultado tras desplegar |
|---|---|
| BD inexistente | Crea BD + tablas + seed + 5 facturas demo |
| BD existe pero tablas borradas | Crea tablas + seed + 5 facturas demo |
| BD y tablas existen, seed vacío | Inserta seed + 5 facturas demo |
| BD y tablas existen, `FACTURA` vacía | Sólo siembra 5 facturas demo |
| BD y tablas existen, `FACTURA` con datos | No-op (idempotente, respeta tus ventas reales) |

### Para desactivar (p.ej. producción real)

```bash
export TICKETPREMIUM_BOOTSTRAP_DISABLED=true
```

### Diferencia entre los 2 scripts SQL

| Script | Uso | Tipo |
|---|---|---|
| [`02. MER/03. FISICO/script_ticketpremium.sql`](../../02.%20MER/03.%20FISICO/script_ticketpremium.sql) | entregable académico (MER físico) | **destructivo** (`DROP DATABASE`) — referencia, no se ejecuta en runtime |
| [`src/main/resources/bootstrap.sql`](src/main/resources/bootstrap.sql) | bootstrap automático del servidor | **idempotente** (`IF NOT EXISTS` + `INSERT IGNORE`) |

Ambos producen el mismo schema y seed, pero con distinta semántica.

### Verificar manualmente

```bash
# Borrar la BD a mano
mysql -u root -padmin123 -e "DROP DATABASE IF EXISTS ticketpremiumDB"

# Re-desplegar el WAR (o reiniciar Payara) → revisar logs:
#   INFO: Iniciando bootstrap de ticketpremiumDB ...
#   INFO:   [OK] CREATE DATABASE (si no existia)
#   INFO: Bootstrap completado: 10 sentencias.

# La BD aparece de vuelta con seed completo
mysql -u root -padmin123 -e "USE ticketpremiumDB; SELECT COUNT(*) FROM USUARIO;"
```

O sin desplegar, usando `PruebaBootstrap` (ver sección 9).

## 7. Build & deploy

### 7.1 NetBeans (recomendado)

1. **File ▸ Open Project** → seleccionar esta carpeta (`servidor_soap_java_federacion_gr06`).
2. Click derecho ▸ **Clean and Build**.
3. Click derecho ▸ **Run** (deploya en el Payara configurado).

### 7.2 Línea de comandos

```bash
mvn clean package
# genera target/servidor_soap_java_federacion_gr06.war
asadmin deploy --contextroot servidor_soap_java_federacion_gr06 \
               target/servidor_soap_java_federacion_gr06.war
```

### 7.3 Verificación

Abrir en navegador:

```
http://localhost:8080/servidor_soap_java_federacion_gr06/
http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl
```

Debes ver el WSDL XML. Si ves `404` revisa los logs de Payara.

## 8. Consumir el WSDL desde un cliente Java

### 8.1 Generar stubs con `wsimport`

```bash
# Dentro de la carpeta del cliente:
wsimport -keep -s src/main/java -p ec.edu.monster.ws \
  http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl
```

Esto genera (en `ec.edu.monster.ws`):
- `WSFederacion.java`, `WSFederacionService.java` (port/service).
- POJOs autogenerados de `Partido`, `Localidad`, `Factura`, `SesionResultado`, etc.
- Wrappers `ListarPartidosDisponiblesResponse`, `RegistrarVentaResponse`, ...

### 8.2 Uso típico (consola)

```java
WSFederacionService service = new WSFederacionService();
WSFederacion port = service.getWSFederacionPort();

// 1. Login
SesionResultado s = port.iniciarSesion("monster", "monster9");
if (!s.isExito()) { System.out.println(s.getMensaje()); return; }
int idUsuario = s.getUsuario().getIdUsuario();

// 2. Mostrar partidos
List<Partido> partidos = port.listarPartidosDisponibles();

// 3. Mostrar localidades del partido 1
List<Localidad> locs = port.listarLocalidadesPorPartido(1);

// 4. Comprar 2 boletos GENERAL del partido 1
Resultado r = port.registrarVenta(idUsuario, 1, "GENERAL", 2);
System.out.println(r.getMensaje());
if (r.isExito()) {
    Factura f = r.getFactura();
    System.out.println("Total: " + f.getTotal());
}
```

### 8.3 Consideraciones por cliente

| Cliente | Herramienta de stubs |
|---|---|
| Consola Java | `wsimport` (JDK 8) o plugin Maven `jaxws-maven-plugin` |
| Swing | igual que consola |
| JSP/Servlets | `wsimport` en el módulo web |
| Android | **NO** usar JAX-WS (no existe). Usar [kSOAP2-android](https://github.com/simpligility/ksoap2-android) y armar los SOAP envelopes a mano. |

## 9. Pruebas locales (sin desplegar Payara)

El paquete `ec.edu.monster.pruebas` contiene **6 programas `main()` + 1 runner** que validan cada capa contra la BD real, sin necesidad de levantar el servidor SOAP. Útil para verificar rápido que tu MySQL local está bien configurado.

| Clase | Capa que prueba | Casos cubiertos |
|---|---|---|
| `PruebaConexion` | `persistencia.ConexionBD` | Driver + conexión + las 5 tablas existen |
| `PruebaSesion` | `servicio.SesionService` | login admin / cliente / credencial inválida / campos vacíos |
| `PruebaPartido` | `servicio.PartidoService` | `listarDisponibles()` ≥ 5 partidos (rúbrica) |
| `PruebaLocalidad` | `servicio.LocalidadService` | `listarPorPartido(1..5)` total ≥ 20 (rúbrica) |
| `PruebaVenta` | `servicio.VentaService` | `registrarVenta` end-to-end: cálculos IVA 15%, stock −cantidad, factura en historial, rechazo `cantidad ≤ 0` |
| `PruebaReporte` | `servicio.ReporteService` | `resumenVentasPorPartido` para los 5 partidos |
| `PruebaBootstrap` | `persistencia.BootstrapEngine` | drop DB + re-bootstrap + verifica seed (no incluida en `EjecutarTodas` porque borra datos) |
| `EjecutarTodas` | **runner** | corre las 6 anteriores en orden y aborta si alguna falla |

### Cómo correrlas

**En NetBeans**: click derecho sobre `EjecutarTodas.java` ▸ **Run File**.
Para correr una sola, click derecho sobre la clase específica ▸ Run File.

**Por línea de comandos** (sin Payara):

```bash
cd "08. SERVIDOR/servidor_soap_java_federacion_gr06"
mvn compile
java -cp "target/classes:$HOME/.m2/repository/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar" \
     ec.edu.monster.pruebas.EjecutarTodas
```

Salida esperada al final:

```
################################################################
#   TODAS LAS PRUEBAS PASARON en NNN ms
################################################################
```

> ⚠️ `PruebaVenta` **inserta una factura y descuenta stock** en cada corrida. Para resetear:
> ```bash
> mysql -u root -padmin123 < "02. MER/03. FISICO/script_ticketpremium.sql"
> ```

## 10. Pruebas rápidas con SoapUI

1. **File ▸ New SOAP Project** → pegar `http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl`.
2. Probar en orden:
   - `iniciarSesion` con `monster / monster9` → guardar `idUsuario=1`.
   - `listarPartidosDisponibles` → 5 partidos.
   - `listarLocalidadesPorPartido` con `codigoPartido=1` → 4 localidades.
   - `registrarVenta` con `idUsuario=1, codigoPartido=1, codigoLocalidad=GENERAL, cantidad=2` → `total=18.40`.
   - `misFacturas` con `idUsuario=1` → 1 factura.
   - `resumenVentasPartido` con `codigoPartido=1` → 1 fila.

## 11. Troubleshooting

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Communications link failure` al arrancar | MySQL no corre | `brew services start mysql` (Mac) / `net start mysql` (Win) |
| `Access denied for user 'root'@'localhost'` | Password equivocado | Setear `TICKETPREMIUM_DB_PASSWORD` o editar `db.properties` |
| `Unknown database 'ticketpremiumDB'` | El bootstrap no se ejecutó al deploy | Verificar logs de Payara al arrancar el WAR: debe aparecer `Iniciando bootstrap de ticketpremiumDB ...`. Si está deshabilitado por `TICKETPREMIUM_BOOTSTRAP_DISABLED`, quitarlo |
| `404` al pedir el WSDL | El WAR no se desplegó | Mirar `domain1/logs/server.log` de Payara |
| Cliente recibe `SOAPFault` con texto vacío | Excepción en servidor antes del retorno | Revisar logs; los `@WebMethod` no deberían lanzar — todos devuelven `Resultado`/`SesionResultado` |

## 12. Decisiones de diseño (FAQ)

- **¿Por qué una fachada única `WSFederacion` y no un `@WebService` por dominio?** Es más simple para los clientes (un solo WSDL, un solo proxy) y reduce el código autogenerado. La separación por dominio se mantiene en la capa `servicio/`.
- **¿Por qué contraseña en claro?** Es un examen didáctico y consistente con los demás proyectos del curso. En producción se usaría BCrypt + sal.
- **¿Por qué `Resultado` en vez de lanzar excepciones?** Los SOAP fault son pesados de manejar en cliente Android (kSOAP2) y en Swing. Un DTO `{exito, mensaje}` es uniforme en los 4 clientes.
- **¿Por qué `String` para `fecha` en los DTOs?** `LocalDateTime` no se serializa por defecto en JAX-WS. Usamos `String` con formato `yyyy-MM-dd HH:mm:ss.S` (toString del `Timestamp`) — los clientes parsean al mostrar.
- **¿Por qué `BigDecimal` para `precio/subtotal/iva/total`?** Evita errores de coma flotante en cálculos monetarios (vs `double`/`float`).
