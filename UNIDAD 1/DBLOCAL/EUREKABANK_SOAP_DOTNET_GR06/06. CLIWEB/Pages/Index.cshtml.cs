using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class IndexModel : PageModel
    {
        private readonly SoapClientService _soap = new("http://localhost:5000");

        public string User => HttpContext.Session.GetString("Usuario") ?? "";
        public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
        public string SelectedCliente { get; set; } = "";
        public string ClienteNombre { get; set; } = "";
        public List<ClienteResumen> Clientes { get; set; } = new();
        public List<CuentaResumen> Cuentas { get; set; } = new();
        public double TotalSaldo { get; set; }

        public IActionResult OnGet(string? cliente)
        {
            if (string.IsNullOrEmpty(User))
                return RedirectToPage("/Login");

            try
            {
                if (IsAdmin)
                {
                    Clientes = _soap.ListarClientes();
                    SelectedCliente = cliente ?? Clientes.FirstOrDefault()?.Codigo ?? "";
                }
                else
                {
                    SelectedCliente = HttpContext.Session.GetString("ClienteCodigo") ?? "";
                }

                if (!string.IsNullOrWhiteSpace(SelectedCliente))
                {
                    Cuentas = _soap.ListarCuentasPorCliente(SelectedCliente);
                    TotalSaldo = Cuentas.Sum(c => c.Saldo);
                    ClienteNombre = Cuentas.FirstOrDefault()?.NombreCliente ?? "";
                }
            }
            catch
            {
            }

            return Page();
        }
    }
}
