using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CLIWEB.Services;

namespace CLIWEB.Pages;

public class RetirarModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    [BindProperty] public string Campo2 { get; set; } = "";
    [BindProperty] public string Campo3 { get; set; } = "01";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { var r = await _api.Retirar(Campo1, Campo2, Campo3); Mensaje = r.Exitoso ? $"OK: {r.Mensaje} Saldo:{r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class SaldoModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { var r = await _api.ConsultarSaldo(Campo1); Mensaje = r.Exitoso ? $"Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class TransferirModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    [BindProperty] public string Campo2 { get; set; } = "";
    [BindProperty] public string Campo3 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { var r = await _api.Transferir(Campo1, Campo2, Campo3, "01"); Mensaje = r.Exitoso ? $"OK: {r.Mensaje} Saldo:{r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class CuentasModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public List<CuentaResumen> Cuentas { get; set; } = new();
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { string cl = IsAdmin ? Campo1 : (HttpContext.Session.GetString("ClienteCodigo") ?? ""); Cuentas = await _api.ListarCuentas(cl); Mensaje = $"Encontradas: {Cuentas.Count}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class ClientesModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    public string Mensaje { get; set; } = "";
    public List<ClienteResumen> Clientes { get; set; } = new();
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { Clientes = await _api.ListarClientes(); Mensaje = $"Total: {Clientes.Count}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class RegClienteModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
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
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { var r = await _api.RegistrarCliente(Campo1, Campo2, Campo3, Campo4, Campo5, Campo6, Campo7, Campo8); Mensaje = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class RegCuentaModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    [BindProperty] public string Campo2 { get; set; } = "01";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { var r = await _api.RegistrarCuenta(Campo1, Campo2); Mensaje = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class ElimCuentaModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { var r = await _api.EliminarCuenta(Campo1); Mensaje = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}

public class MovimientosModel : PageModel {
    private readonly ApiClient _api = new("http://localhost:5010");
    [BindProperty] public string Campo1 { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public List<MovimientoModel> Movimientos { get; set; } = new();
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
    public async Task<IActionResult> OnPost() { if (string.IsNullOrEmpty(User)) return RedirectToPage("/Login"); try { Movimientos = await _api.ListarMovimientos(Campo1); Mensaje = $"Total: {Movimientos.Count}"; } catch (Exception ex) { Mensaje = $"Error: {ex.Message}"; } return Page(); }
}
