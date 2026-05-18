using Microsoft.Data.SqlClient;
using SERVIDOR.Modelo;

namespace SERVIDOR.Persistencia
{
    public class MovimientoDAO
    {
        public List<MovimientoModel> ListarPorCuenta(string codigoCuenta)
        {
            var lista = new List<MovimientoModel>();
            string sql = @"SELECT m.chr_cuencodigo, m.int_movinumero, CONVERT(varchar, m.dtt_movifecha, 23) AS fecha,
                                  m.chr_emplcodigo, m.chr_tipocodigo, t.vch_tipodescripcion, m.dec_moviimporte,
                                  ISNULL(m.chr_cuenreferencia, '') AS chr_cuenreferencia,
                                  ISNULL(m.chr_movimonori, '') AS chr_movimonori,
                                  m.dec_moviimporteori, m.dec_movitasa
                           FROM dbo.movimiento m
                           LEFT JOIN dbo.tipomovimiento t ON m.chr_tipocodigo = t.chr_tipocodigo
                           WHERE m.chr_cuencodigo = @codigo
                           ORDER BY m.dtt_movifecha DESC, m.int_movinumero DESC";

            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@codigo", codigoCuenta);
            using var dr = cmd.ExecuteReader();

            while (dr.Read())
            {
                lista.Add(new MovimientoModel
                {
                    CodigoCuenta = dr["chr_cuencodigo"].ToString() ?? string.Empty,
                    NumeroMovimiento = Convert.ToInt32(dr["int_movinumero"]),
                    FechaMovimiento = dr["fecha"].ToString() ?? string.Empty,
                    CodigoEmpleado = dr["chr_emplcodigo"].ToString() ?? string.Empty,
                    CodigoTipoMovimiento = dr["chr_tipocodigo"].ToString() ?? string.Empty,
                    TipoDescripcion = dr["vch_tipodescripcion"].ToString() ?? string.Empty,
                    ImporteMovimiento = Convert.ToDouble(dr["dec_moviimporte"]),
                    CuentaReferencia = dr["chr_cuenreferencia"].ToString() ?? string.Empty,
                    MonedaOrigen = dr["chr_movimonori"].ToString() ?? string.Empty,
                    ImporteOrigen = dr["dec_moviimporteori"] == DBNull.Value ? null : Convert.ToDouble(dr["dec_moviimporteori"]),
                    TasaAplicada = dr["dec_movitasa"] == DBNull.Value ? null : Convert.ToDouble(dr["dec_movitasa"])
                });
            }

            return lista;
        }

        public int SiguienteNumero(SqlConnection cn, string codigoCuenta)
        {
            string sql = "SELECT ISNULL(MAX(int_movinumero), 0) + 1 FROM dbo.movimiento WHERE chr_cuencodigo = @codigo";
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@codigo", codigoCuenta);
            return Convert.ToInt32(cmd.ExecuteScalar());
        }

        public void Insertar(SqlConnection cn, MovimientoModel m)
        {
            string sql = @"INSERT INTO dbo.movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo,
                              chr_tipocodigo, dec_moviimporte, chr_cuenreferencia, chr_movimonori, dec_moviimporteori, dec_movitasa)
                           VALUES (@cuenta, @numero, @fecha, @empleado, @tipo, @importe, @referencia, @monedaOri, @importeOri, @tasa)";

            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@cuenta", m.CodigoCuenta);
            cmd.Parameters.AddWithValue("@numero", m.NumeroMovimiento);
            cmd.Parameters.AddWithValue("@fecha", DateTime.Parse(m.FechaMovimiento));
            cmd.Parameters.AddWithValue("@empleado", m.CodigoEmpleado);
            cmd.Parameters.AddWithValue("@tipo", m.CodigoTipoMovimiento);
            cmd.Parameters.AddWithValue("@importe", m.ImporteMovimiento);
            cmd.Parameters.AddWithValue("@referencia", string.IsNullOrEmpty(m.CuentaReferencia) ? (object)DBNull.Value : m.CuentaReferencia);
            cmd.Parameters.AddWithValue("@monedaOri", string.IsNullOrEmpty(m.MonedaOrigen) ? (object)DBNull.Value : m.MonedaOrigen);
            cmd.Parameters.AddWithValue("@importeOri", m.ImporteOrigen.HasValue ? (object)m.ImporteOrigen.Value : DBNull.Value);
            cmd.Parameters.AddWithValue("@tasa", m.TasaAplicada.HasValue ? (object)m.TasaAplicada.Value : DBNull.Value);
            cmd.ExecuteNonQuery();
        }
    }
}
