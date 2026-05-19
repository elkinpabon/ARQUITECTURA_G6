using cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_web_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace cliente_web_rest_dotnet_ticketpremium_gr06.Controlador;

public class PartidosController(ITicketPremiumApiService servicioApi) : BaseController
{
    public async Task<IActionResult> Index()
    {
        var redir = RedirigirSiNoSesion();
        if (redir is not EmptyResult)
        {
            return redir;
        }

        return View(new InicioViewModel { Partidos = await servicioApi.ObtenerPartidosAsync() });
    }

    public async Task<IActionResult> Localidades(int codigoPartido)
    {
        var redir = RedirigirSiNoSesion();
        if (redir is not EmptyResult)
        {
            return redir;
        }

        var partidos = await servicioApi.ObtenerPartidosAsync();
        var partido = partidos.FirstOrDefault(x => x.Codigo == codigoPartido);
        if (partido is null)
        {
            return NotFound();
        }

        return View(new CompraViewModel
        {
            Partido = partido,
            Localidades = await servicioApi.ObtenerLocalidadesAsync(codigoPartido)
        });
    }
}
