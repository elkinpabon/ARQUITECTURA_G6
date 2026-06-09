using System.Net.Http.Json;
using System.Text.Json;

namespace CLIMOVIL.Services;

public class ApiClient
{
    private readonly HttpClient _http;
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web);

    public ApiClient()
    {
        _http = new HttpClient { BaseAddress = new Uri(Constantes.BaseUrl.TrimEnd('/') + "/") };
    }

    public async Task<bool> IniciarSesion(string u, string c)
    {
        var resp = await _http.PostAsJsonAsync("api/auth/login", new { usuario = u, clave = c });
        if (!resp.IsSuccessStatusCode) return false;

        try
        {
            var body = await resp.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<bool>(body, JsonOptions);
        }
        catch
        {
            return false;
        }
    }

    public async Task<string> ClienteDeUsuario(string u)
    {
        var resp = await _http.GetAsync($"api/auth/cliente/{u}");
        return resp.IsSuccessStatusCode ? await resp.Content.ReadAsStringAsync() : "";
    }

    public async Task<Resultado> Depositar(string c, string m, string mo)
    {
        var r = await _http.PostAsJsonAsync("api/cuenta/depositar", new { cuenta = c, monto = m, moneda = mo });
        return await ReadResultadoAsync(r);
    }

    public async Task<Resultado> Retirar(string c, string m, string mo)
    {
        var r = await _http.PostAsJsonAsync("api/cuenta/retirar", new { cuenta = c, monto = m, moneda = mo });
        return await ReadResultadoAsync(r);
    }

    public async Task<Resultado> ConsultarSaldo(string c)
    {
        var r = await _http.GetAsync($"api/cuenta/saldo/{c}");
        return await ReadResultadoAsync(r);
    }

    public async Task<Resultado> Transferir(string o, string d, string m, string mo)
    {
        var r = await _http.PostAsJsonAsync("api/cuenta/transferir", new { origen = o, destino = d, monto = m, moneda = mo });
        return await ReadResultadoAsync(r);
    }

    public async Task<List<CuentaResumen>> ListarCuentas(string c)
    {
        var r = await _http.GetAsync($"api/cuenta/cliente/{c}");
        return await ReadListAsync<CuentaResumen>(r);
    }

    public async Task<List<ClienteResumen>> ListarClientes()
    {
        var r = await _http.GetAsync("api/cuenta/clientes");
        return await ReadListAsync<ClienteResumen>(r);
    }

    public async Task<Resultado> RegistrarCliente(string p, string m, string n, string d, string c, string di, string t, string e)
    {
        var r = await _http.PostAsJsonAsync("api/cuenta/cliente", new { paterno = p, materno = m, nombre = n, dni = d, ciudad = c, direccion = di, telefono = t, email = e });
        return await ReadResultadoAsync(r);
    }

    public async Task<Resultado> RegistrarCuenta(string c, string m)
    {
        var r = await _http.PostAsJsonAsync("api/cuenta", new { cliente = c, moneda = m });
        return await ReadResultadoAsync(r);
    }

    public async Task<Resultado> EliminarCuenta(string c)
    {
        var r = await _http.DeleteAsync($"api/cuenta/{c}");
        return await ReadResultadoAsync(r);
    }

    public async Task<List<MovimientoModel>> ListarMovimientos(string c)
    {
        var r = await _http.GetAsync($"api/movimiento/{c}");
        return await ReadListAsync<MovimientoModel>(r);
    }

    private static async Task<Resultado> ReadResultadoAsync(HttpResponseMessage response)
    {
        var body = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
        {
            return new Resultado
            {
                Exitoso = false,
                Mensaje = string.IsNullOrWhiteSpace(body) ? $"HTTP {(int)response.StatusCode}" : body
            };
        }

        try
        {
            return JsonSerializer.Deserialize<Resultado>(body, JsonOptions) ?? new Resultado { Exitoso = false, Mensaje = "Respuesta invalida" };
        }
        catch
        {
            return new Resultado { Exitoso = false, Mensaje = string.IsNullOrWhiteSpace(body) ? "Respuesta invalida" : body };
        }
    }

    private static async Task<List<T>> ReadListAsync<T>(HttpResponseMessage response)
    {
        if (!response.IsSuccessStatusCode)
        {
            return new List<T>();
        }

        var body = await response.Content.ReadAsStringAsync();
        try
        {
            return JsonSerializer.Deserialize<List<T>>(body, JsonOptions) ?? new List<T>();
        }
        catch
        {
            return new List<T>();
        }
    }
}

public class Resultado { public bool Exitoso { get; set; } public string Mensaje { get; set; } = ""; public double Saldo { get; set; } }
public class ClienteResumen
{
    public string Codigo { get; set; } = "";
    public string Dni { get; set; } = "";
    public string Nombre { get; set; } = "";
    public string DisplayText => string.IsNullOrWhiteSpace(Dni) ? $"{Codigo} | {Nombre}" : $"{Codigo} | {Dni} | {Nombre}";
}

public class CuentaResumen
{
    public string CodigoCuenta { get; set; } = "";
    public string Moneda { get; set; } = "";
    public double Saldo { get; set; }
    public string Estado { get; set; } = "";
    public string CodigoCliente { get; set; } = "";
    public string NombreCliente { get; set; } = "";
    public string MonedaNombre => Moneda == "01" ? "Soles" : Moneda == "02" ? "Dólares" : Moneda;
    public string DisplayText => $"{CodigoCuenta} | {MonedaNombre} | {Saldo:F2} | {Estado}";
}

public class MovimientoModel
{
    private static readonly HashSet<string> Ingresos = new(StringComparer.OrdinalIgnoreCase) { "001", "003", "005", "008" };

    public string CodigoCuenta { get; set; } = "";
    public int NumeroMovimiento { get; set; }
    public string FechaMovimiento { get; set; } = "";
    public string CodigoEmpleado { get; set; } = "";
    public string TipoDescripcion { get; set; } = "";
    public string CodigoTipoMovimiento { get; set; } = "";
    public string CuentaReferencia { get; set; } = "";
    public string MonedaOrigen { get; set; } = "";
    public double ImporteMovimiento { get; set; }
    public double? ImporteOrigen { get; set; }
    public double? TasaAplicada { get; set; }
    public bool EsIngreso => Ingresos.Contains(CodigoTipoMovimiento);
    public string MonedaOrigenNombre => MonedaOrigen == "02" ? "Dólares" : MonedaOrigen == "01" ? "Soles" : MonedaOrigen;
    public string DisplayText
    {
        get
        {
            var sign = EsIngreso ? "+" : "-";
            var extra = string.IsNullOrWhiteSpace(CuentaReferencia) ? string.Empty : $"\nRef: {CuentaReferencia}";
            var conv = ImporteOrigen.HasValue && TasaAplicada.HasValue
                ? $"\nConv: {ImporteOrigen.Value:F2} {MonedaOrigenNombre} x {TasaAplicada.Value:F4}"
                : string.Empty;
            return $"#{NumeroMovimiento} | {FechaMovimiento}\n{TipoDescripcion}\n{sign} {ImporteMovimiento:F2}{extra}{conv}";
        }
    }
}
