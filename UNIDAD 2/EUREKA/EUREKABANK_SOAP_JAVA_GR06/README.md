# EUREKABANK_SOAP_JAVA_GR06

Sistema bancario SOAP (Java / Jakarta JAX-WS) — ESPE Arquitectura GR06.
Servidor + 4 clientes, base de datos MySQL local `eurekabank`.

## Estructura

| Carpeta | Contenido |
|---|---|
| 01. UML | Diagramas UML |
| 02. MER | Modelo Entidad-Relación (conceptual / lógico / físico) |
| 03. BDD | Scripts SQL (estructura + datos) |
| 04. CLICONSOLA | Cliente consola Java (Maven) |
| 05. CLIESCRITORIO | Cliente escritorio Swing (Maven) |
| 06. CLIWEB | Cliente web Node.js + Express |
| 07. CLIMOVIL | Cliente móvil Android |
| 08. SERVIDOR | Servidor SOAP JAX-WS (Maven, Jakarta EE 10) |
| 09. DOCUMENTACION | Documentación del proyecto |

## Base de datos local

- Motor: MySQL 9.6 (Homebrew) — `localhost:3306`
- Usuario: `root` / Contraseña: `admin2002`
- Base: `eurekabank`
- Carga: `mysql -u root -padmin2002 < "03. BDD/01_estructura.sql"` y luego `02_datos.sql`

## Arquitectura del servidor (MVC)

```
controlador (ws)         -> clases @WebService (fachada SOAP)
servicio                 -> logica de negocio (transacciones, validaciones)
persistencia (dao)       -> acceso a datos
modelo                   -> DTOs / POJOs
persistencia.ConexionBD  -> UNICA clase de conexion (conectar / desconectar)
```
