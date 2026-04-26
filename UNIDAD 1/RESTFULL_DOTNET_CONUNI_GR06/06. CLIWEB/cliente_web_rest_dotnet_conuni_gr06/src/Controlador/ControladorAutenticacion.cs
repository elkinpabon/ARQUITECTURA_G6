using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controlador;

[Controller]
public class ControladorAutenticacion : Controller
{
    private readonly ServicioAutenticacion autenticacion;

    public ControladorAutenticacion(ServicioAutenticacion autenticacion)
    {
        this.autenticacion = autenticacion;
    }

    [HttpGet]
    public IActionResult IniciarSesion()
    {
        return View("~/Views/Autenticacion/IniciarSesion.cshtml", new Resultado());
    }

    [HttpPost]
    public IActionResult IniciarSesion(string usuario, string contrasena)
    {
        if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(contrasena))
        {
            return View("~/Views/Autenticacion/IniciarSesion.cshtml", new Resultado
            {
                Exito = false,
                Mensaje = "Completa usuario y contrasena"
            });
        }

        var resultado = autenticacion.Autenticar(usuario, contrasena);
        if (resultado.Exito)
        {
            HttpContext.Session.SetString("Usuario", usuario);
            return RedirectToAction("Index", "ControladorMenu");
        }

        return View("~/Views/Autenticacion/IniciarSesion.cshtml", resultado);
    }
}
