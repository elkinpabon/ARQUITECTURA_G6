using Microsoft.Data.SqlClient;

namespace SERVIDOR.Data;

public class TasaCambioDAO
{
    public double Tasa(SqlConnection cn, string origen, string destino)
    {
        if (string.IsNullOrEmpty(origen) || string.IsNullOrEmpty(destino) || origen == destino)
            return 1.0;

        const string sql = """
            SELECT dec_tasa FROM tasacambio
            WHERE chr_origen = @origen AND chr_destino = @destino
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@origen", origen);
        ps.Parameters.AddWithValue("@destino", destino);
        using var rs = ps.ExecuteReader();
        if (rs.Read())
            return rs.GetDouble(0);

        throw new Exception($"No hay tasa de cambio configurada de {origen} a {destino}.");
    }
}
