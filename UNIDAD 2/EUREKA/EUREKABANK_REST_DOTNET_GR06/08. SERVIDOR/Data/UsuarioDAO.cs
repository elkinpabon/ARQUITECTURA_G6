using Microsoft.Data.SqlClient;
using SERVIDOR.Models;

namespace SERVIDOR.Data;

public class UsuarioDAO
{
    public Usuario? BuscarPorUsuario(string usuario)
    {
        const string sql = """
            SELECT chr_emplcodigo, vch_emplusuario, vch_emplclave,
                   vch_emplestado, chr_cliecodigo
            FROM usuario WHERE vch_emplusuario = @usuario
            """;

        using var cn = ConexionBD.CrearConexion();
        using var ps = new SqlCommand(sql, cn);
        ps.Parameters.AddWithValue("@usuario", usuario);
        cn.Open();
        using var rs = ps.ExecuteReader();
        if (rs.Read())
        {
            return new Usuario
            {
                CodigoEmpleado = rs.GetString(0),
                UsuarioNombre = rs.GetString(1),
                Clave = rs.GetString(2),
                Estado = rs.GetString(3),
                ClienteCodigo = rs.IsDBNull(4) ? null : rs.GetString(4)
            };
        }
        return null;
    }
}
