package ec.edu.monster.servicio;

import ec.edu.monster.util.ClienteRest;
import org.json.JSONObject;

public class ServicioTemperatura {

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
        JSONObject respuesta = ClienteRest.get(ruta, valor);
        return respuesta.getDouble("valor");
    }
}
