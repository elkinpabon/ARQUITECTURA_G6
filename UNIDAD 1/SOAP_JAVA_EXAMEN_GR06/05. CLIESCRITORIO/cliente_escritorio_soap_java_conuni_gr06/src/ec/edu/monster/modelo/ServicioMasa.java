package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioMasa {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public double kilogramosALibras(double kilogramos) throws Exception {
        return invocarUnario("kilogramosALibras", "kilogramos", kilogramos);
    }

    public double gramosAOnzas(double gramos) throws Exception {
        return invocarUnario("gramosAOnzas", "gramos", gramos);
    }

    public double toneladasAKilogramos(double toneladas) throws Exception {
        return invocarUnario("toneladasAKilogramos", "toneladas", toneladas);
    }

    public double librasAOnzas(double libras) throws Exception {
        return invocarUnario("librasAOnzas", "libras", libras);
    }

    public double miligramosAGramos(double miligramos) throws Exception {
        return invocarUnario("miligramosAGramos", "miligramos", miligramos);
    }

    private double invocarUnario(String operacion, String nombreParametro, double valor) throws Exception {
        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put(nombreParametro, String.valueOf(valor));
        String respuesta = clienteSoap.invocar(operacion, parametros);
        return Double.parseDouble(respuesta);
    }
}
