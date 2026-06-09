# EurekaBank REST .NET - GR06

## Estructura
```
03. BDD/          # Scripts SQL Server (misma BD que SOAP)
04. CLICONSOLA/   # Cliente consola
05. CLIESCRITORIO/# Cliente WinForms
06. CLIWEB/       # Cliente ASP.NET Razor
07. CLIMOVIL/     # Cliente MAUI (movil)
08. SERVIDOR/     # Servidor REST API
09. DOCUMENTACION/
```

## Base de datos
Misma base de datos que el proyecto SOAP:
- Server: `.\SQLEXPRESS`
- Database: `EurekaBank`
- User: `sa` / Password: `admin123`

## Ejecutar
```
dotnet run --project "08. SERVIDOR\SERVIDOR"  # http://localhost:5010
dotnet run --project "04. CLICONSOLA\CLICONSOLA"   # Consola
dotnet run --project "05. CLIESCRITORIO\CLIESCRITORIO" # WinForms
dotnet run --project "06. CLIWEB\CLIWEB"       # http://localhost:5011
dotnet build "07. CLIMOVIL"                    # MAUI
```

## Endpoints REST
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/auth/login` | Iniciar sesion |
| GET | `/api/auth/cliente/{usuario}` | Obtener codigo cliente |
| POST | `/api/cuenta/depositar` | Depositar |
| POST | `/api/cuenta/retirar` | Retirar |
| GET | `/api/cuenta/saldo/{cuenta}` | Consultar saldo |
| POST | `/api/cuenta/transferir` | Transferir |
| GET | `/api/cuenta/cliente/{cliente}` | Listar cuentas por cliente |
| GET | `/api/cuenta/clientes` | Listar todos los clientes |
| POST | `/api/cuenta/cliente` | Registrar cliente |
| POST | `/api/cuenta` | Registrar cuenta |
| DELETE | `/api/cuenta/{cuenta}` | Eliminar cuenta |
| GET | `/api/movimiento/{cuenta}` | Listar movimientos |

## Credenciales
| Usuario | Clave | Rol |
|---------|-------|-----|
| monster | monster9 | ADMIN |
| cromero | admin2002 | Cliente 00001 |
| cvalencia | admin2002 | Cliente 00005 |
| jmarin | demo123 | Cliente 00021 |
| msalcedo | demo123 | Cliente 00022 |
| epabon | demo123 | Cliente 00023 |
| jperez | demo123 | Cliente 00024 |

## Diferencias con SOAP
- Protocolo: HTTP REST vs SOAP/XML
- Serializacion: JSON vs XML
- Puertos: 5010 (REST) vs 5000 (SOAP)
- Misma funcionalidad, misma BD, mismos clientes
