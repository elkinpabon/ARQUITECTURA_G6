using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class RegistrarCuentaModel : PageModel
{
    private readonly ApiClient _api = new();
    [BindProperty] public string Campo1 { get; set; } = "";
    [BindProperty] public string Campo2 { get; set; } = "01";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";

    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();

    public async Task<IActionResult> OnPost()
    {
        if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
        try { var r = await _api.RegistrarCuenta(Campo1, Campo2); Mensaje = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; }
        catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
        return Page();
    }
}
