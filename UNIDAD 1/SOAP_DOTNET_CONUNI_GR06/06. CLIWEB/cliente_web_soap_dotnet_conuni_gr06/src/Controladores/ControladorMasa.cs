using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utilidades;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controladores;

[Controller]
[FiltroSesion]
public class ControladorMasa : Controller
{
    private readonly ServicioMasa servicio = new();

    [HttpGet]
    public IActionResult Index()
    {
        ViewBag.OperacionSeleccionada = "kilogramosALibras";
        ViewBag.ValorIngresado = string.Empty;
        return View("~/Views/Masa/Index.cshtml", null);
    }

    [HttpPost]
    public IActionResult Index(string operacion, string valor)
    {
        if (!double.TryParse(valor.Replace(',', '.'), System.Globalization.NumberStyles.Any, System.Globalization.CultureInfo.InvariantCulture, out var valorNumero))
        {
            ViewBag.OperacionSeleccionada = operacion;
            ViewBag.ValorIngresado = valor;
            return View("~/Views/Masa/Index.cshtml", new Resultado { Exito = false, Mensaje = "Ingresa un numero valido" });
        }

        var resultado = operacion switch
        {
            "kilogramosALibras" => new Resultado { Exito = true, Valor = servicio.KilogramosALibras(valorNumero) },
            "gramosAOnzas" => new Resultado { Exito = true, Valor = servicio.GramosAOnzas(valorNumero) },
            "toneladasAKilogramos" => new Resultado { Exito = true, Valor = servicio.ToneladasAKilogramos(valorNumero) },
            "librasAOnzas" => new Resultado { Exito = true, Valor = servicio.LibrasAOnzas(valorNumero) },
            "miligramosAGramos" => new Resultado { Exito = true, Valor = servicio.MiligramosAGramos(valorNumero) },
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };

        ViewBag.OperacionSeleccionada = operacion;
        ViewBag.ValorIngresado = valor;
        return View("~/Views/Masa/Index.cshtml", new Resultado
        {
            Exito = resultado.Exito,
            Mensaje = resultado.Exito ? string.Empty : resultado.Mensaje,
            Valor = resultado.Valor
        });
    }
}
