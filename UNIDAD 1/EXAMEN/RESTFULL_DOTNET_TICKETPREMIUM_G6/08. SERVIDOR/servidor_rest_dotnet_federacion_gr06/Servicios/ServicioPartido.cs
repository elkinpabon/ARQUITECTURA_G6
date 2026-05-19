using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Datos;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Servicios;

public class ServicioPartido(ContextoAplicacion contexto) : IServicioPartido
{
    public async Task<IEnumerable<PartidoFutbol>> ObtenerDisponiblesAsync(CancellationToken cancellationToken = default)
    {
        return await contexto.PartidosFutbol
            .AsNoTracking()
            .Where(partido => partido.Fecha >= DateTime.Now)
            .OrderBy(partido => partido.Fecha)
            .ToListAsync(cancellationToken);
    }

    public Task<PartidoFutbol?> ObtenerPorCodigoAsync(int codigoPartido, CancellationToken cancellationToken = default)
    {
        return contexto.PartidosFutbol.FirstOrDefaultAsync(partido => partido.Codigo == codigoPartido, cancellationToken);
    }
}
