using System.Diagnostics;
using cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_web_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace cliente_web_rest_dotnet_ticketpremium_gr06.Controlador;

public class InicioController(ITicketPremiumApiService servicioApi) : BaseController
{
    public async Task<IActionResult> Index()
    {
        var redir = RedirigirSiNoSesion();
        if (redir is not EmptyResult)
        {
            return redir;
        }

        var partidos = await servicioApi.ObtenerPartidosAsync();
        return View(new InicioViewModel { Partidos = partidos.Take(3).ToList() });
    }

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error()
    {
        return View("~/Vista/Compartido/Error.cshtml", new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
    }
}
