package ec.edu.monster.servicio;

public class ServicioMasa {

    public double kilogramosALibras(double kilogramos) {
        return kilogramos * 2.20462;
    }

    public double gramosAOnzas(double gramos) {
        return gramos * 0.035274;
    }

    public double toneladasAKilogramos(double toneladas) {
        return toneladas * 1000.0;
    }

    public double librasAOnzas(double libras) {
        return libras * 16.0;
    }

    public double miligramosAGramos(double miligramos) {
        return miligramos / 1000.0;
    }
}
