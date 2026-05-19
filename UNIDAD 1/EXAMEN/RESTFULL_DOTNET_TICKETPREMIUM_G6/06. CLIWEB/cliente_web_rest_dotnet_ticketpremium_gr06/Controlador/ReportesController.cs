using cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_web_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace cliente_web_rest_dotnet_ticketpremium_gr06.Controlador;

public class ReportesController(ITicketPremiumApiService servicioApi) : BaseController
{
    public async Task<IActionResult> Index(int? codigoPartido = null)
    {
        var redir = RedirigirSiNoSesion();
        if (redir is not EmptyResult)
        {
            return redir;
        }

        var partidos = await servicioApi.ObtenerPartidosAsync();
        var partido = partidos.FirstOrDefault(p => p.Codigo == codigoPartido) ?? partidos.FirstOrDefault();

        return View(new ReporteViewModel
        {
            Partidos = partidos,
            CodigoPartido = partido?.Codigo ?? 0,
            Registros = partido is null ? Array.Empty<ResumenVentaDto>() : await servicioApi.ObtenerReporteAsync(partido.Codigo)
        });
    }
}
