package ec.edu.monster.modelo;

public class ServicioTemperatura {

    private final ClienteRest clienteRest = new ClienteRest();

    public double celsiusAFahrenheit(double celsius) throws Exception {
        return invocar("/temperatura/celsius-a-fahrenheit", celsius);
    }

    public double fahrenheitACelsius(double fahrenheit) throws Exception {
        return invocar("/temperatura/fahrenheit-a-celsius", fahrenheit);
    }

    public double celsiusAKelvin(double celsius) throws Exception {
        return invocar("/temperatura/celsius-a-kelvin", celsius);
    }

    public double kelvinACelsius(double kelvin) throws Exception {
        return invocar("/temperatura/kelvin-a-celsius", kelvin);
    }

    public double fahrenheitAKelvin(double fahrenheit) throws Exception {
        return invocar("/temperatura/fahrenheit-a-kelvin", fahrenheit);
    }

    private double invocar(String ruta, double valor) throws Exception {
        String respuesta = clienteRest.get(ruta, valor);
        return ClienteRest.extraerNumero(respuesta, "valor");
    }
}
