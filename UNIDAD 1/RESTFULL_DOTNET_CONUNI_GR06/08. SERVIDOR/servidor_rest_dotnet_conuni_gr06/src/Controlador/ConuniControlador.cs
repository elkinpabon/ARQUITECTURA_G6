using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;
using Microsoft.AspNetCore.Mvc;

namespace Ec.Edu.Monster.Controlador;

[ApiController]
[Route("api/conuni")]
public class ConuniControlador : ControllerBase
{
    private readonly ServicioAutenticacion autenticacion;
    private readonly ServicioLongitud longitud;
    private readonly ServicioMasa masa;
    private readonly ServicioTemperatura temperatura;

    public ConuniControlador(
        ServicioAutenticacion autenticacion,
        ServicioLongitud longitud,
        ServicioMasa masa,
        ServicioTemperatura temperatura)
    {
        this.autenticacion = autenticacion;
        this.longitud = longitud;
        this.masa = masa;
        this.temperatura = temperatura;
    }

    [HttpPost("login")]
    public ActionResult<Resultado> Login([FromBody] Credencial credencial)
    {
        return autenticacion.Validar(credencial);
    }

    [HttpGet("conversion/{categoria}/{operacion}")]
    public ActionResult<Resultado> Convertir(string categoria, string operacion, [FromQuery] double valor)
    {
        return categoria switch
        {
            "longitud" => ConvertirLongitud(operacion, valor),
            "masa" => ConvertirMasa(operacion, valor),
            "temperatura" => ConvertirTemperatura(operacion, valor),
            _ => new Resultado { Exito = false, Mensaje = "Categoria no valida" }
        };
    }

    private Resultado ConvertirLongitud(string operacion, double valor) => operacion switch
    {
        "metrosAPies" => new Resultado { Exito = true, Valor = longitud.MetrosAPies(valor) },
        "kilometrosAMillas" => new Resultado { Exito = true, Valor = longitud.KilometrosAMillas(valor) },
        "centimetrosAPulgadas" => new Resultado { Exito = true, Valor = longitud.CentimetrosAPulgadas(valor) },
        "yardasAMetros" => new Resultado { Exito = true, Valor = longitud.YardasAMetros(valor) },
        "milimetrosAPulgadas" => new Resultado { Exito = true, Valor = longitud.MilimetrosAPulgadas(valor) },
        _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
    };

    private Resultado ConvertirMasa(string operacion, double valor) => operacion switch
    {
        "kilogramosALibras" => new Resultado { Exito = true, Valor = masa.KilogramosALibras(valor) },
        "gramosAOnzas" => new Resultado { Exito = true, Valor = masa.GramosAOnzas(valor) },
        "toneladasAKilogramos" => new Resultado { Exito = true, Valor = masa.ToneladasAKilogramos(valor) },
        "librasAOnzas" => new Resultado { Exito = true, Valor = masa.LibrasAOnzas(valor) },
        "miligramosAGramos" => new Resultado { Exito = true, Valor = masa.MiligramosAGramos(valor) },
        _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
    };

    private Resultado ConvertirTemperatura(string operacion, double valor) => operacion switch
    {
        "celsiusAFahrenheit" => new Resultado { Exito = true, Valor = temperatura.CelsiusAFahrenheit(valor) },
        "fahrenheitACelsius" => new Resultado { Exito = true, Valor = temperatura.FahrenheitACelsius(valor) },
        "celsiusAKelvin" => new Resultado { Exito = true, Valor = temperatura.CelsiusAKelvin(valor) },
        "kelvinACelsius" => new Resultado { Exito = true, Valor = temperatura.KelvinACelsius(valor) },
        "fahrenheitAKelvin" => new Resultado { Exito = true, Valor = temperatura.FahrenheitAKelvin(valor) },
        _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
    };
}
