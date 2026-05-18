namespace SERVIDOR.Models;

public class MovimientoModel
{
    public string CodigoCuenta { get; set; } = string.Empty;
    public int NumeroMovimiento { get; set; }
    public string FechaMovimiento { get; set; } = string.Empty;
    public string CodigoEmpleado { get; set; } = string.Empty;
    public string CodigoTipoMovimiento { get; set; } = string.Empty;
    public string TipoDescripcion { get; set; } = string.Empty;
    public double ImporteMovimiento { get; set; }
    public string? CuentaReferencia { get; set; }
    public string? MonedaOrigen { get; set; }
    public double? ImporteOrigen { get; set; }
    public double? TasaAplicada { get; set; }
}
