using Microsoft.Data.SqlClient;

namespace SERVIDOR.Persistencia
{
    public class ConexionBD
    {
        private static string ConnectionString = string.Empty;

        public static void Configure(string? connectionString)
        {
            ConnectionString = string.IsNullOrWhiteSpace(connectionString)
                ? "Server=3.239.254.34,1433;Database=EurekaBank;User Id=sa;Password=SqlAmazon2026!;TrustServerCertificate=True;"
                : connectionString;
        }

        public static SqlConnection Conectar()
        {
            if (string.IsNullOrWhiteSpace(ConnectionString))
            {
                Configure(null);
            }

            var cn = new SqlConnection(ConnectionString);
            cn.Open();
            return cn;
        }

        public static void Desconectar(SqlConnection? cn, SqlCommand? cmd = null, SqlDataReader? dr = null)
        {
            dr?.Close();
            cmd?.Dispose();
            if (cn != null && cn.State == System.Data.ConnectionState.Open)
            {
                cn.Close();
            }
            cn?.Dispose();
        }
    }
}
