using Microsoft.AspNetCore.Mvc;
using SERVIDOR.Services;

namespace SERVIDOR.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly LoginService _loginService;

    public AuthController(LoginService loginService)
    {
        _loginService = loginService;
    }

    [HttpPost("login")]
    public IActionResult Login([FromBody] LoginRequest request)
    {
        var result = _loginService.Login(request.Usuario, request.Clave);
        return Ok(result);
    }

    [HttpGet("cliente/{usuario}")]
    public IActionResult ClienteDeUsuario(string usuario)
    {
        var result = _loginService.ClienteDeUsuario(usuario);
        return Ok(result);
    }
}

public class LoginRequest
{
    public string Usuario { get; set; } = string.Empty;
    public string Clave { get; set; } = string.Empty;
}
