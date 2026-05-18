using Microsoft.Data.SqlClient;
using SERVIDOR.Models;

namespace SERVIDOR.Data;

public class MovimientoDAO
{
    public List<MovimientoModel> ListarPorCuenta(string codigoCuenta)
    {
        const string sql = """
            SELECT m.chr_cuencodigo, m.int_movinumero, m.dtt_movifecha,
                   m.chr_emplcodigo, m.chr_tipocodigo, m.dec_moviimporte,
                   m.chr_cuenreferencia, m.chr_movimonori, m.dec_moviimporteori,
                   m.dec_movitasa, t.vch_tipodescripcion AS tipoDescripcion
            FROM movimiento m
            INNER JOIN tipomovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo
            WHERE m.chr_cuencodigo = @codigo
            ORDER BY m.dtt_movifecha DESC, m.int_movinumero DESC
            """;
        var lista = new List<MovimientoModel>();
        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@codigo", codigoCuenta);
        cn.Open();
        using var rs = ps.ExecuteReader();
        while (rs.Read())
        {
            var m = new MovimientoModel
            {
                CodigoCuenta = rs.GetString(0),
                NumeroMovimiento = rs.GetInt32(1),
                FechaMovimiento = rs.GetDateTime(2).ToString("yyyy-MM-dd"),
                CodigoEmpleado = rs.GetString(3),
                CodigoTipoMovimiento = rs.GetString(4),
                TipoDescripcion = rs.GetString(10),
                ImporteMovimiento = rs.GetDouble(5),
                CuentaReferencia = rs.IsDBNull(6) ? null : rs.GetString(6),
                MonedaOrigen = rs.IsDBNull(7) ? null : rs.GetString(7)
            };
            if (!rs.IsDBNull(8))
                m.ImporteOrigen = rs.GetDouble(8);
            if (!rs.IsDBNull(9))
                m.TasaAplicada = rs.GetDouble(9);
            lista.Add(m);
        }
        return lista;
    }

    public int SiguienteNumero(SqlConnection cn, string codigoCuenta)
    {
        const string sql = """
            SELECT COALESCE(MAX(int_movinumero), 0) + 1 AS sig
            FROM movimiento WHERE chr_cuencodigo = @codigo
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@codigo", codigoCuenta);
        return (int)ps.ExecuteScalar()!;
    }

    public void Insertar(SqlConnection cn, MovimientoModel m)
    {
        const string sql = """
            INSERT INTO movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha,
                   chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia,
                   chr_movimonori, dec_moviimporteori, dec_movitasa)
            VALUES (@cuenta, @numero, @fecha, @empleado, @tipo, @importe, @referencia,
                    @monedaOrigen, @importeOrigen, @tasa)
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@cuenta", m.CodigoCuenta);
        ps.Parameters.AddWithValue("@numero", m.NumeroMovimiento);
        ps.Parameters.AddWithValue("@fecha", DateTime.Parse(m.FechaMovimiento));
        ps.Parameters.AddWithValue("@empleado", m.CodigoEmpleado);
        ps.Parameters.AddWithValue("@tipo", m.CodigoTipoMovimiento);
        ps.Parameters.AddWithValue("@importe", m.ImporteMovimiento);
        ps.Parameters.AddWithValue("@referencia",
            string.IsNullOrWhiteSpace(m.CuentaReferencia)
                ? DBNull.Value
                : m.CuentaReferencia);
        if (m.MonedaOrigen == null)
        {
            ps.Parameters.AddWithValue("@monedaOrigen", DBNull.Value);
            ps.Parameters.AddWithValue("@importeOrigen", DBNull.Value);
            ps.Parameters.AddWithValue("@tasa", DBNull.Value);
        }
        else
        {
            ps.Parameters.AddWithValue("@monedaOrigen", m.MonedaOrigen);
            ps.Parameters.AddWithValue("@importeOrigen", (object)m.ImporteOrigen!);
            ps.Parameters.AddWithValue("@tasa", (object)m.TasaAplicada!);
        }
        ps.ExecuteNonQuery();
    }
}
