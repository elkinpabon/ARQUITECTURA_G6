using cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_web_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace cliente_web_rest_dotnet_ticketpremium_gr06.Controlador;

public class CuentaController(ITicketPremiumApiService servicioApi) : Controller
{
    public IActionResult Login()
    {
        if (!string.IsNullOrWhiteSpace(HttpContext.Session.GetString("Usuario")))
        {
            return RedirectToAction("Index", "Inicio");
        }

        return View();
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Login(AutenticacionRequest request)
    {
        try
        {
            var respuesta = await servicioApi.IniciarSesionAsync(request);
            HttpContext.Session.SetString("Usuario", respuesta.NombreCompleto);
            HttpContext.Session.SetString("UsuarioLogin", respuesta.Usuario);
            return RedirectToAction("Index", "Inicio");
        }
        catch (Exception ex)
        {
            ViewBag.Error = ex.Message;
            return View(request);
        }
    }

    public IActionResult Registro() => View();

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Registro(RegistroUsuarioRequest request)
    {
        try
        {
            var respuesta = await servicioApi.RegistrarUsuarioAsync(request);
            HttpContext.Session.SetString("Usuario", respuesta.NombreCompleto);
            HttpContext.Session.SetString("UsuarioLogin", respuesta.Usuario);
            return RedirectToAction("Index", "Inicio");
        }
        catch (Exception ex)
        {
            ViewBag.Error = ex.Message;
            return View(request);
        }
    }

    public IActionResult CerrarSesion()
    {
        HttpContext.Session.Clear();
        return RedirectToAction("Login");
    }
}
