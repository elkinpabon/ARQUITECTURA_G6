package ec.edu.monster.servicio;

import ec.edu.monster.util.ClienteRest;
import org.json.JSONObject;

public class ServicioLongitud {

    public double metrosAPies(double metros) throws Exception {
        return invocar("/longitud/metros-a-pies", metros);
    }

    public double kilometrosAMillas(double kilometros) throws Exception {
        return invocar("/longitud/kilometros-a-millas", kilometros);
    }

    public double centimetrosAPulgadas(double centimetros) throws Exception {
        return invocar("/longitud/centimetros-a-pulgadas", centimetros);
    }

    public double yardasAMetros(double yardas) throws Exception {
        return invocar("/longitud/yardas-a-metros", yardas);
    }

    public double milimetrosAPulgadas(double milimetros) throws Exception {
        return invocar("/longitud/milimetros-a-pulgadas", milimetros);
    }

    private double invocar(String ruta, double valor) throws Exception {
        JSONObject respuesta = ClienteRest.get(ruta, valor);
        return respuesta.getDouble("valor");
    }
}
