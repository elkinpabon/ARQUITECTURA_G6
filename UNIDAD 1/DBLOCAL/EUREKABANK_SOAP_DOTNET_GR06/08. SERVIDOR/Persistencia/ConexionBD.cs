using Microsoft.Data.SqlClient;

namespace SERVIDOR.Persistencia
{
    public class ConexionBD
    {
        private static readonly string ConnectionString = "Server=.\\SQLEXPRESS;Database=EurekaBank;User Id=sa;Password=admin123;TrustServerCertificate=True;";

        public static SqlConnection Conectar()
        {
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
