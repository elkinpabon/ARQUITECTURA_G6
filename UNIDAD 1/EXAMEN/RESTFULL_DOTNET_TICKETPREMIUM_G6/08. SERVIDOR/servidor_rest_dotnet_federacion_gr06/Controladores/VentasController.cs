using Microsoft.AspNetCore.Mvc;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Controladores;

[ApiController]
[Route("api/ventas")]
public class VentasController(IServicioVenta servicioVenta) : ControllerBase
{
    [HttpPost]
    public async Task<ActionResult<CompraResponse>> RegistrarCompra([FromBody] CompraRequest request, CancellationToken cancellationToken)
    {
        try
        {
            var respuesta = await servicioVenta.RegistrarCompraAsync(request, cancellationToken);
            return Ok(respuesta);
        }
        catch (InvalidOperationException ex)
        {
            return BadRequest(new { mensaje = ex.Message });
        }
    }
}
