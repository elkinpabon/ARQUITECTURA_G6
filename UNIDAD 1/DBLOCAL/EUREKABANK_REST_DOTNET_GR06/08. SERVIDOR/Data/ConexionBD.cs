using Microsoft.Data.SqlClient;

namespace SERVIDOR.Data;

public static class ConexionBD
{
    private const string ConnectionString =
        "Server=.\\SQLEXPRESS;Database=EurekaBank;User Id=sa;Password=admin123;TrustServerCertificate=True;";

    public static SqlConnection CrearConexion() => new(ConnectionString);
}
