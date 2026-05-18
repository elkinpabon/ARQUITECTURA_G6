using Microsoft.Data.SqlClient;
using SERVIDOR.Models;

namespace SERVIDOR.Data;

public class ClienteDAO
{
    public List<ClienteResumen> ListarTodos()
    {
        const string sql = """
            SELECT chr_cliecodigo, chr_cliedni,
                   vch_clienombre, vch_cliepaterno, vch_cliematerno
            FROM cliente ORDER BY vch_cliepaterno, vch_cliematerno, vch_clienombre
            """;

        var lista = new List<ClienteResumen>();
        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(sql, cn);
        cn.Open();
        using var rs = ps.ExecuteReader();
        while (rs.Read())
        {
            lista.Add(new ClienteResumen
            {
                Codigo = rs.GetString(0),
                Dni = rs.GetString(1),
                Nombre = $"{rs.GetString(2)} {rs.GetString(3)} {rs.GetString(4)}".Trim()
            });
        }
        return lista;
    }

    public string Insertar(string paterno, string materno, string nombre,
                           string dni, string ciudad, string direccion,
                           string telefono, string email)
    {
        using var cn = ConexionBD.CrearConexion();
        cn.Open();

        string codigo;
        using (var pm = new SqlCommand(
            "SELECT RIGHT('00000' + CAST(COALESCE(MAX(CAST(chr_cliecodigo AS INT)), 0) + 1 AS VARCHAR(5)), 5) FROM cliente", cn))
        {
            codigo = pm.ExecuteScalar()!.ToString()!;
        }

        const string sql = """
            INSERT INTO cliente (chr_cliecodigo, vch_cliepaterno,
                   vch_cliematerno, vch_clienombre, chr_cliedni, vch_clieciudad,
                   vch_cliedireccion, vch_clietelefono, vch_clieemail)
            VALUES (@codigo, @paterno, @materno, @nombre, @dni, @ciudad, @direccion, @telefono, @email)
            """;
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@codigo", codigo);
        ps.Parameters.AddWithValue("@paterno", paterno);
        ps.Parameters.AddWithValue("@materno", materno);
        ps.Parameters.AddWithValue("@nombre", nombre);
        ps.Parameters.AddWithValue("@dni", dni);
        ps.Parameters.AddWithValue("@ciudad", ciudad);
        ps.Parameters.AddWithValue("@direccion", direccion);
        ps.Parameters.AddWithValue("@telefono", telefono);
        ps.Parameters.AddWithValue("@email", email);
        ps.ExecuteNonQuery();
        return codigo;
    }

    public bool Existe(string codigo)
    {
        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(
            "SELECT 1 FROM cliente WHERE chr_cliecodigo = @codigo", cn);
        ps.Parameters.AddWithValue("@codigo", codigo);
        cn.Open();
        return ps.ExecuteScalar() != null;
    }
}
