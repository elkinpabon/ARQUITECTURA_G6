namespace Ec.Edu.Monster.Servicio;

public class ServicioMasa
{
    public double KilogramosALibras(double kilogramos) => kilogramos * 2.20462;

    public double GramosAOnzas(double gramos) => gramos / 28.3495;

    public double ToneladasAKilogramos(double toneladas) => toneladas * 1000;

    public double LibrasAOnzas(double libras) => libras * 16;

    public double MiligramosAGramos(double miligramos) => miligramos / 1000;
}
