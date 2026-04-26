using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;

namespace Ec.Edu.Monster.Controlador;

public class AplicacionControlador
{
    private readonly ServicioAutenticacion autenticacion = new();
    private readonly ServicioLongitud longitud = new();
    private readonly ServicioMasa masa = new();
    private readonly ServicioTemperatura temperatura = new();

    public Resultado IniciarSesion(string usuario, string contrasena)
    {
        try
        {
            return autenticacion.Autenticar(usuario, contrasena);
        }
        catch (Exception ex)
        {
            return new Resultado
            {
                Exito = false,
                Mensaje = $"No se pudo conectar al servidor REST: {ex.Message}"
            };
        }
    }

    public async Task<Resultado> IniciarSesionAsync(string usuario, string contrasena)
    {
        try
        {
            return await autenticacion.AutenticarAsync(usuario, contrasena).ConfigureAwait(false);
        }
        catch (Exception ex)
        {
            return new Resultado
            {
                Exito = false,
                Mensaje = $"No se pudo conectar al servidor REST: {ex.Message}"
            };
        }
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
