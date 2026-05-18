using CLICONSOLA.Servicio;

namespace CLICONSOLA.Servicio
{
    public static class Sesion
    {
        public static string Usuario { get; set; } = string.Empty;
        public static bool EsAdmin { get; set; }
        public static string ClienteAsignado { get; set; } = string.Empty;
        public static List<CuentaResumen> CuentasCargadas { get; set; } = new List<CuentaResumen>();
    }

    public class CuentaResumen
    {
        public string CodigoCuenta { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public double Saldo { get; set; }
        public string Estado { get; set; } = string.Empty;
        public string CodigoCliente { get; set; } = string.Empty;
        public string NombreCliente { get; set; } = string.Empty;
    }

    public class ClienteResumen
    {
        public string Codigo { get; set; } = string.Empty;
        public string Dni { get; set; } = string.Empty;
        public string Nombre { get; set; } = string.Empty;
    }

    public class MovimientoModel
    {
        public string CodigoCuenta { get; set; } = string.Empty;
        public int NumeroMovimiento { get; set; }
        public string FechaMovimiento { get; set; } = string.Empty;
        public string CodigoEmpleado { get; set; } = string.Empty;
        public string CodigoTipoMovimiento { get; set; } = string.Empty;
        public string TipoDescripcion { get; set; } = string.Empty;
        public double ImporteMovimiento { get; set; }
        public string CuentaReferencia { get; set; } = string.Empty;
        public string MonedaOrigen { get; set; } = string.Empty;
        public double? ImporteOrigen { get; set; }
        public double? TasaAplicada { get; set; }
    }

    public class Resultado
    {
        public bool Exitoso { get; set; }
        public string Mensaje { get; set; } = string.Empty;
        public double Saldo { get; set; }
    }
}
