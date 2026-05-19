using cliente_movil_rest_dotnet_ticketpremium_gr06.Modelo;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06.Servicios;

public interface ITicketPremiumApiService
{
    Task<AutenticacionResponse> IniciarSesionAsync(AutenticacionRequest request);

    Task<AutenticacionResponse> RegistrarUsuarioAsync(RegistroUsuarioRequest request);

    Task<IReadOnlyList<PartidoDto>> ObtenerPartidosAsync();

    Task<IReadOnlyList<LocalidadDto>> ObtenerLocalidadesAsync(int codigoPartido);

    Task<CompraResponse> ComprarAsync(CompraRequest request);

    Task<IReadOnlyList<ResumenVentaDto>> ObtenerReporteAsync(int codigoPartido);
}
