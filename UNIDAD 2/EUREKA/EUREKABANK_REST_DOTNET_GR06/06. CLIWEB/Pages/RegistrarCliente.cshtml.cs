using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class RegistrarClienteModel : PageModel
{
    private readonly ApiClient _api = new();
    [BindProperty] public string Campo1 { get; set; } = "";
    [BindProperty] public string Campo2 { get; set; } = "";
    [BindProperty] public string Campo3 { get; set; } = "";
    [BindProperty] public string Campo4 { get; set; } = "";
    [BindProperty] public string Campo5 { get; set; } = "";
    [BindProperty] public string Campo6 { get; set; } = "";
    [BindProperty] public string Campo7 { get; set; } = "";
    [BindProperty] public string Campo8 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";

    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();

    public async Task<IActionResult> OnPost()
    {
        if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
        try { var r = await _api.RegistrarCliente(Campo1, Campo2, Campo3, Campo4, Campo5, Campo6, Campo7, Campo8); Mensaje = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; }
        catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
        return Page();
    }
}
