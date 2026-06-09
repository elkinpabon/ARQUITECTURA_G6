using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class MovimientosModel : PageModel
{
    private readonly ApiClient _api = new();
    [BindProperty] public string Campo1 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public List<MovimientoModel> Movimientos { get; set; } = new();
    public string User => HttpContext.Session.GetString("Usuario") ?? "";

    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();

    public async Task<IActionResult> OnPost()
    {
        if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
        try { Movimientos = await _api.ListarMovimientos(Campo1); Mensaje = $"Total: {Movimientos.Count}"; }
        catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
        return Page();
    }
}
