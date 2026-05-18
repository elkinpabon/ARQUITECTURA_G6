using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using CLICONSOLA.Models;

namespace CLICONSOLA.Services;

public class ApiClient
{
    private readonly HttpClient _http;

    public ApiClient(string baseUrl = "http://localhost:5010")
    {
        _http = new HttpClient { BaseAddress = new Uri(baseUrl.TrimEnd('/') + "/") };
    }

    public async Task<bool> IniciarSesion(string usuario, string clave)
    {
        var resp = await _http.PostAsJsonAsync("api/auth/login", new { usuario, clave });
        return resp.IsSuccessStatusCode && await resp.Content.ReadFromJsonAsync<bool>();
    }

    public async Task<string> ClienteDeUsuario(string usuario)
    {
        var resp = await _http.GetAsync($"api/auth/cliente/{usuario}");
        return resp.IsSuccessStatusCode ? await resp.Content.ReadAsStringAsync() : "";
    }

    public async Task<Resultado> Depositar(string cuenta, string monto, string moneda)
    {
        var resp = await _http.PostAsJsonAsync("api/cuenta/depositar", new { cuenta, monto, moneda });
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<Resultado> Retirar(string cuenta, string monto, string moneda)
    {
        var resp = await _http.PostAsJsonAsync("api/cuenta/retirar", new { cuenta, monto, moneda });
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<Resultado> ConsultarSaldo(string cuenta)
    {
        var resp = await _http.GetAsync($"api/cuenta/saldo/{cuenta}");
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<Resultado> Transferir(string origen, string destino, string monto, string moneda)
    {
        var resp = await _http.PostAsJsonAsync("api/cuenta/transferir", new { origen, destino, monto, moneda });
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<List<CuentaResumen>> ListarCuentasPorCliente(string cliente)
    {
        var resp = await _http.GetAsync($"api/cuenta/cliente/{cliente}");
        return await resp.Content.ReadFromJsonAsync<List<CuentaResumen>>() ?? new List<CuentaResumen>();
    }

    public async Task<List<ClienteResumen>> ListarClientes()
    {
        var resp = await _http.GetAsync("api/cuenta/clientes");
        return await resp.Content.ReadFromJsonAsync<List<ClienteResumen>>() ?? new List<ClienteResumen>();
    }

    public async Task<Resultado> RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
    {
        var resp = await _http.PostAsJsonAsync("api/cuenta/cliente", new { paterno, materno, nombre, dni, ciudad, direccion, telefono, email });
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<Resultado> RegistrarCuenta(string cliente, string moneda)
    {
        var resp = await _http.PostAsJsonAsync("api/cuenta", new { cliente, moneda });
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<Resultado> EliminarCuenta(string cuenta)
    {
        var resp = await _http.DeleteAsync($"api/cuenta/{cuenta}");
        return await resp.Content.ReadFromJsonAsync<Resultado>() ?? new Resultado { Exitoso = false, Mensaje = "Error" };
    }

    public async Task<List<MovimientoModel>> ListarMovimientos(string cuenta)
    {
        var resp = await _http.GetAsync($"api/movimiento/{cuenta}");
        return await resp.Content.ReadFromJsonAsync<List<MovimientoModel>>() ?? new List<MovimientoModel>();
    }
}
