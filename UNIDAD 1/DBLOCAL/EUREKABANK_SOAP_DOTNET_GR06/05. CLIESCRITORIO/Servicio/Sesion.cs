using CLIESCRITORIO.Servicio;

namespace CLIESCRITORIO.Servicio
{
    public class Sesion
    {
        public string? Usuario { get; set; }
        public bool Admin { get; set; }
        public string ClienteAsignado { get; set; } = string.Empty;
        public List<CuentaResumen> Cuentas { get; set; } = new();

        public bool Activa => !string.IsNullOrWhiteSpace(Usuario);

        public bool CuentaPropia(string codigo)
        {
            if (string.IsNullOrWhiteSpace(codigo)) return false;
            return Cuentas.Any(c => codigo == c.CodigoCuenta);
        }
    }
}
