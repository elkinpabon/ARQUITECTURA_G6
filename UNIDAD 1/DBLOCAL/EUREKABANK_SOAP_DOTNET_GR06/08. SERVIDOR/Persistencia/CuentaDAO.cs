using Microsoft.Data.SqlClient;
using SERVIDOR.Modelo;

namespace SERVIDOR.Persistencia
{
    public class CuentaDAO
    {
        public Dictionary<string, object>? ObtenerParaActualizar(SqlConnection cn, string codigoCuenta)
        {
            string sql = "SELECT chr_cuencodigo, chr_monecodigo, chr_cliecodigo, dec_cuensaldo, int_cuencontmov, vch_cuenestado FROM dbo.cuenta WHERE chr_cuencodigo = @codigo FOR UPDATE";
            // SQL Server no tiene FOR UPDATE, usamos UPDLOCK, ROWLOCK
            sql = "SELECT chr_cuencodigo, chr_monecodigo, chr_cliecodigo, dec_cuensaldo, int_cuencontmov, vch_cuenestado FROM dbo.cuenta WITH (UPDLOCK, ROWLOCK) WHERE chr_cuencodigo = @codigo";

            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@codigo", codigoCuenta);
            using var dr = cmd.ExecuteReader();

            if (dr.Read())
            {
                return new Dictionary<string, object>
                {
                    ["chr_cuencodigo"] = dr["chr_cuencodigo"].ToString()!,
                    ["chr_monecodigo"] = dr["chr_monecodigo"].ToString()!,
                    ["chr_cliecodigo"] = dr["chr_cliecodigo"].ToString()!,
                    ["dec_cuensaldo"] = Convert.ToDouble(dr["dec_cuensaldo"]),
                    ["int_cuencontmov"] = Convert.ToInt32(dr["int_cuencontmov"]),
                    ["vch_cuenestado"] = dr["vch_cuenestado"].ToString()!
                };
            }

            return null;
        }

        public void ActualizarSaldo(SqlConnection cn, string codigoCuenta, double delta)
        {
            string sql = "UPDATE dbo.cuenta SET dec_cuensaldo = dec_cuensaldo + @delta, int_cuencontmov = int_cuencontmov + 1 WHERE chr_cuencodigo = @codigo";
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@delta", delta);
            cmd.Parameters.AddWithValue("@codigo", codigoCuenta);
            cmd.ExecuteNonQuery();
        }

        public Dictionary<string, object>? ObtenerPorCodigo(string codigoCuenta)
        {
            string sql = "SELECT chr_cuencodigo, chr_monecodigo, chr_cliecodigo, dec_cuensaldo, int_cuencontmov, vch_cuenestado FROM dbo.cuenta WHERE chr_cuencodigo = @codigo";
            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@codigo", codigoCuenta);
            using var dr = cmd.ExecuteReader();

            if (dr.Read())
            {
                return new Dictionary<string, object>
                {
                    ["chr_cuencodigo"] = dr["chr_cuencodigo"].ToString()!,
                    ["chr_monecodigo"] = dr["chr_monecodigo"].ToString()!,
                    ["chr_cliecodigo"] = dr["chr_cliecodigo"].ToString()!,
                    ["dec_cuensaldo"] = Convert.ToDouble(dr["dec_cuensaldo"]),
                    ["int_cuencontmov"] = Convert.ToInt32(dr["int_cuencontmov"]),
                    ["vch_cuenestado"] = dr["vch_cuenestado"].ToString()!
                };
            }

            return null;
        }

        public List<CuentaResumen> ListarPorCliente(string criterio)
        {
            var lista = new List<CuentaResumen>();
            string sql = @"SELECT c.chr_cuencodigo, c.chr_monecodigo, c.dec_cuensaldo, c.vch_cuenestado, c.chr_cliecodigo,
                                  cl.vch_cliepaterno + ' ' + cl.vch_cliematerno + ' ' + cl.vch_clienombre AS nombreCliente
                           FROM dbo.cuenta c
                           INNER JOIN dbo.cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo
                           WHERE c.chr_cliecodigo = @criterio OR cl.chr_cliedni = @criterio
                           ORDER BY c.chr_cuencodigo";

            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@criterio", criterio);
            using var dr = cmd.ExecuteReader();

            while (dr.Read())
            {
                lista.Add(new CuentaResumen
                {
                    CodigoCuenta = dr["chr_cuencodigo"].ToString() ?? string.Empty,
                    Moneda = dr["chr_monecodigo"].ToString() ?? string.Empty,
                    Saldo = Convert.ToDouble(dr["dec_cuensaldo"]),
                    Estado = dr["vch_cuenestado"].ToString() ?? string.Empty,
                    CodigoCliente = dr["chr_cliecodigo"].ToString() ?? string.Empty,
                    NombreCliente = dr["nombreCliente"].ToString() ?? string.Empty
                });
            }

            return lista;
        }

        public string Insertar(string clienteCodigo, string moneda)
        {
            string sql = "SELECT ISNULL(MAX(CAST(chr_cuencodigo AS INT)), 0) + 1 FROM dbo.cuenta";
            string nuevoCodigo;

            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            var result = cmd.ExecuteScalar();
            int codigo = Convert.ToInt32(result);
            nuevoCodigo = codigo.ToString("D8");

            sql = @"INSERT INTO dbo.cuenta (chr_cuencodigo, chr_monecodigo, chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo,
                       dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, int_cuencontmov, chr_cuenclave)
                    VALUES (@codigo, @moneda, '001', '9999', @cliente, 0.0, GETDATE(), 'ACTIVO', 0, '123456')";

            using var cmd2 = new SqlCommand(sql, cn);
            cmd2.Parameters.AddWithValue("@codigo", nuevoCodigo);
            cmd2.Parameters.AddWithValue("@moneda", moneda);
            cmd2.Parameters.AddWithValue("@cliente", clienteCodigo);
            cmd2.ExecuteNonQuery();

            return nuevoCodigo;
        }

        public bool Existe(string codigoCuenta)
        {
            string sql = "SELECT COUNT(1) FROM dbo.cuenta WHERE chr_cuencodigo = @codigo";
            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@codigo", codigoCuenta);
            return Convert.ToInt32(cmd.ExecuteScalar()) > 0;
        }

        public void Eliminar(string codigoCuenta)
        {
            using var cn = ConexionBD.Conectar();
            using var tx = cn.BeginTransaction();

            try
            {
                using var cmd1 = new SqlCommand("DELETE FROM dbo.movimiento WHERE chr_cuencodigo = @codigo", cn, tx);
                cmd1.Parameters.AddWithValue("@codigo", codigoCuenta);
                cmd1.ExecuteNonQuery();

                using var cmd2 = new SqlCommand("DELETE FROM dbo.cuenta WHERE chr_cuencodigo = @codigo", cn, tx);
                cmd2.Parameters.AddWithValue("@codigo", codigoCuenta);
                cmd2.ExecuteNonQuery();

                tx.Commit();
            }
            catch
            {
                tx.Rollback();
                throw;
            }
        }
    }
}
