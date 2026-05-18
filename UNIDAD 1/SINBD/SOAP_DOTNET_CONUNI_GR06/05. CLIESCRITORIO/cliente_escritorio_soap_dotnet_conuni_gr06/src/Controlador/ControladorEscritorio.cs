using Ec.Edu.Monster.Modelo;

namespace Ec.Edu.Monster.Controlador;

public class ControladorEscritorio
{
    private readonly ServicioAutenticacion autenticacion = new();
    private readonly ServicioLongitud longitud = new();
    private readonly ServicioMasa masa = new();
    private readonly ServicioTemperatura temperatura = new();

    public Resultado IniciarSesion(string usuario, string contrasena)
    {
        var exito = autenticacion.Autenticar(usuario, contrasena);
        return new Resultado
        {
            Exito = exito,
            Mensaje = exito ? "Sesion iniciada" : "Credenciales invalidas"
        };
    }

    public Resultado ConvertirLongitud(string operacion, double valor)
    {
        return operacion switch
        {
            "metrosAPies" => new Resultado { Exito = true, Valor = longitud.MetrosAPies(valor) },
            "kilometrosAMillas" => new Resultado { Exito = true, Valor = longitud.KilometrosAMillas(valor) },
            "centimetrosAPulgadas" => new Resultado { Exito = true, Valor = longitud.CentimetrosAPulgadas(valor) },
            "yardasAMetros" => new Resultado { Exito = true, Valor = longitud.YardasAMetros(valor) },
            "milimetrosAPulgadas" => new Resultado { Exito = true, Valor = longitud.MilimetrosAPulgadas(valor) },
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };
    }

    public Resultado ConvertirMasa(string operacion, double valor)
    {
        return operacion switch
        {
            "kilogramosALibras" => new Resultado { Exito = true, Valor = masa.KilogramosALibras(valor) },
            "gramosAOnzas" => new Resultado { Exito = true, Valor = masa.GramosAOnzas(valor) },
            "toneladasAKilogramos" => new Resultado { Exito = true, Valor = masa.ToneladasAKilogramos(valor) },
            "librasAOnzas" => new Resultado { Exito = true, Valor = masa.LibrasAOnzas(valor) },
            "miligramosAGramos" => new Resultado { Exito = true, Valor = masa.MiligramosAGramos(valor) },
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };
    }

    public Resultado ConvertirTemperatura(string operacion, double valor)
    {
        return operacion switch
        {
            "celsiusAFahrenheit" => new Resultado { Exito = true, Valor = temperatura.CelsiusAFahrenheit(valor) },
            "fahrenheitACelsius" => new Resultado { Exito = true, Valor = temperatura.FahrenheitACelsius(valor) },
            "celsiusAKelvin" => new Resultado { Exito = true, Valor = temperatura.CelsiusAKelvin(valor) },
            "kelvinACelsius" => new Resultado { Exito = true, Valor = temperatura.KelvinACelsius(valor) },
            "fahrenheitAKelvin" => new Resultado { Exito = true, Valor = temperatura.FahrenheitAKelvin(valor) },
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };
    }
}
