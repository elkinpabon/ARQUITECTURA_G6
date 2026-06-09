# Documentación — Conexión a la Base de Datos en AWS (EUREKABANK GR06)

**Materia:** Arquitectura de Software · Grupo 6 (ESPE)
**Unidad 2** — Bases de datos alojadas en una máquina virtual de **Amazon Web Services (AWS / EC2)**.

---

## 1. Servidor de base de datos en AWS

Todas las bases viven en **una sola instancia EC2 de AWS** con IP pública **`3.239.254.34`**, que ejecuta **dos motores de base de datos** a la vez:

| Motor | Puerto | Usado por | Base de datos |
|-------|--------|-----------|---------------|
| **MySQL 9.x** | `3306` | Proyectos **Java** (SOAP / REST) | `eurekasopajava`, `eurekarestjava` |
| **Microsoft SQL Server 2022 Express** | `1433` | Proyectos **.NET** (SOAP / REST) | `EurekaBank`, `EurekaBankRest` |

> Requisito de red: el **Security Group** de la instancia EC2 debe permitir el tráfico entrante a los puertos `3306` (MySQL) y `1433` (SQL Server) desde la IP del cliente. El motor debe escuchar en todas las interfaces (`0.0.0.0`), no solo en `localhost`.

A continuación se documentan **dos ejemplos**: el servidor **SOAP en Java** (MySQL) y el servidor **SOAP en .NET** (SQL Server).

---

## 2. Ejemplo A — SOAP en **Java** (MySQL en AWS)

### 2.1 Datos de conexión

| Parámetro | Valor |
|-----------|-------|
| Motor | MySQL |
| Host / Puerto | `3.239.254.34` : `3306` |
| Base de datos | `eurekasopajava` |
| Usuario | `admin` |
| Contraseña | `SqlAmazon2026!` |
| Driver JDBC | `com.mysql.cj.jdbc.Driver` |

### 2.2 Código que se conecta con la base de datos

**Archivo:** `EUREKABANK_SOAP_JAVA_GR06/08. SERVIDOR/eurekabank_soap_java_gr06/src/java/ec/edu/monster/persistencia/ConexionBD.java`

```java
public final class ConexionBD {

    // Datos de conexion al MySQL en AWS (maquina virtual Amazon, BD eurekasopajava).
    private static final String URL =
            "jdbc:mysql://3.239.254.34:3306/eurekasopajava"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "admin";
    private static final String CLAVE = "SqlAmazon2026!";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");   // Carga del driver JDBC de MySQL
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /** Abre y devuelve una nueva conexion a la base de datos en AWS. */
    public static Connection conectar() throws SQLException {
        Connection cn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        return cn;
    }
}
```

La cadena **`jdbc:mysql://3.239.254.34:3306/eurekasopajava`** es la que apunta directamente a la máquina de AWS. Todos los DAO del servidor abren su conexión llamando a `ConexionBD.conectar()`.

### 2.3 Cómo se crea la base de datos (MySQL)

**Script:** `EUREKABANK_SOAP_JAVA_GR06/03. BDD/01_estructura.sql`

```sql
CREATE DATABASE IF NOT EXISTS eurekasopajava
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE eurekasopajava;
-- ... (creación de tablas: cliente, cuenta, empleado, usuario, movimiento, etc.)
```

**Carga de los scripts contra AWS** (cliente `mysql.exe` desde la PC):

```powershell
$mysql = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
$H = "3.239.254.34"; $U = "admin"; $P = "SqlAmazon2026!"

# 01-03 ya traen CREATE DATABASE / USE
& $mysql -h $H -u $U "-p$P" -e "source 01_estructura.sql"
& $mysql -h $H -u $U "-p$P" -e "source 02_datos.sql"
& $mysql -h $H -u $U "-p$P" -e "source 03_gr06_seed.sql"

# 04-07 sin USE -> se indica la base explícitamente
& $mysql -h $H -u $U "-p$P" eurekasopajava -e "source 04_gr06_rol.sql"
```

---

## 3. Ejemplo B — SOAP en **.NET** (SQL Server en AWS)

### 3.1 Datos de conexión

