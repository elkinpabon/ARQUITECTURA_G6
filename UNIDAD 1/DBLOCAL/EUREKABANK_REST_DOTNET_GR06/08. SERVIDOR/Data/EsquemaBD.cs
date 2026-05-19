using Microsoft.Data.SqlClient;

namespace SERVIDOR.Data
{
    public static class EsquemaBD
    {
        public static void Asegurar()
        {
            using var cn = ConexionBD.CrearConexion();
            cn.Open();
            using var cmd = cn.CreateCommand();
            cmd.CommandText = @"
IF EXISTS (
    SELECT 1
    FROM sys.columns
    WHERE object_id = OBJECT_ID(N'dbo.cliente')
      AND name = N'chr_cliedni'
      AND max_length <> 8
)
BEGIN
    ALTER TABLE dbo.cliente ALTER COLUMN chr_cliedni CHAR(8) NOT NULL;
END";
            cmd.ExecuteNonQuery();

            AsegurarReferencia(cn, "dbo.moneda", "chr_monecodigo", "01", "Soles");
            AsegurarReferencia(cn, "dbo.moneda", "chr_monecodigo", "02", "Dolares");
            AsegurarReferencia(cn, "dbo.sucursal", "chr_sucucodigo", "001", "Sipan", "Chiclayo", "Av. Balta 1456", "2");
            AsegurarReferencia(cn, "dbo.empleado", "chr_emplcodigo", "0001", "Romero", "Castillo", "Carlos Alberto", "Trujillo", "Call1 1 Nro. 456");
            AsegurarReferencia(cn, "dbo.empleado", "chr_emplcodigo", "9999", "Internet", "Internet", "internet", "Internet", "internet");
        }

        private static void AsegurarReferencia(SqlConnection cn, string tabla, string campoCodigo, params string[] valores)
        {
            using var check = cn.CreateCommand();
            check.CommandText = $"SELECT COUNT(1) FROM {tabla} WHERE {campoCodigo} = @codigo";
            check.Parameters.AddWithValue("@codigo", valores[0]);

            if (Convert.ToInt32(check.ExecuteScalar()) > 0)
            {
                return;
            }

            using var insert = cn.CreateCommand();
            if (tabla.EndsWith("moneda"))
            {
                insert.CommandText = "INSERT INTO dbo.moneda (chr_monecodigo, vch_monedescripcion) VALUES (@codigo, @descripcion)";
                insert.Parameters.AddWithValue("@codigo", valores[0]);
                insert.Parameters.AddWithValue("@descripcion", valores[1]);
            }
            else if (tabla.EndsWith("sucursal"))
            {
                insert.CommandText = "INSERT INTO dbo.sucursal (chr_sucucodigo, vch_sucunombre, vch_sucuciudad, vch_sucudireccion, int_sucucontcuenta) VALUES (@codigo, @nombre, @ciudad, @direccion, @cajas)";
                insert.Parameters.AddWithValue("@codigo", valores[0]);
                insert.Parameters.AddWithValue("@nombre", valores[1]);
                insert.Parameters.AddWithValue("@ciudad", valores[2]);
                insert.Parameters.AddWithValue("@direccion", valores[3]);
                insert.Parameters.AddWithValue("@cajas", int.Parse(valores[4]));
            }
            else
            {
                insert.CommandText = "INSERT INTO dbo.empleado (chr_emplcodigo, vch_emplpaterno, vch_emplmaterno, vch_emplnombre, vch_emplciudad, vch_empldireccion) VALUES (@codigo, @paterno, @materno, @nombre, @ciudad, @direccion)";
                insert.Parameters.AddWithValue("@codigo", valores[0]);
                insert.Parameters.AddWithValue("@paterno", valores[1]);
                insert.Parameters.AddWithValue("@materno", valores[2]);
                insert.Parameters.AddWithValue("@nombre", valores[3]);
                insert.Parameters.AddWithValue("@ciudad", valores[4]);
                insert.Parameters.AddWithValue("@direccion", valores[5]);
            }

            insert.ExecuteNonQuery();
        }
    }
}
