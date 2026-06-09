using CLIESCRITORIO.Services;

namespace CLIESCRITORIO.Controlador;

public class Sesion
{
    public string? Usuario { get; set; }
    public bool Admin { get; set; }
    public string ClienteAsignado { get; set; } = string.Empty;
    public List<CuentaResumen> Cuentas { get; set; } = new();

    public bool CuentaPropia(string cuenta)
    {
        if (Admin)
        {
            return true;
        }

        return Cuentas.Any(c => c.CodigoCuenta.Equals(cuenta, StringComparison.OrdinalIgnoreCase));
    }
}