| Parámetro | Valor |
|-----------|-------|
| Motor | Microsoft SQL Server 2022 Express |
| Host / Puerto | `3.239.254.34` , `1433` |
| Base de datos | `EurekaBank` |
| Usuario | `sa` |
| Contraseña | `SqlAmazon2026!` |
| Proveedor | `Microsoft.Data.SqlClient` |

### 3.2 Código que se conecta con la base de datos

**Archivo:** `EUREKABANK_SOAP_DOTNET_GR06/08. SERVIDOR/Persistencia/ConexionBD.cs`

```csharp
using Microsoft.Data.SqlClient;

public class ConexionBD
{
    public static void Configure(string? connectionString)
    {
        ConnectionString = string.IsNullOrWhiteSpace(connectionString)
            // Cadena de conexion al SQL Server en AWS (BD EurekaBank).
            ? "Server=3.239.254.34,1433;Database=EurekaBank;User Id=sa;Password=SqlAmazon2026!;TrustServerCertificate=True;"
            : connectionString;
    }

    public static SqlConnection Conectar()
    {
        var cn = new SqlConnection(ConnectionString);
        cn.Open();          // Abre la conexion contra AWS
        return cn;
    }
}
```

**Archivo:** `EUREKABANK_SOAP_DOTNET_GR06/08. SERVIDOR/appsettings.json` (la cadena que realmente usa el servidor al arrancar)

```json
"ConnectionStrings": {
  "EurekaBank": "Server=3.239.254.34,1433;Database=EurekaBank;User Id=sa;Password=SqlAmazon2026!;TrustServerCertificate=True;"
}
```

> En `Program.cs`, el servidor lee esa cadena con
> `ConexionBD.Configure(builder.Configuration.GetConnectionString("EurekaBank"))`.
> El parámetro **`Server=3.239.254.34,1433`** (host,puerto separados por coma, sintaxis de SQL Server) apunta a la máquina de AWS.

### 3.3 Cómo se crea la base de datos (SQL Server / T-SQL)

**Script:** `EUREKABANK_SOAP_DOTNET_GR06/03. BDD/01_estructura.sql`

```sql
USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'EurekaBank')
BEGIN
    CREATE DATABASE EurekaBank;
END
GO

USE EurekaBank;
GO
-- ... (creación de tablas con dbo.cliente, dbo.cuenta, dbo.empleado, etc.)
```

**Carga de los scripts contra AWS** (cliente `sqlcmd` desde la PC):

```powershell
$sqlcmd = "C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\SQLCMD.EXE"
$S = "3.239.254.34,1433"; $U = "sa"; $P = "SqlAmazon2026!"

& $sqlcmd -S $S -U $U -P $P -i "01_estructura.sql"   # crea la BD EurekaBank
& $sqlcmd -S $S -U $U -P $P -i "02_datos.sql"        # inserta datos base
& $sqlcmd -S $S -U $U -P $P -i "03_gr06_seed.sql"    # usuarios de prueba (monster/monster9)
```

---

## 4. Resumen de la arquitectura de conexión

```
                         Instancia EC2 en AWS  (IP publica 3.239.254.34)
                         +-------------------------------------------------+
   Servidor SOAP Java ---|--> MySQL  :3306  -> BD eurekasopajava           |
   (ConexionBD.java)     |                                                 |
   Servidor SOAP .NET ---|--> SQL Server :1433 -> BD EurekaBank            |
   (ConexionBD.cs)       |                                                 |
                         +-------------------------------------------------+
```

- Cada **servidor** abre la conexión a AWS mediante su clase `ConexionBD` (Java) / `ConexionBD.cs` (.NET).
- Los **clientes** (consola, escritorio, web, móvil) **no** se conectan a la base directamente: hablan con su servidor por SOAP/REST, y es el servidor quien consulta la base en AWS. Por eso migrar la BD a la nube es transparente para los clientes.
- Las credenciales de la BD en AWS son **`admin` / `SqlAmazon2026!`** (MySQL) y **`sa` / `SqlAmazon2026!`** (SQL Server).

---

> **Para incluir capturas de pantalla en el informe:** abre en tu IDE los archivos
> `ConexionBD.java` y `ConexionBD.cs` (rutas indicadas arriba) y captura las líneas de la
> cadena de conexión; y abre los `01_estructura.sql` para capturar el `CREATE DATABASE`.
