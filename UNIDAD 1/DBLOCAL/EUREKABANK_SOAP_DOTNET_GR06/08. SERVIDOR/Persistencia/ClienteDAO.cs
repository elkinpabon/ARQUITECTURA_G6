using Microsoft.Data.SqlClient;
using SERVIDOR.Modelo;

namespace SERVIDOR.Persistencia
{
    public class ClienteDAO
    {
        public List<ClienteResumen> ListarTodos()
        {
            var lista = new List<ClienteResumen>();
            string sql = "SELECT chr_cliecodigo, chr_cliedni, vch_clienombre FROM dbo.cliente ORDER BY chr_cliecodigo";

            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            using var dr = cmd.ExecuteReader();

            while (dr.Read())
            {
                lista.Add(new ClienteResumen
                {
                    Codigo = dr["chr_cliecodigo"].ToString() ?? string.Empty,
                    Dni = dr["chr_cliedni"].ToString() ?? string.Empty,
                    Nombre = dr["vch_clienombre"].ToString() ?? string.Empty
                });
            }

            return lista;
        }

        public string Insertar(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
        {
            EsquemaBD.Asegurar();
            dni = NormalizarDni(dni);

            string sql = "SELECT ISNULL(MAX(CAST(chr_cliecodigo AS INT)), 0) + 1 FROM dbo.cliente";
            string nuevoCodigo;

            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            var result = cmd.ExecuteScalar();
            int codigo = Convert.ToInt32(result);
            nuevoCodigo = codigo.ToString("D5");

            sql = "INSERT INTO dbo.cliente (chr_cliecodigo, vch_cliepaterno, vch_cliematerno, vch_clienombre, chr_cliedni, vch_clieciudad, vch_cliedireccion, vch_clietelefono, vch_clieemail) " +
                  "VALUES (@codigo, @paterno, @materno, @nombre, @dni, @ciudad, @direccion, @telefono, @email)";

            using var cmd2 = new SqlCommand(sql, cn);
            cmd2.Parameters.Add("@codigo", System.Data.SqlDbType.Char, 5).Value = nuevoCodigo;
            cmd2.Parameters.Add("@paterno", System.Data.SqlDbType.VarChar, 25).Value = paterno;
            cmd2.Parameters.Add("@materno", System.Data.SqlDbType.VarChar, 25).Value = materno;
            cmd2.Parameters.Add("@nombre", System.Data.SqlDbType.VarChar, 30).Value = nombre;
            cmd2.Parameters.Add("@dni", System.Data.SqlDbType.Char, 8).Value = dni;
            cmd2.Parameters.Add("@ciudad", System.Data.SqlDbType.VarChar, 30).Value = ciudad;
            cmd2.Parameters.Add("@direccion", System.Data.SqlDbType.VarChar, 50).Value = direccion;
            cmd2.Parameters.Add("@telefono", System.Data.SqlDbType.VarChar, 20).Value = string.IsNullOrEmpty(telefono) ? (object)DBNull.Value : telefono;
            cmd2.Parameters.Add("@email", System.Data.SqlDbType.VarChar, 50).Value = string.IsNullOrEmpty(email) ? (object)DBNull.Value : email;
            try
            {
                cmd2.ExecuteNonQuery();
            }
            catch (SqlException ex) when (ex.Number is 8152 or 2628)
            {
                EsquemaBD.Asegurar();
                cmd2.ExecuteNonQuery();
            }

            return nuevoCodigo;
        }

        private static string NormalizarDni(string dni)
        {
            var limpio = new string((dni ?? string.Empty).Where(char.IsDigit).ToArray());
            return limpio.Length > 8 ? limpio[..8] : limpio;
        }

        public bool Existe(string codigo)
        {
            string sql = "SELECT COUNT(1) FROM dbo.cliente WHERE chr_cliecodigo = @codigo";
            using var cn = ConexionBD.Conectar();
            using var cmd = new SqlCommand(sql, cn);
            cmd.Parameters.AddWithValue("@codigo", codigo);
            return Convert.ToInt32(cmd.ExecuteScalar()) > 0;
        }
    }
}
