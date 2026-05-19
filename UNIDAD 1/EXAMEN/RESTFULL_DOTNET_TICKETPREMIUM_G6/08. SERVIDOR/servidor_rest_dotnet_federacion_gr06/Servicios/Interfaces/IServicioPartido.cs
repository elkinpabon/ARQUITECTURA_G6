using servidor_rest_dotnet_federacion_gr06.Modelos;

namespace servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

public interface IServicioPartido
{
    Task<IEnumerable<PartidoFutbol>> ObtenerDisponiblesAsync(CancellationToken cancellationToken = default);

    Task<PartidoFutbol?> ObtenerPorCodigoAsync(int codigoPartido, CancellationToken cancellationToken = default);
}
