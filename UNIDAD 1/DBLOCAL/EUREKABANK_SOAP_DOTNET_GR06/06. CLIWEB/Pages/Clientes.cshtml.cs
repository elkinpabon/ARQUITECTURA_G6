using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class ClientesModel : PageModel
    {
        private readonly SoapClientService _soap = new();
        public List<ClienteResumen> Clientes { get; set; } = new();
        public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
        public new string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet()
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            if (!IsAdmin) return RedirectToPage("/Index");
            try { Clientes = _soap.ListarClientes(); } catch { }
            return Page();
        }
    }
}
