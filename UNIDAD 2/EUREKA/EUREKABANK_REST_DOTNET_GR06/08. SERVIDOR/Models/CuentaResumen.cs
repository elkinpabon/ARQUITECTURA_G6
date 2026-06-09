namespace SERVIDOR.Models;

public class CuentaResumen
{
    public string CodigoCuenta { get; set; } = string.Empty;
    public string Moneda { get; set; } = string.Empty;
    public double Saldo { get; set; }
    public string Estado { get; set; } = string.Empty;
    public string CodigoCliente { get; set; } = string.Empty;
    public string NombreCliente { get; set; } = string.Empty;
}
