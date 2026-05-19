using System.Net.Http.Json;
using System.Text.Json;
using cliente_consola_rest_dotnet_ticketpremium_gr06.Modelo;

namespace cliente_consola_rest_dotnet_ticketpremium_gr06.Servicios;

public class ClienteTicketPremiumService : IDisposable
{
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive = true
    };

    private readonly HttpClient _httpClient;

    public ClienteTicketPremiumService(string baseUrl)
    {
        _httpClient = new HttpClient
        {
            BaseAddress = new Uri(baseUrl, UriKind.Absolute)
        };
    }

    public async Task<IReadOnlyList<PartidoDto>> ObtenerPartidosAsync()
    {
        var response = await _httpClient.GetAsync("api/partidos/disponibles");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<PartidoDto>>(JsonOptions) ?? [];
    }

    public async Task<IReadOnlyList<LocalidadDto>> ObtenerLocalidadesAsync(int codigoPartido)
    {
        var response = await _httpClient.GetAsync($"api/partidos/{codigoPartido}/localidades");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<LocalidadDto>>(JsonOptions) ?? [];
    }

    public async Task<CompraResponse> ComprarAsync(CompraRequest request)
    {
        var response = await _httpClient.PostAsJsonAsync("api/ventas", request, JsonOptions);
        if (!response.IsSuccessStatusCode)
        {
            throw new InvalidOperationException(await ObtenerMensajeErrorAsync(response));
        }

        return await response.Content.ReadFromJsonAsync<CompraResponse>(JsonOptions)
            ?? throw new InvalidOperationException("La API no devolvio datos de la compra.");
    }

    public async Task<IReadOnlyList<ResumenVentaDto>> ObtenerReporteAsync(int codigoPartido)
    {
        var response = await _httpClient.GetAsync($"api/reportes/ventas/{codigoPartido}");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<List<ResumenVentaDto>>(JsonOptions) ?? [];
    }

    public async Task<AutenticacionResponse> IniciarSesionAsync(AutenticacionRequest request)
    {
        var response = await _httpClient.PostAsJsonAsync("api/auth/login", request, JsonOptions);
        if (!response.IsSuccessStatusCode)
        {
            throw new InvalidOperationException(await ObtenerMensajeErrorAsync(response));
        }

        return await response.Content.ReadFromJsonAsync<AutenticacionResponse>(JsonOptions)
            ?? throw new InvalidOperationException("La API no devolvio datos de autenticacion.");
    }

    public async Task<AutenticacionResponse> RegistrarUsuarioAsync(RegistroUsuarioRequest request)
    {
        var response = await _httpClient.PostAsJsonAsync("api/auth/register", request, JsonOptions);
        if (!response.IsSuccessStatusCode)
        {
            throw new InvalidOperationException(await ObtenerMensajeErrorAsync(response));
        }

        return await response.Content.ReadFromJsonAsync<AutenticacionResponse>(JsonOptions)
            ?? throw new InvalidOperationException("La API no devolvio datos de registro.");
    }

    private static async Task<string> ObtenerMensajeErrorAsync(HttpResponseMessage response)
    {
        var contenido = await response.Content.ReadAsStringAsync();

        try
        {
            using var document = JsonDocument.Parse(contenido);
            if (document.RootElement.TryGetProperty("mensaje", out var mensaje))
            {
                return mensaje.GetString() ?? contenido;
            }
        }
        catch
        {
        }

        return string.IsNullOrWhiteSpace(contenido) ? "Error inesperado al consumir la API." : contenido;
    }

    public void Dispose() => _httpClient.Dispose();
}
