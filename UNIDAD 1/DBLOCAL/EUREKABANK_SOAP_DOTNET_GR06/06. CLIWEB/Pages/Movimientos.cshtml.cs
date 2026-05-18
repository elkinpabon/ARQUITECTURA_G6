using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class MovimientosModel : PageModel
    {
        private readonly SoapClientService _soap = new("http://localhost:5000");
        public string Campo1 { get; set; } = "";
        public List<MovimientoModel> Movimientos { get; set; } = new();
        public string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet(string Campo1)
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            this.Campo1 = Campo1 ?? "";
            if (!string.IsNullOrEmpty(this.Campo1))
            {
                try { Movimientos = _soap.ListarMovimientos(this.Campo1); } catch { }
            }
            return Page();
        }
    }
}
