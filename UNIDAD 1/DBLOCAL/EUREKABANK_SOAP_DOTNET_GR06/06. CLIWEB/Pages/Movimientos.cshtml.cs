using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class MovimientosModel : PageModel
    {
        private readonly SoapClientService _soap = new();
        private static readonly HashSet<string> Ingresos = new(new[] { "001", "003", "005", "008" });

        public string Campo1 { get; set; } = "";
        public List<MovimientoModel> Movimientos { get; set; } = new();
        public string Titular { get; set; } = "-";
        public string MonedaCuenta { get; set; } = "-";
        public string SaldoCuenta { get; set; } = "-";
        public double TotalCreditos { get; set; }
        public double TotalDebitos { get; set; }
        public string Emitido { get; set; } = DateTime.Now.ToString("dd/MM/yyyy HH:mm");
        public new string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet(string Campo1)
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            this.Campo1 = Campo1 ?? "";
            if (!string.IsNullOrEmpty(this.Campo1))
            {
                try { Movimientos = _soap.ListarMovimientos(this.Campo1); } catch { }
                try { CargarDatosCuenta(this.Campo1); } catch { }
                CalcularTotales();
            }
            return Page();
        }

        private void CargarDatosCuenta(string cuenta)
        {
            var cliente = HttpContext.Session.GetString("ClienteCodigo") ?? "";
            if (!string.IsNullOrWhiteSpace(cliente))
            {
                var cuentas = _soap.ListarCuentasPorCliente(cliente);
                var actual = cuentas.FirstOrDefault(c => c.CodigoCuenta == cuenta);
                if (actual != null)
                {
                    Titular = actual.NombreCliente;
                    MonedaCuenta = actual.Moneda;
                    SaldoCuenta = actual.Saldo.ToString("N2");
                    return;
                }
            }

            foreach (var clienteItem in _soap.ListarClientes())
            {
                var cuentas = _soap.ListarCuentasPorCliente(clienteItem.Codigo);
                var actual = cuentas.FirstOrDefault(c => c.CodigoCuenta == cuenta);
                if (actual != null)
                {
                    Titular = actual.NombreCliente;
                    MonedaCuenta = actual.Moneda;
                    SaldoCuenta = actual.Saldo.ToString("N2");
                    return;
                }
            }
        }

        private void CalcularTotales()
        {
            foreach (var m in Movimientos)
            {
                if (Ingresos.Contains(m.CodigoTipoMovimiento))
                {
                    TotalCreditos += m.ImporteMovimiento;
                }
                else
                {
                    TotalDebitos += m.ImporteMovimiento;
                }
            }
        }
    }
}
