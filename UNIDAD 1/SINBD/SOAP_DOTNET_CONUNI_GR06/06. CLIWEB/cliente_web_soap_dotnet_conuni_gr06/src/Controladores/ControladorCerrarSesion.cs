using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controladores;

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
