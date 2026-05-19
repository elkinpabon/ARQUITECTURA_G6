using cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_escritorio_rest_dotnet_ticketpremium_gr06.Servicios;

namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Controlador;

public sealed class ClienteTicketPremiumControlador : IDisposable
{
    private readonly ClienteTicketPremiumService _servicio;

    public ClienteTicketPremiumControlador(string apiBaseUrl)
    {
        _servicio = new ClienteTicketPremiumService(apiBaseUrl);
    }

    public Task<AutenticacionResponse> IniciarSesionAsync(AutenticacionRequest request) => _servicio.IniciarSesionAsync(request);

    public Task<AutenticacionResponse> RegistrarUsuarioAsync(RegistroUsuarioRequest request) => _servicio.RegistrarUsuarioAsync(request);

    public Task<IReadOnlyList<PartidoDto>> ObtenerPartidosAsync() => _servicio.ObtenerPartidosAsync();

    public Task<IReadOnlyList<LocalidadDto>> ObtenerLocalidadesAsync(int codigoPartido) => _servicio.ObtenerLocalidadesAsync(codigoPartido);

    public Task<CompraResponse> ComprarAsync(CompraRequest request) => _servicio.ComprarAsync(request);

    public Task<IReadOnlyList<ResumenVentaDto>> ObtenerReporteAsync(int codigoPartido) => _servicio.ObtenerReporteAsync(codigoPartido);

    public void Dispose() => _servicio.Dispose();
}
