using Microsoft.AspNetCore.Mvc;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Controladores;

[ApiController]
[Route("api/reportes")]
public class ReportesController(IServicioReporte servicioReporte) : ControllerBase
{
    [HttpGet("ventas/{codigoPartido:int}")]
    public async Task<ActionResult<IEnumerable<ResumenVentaDto>>> ObtenerResumenVentas(int codigoPartido, CancellationToken cancellationToken)
    {
        var resumen = await servicioReporte.ObtenerResumenVentasAsync(codigoPartido, cancellationToken);
        return Ok(resumen);
    }
}
