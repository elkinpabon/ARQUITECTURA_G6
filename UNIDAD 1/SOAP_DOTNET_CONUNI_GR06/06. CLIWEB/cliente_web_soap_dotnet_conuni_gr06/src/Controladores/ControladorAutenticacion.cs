using Ec.Edu.Monster.Modelo;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controladores;

[Controller]
public class ControladorAutenticacion : Controller
{
    private readonly ServicioAutenticacion autenticacion = new();

    [HttpGet]
    public IActionResult IniciarSesion()
    {
        return View("~/Views/Autenticacion/IniciarSesion.cshtml", null);
    }

    [HttpPost]
    public IActionResult IniciarSesion(string usuario, string contrasena)
    {
        var exito = autenticacion.Autenticar(usuario, contrasena);
        if (exito)
        {
            HttpContext.Session.SetString("Usuario", usuario);
            return RedirectToAction("Index", "ControladorMenu");
        }

        return View("~/Views/Autenticacion/IniciarSesion.cshtml", new Resultado
        {
            Exito = false,
            Mensaje = "Credenciales invalidas"
        });
    }
}
