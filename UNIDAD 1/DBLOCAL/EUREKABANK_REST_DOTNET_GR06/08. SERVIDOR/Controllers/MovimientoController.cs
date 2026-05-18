using Microsoft.AspNetCore.Mvc;
using SERVIDOR.Models;
using SERVIDOR.Services;

namespace SERVIDOR.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MovimientoController : ControllerBase
{
    private readonly MovimientoService _movimientoService;

    public MovimientoController(MovimientoService movimientoService)
    {
        _movimientoService = movimientoService;
    }

    [HttpGet("{cuenta}")]
    public ActionResult<List<MovimientoModel>> ListarMovimientos(string cuenta)
    {
        var result = _movimientoService.ListarMovimientos(cuenta);
        return Ok(result);
    }
}
