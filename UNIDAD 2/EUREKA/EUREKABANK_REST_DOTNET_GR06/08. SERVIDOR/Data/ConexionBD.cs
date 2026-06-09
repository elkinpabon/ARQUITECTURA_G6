using Microsoft.Data.SqlClient;

namespace SERVIDOR.Data;

public static class ConexionBD
{
    private const string ConnectionString =
        "Server=3.239.254.34,1433;Database=EurekaBankRest;User Id=sa;Password=SqlAmazon2026!;TrustServerCertificate=True;";

    public static SqlConnection CrearConexion() => new(ConnectionString);
}
