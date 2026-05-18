using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class ConsultarSaldoModel : PageModel
    {
        private readonly SoapClientService _soap = new("http://localhost:5000");
        [BindProperty] public string Campo1 { get; set; } = "";
        public string Mensaje { get; set; } = "";
        public string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();

        public IActionResult OnPost()
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            try
            {
                var r = _soap.ConsultarSaldo(Campo1);
                Mensaje = r.Exitoso ? $"OK: {r.Mensaje} - Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}";
            }
            catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
            return Page();
        }
    }
}
