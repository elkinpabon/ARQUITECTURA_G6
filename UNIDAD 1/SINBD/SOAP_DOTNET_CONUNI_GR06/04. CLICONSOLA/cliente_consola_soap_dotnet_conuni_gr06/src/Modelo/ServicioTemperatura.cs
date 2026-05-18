namespace Ec.Edu.Monster.Modelo;

public class ServicioTemperatura
{
    private readonly ClienteSoap cliente = new();

    public double CelsiusAFahrenheit(double celsius) => cliente.Ejecutar(servicio => servicio.CelsiusAFahrenheit(celsius));

    public double FahrenheitACelsius(double fahrenheit) => cliente.Ejecutar(servicio => servicio.FahrenheitACelsius(fahrenheit));

    public double CelsiusAKelvin(double celsius) => cliente.Ejecutar(servicio => servicio.CelsiusAKelvin(celsius));

    public double KelvinACelsius(double kelvin) => cliente.Ejecutar(servicio => servicio.KelvinACelsius(kelvin));

    public double FahrenheitAKelvin(double fahrenheit) => cliente.Ejecutar(servicio => servicio.FahrenheitAKelvin(fahrenheit));

    public double KelvinAFahrenheit(double kelvin) => cliente.Ejecutar(servicio => servicio.KelvinAFahrenheit(kelvin));
}
