using servidor_rest_dotnet_federacion_gr06.Modelos;

namespace servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

public interface IServicioReporte
{
    Task<IEnumerable<ResumenVentaDto>> ObtenerResumenVentasAsync(int codigoPartido, CancellationToken cancellationToken = default);
}
