using System.Net.Http.Json;

namespace CLIMOVIL.Services;

public class ApiClient
{
    private readonly HttpClient _http;
    public ApiClient(string baseUrl = "http://10.0.2.2:5010") { _http = new HttpClient { BaseAddress = new Uri(baseUrl.TrimEnd('/') + "/") }; }

    public async Task<bool> IniciarSesion(string u, string c) { var r = await _http.PostAsJsonAsync("api/auth/login", new { usuario = u, clave = c }); return r.IsSuccessStatusCode && await r.Content.ReadFromJsonAsync<bool>(); }
    public async Task<string> ClienteDeUsuario(string u) { var r = await _http.GetAsync($"api/auth/cliente/{u}"); return r.IsSuccessStatusCode ? await r.Content.ReadAsStringAsync() : ""; }
    public async Task<Resultado> Depositar(string c, string m, string mo) { var r = await _http.PostAsJsonAsync("api/cuenta/depositar", new { cuenta = c, monto = m, moneda = mo }); return await r.Content.ReadFromJsonAsync<Resultado>() ?? new(); }
    public async Task<Resultado> Retirar(string c, string m, string mo) { var r = await _http.PostAsJsonAsync("api/cuenta/retirar", new { cuenta = c, monto = m, moneda = mo }); return await r.Content.ReadFromJsonAsync<Resultado>() ?? new(); }
    public async Task<Resultado> ConsultarSaldo(string c) { var r = await _http.GetAsync($"api/cuenta/saldo/{c}"); return await r.Content.ReadFromJsonAsync<Resultado>() ?? new(); }
    public async Task<Resultado> Transferir(string o, string d, string m, string mo) { var r = await _http.PostAsJsonAsync("api/cuenta/transferir", new { origen = o, destino = d, monto = m, moneda = mo }); return await r.Content.ReadFromJsonAsync<Resultado>() ?? new(); }
    public async Task<List<CuentaResumen>> ListarCuentas(string c) { var r = await _http.GetAsync($"api/cuenta/cliente/{c}"); return await r.Content.ReadFromJsonAsync<List<CuentaResumen>>() ?? new(); }
    public async Task<List<MovimientoModel>> ListarMovimientos(string c) { var r = await _http.GetAsync($"api/movimiento/{c}"); return await r.Content.ReadFromJsonAsync<List<MovimientoModel>>() ?? new(); }
}

public class Resultado { public bool Exitoso { get; set; } public string Mensaje { get; set; } = ""; public double Saldo { get; set; } }
public class CuentaResumen { public string CodigoCuenta { get; set; } = ""; public string Moneda { get; set; } = ""; public double Saldo { get; set; } public string Estado { get; set; } = ""; public string CodigoCliente { get; set; } = ""; public string NombreCliente { get; set; } = ""; }
public class MovimientoModel { public string CodigoCuenta { get; set; } = ""; public int NumeroMovimiento { get; set; } public string FechaMovimiento { get; set; } = ""; public string TipoDescripcion { get; set; } = ""; public double ImporteMovimiento { get; set; } }
