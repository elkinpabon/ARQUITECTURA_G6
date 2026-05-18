using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controlador;

[Controller]
public class ControladorMenu : Controller
{
    [HttpGet]
    public IActionResult Index()
    {
        var usuario = HttpContext.Session.GetString("Usuario");
        if (string.IsNullOrWhiteSpace(usuario))
        {
            return RedirectToAction("IniciarSesion", "ControladorAutenticacion");
        }

        ViewBag.Usuario = usuario;
        return View("~/Views/Menu/Index.cshtml");
    }
}
