using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utils;

namespace Ec.Edu.Monster.Servicio;

public class ServicioTemperatura
{
    private readonly ClienteRest cliente = new();

    public Resultado CelsiusAFahrenheit(double celsius) => cliente.Convertir("temperatura", "celsiusAFahrenheit", celsius);
    public Resultado FahrenheitACelsius(double fahrenheit) => cliente.Convertir("temperatura", "fahrenheitACelsius", fahrenheit);
    public Resultado CelsiusAKelvin(double celsius) => cliente.Convertir("temperatura", "celsiusAKelvin", celsius);
    public Resultado KelvinACelsius(double kelvin) => cliente.Convertir("temperatura", "kelvinACelsius", kelvin);
    public Resultado FahrenheitAKelvin(double fahrenheit) => cliente.Convertir("temperatura", "fahrenheitAKelvin", fahrenheit);
}
