using Microsoft.Data.SqlClient;
using SERVIDOR.Models;

namespace SERVIDOR.Data;

public class CuentaDAO
{
    public Dictionary<string, object>? ObtenerParaActualizar(SqlConnection cn, string codigoCuenta)
    {
        const string sql = """
            SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo, vch_cuenestado,
                   int_cuencontmov, chr_cuenclave
            FROM cuenta WITH (UPDLOCK, ROWLOCK) WHERE chr_cuencodigo = @codigo
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@codigo", codigoCuenta);
        using var rs = ps.ExecuteReader();
        if (rs.Read())
        {
            return new Dictionary<string, object>
            {
                ["chr_cuencodigo"] = rs.GetString(0),
                ["chr_monecodigo"] = rs.GetString(1),
                ["dec_cuensaldo"] = rs.GetDouble(2),
                ["vch_cuenestado"] = rs.GetString(3),
                ["int_cuencontmov"] = rs.GetInt32(4),
                ["chr_cuenclave"] = rs.GetString(5)
            };
        }
        return null;
    }

    public int ActualizarSaldo(SqlConnection cn, string codigoCuenta, double delta)
    {
        const string sql = """
            UPDATE cuenta
            SET dec_cuensaldo = dec_cuensaldo + @delta,
                int_cuencontmov = int_cuencontmov + 1
            WHERE chr_cuencodigo = @codigo
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@delta", delta);
        ps.Parameters.AddWithValue("@codigo", codigoCuenta);
        return ps.ExecuteNonQuery();
    }

    public Dictionary<string, object>? ObtenerPorCodigo(string codigoCuenta)
    {
        const string sql = """
            SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo, vch_cuenestado,
                   int_cuencontmov, chr_cuenclave
            FROM cuenta WHERE chr_cuencodigo = @codigo
            """;
        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@codigo", codigoCuenta);
        cn.Open();
        using var rs = ps.ExecuteReader();
        if (rs.Read())
        {
            return new Dictionary<string, object>
            {
                ["chr_cuencodigo"] = rs.GetString(0),
                ["chr_monecodigo"] = rs.GetString(1),
                ["dec_cuensaldo"] = rs.GetDouble(2),
                ["vch_cuenestado"] = rs.GetString(3),
                ["int_cuencontmov"] = rs.GetInt32(4),
                ["chr_cuenclave"] = rs.GetString(5)
            };
        }
        return null;
    }

    public List<CuentaResumen> ListarPorCliente(string criterio)
    {
        const string sql = """
            SELECT cu.chr_cuencodigo, cu.chr_monecodigo, cu.dec_cuensaldo,
                   cu.vch_cuenestado, cl.chr_cliecodigo,
                   cl.vch_cliepaterno, cl.vch_cliematerno, cl.vch_clienombre
            FROM cuenta cu
            JOIN cliente cl ON cl.chr_cliecodigo = cu.chr_cliecodigo
            WHERE cl.chr_cliecodigo = @criterio OR cl.chr_cliedni = @criterio
            ORDER BY cu.chr_cuencodigo
            """;
        var lista = new List<CuentaResumen>();
        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@criterio", criterio);
        cn.Open();
        using var rs = ps.ExecuteReader();
        while (rs.Read())
        {
            lista.Add(new CuentaResumen
            {
                CodigoCuenta = rs.GetString(0),
                Moneda = rs.GetString(1),
                Saldo = rs.GetDouble(2),
                Estado = rs.GetString(3),
                CodigoCliente = rs.GetString(4),
                NombreCliente = $"{rs.GetString(7)} {rs.GetString(5)} {rs.GetString(6)}".Trim()
            });
        }
        return lista;
    }

    public string Insertar(string clienteCodigo, string moneda)
    {
        using var cn = ConexionBD.CrearConexion();
        cn.Open();

        string codigo;
        using (var pm = new SqlCommand(
            "SELECT RIGHT('00000000' + CAST(COALESCE(MAX(CAST(chr_cuencodigo AS INT)), 0) + 1 AS VARCHAR(8)), 8) FROM cuenta", cn))
        {
            codigo = pm.ExecuteScalar()!.ToString()!;
        }

        const string sql = """
            INSERT INTO cuenta (chr_cuencodigo, chr_monecodigo,
                   chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo,
                   dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado,
                   int_cuencontmov, chr_cuenclave)
            VALUES (@codigo, @moneda, '001', '0001', @cliente, 0.00, CAST(GETDATE() AS DATE), 'ACTIVO', 0, '123456')
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@codigo", codigo);
        ps.Parameters.AddWithValue("@moneda", moneda);
        ps.Parameters.AddWithValue("@cliente", clienteCodigo);
        ps.ExecuteNonQuery();
        return codigo;
    }

    public bool Existe(string codigoCuenta)
    {
        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(
            "SELECT 1 FROM cuenta WHERE chr_cuencodigo = @codigo", cn);
        ps.Parameters.AddWithValue("@codigo", codigoCuenta);
        cn.Open();
        return ps.ExecuteScalar() != null;
    }

    public int Eliminar(string codigoCuenta)
    {
        using var cn = ConexionBD.CrearConexion();
        cn.Open();
        using var tx = cn.BeginTransaction();
        try
        {
            using (var pm = new SqlCommand(
                "DELETE FROM movimiento WHERE chr_cuencodigo = @codigo", cn, tx))
            {
                pm.Parameters.AddWithValue("@codigo", codigoCuenta);
                pm.ExecuteNonQuery();
            }
            int filas;
            using (var pc = new SqlCommand(
                "DELETE FROM cuenta WHERE chr_cuencodigo = @codigo", cn, tx))
            {
                pc.Parameters.AddWithValue("@codigo", codigoCuenta);
                filas = pc.ExecuteNonQuery();
            }
            tx.Commit();
            return filas;
        }
        catch
        {
            tx.Rollback();
            throw;
        }
    }
}
