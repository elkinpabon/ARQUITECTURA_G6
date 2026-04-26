namespace Ec.Edu.Monster.Servicio;

public class ServicioTemperatura
{
    public double CelsiusAFahrenheit(double celsius) => (celsius * 9 / 5) + 32;

    public double FahrenheitACelsius(double fahrenheit) => (fahrenheit - 32) * 5 / 9;

    public double CelsiusAKelvin(double celsius) => celsius + 273.15;

    public double KelvinACelsius(double kelvin) => kelvin - 273.15;

    public double FahrenheitAKelvin(double fahrenheit) => CelsiusAKelvin(FahrenheitACelsius(fahrenheit));

    public double KelvinAFahrenheit(double kelvin) => CelsiusAFahrenheit(KelvinACelsius(kelvin));
}
