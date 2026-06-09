using Microsoft.AspNetCore.Mvc;
using SERVIDOR.Models;
using SERVIDOR.Services;

namespace SERVIDOR.Controllers;

[ApiController]
[Route("api/[controller]")]
public class CuentaController : ControllerBase
{
    private readonly CuentaService _cuentaService;

    public CuentaController(CuentaService cuentaService)
    {
        _cuentaService = cuentaService;
    }

    [HttpPost("depositar")]
    public ActionResult<Resultado> Depositar([FromBody] DepositarRequest request)
    {
        var result = _cuentaService.Depositar(request.Cuenta, request.Monto, request.Moneda);
        return Ok(result);
    }

    [HttpPost("retirar")]
    public ActionResult<Resultado> Retirar([FromBody] RetirarRequest request)
    {
        var result = _cuentaService.Retirar(request.Cuenta, request.Monto, request.Moneda);
        return Ok(result);
    }

    [HttpGet("saldo/{cuenta}")]
    public ActionResult<Resultado> ConsultarSaldo(string cuenta)
    {
        var result = _cuentaService.ConsultarSaldo(cuenta);
        return Ok(result);
    }

    [HttpPost("transferir")]
    public ActionResult<Resultado> Transferir([FromBody] TransferirRequest request)
    {
        var result = _cuentaService.Transferir(request.Origen, request.Destino, request.Monto, request.Moneda);
        return Ok(result);
    }

    [HttpGet("cliente/{cliente}")]
    public ActionResult<List<CuentaResumen>> ListarCuentasPorCliente(string cliente)
    {
        var result = _cuentaService.ListarCuentasPorCliente(cliente);
        return Ok(result);
    }

    [HttpGet("clientes")]
    public ActionResult<List<ClienteResumen>> ListarClientes()
    {
        var result = _cuentaService.ListarClientes();
        return Ok(result);
    }

    [HttpPost("cliente")]
    public ActionResult<Resultado> RegistrarCliente([FromBody] RegistrarClienteRequest request)
    {
        var result = _cuentaService.RegistrarCliente(
            request.Paterno, request.Materno, request.Nombre,
            request.Dni, request.Ciudad, request.Direccion,
            request.Telefono, request.Email);
        return Ok(result);
    }

    [HttpPost]
    public ActionResult<Resultado> RegistrarCuenta([FromBody] RegistrarCuentaRequest request)
    {
        var result = _cuentaService.RegistrarCuenta(request.Cliente, request.Moneda);
        return Ok(result);
    }

    [HttpDelete("{cuenta}")]
    public ActionResult<Resultado> EliminarCuenta(string cuenta)
    {
        var result = _cuentaService.EliminarCuenta(cuenta);
        return Ok(result);
    }
}

public class DepositarRequest
{
    public string Cuenta { get; set; } = string.Empty;
    public string Monto { get; set; } = string.Empty;
    public string Moneda { get; set; } = string.Empty;
}

public class RetirarRequest
{
    public string Cuenta { get; set; } = string.Empty;
    public string Monto { get; set; } = string.Empty;
    public string Moneda { get; set; } = string.Empty;
}

public class TransferirRequest
{
    public string Origen { get; set; } = string.Empty;
    public string Destino { get; set; } = string.Empty;
    public string Monto { get; set; } = string.Empty;
    public string Moneda { get; set; } = string.Empty;
}

public class RegistrarClienteRequest
{
    public string Paterno { get; set; } = string.Empty;
    public string Materno { get; set; } = string.Empty;
    public string Nombre { get; set; } = string.Empty;
    public string Dni { get; set; } = string.Empty;
    public string Ciudad { get; set; } = string.Empty;
    public string Direccion { get; set; } = string.Empty;
    public string Telefono { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
}

public class RegistrarCuentaRequest
{
    public string Cliente { get; set; } = string.Empty;
    public string Moneda { get; set; } = string.Empty;
}
