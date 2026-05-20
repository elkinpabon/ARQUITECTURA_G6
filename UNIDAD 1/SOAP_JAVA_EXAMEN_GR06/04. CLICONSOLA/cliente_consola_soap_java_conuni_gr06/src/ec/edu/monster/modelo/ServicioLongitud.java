package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioLongitud {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public double metrosAPies(double metros) throws Exception {
        return invocarUnario("metrosAPies", "metros", metros);
    }

    public double kilometrosAMillas(double kilometros) throws Exception {
        return invocarUnario("kilometrosAMillas", "kilometros", kilometros);
    }

    public double centimetrosAPulgadas(double centimetros) throws Exception {
        return invocarUnario("centimetrosAPulgadas", "centimetros", centimetros);
    }

    public double yardasAMetros(double yardas) throws Exception {
        return invocarUnario("yardasAMetros", "yardas", yardas);
    }

    public double milimetrosAPulgadas(double milimetros) throws Exception {
        return invocarUnario("milimetrosAPulgadas", "milimetros", milimetros);
    }

    private double invocarUnario(String operacion, String nombreParametro, double valor) throws Exception {
        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put(nombreParametro, String.valueOf(valor));
        String respuesta = clienteSoap.invocar(operacion, parametros);
        return Double.parseDouble(respuesta);
    }
}
