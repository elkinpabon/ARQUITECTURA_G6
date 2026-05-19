using servidor_rest_dotnet_federacion_gr06.Modelos;

namespace servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

public interface IServicioVenta
{
    Task<CompraResponse> RegistrarCompraAsync(CompraRequest request, CancellationToken cancellationToken = default);
}
