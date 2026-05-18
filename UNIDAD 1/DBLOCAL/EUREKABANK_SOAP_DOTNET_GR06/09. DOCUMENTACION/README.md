# EurekaBank SOAP .NET - GR06

## Estructura
```
03. BDD/          # Scripts SQL Server
04. CLICONSOLA/   # Cliente consola
05. CLIESCRITORIO/# Cliente WinForms
06. CLIWEB/       # Cliente ASP.NET Razor
07. CLIMOVIL/     # Cliente MAUI (movil)
08. SERVIDOR/     # Servidor SOAP (SoapCore)
09. DOCUMENTACION/
```

## Base de datos
- Server: `.\SQLEXPRESS`
- Database: `EurekaBank`
- User: `sa` / Password: `admin123`

Ejecutar en orden:
```
sqlcmd -S ".\SQLEXPRESS" -U "sa" -P "admin123" -i "03. BDD\01_estructura.sql"
sqlcmd -S ".\SQLEXPRESS" -U "sa" -P "admin123" -d "EurekaBank" -i "03. BDD\02_datos.sql"
sqlcmd -S ".\SQLEXPRESS" -U "sa" -P "admin123" -d "EurekaBank" -i "03. BDD\03_gr06_seed.sql"
```

## Ejecutar
```
dotnet run --project "08. SERVIDOR"     # http://localhost:5000
dotnet run --project "04. CLICONSOLA"   # Consola
dotnet run --project "05. CLIESCRITORIO" # WinForms
dotnet run --project "06. CLIWEB"       # http://localhost:5001
dotnet build "07. CLIMOVIL"             # MAUI (deploy a dispositivo/emulador)
```

## Endpoints SOAP
- `http://localhost:5000/WSLogin.asmx?wsdl`
- `http://localhost:5000/WSCuenta.asmx?wsdl`
- `http://localhost:5000/WSMovimiento.asmx?wsdl`

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
