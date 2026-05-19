using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Datos;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Servicios;

public class ServicioReporte(ContextoAplicacion contexto) : IServicioReporte
{
    public async Task<IEnumerable<ResumenVentaDto>> ObtenerResumenVentasAsync(int codigoPartido, CancellationToken cancellationToken = default)
    {
        var partido = await contexto.PartidosFutbol
            .AsNoTracking()
            .FirstOrDefaultAsync(p => p.Codigo == codigoPartido, cancellationToken);

        if (partido is null)
        {
            return Enumerable.Empty<ResumenVentaDto>();
        }

        return await contexto.DetallesFactura
            .AsNoTracking()
            .Where(detalle => detalle.CodigoPartido == codigoPartido)
            .GroupBy(detalle => detalle.Localidad)
            .Select(grupo => new ResumenVentaDto
            {
                Partido = partido.EquipoLocal + " vs " + partido.EquipoVisita,
                Fecha = partido.Fecha,
                Localidad = grupo.Key,
                Vendidos = grupo.Sum(detalle => detalle.Cantidad),
                TotalRecaudado = grupo.Sum(detalle => detalle.Total)
            })
            .OrderBy(resumen => resumen.Localidad)
            .ToListAsync(cancellationToken);
    }
}
