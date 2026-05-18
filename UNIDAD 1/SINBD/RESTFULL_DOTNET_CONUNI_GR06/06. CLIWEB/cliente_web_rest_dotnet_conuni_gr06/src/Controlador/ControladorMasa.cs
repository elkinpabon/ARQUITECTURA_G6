using System.Globalization;
using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controlador;

[Controller]
public class ControladorMasa : Controller
{
    private readonly ServicioMasa servicio;

    public ControladorMasa(ServicioMasa servicio)
    {
        this.servicio = servicio;
    }

    [HttpGet]
    public IActionResult Index()
    {
        if (!TieneSesion())
        {
            return RedirectToAction("IniciarSesion", "ControladorAutenticacion");
        }

        ViewBag.Usuario = HttpContext.Session.GetString("Usuario");
        return View("~/Views/Masa/Index.cshtml", new Resultado());
    }

    [HttpPost]
    public IActionResult Index(string operacion, string valor)
    {
        if (!TieneSesion())
        {
            return RedirectToAction("IniciarSesion", "ControladorAutenticacion");
        }

        ViewBag.Usuario = HttpContext.Session.GetString("Usuario");
        if (!double.TryParse((valor ?? string.Empty).Replace(',', '.'), NumberStyles.Any, CultureInfo.InvariantCulture, out var numero))
        {
            return View("~/Views/Masa/Index.cshtml", new Resultado { Exito = false, Mensaje = "Ingresa un numero valido" });
        }

        var resultado = operacion switch
        {
            "kilogramosALibras" => Formatear(servicio.KilogramosALibras(numero), numero, "kilogramos", "libras"),
            "gramosAOnzas" => Formatear(servicio.GramosAOnzas(numero), numero, "gramos", "onzas"),
            "toneladasAKilogramos" => Formatear(servicio.ToneladasAKilogramos(numero), numero, "toneladas", "kilogramos"),
            "librasAOnzas" => Formatear(servicio.LibrasAOnzas(numero), numero, "libras", "onzas"),
            "miligramosAGramos" => Formatear(servicio.MiligramosAGramos(numero), numero, "miligramos", "gramos"),
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };

        return View("~/Views/Masa/Index.cshtml", resultado);
    }

    private static Resultado Formatear(Resultado resultado, double entrada, string origen, string destino)
    {
        return new Resultado
        {
            Exito = resultado.Exito,
            Valor = resultado.Valor,
            Mensaje = resultado.Exito ? $"{entrada:0.##} {origen} = {resultado.Valor:0.000} {destino}" : resultado.Mensaje
        };
    }

    private bool TieneSesion() => !string.IsNullOrWhiteSpace(HttpContext.Session.GetString("Usuario"));
}
