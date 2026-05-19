using Microsoft.AspNetCore.Mvc;

namespace cliente_web_rest_dotnet_ticketpremium_gr06.Controlador;

public abstract class BaseController : Controller
{
    protected bool TieneSesion => !string.IsNullOrWhiteSpace(HttpContext.Session.GetString("Usuario"));

    protected IActionResult RedirigirSiNoSesion()
    {
        return TieneSesion ? new EmptyResult() : RedirectToAction("Login", "Cuenta");
    }
}
