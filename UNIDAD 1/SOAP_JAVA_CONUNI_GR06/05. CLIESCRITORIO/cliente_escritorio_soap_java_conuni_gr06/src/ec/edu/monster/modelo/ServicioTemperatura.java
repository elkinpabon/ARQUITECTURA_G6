package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioTemperatura {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public double celsiusAFahrenheit(double celsius) throws Exception {
        return invocarUnario("celsiusAFahrenheit", "celsius", celsius);
    }

    public double fahrenheitACelsius(double fahrenheit) throws Exception {
        return invocarUnario("fahrenheitACelsius", "fahrenheit", fahrenheit);
    }

    public double celsiusAKelvin(double celsius) throws Exception {
        return invocarUnario("celsiusAKelvin", "celsius", celsius);
    }

    public double kelvinACelsius(double kelvin) throws Exception {
        return invocarUnario("kelvinACelsius", "kelvin", kelvin);
    }

    public double fahrenheitAKelvin(double fahrenheit) throws Exception {
        return invocarUnario("fahrenheitAKelvin", "fahrenheit", fahrenheit);
    }

    private double invocarUnario(String operacion, String nombreParametro, double valor) throws Exception {
        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put(nombreParametro, String.valueOf(valor));
        String respuesta = clienteSoap.invocar(operacion, parametros);
        return Double.parseDouble(respuesta);
    }
}
