using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Datos;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Servicios;

public class ServicioLocalidad(ContextoAplicacion contexto) : IServicioLocalidad
{
    public async Task<IEnumerable<LocalidadPartido>> ObtenerDisponiblesAsync(int codigoPartido, CancellationToken cancellationToken = default)
    {
        return await contexto.LocalidadesPartido
            .AsNoTracking()
            .Where(localidad => localidad.CodigoPartido == codigoPartido && localidad.Disponibilidad > 0)
            .OrderBy(localidad => localidad.CodigoLocalidad)
            .ToListAsync(cancellationToken);
    }

    public Task<LocalidadPartido?> ObtenerPorPartidoYCodigoAsync(int codigoPartido, string codigoLocalidad, CancellationToken cancellationToken = default)
    {
        return contexto.LocalidadesPartido.FirstOrDefaultAsync(
            localidad => localidad.CodigoPartido == codigoPartido && localidad.CodigoLocalidad == codigoLocalidad,
            cancellationToken);
    }
}
