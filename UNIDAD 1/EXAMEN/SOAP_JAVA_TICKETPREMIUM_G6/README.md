# SOAP_JAVA_TICKETPREMIUM_G6 — Examen Práctico Primera Unidad

**Equipo:** Josue Marin · Mikaela Salcedo · Elkin Pabon (GR06)
**Stack:** Java 17 · Jakarta EE 10 · JAX-WS (Metro 2.3) · Payara/GlassFish · MySQL 9
**Paquete raíz:** `ec.edu.monster` · **BD:** `ticketpremiumDB`

---

## 1. Estructura

```
SOAP_JAVA_TICKETPREMIUM_G6/
├── 01. UML/
├── 02. MER/
│   ├── 01. CONCEPTUAL/
│   ├── 02. LOGICO/
│   └── 03. FISICO/script_ticketpremium.sql      ← script DDL + seed
├── 03. BDD/                                     ← vacío a propósito (el servidor bootstrap solo)
├── 04. CLICONSOLA/                              ← Java console ✅
├── 05. CLIESCRITORIO/                           ← Swing ✅
├── 06. CLIWEB/                                  ← JSP/Servlets ✅
├── 07. CLIMOVIL/                                ← Android (pendiente)
├── 08. SERVIDOR/servidor_soap_java_federacion_gr06/   ← ✅ LISTO
└── 09. DOCUMENTACION/
```

## 2. Requisitos previos (cualquier máquina)

