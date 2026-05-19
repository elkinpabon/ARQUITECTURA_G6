using Microsoft.AspNetCore.Mvc;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Controladores;

[ApiController]
[Route("api/auth")]
public class AutenticacionController(IServicioAutenticacion servicioAutenticacion) : ControllerBase
{
    [HttpPost("register")]
    public async Task<ActionResult<AutenticacionResponse>> Registrar([FromBody] RegistroUsuarioRequest request, CancellationToken cancellationToken)
    {
        try
        {
            var respuesta = await servicioAutenticacion.RegistrarAsync(request, cancellationToken);
            return Ok(respuesta);
        }
        catch (InvalidOperationException ex)
        {
            return BadRequest(new { mensaje = ex.Message });
        }
    }

    [HttpPost("login")]
    public async Task<ActionResult<AutenticacionResponse>> Login([FromBody] LoginUsuarioRequest request, CancellationToken cancellationToken)
    {
        try
        {
            var respuesta = await servicioAutenticacion.IniciarSesionAsync(request, cancellationToken);
            return Ok(respuesta);
        }
        catch (InvalidOperationException ex)
        {
            return BadRequest(new { mensaje = ex.Message });
        }
    }
}
