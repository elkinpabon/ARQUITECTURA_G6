using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utilidades;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controladores;

[Controller]
[FiltroSesion]
public class ControladorLongitud : Controller
{
    private readonly ServicioLongitud servicio = new();

    [HttpGet]
    public IActionResult Index()
    {
        ViewBag.OperacionSeleccionada = "metrosAPies";
        ViewBag.ValorIngresado = string.Empty;
        return View("~/Views/Longitud/Index.cshtml", null);
    }

    [HttpPost]
    public IActionResult Index(string operacion, string valor)
    {
        if (!double.TryParse(valor.Replace(',', '.'), System.Globalization.NumberStyles.Any, System.Globalization.CultureInfo.InvariantCulture, out var valorNumero))
        {
            ViewBag.OperacionSeleccionada = operacion;
            ViewBag.ValorIngresado = valor;
            return View("~/Views/Longitud/Index.cshtml", new Resultado { Exito = false, Mensaje = "Ingresa un numero valido" });
        }

        var resultado = operacion switch
        {
            "metrosAPies" => new Resultado { Exito = true, Valor = servicio.MetrosAPies(valorNumero) },
            "kilometrosAMillas" => new Resultado { Exito = true, Valor = servicio.KilometrosAMillas(valorNumero) },
            "centimetrosAPulgadas" => new Resultado { Exito = true, Valor = servicio.CentimetrosAPulgadas(valorNumero) },
            "yardasAMetros" => new Resultado { Exito = true, Valor = servicio.YardasAMetros(valorNumero) },
            "milimetrosAPulgadas" => new Resultado { Exito = true, Valor = servicio.MilimetrosAPulgadas(valorNumero) },
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };

        ViewBag.OperacionSeleccionada = operacion;
        ViewBag.ValorIngresado = valor;
        return View("~/Views/Longitud/Index.cshtml", new Resultado
        {
            Exito = resultado.Exito,
            Mensaje = resultado.Exito ? string.Empty : resultado.Mensaje,
            Valor = resultado.Valor
        });
    }
}
