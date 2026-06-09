using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class CuentasModel : PageModel
    {
        private readonly SoapClientService _soap = new();
        public List<CuentaResumen> Cuentas { get; set; } = new();
        public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
        public new string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet(string? cliente)
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            if (!IsAdmin) return RedirectToPage("/Index");
            try
            {
                if (!string.IsNullOrWhiteSpace(cliente))
                {
                    Cuentas = _soap.ListarCuentasPorCliente(cliente);
                }
            }
            catch { }
            return Page();
        }
    }
}
