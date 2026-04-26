using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utilidades;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controladores;

[Controller]
[FiltroSesion]
public class ControladorTemperatura : Controller
{
    private readonly ServicioTemperatura servicio = new();

    [HttpGet]
    public IActionResult Index()
    {
        ViewBag.OperacionSeleccionada = "celsiusAFahrenheit";
        ViewBag.ValorIngresado = string.Empty;
        return View("~/Views/Temperatura/Index.cshtml", null);
    }

    [HttpPost]
    public IActionResult Index(string operacion, string valor)
    {
        if (!double.TryParse(valor.Replace(',', '.'), System.Globalization.NumberStyles.Any, System.Globalization.CultureInfo.InvariantCulture, out var valorNumero))
        {
            ViewBag.OperacionSeleccionada = operacion;
            ViewBag.ValorIngresado = valor;
            return View("~/Views/Temperatura/Index.cshtml", new Resultado { Exito = false, Mensaje = "Ingresa un numero valido" });
        }

        var resultado = operacion switch
        {
            "celsiusAFahrenheit" => new Resultado { Exito = true, Valor = servicio.CelsiusAFahrenheit(valorNumero) },
            "fahrenheitACelsius" => new Resultado { Exito = true, Valor = servicio.FahrenheitACelsius(valorNumero) },
            "celsiusAKelvin" => new Resultado { Exito = true, Valor = servicio.CelsiusAKelvin(valorNumero) },
            "kelvinACelsius" => new Resultado { Exito = true, Valor = servicio.KelvinACelsius(valorNumero) },
            "fahrenheitAKelvin" => new Resultado { Exito = true, Valor = servicio.FahrenheitAKelvin(valorNumero) },
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };

        ViewBag.OperacionSeleccionada = operacion;
        ViewBag.ValorIngresado = valor;
        return View("~/Views/Temperatura/Index.cshtml", new Resultado
        {
            Exito = resultado.Exito,
            Mensaje = resultado.Exito ? string.Empty : resultado.Mensaje,
            Valor = resultado.Valor
        });
    }
}
