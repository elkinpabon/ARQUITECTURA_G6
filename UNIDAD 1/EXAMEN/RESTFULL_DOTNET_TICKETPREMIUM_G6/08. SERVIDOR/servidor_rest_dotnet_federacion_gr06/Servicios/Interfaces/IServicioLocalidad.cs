using servidor_rest_dotnet_federacion_gr06.Modelos;

namespace servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

public interface IServicioLocalidad
{
    Task<IEnumerable<LocalidadPartido>> ObtenerDisponiblesAsync(int codigoPartido, CancellationToken cancellationToken = default);

    Task<LocalidadPartido?> ObtenerPorPartidoYCodigoAsync(int codigoPartido, string codigoLocalidad, CancellationToken cancellationToken = default);
}
