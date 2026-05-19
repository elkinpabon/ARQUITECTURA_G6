using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;
using System.Linq;

namespace CLIWEB.Pages;

public class IndexModel : PageModel
{
    private readonly ApiClient _api = new();

    public new string User => HttpContext.Session.GetString("Usuario") ?? "";
    public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
    public string SelectedCliente { get; set; } = "";
    public string ClienteNombre { get; set; } = "";
    public List<ClienteResumen> Clientes { get; set; } = new();
    public List<CuentaResumen> Cuentas { get; set; } = new();
    public double TotalSaldo { get; set; }

    public async Task<IActionResult> OnGetAsync(string? cliente)
    {
        if (string.IsNullOrEmpty(User))
        {
            return RedirectToPage("/Login");
        }

        try
        {
            if (IsAdmin)
            {
                Clientes = await _api.ListarClientes();
                SelectedCliente = cliente ?? string.Empty;
            }
            else
            {
                SelectedCliente = HttpContext.Session.GetString("ClienteCodigo") ?? string.Empty;
            }

            if (!string.IsNullOrWhiteSpace(SelectedCliente))
            {
                Cuentas = await _api.ListarCuentas(SelectedCliente);
                TotalSaldo = Cuentas.Sum(c => c.Saldo);
                ClienteNombre = Cuentas.FirstOrDefault()?.NombreCliente ?? string.Empty;
            }
        }
        catch
        {
        }

        return Page();
    }
}
