using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class CuentasModel : PageModel
    {
        private readonly SoapClientService _soap = new("http://localhost:5000");
        public List<CuentaResumen> Cuentas { get; set; } = new();
        public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
        public string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet()
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            try
            {
                if (IsAdmin)
                    Cuentas = _soap.ListarCuentasPorCliente("");
                else
                {
                    string cliente = HttpContext.Session.GetString("ClienteCodigo") ?? "";
                    Cuentas = _soap.ListarCuentasPorCliente(cliente);
                }
            }
            catch { }
            return Page();
        }
    }
}
