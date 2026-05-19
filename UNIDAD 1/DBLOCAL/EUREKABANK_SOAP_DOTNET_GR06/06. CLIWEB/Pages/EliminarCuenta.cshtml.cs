using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class EliminarCuentaModel : PageModel
    {
        private readonly SoapClientService _soap = new();
        [BindProperty] public string Campo1 { get; set; } = "";
        public string Mensaje { get; set; } = "";
        public new string User => HttpContext.Session.GetString("Usuario") ?? "";

        public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";

        public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : IsAdmin ? Page() : RedirectToPage("/Index");

        public IActionResult OnPost()
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            if (!IsAdmin) return RedirectToPage("/Index");
            try
            {
                var r = _soap.EliminarCuenta(Campo1);
                Mensaje = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}";
            }
            catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
            return Page();
        }
    }
}
