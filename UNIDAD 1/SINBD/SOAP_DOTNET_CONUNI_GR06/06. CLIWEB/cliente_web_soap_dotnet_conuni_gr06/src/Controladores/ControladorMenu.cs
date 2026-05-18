using Microsoft.AspNetCore.Mvc;
using Ec.Edu.Monster.Utilidades;
using Microsoft.AspNetCore.Http;

namespace Ec.Edu.Monster.Controladores;

[Controller]
[FiltroSesion]
public class ControladorMenu : Controller
{
    [HttpGet]
    public IActionResult Index()
    {
        ViewBag.Usuario = HttpContext.Session.GetString("Usuario") ?? string.Empty;
        return View("~/Views/Menu/Index.cshtml");
    }
}
