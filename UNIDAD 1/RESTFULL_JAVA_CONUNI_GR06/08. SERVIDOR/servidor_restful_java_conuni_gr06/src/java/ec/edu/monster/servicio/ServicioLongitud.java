package ec.edu.monster.servicio;

public class ServicioLongitud {

    public double metrosAPies(double metros) {
        return metros * 3.28084;
    }

    public double kilometrosAMillas(double kilometros) {
        return kilometros * 0.621371;
    }

    public double centimetrosAPulgadas(double centimetros) {
        return centimetros / 2.54;
    }

    public double yardasAMetros(double yardas) {
        return yardas / 1.09361;
    }

    public double milimetrosAPulgadas(double milimetros) {
        return milimetros * 0.0393701;
    }
}
