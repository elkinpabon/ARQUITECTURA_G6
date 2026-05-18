using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class ClientesModel : PageModel
    {
        private readonly SoapClientService _soap = new("http://localhost:5000");
        public List<ClienteResumen> Clientes { get; set; } = new();
        public string User => HttpContext.Session.GetString("Usuario") ?? "";

        public IActionResult OnGet()
        {
            if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
            try { Clientes = _soap.ListarClientes(); } catch { }
            return Page();
        }
    }
}
