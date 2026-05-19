using cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_web_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.AspNetCore.Mvc;

namespace cliente_web_rest_dotnet_ticketpremium_gr06.Controlador;

public class VentasController(ITicketPremiumApiService servicioApi) : BaseController
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

        return View("Comprar", new CompraViewModel
        {
            Partido = partido,
            Localidades = partido is null ? Array.Empty<LocalidadDto>() : await servicioApi.ObtenerLocalidadesAsync(partido.Codigo)
        });
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Comprar(CompraViewModel modelo)
    {
        var redir = RedirigirSiNoSesion();
        if (redir is not EmptyResult)
        {
            return redir;
        }

        var partidos = await servicioApi.ObtenerPartidosAsync();
        if (modelo.Partido is not null)
        {
            modelo.Partido = partidos.FirstOrDefault(p => p.Codigo == modelo.Partido.Codigo) ?? modelo.Partido;
        }
        modelo.Partido ??= partidos.FirstOrDefault();

        if (modelo.Partido is null)
        {
            modelo.Mensaje = "No hay partidos disponibles.";
            return View(modelo);
        }

        modelo.Localidades = await servicioApi.ObtenerLocalidadesAsync(modelo.Partido.Codigo);

        if (string.IsNullOrWhiteSpace(modelo.CodigoLocalidad))
        {
            modelo.Mensaje = "Seleccione una localidad.";
            return View(modelo);
        }

        try
        {
            modelo.Resultado = await servicioApi.ComprarAsync(new CompraRequest
            {
                CodigoPartido = modelo.Partido.Codigo,
                CodigoLocalidad = modelo.CodigoLocalidad,
                Cantidad = modelo.Cantidad
            });

            return View("Confirmacion", modelo);
        }
        catch (Exception ex)
        {
            modelo.Mensaje = ex.Message;
            return View(modelo);
        }
    }
}