| Componente | Versión | Mac (Homebrew) | Windows | Linux |
|---|---|---|---|---|
| JDK | 17 | `brew install openjdk@17` | [adoptium.net](https://adoptium.net) | `sudo apt install openjdk-17-jdk` |
| MySQL Server | 8+ | `brew install mysql && brew services start mysql` | [installer MSI](https://dev.mysql.com/downloads/installer/) | `sudo apt install mysql-server` |
| Maven | 3.9+ | `brew install maven` | [maven.apache.org](https://maven.apache.org/download.cgi) | `sudo apt install maven` |
| NetBeans | 17+ | descargar `.dmg` | descargar `.exe` | descargar `.deb` |
| Payara/GlassFish | 6.x (Jakarta EE 10) | [payara.fish/downloads](https://www.payara.fish/downloads/) | idem | idem |

> **Maven descarga solo** las dependencias del `pom.xml` (Jakarta EE, MySQL Connector/J) la primera vez que se hace **Clean and Build** en NetBeans. No hace falta descargar nada manualmente.

## 3. Base de datos — automática

**No hay que crear nada manualmente.** Al desplegar el WAR, Payara invoca `BootstrapBD` (un `ServletContextListener`) que:

1. Crea `ticketpremiumDB` si no existe
2. Crea las 5 tablas (idempotente con `CREATE … IF NOT EXISTS`)
3. Inserta los 4 usuarios con sus claves (`ON DUPLICATE KEY UPDATE` → siempre vigentes)
4. Inserta 5 partidos y 20 localidades
5. Siembra 5 facturas demo (sólo si `FACTURA` está vacía)

### Qué queda en la BD

| Tabla | Filas seed | Rúbrica |
|---|---|---|
| `USUARIO` | 4 (1 admin + 3 clientes) | mejora |
| `PARTIDO_FUTBOL` | 5 | 0.5 pt |
| `LOCALIDAD_PARTIDO` | 20 | 0.5 pt |
| `FACTURA` | 5 (demo) | 0.5 pt |
| `DETALLE_FACTURA` | 5 (demo) | (parte del 0.5) |

### Credenciales (auto-sembradas por el bootstrap)

| Usuario | Contraseña | Rol |
|---|---|---|
| **monster** | **monster9** | ADMIN (puede ver reportes y todas las facturas) |
| josue | admin2002 | CLIENTE |
| mikaela | admin2002 | CLIENTE |
| elkin | admin2002 | CLIENTE |

> Detalles del bootstrap (incluyendo cómo desactivarlo y cómo resetear desde cero): [README del servidor § 6.5](08.%20SERVIDOR/servidor_soap_java_federacion_gr06/README.md).

## 4. Configurar el servidor para cualquier máquina

El `ConexionBD` lee la configuración en este orden:

1. **Variables de entorno** (en el panel del servidor de aplicaciones):
   - `TICKETPREMIUM_DB_HOST` (default `localhost`)
   - `TICKETPREMIUM_DB_PORT` (default `3306`)
   - `TICKETPREMIUM_DB_NAME` (default `ticketpremiumDB`)
   - `TICKETPREMIUM_DB_USER` (default `root`)
   - `TICKETPREMIUM_DB_PASSWORD` (default `admin2002`)
2. Archivo [`src/main/resources/db.properties`](08.%20SERVIDOR/servidor_soap_java_federacion_gr06/src/main/resources/db.properties).
3. Defaults hardcodeados de fallback.

> Así el **mismo WAR** corre en cualquier máquina sin recompilar: basta cambiar las variables de entorno del Payara.

## 5. Desplegar el servidor en NetBeans

1. **File ▸ Open Project** → seleccionar la carpeta:
   `08. SERVIDOR/servidor_soap_java_federacion_gr06`
2. NetBeans detecta el `pom.xml` y descarga dependencias (1–2 min la primera vez).
3. Click derecho proyecto ▸ **Clean and Build**.
4. Click derecho proyecto ▸ **Run** (deploya en Payara/GlassFish configurado como `pfv5ee8`).
5. Verificar el WSDL en navegador:

   ```
   http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl
   ```

## 6. Operaciones SOAP expuestas

### 6.1 Rúbrica oficial

| `@WebMethod` | Rúbrica | Descripción |
|---|---|---|
| `listarPartidosDisponibles()` | 1.0 pt | Partidos con `FECHA >= NOW()` |
| `listarLocalidadesPorPartido(codigoPartido)` | 1.0 pt | Localidades con `DISPONIBILIDAD > 0` |
| `registrarVenta(idUsuario, codigoPartido, codigoLocalidad, cantidad)` | 0.5 pt | Transacción: factura + detalle + descuento stock (IVA 15%) |
| `resumenVentasPartido(codigoPartido)` | 1.0 pt | Reporte agrupado por localidad |

### 6.2 Mejoras (auth + historial)

| `@WebMethod` | Descripción |
|---|---|
| `iniciarSesion(usuario, contrasena)` | Devuelve `{exito, mensaje, usuario{id, nombre, rol}}` |
| `misFacturas(idUsuario)` | Historial de compras del usuario |

## 7. Cálculos

```
SUBTOTAL = PRECIO_UNITARIO × CANTIDAD
IVA      = SUBTOTAL × 0.15
TOTAL    = SUBTOTAL + IVA
```

## 8. Pruebas rápidas con SoapUI

1. **New SOAP Project** → pegar el WSDL.
2. `iniciarSesion` con `usuario=monster, contrasena=monster9` → debe devolver `exito=true, rol=ADMIN, idUsuario=1`.
3. `listarPartidosDisponibles` (sin parámetros) → 5 partidos.
4. `listarLocalidadesPorPartido` con `codigoPartido=1` → 4 localidades.
5. `registrarVenta` con `idUsuario=2, codigoPartido=1, codigoLocalidad=GENERAL, cantidad=2`:
   - subtotal = `16.00`, iva = `2.40`, total = `18.40`.
6. `misFacturas` con `idUsuario=2` → devuelve la factura recién creada.
7. `resumenVentasPartido` con `codigoPartido=1` → fila `GENERAL, vendidos=2, recaudado=16.00`.

## 9. Pendientes (clientes — 3 pt rúbrica + 1 pt reporte)

| Cliente | Tecnología | Pendiente |
|---|---|---|
| 04. CLICONSOLA | Java Console | Login, listar partidos/localidades, compra, facturas, reporte |
| 05. CLIESCRITORIO | Swing | Login, listar partidos/localidades, compra, facturas, reporte |
| 06. CLIWEB | Servlet+JSP | Login, listar partidos/localidades, compra, facturas, reporte |
| 07. CLIMOVIL | Android (kSOAP2) | Activities + login + compra |

> Cada cliente debe permitir **mostrar partidos, mostrar localidades y registrar compra**. El **reporte** "Resumen de Ventas de un Partido" se puede consumir desde cualquier cliente — lo más natural es en el web.
