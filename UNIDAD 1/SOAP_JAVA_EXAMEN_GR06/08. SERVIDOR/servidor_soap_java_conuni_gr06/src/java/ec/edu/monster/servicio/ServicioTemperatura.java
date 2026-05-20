package ec.edu.monster.servicio;

public class ServicioTemperatura {

    public double celsiusAFahrenheit(double celsius) {
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public double fahrenheitACelsius(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }

    public double celsiusAKelvin(double celsius) {
        return celsius + 273.15;
    }

    public double kelvinACelsius(double kelvin) {
        return kelvin - 273.15;
    }

    public double fahrenheitAKelvin(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0 + 273.15;
    }
}
