using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controlador;

[Controller]
public class ControladorCerrarSesion : Controller
{
    [HttpPost]
    public IActionResult CerrarSesion()
    {
        HttpContext.Session.Clear();
        return RedirectToAction("IniciarSesion", "ControladorAutenticacion");
    }
}
