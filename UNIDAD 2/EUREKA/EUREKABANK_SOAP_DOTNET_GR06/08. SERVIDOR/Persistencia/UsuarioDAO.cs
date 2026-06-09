using Microsoft.Data.SqlClient;
using SERVIDOR.Modelo;

namespace SERVIDOR.Persistencia
{
    public class UsuarioDAO
    {
        public Usuario? BuscarPorUsuario(string usuario)
        {
            Usuario? u = null;
            string sql = "SELECT chr_emplcodigo, vch_emplusuario, vch_emplclave, vch_emplestado, chr_cliecodigo FROM dbo.usuario WHERE vch_emplusuario = @usuario";

            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@usuario", usuario);

            using var dr = cmd.ExecuteReader();
            if (dr.Read())
            {
                u = new Usuario
                {
                    CodigoEmpleado = dr["chr_emplcodigo"].ToString() ?? string.Empty,
                    UsuarioNombre = dr["vch_emplusuario"].ToString() ?? string.Empty,
                    Clave = dr["vch_emplclave"].ToString() ?? string.Empty,
                    Estado = dr["vch_emplestado"].ToString() ?? string.Empty,
                    ClienteCodigo = dr["chr_cliecodigo"] == DBNull.Value ? string.Empty : dr["chr_cliecodigo"].ToString()!
                };
            }

            return u;
        }
    }
}
