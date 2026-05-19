using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class CuentasModel : PageModel
{
    private readonly ApiClient _api = new();
    [BindProperty] public string Campo1 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";

    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();

    public async Task<IActionResult> OnPost()
    {
        if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
        try { string cl = IsAdmin ? Campo1 : (HttpContext.Session.GetString("ClienteCodigo") ?? ""); var list = await _api.ListarCuentas(cl); Mensaje = $"Encontradas: {list.Count}"; }
        catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
        return Page();
    }
}
