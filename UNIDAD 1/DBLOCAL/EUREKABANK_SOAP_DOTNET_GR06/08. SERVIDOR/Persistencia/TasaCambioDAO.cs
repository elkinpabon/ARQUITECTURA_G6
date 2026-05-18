using Microsoft.Data.SqlClient;

namespace SERVIDOR.Persistencia
{
    public class TasaCambioDAO
    {
        public double Tasa(SqlConnection cn, string origen, string destino)
        {
            if (origen == destino) return 1.0;

            string sql = "SELECT dec_tasa FROM dbo.tasacambio WHERE chr_origen = @origen AND chr_destino = @destino";
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@origen", origen);
            cmd.Parameters.AddWithValue("@destino", destino);

            var result = cmd.ExecuteScalar();
            if (result != null && result != DBNull.Value)
            {
                return Convert.ToDouble(result);
            }

            return 1.0;
        }
    }
}
