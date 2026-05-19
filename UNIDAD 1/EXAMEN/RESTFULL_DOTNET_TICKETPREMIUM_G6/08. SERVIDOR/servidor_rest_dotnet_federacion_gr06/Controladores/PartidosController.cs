using Microsoft.AspNetCore.Mvc;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Controladores;

[ApiController]
[Route("api/partidos")]
public class PartidosController(IServicioPartido servicioPartido, IServicioLocalidad servicioLocalidad) : ControllerBase
{
    [HttpGet("disponibles")]
    public async Task<ActionResult<IEnumerable<PartidoFutbol>>> ObtenerDisponibles(CancellationToken cancellationToken)
    {
        var partidos = await servicioPartido.ObtenerDisponiblesAsync(cancellationToken);
        return Ok(partidos);
    }

    [HttpGet("{codigoPartido:int}/localidades")]
    public async Task<ActionResult<IEnumerable<LocalidadPartido>>> ObtenerLocalidadesDisponibles(int codigoPartido, CancellationToken cancellationToken)
    {
        var localidades = await servicioLocalidad.ObtenerDisponiblesAsync(codigoPartido, cancellationToken);
        return Ok(localidades);
    }
}
