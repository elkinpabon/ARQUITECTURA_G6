using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class ClientesModel : PageModel
{
    private readonly ApiClient _api = new();
    public string Mensaje { get; set; } = "";
    public List<ClienteResumen> Clientes { get; set; } = new();
    public string User => HttpContext.Session.GetString("Usuario") ?? "";

    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();

    public async Task<IActionResult> OnPost()
    {
        if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login");
        try { Clientes = await _api.ListarClientes(); Mensaje = $"Total: {Clientes.Count}"; }
        catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; }
        return Page();
    }
}
