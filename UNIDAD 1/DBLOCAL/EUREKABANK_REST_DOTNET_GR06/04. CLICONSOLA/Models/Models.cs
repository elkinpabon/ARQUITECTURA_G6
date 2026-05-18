namespace CLICONSOLA.Models;
public class Resultado { public bool Exitoso { get; set; } public string Mensaje { get; set; } = ""; public double Saldo { get; set; } }
public class CuentaResumen { public string CodigoCuenta { get; set; } = ""; public string Moneda { get; set; } = ""; public double Saldo { get; set; } public string Estado { get; set; } = ""; public string CodigoCliente { get; set; } = ""; public string NombreCliente { get; set; } = ""; }
public class ClienteResumen { public string Codigo { get; set; } = ""; public string Dni { get; set; } = ""; public string Nombre { get; set; } = ""; }
public class MovimientoModel { public string CodigoCuenta { get; set; } = ""; public int NumeroMovimiento { get; set; } public string FechaMovimiento { get; set; } = ""; public string TipoDescripcion { get; set; } = ""; public double ImporteMovimiento { get; set; } }
