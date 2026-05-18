using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;

namespace Ec.Edu.Monster.Controlador;

public class ControladorEscritorio
{
    private readonly ServicioAutenticacion autenticacion = new();
    private readonly ServicioLongitud longitud = new();
    private readonly ServicioMasa masa = new();
    private readonly ServicioTemperatura temperatura = new();

    public Resultado IniciarSesion(string usuario, string contrasena)
    {
        return autenticacion.Autenticar(usuario, contrasena);
    }

    public Resultado ConvertirLongitud(string operacion, double valor)
    {
        return operacion switch
        {
            "metrosAPies" => longitud.MetrosAPies(valor),
            "kilometrosAMillas" => longitud.KilometrosAMillas(valor),
            "centimetrosAPulgadas" => longitud.CentimetrosAPulgadas(valor),
            "yardasAMetros" => longitud.YardasAMetros(valor),
            "milimetrosAPulgadas" => longitud.MilimetrosAPulgadas(valor),
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };
    }

    public Resultado ConvertirMasa(string operacion, double valor)
    {
        return operacion switch
        {
            "kilogramosALibras" => masa.KilogramosALibras(valor),
            "gramosAOnzas" => masa.GramosAOnzas(valor),
            "toneladasAKilogramos" => masa.ToneladasAKilogramos(valor),
            "librasAOnzas" => masa.LibrasAOnzas(valor),
            "miligramosAGramos" => masa.MiligramosAGramos(valor),
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };
    }

    public Resultado ConvertirTemperatura(string operacion, double valor)
    {
        return operacion switch
        {
            "celsiusAFahrenheit" => temperatura.CelsiusAFahrenheit(valor),
            "fahrenheitACelsius" => temperatura.FahrenheitACelsius(valor),
            "celsiusAKelvin" => temperatura.CelsiusAKelvin(valor),
            "kelvinACelsius" => temperatura.KelvinACelsius(valor),
            "fahrenheitAKelvin" => temperatura.FahrenheitAKelvin(valor),
            _ => new Resultado { Exito = false, Mensaje = "Operacion no valida" }
        };
    }
}
