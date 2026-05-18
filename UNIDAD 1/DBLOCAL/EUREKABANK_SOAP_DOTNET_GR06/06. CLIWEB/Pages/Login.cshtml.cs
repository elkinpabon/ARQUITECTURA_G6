using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages
{
    public class LoginModel : PageModel
    {
        private readonly SoapClientService _soap;

        public LoginModel()
        {
            _soap = new SoapClientService("http://localhost:5000");
        }

        [BindProperty] public string Usuario { get; set; } = "";
        [BindProperty] public string Clave { get; set; } = "";
        public string Error { get; set; } = "";

        public IActionResult OnGet() => Page();

        public IActionResult OnPost()
        {
            try
            {
                bool success = _soap.IniciarSesion(Usuario, Clave);
                if (success)
                {
                    string cliente = _soap.ClienteDeUsuario(Usuario);
                    HttpContext.Session.SetString("Usuario", Usuario);
                    HttpContext.Session.SetString("EsAdmin", string.IsNullOrEmpty(cliente) ? "true" : "false");
                    HttpContext.Session.SetString("ClienteCodigo", cliente);
                    return RedirectToPage("/Index");
                }
                else
                {
                    Error = "Usuario o clave incorrectos";
                    return Page();
                }
            }
            catch
            {
                Error = "Error conectando al servidor";
                return Page();
            }
        }
    }
}
