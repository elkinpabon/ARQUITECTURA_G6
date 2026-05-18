using System.Globalization;
using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controlador;

[Controller]
public class ControladorTemperatura : Controller
{
    private readonly ServicioTemperatura servicio;

    public ControladorTemperatura(ServicioTemperatura servicio)
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
        return View("~/Views/Temperatura/Index.cshtml", new Resultado());
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
            return View("~/Views/Temperatura/Index.cshtml", new Resultado { Exito = false, Mensaje = "Ingresa un numero valido" });
        }

        var resultado = operacion switch
        {
            "celsiusAFahrenheit" => Formatear(servicio.CelsiusAFahrenheit(numero), numero, "celsius", "fahrenheit"),
            "fahrenheitACelsius" => Formatear(servicio.FahrenheitACelsius(numero), numero, "fahrenheit", "celsius"),
            "celsiusAKelvin" => Formatear(servicio.CelsiusAKelvin(numero), numero, "celsius", "kelvin"),
            "kelvinACelsius" => Formatear(servicio.KelvinACelsius(numero), numero, "kelvin", "celsius"),
            "fahrenheitAKelvin" => Formatear(servicio.FahrenheitAKelvin(numero), numero, "fahrenheit", "kelvin"),
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };

        return View("~/Views/Temperatura/Index.cshtml", resultado);
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
