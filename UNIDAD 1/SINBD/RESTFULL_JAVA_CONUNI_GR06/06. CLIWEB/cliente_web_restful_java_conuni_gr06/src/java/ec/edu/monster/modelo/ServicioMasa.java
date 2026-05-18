package ec.edu.monster.modelo;

public class ServicioMasa {

    private final ClienteRest clienteRest = new ClienteRest();

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
        String respuesta = clienteRest.get(ruta, valor);
        return ClienteRest.extraerNumero(respuesta, "valor");
    }
}
