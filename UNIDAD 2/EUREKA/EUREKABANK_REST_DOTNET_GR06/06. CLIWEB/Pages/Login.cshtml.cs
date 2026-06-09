using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class LoginModel : PageModel
{
    private readonly ApiClient _api = new();
    [BindProperty] public string Usuario { get; set; } = "";
    [BindProperty] public string Clave { get; set; } = "";
    public string Error { get; set; } = "";

    public IActionResult OnGet() => Page();

    public async Task<IActionResult> OnPost()
    {
        try
        {
            bool ok = await _api.IniciarSesion(Usuario, Clave);
            if (ok)
            {
                string cliente = await _api.ClienteDeUsuario(Usuario);
                HttpContext.Session.SetString("Usuario", Usuario);
                HttpContext.Session.SetString("EsAdmin", string.IsNullOrEmpty(cliente) ? "true" : "false");
                HttpContext.Session.SetString("ClienteCodigo", cliente);
                return RedirectToPage("/Index");
            }
            Error = "Usuario o clave incorrectos";
        }
        catch { Error = "Error conectando al servidor"; }
        return Page();
    }
}
