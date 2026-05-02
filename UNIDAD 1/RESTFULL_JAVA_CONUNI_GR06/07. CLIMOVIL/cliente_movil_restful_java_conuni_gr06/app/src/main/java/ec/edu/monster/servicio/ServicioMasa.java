package ec.edu.monster.servicio;

import ec.edu.monster.util.ClienteRest;
import org.json.JSONObject;

public class ServicioMasa {

    public double kilogramosALibras(double kilogramos) throws Exception {
        return invocar("/masa/kilogramos-a-libras", kilogramos);
    }

    public double gramosAOnzas(double gramos) throws Exception {
        return invocar("/masa/gramos-a-onzas", gramos);
    }

    public double toneladasAKilogramos(double toneladas) throws Exception {
        return invocar("/masa/toneladas-a-kilogramos", toneladas);
    }

    public double librasAOnzas(double libras) throws Exception {
        return invocar("/masa/libras-a-onzas", libras);
    }

    public double miligramosAGramos(double miligramos) throws Exception {
        return invocar("/masa/miligramos-a-gramos", miligramos);
    }

    private double invocar(String ruta, double valor) throws Exception {
        JSONObject respuesta = ClienteRest.get(ruta, valor);
        return respuesta.getDouble("valor");
    }
}
