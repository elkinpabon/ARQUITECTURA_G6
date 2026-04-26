namespace Ec.Edu.Monster.Servicio;

public class ServicioMasa
{
    public double KilogramosALibras(double kilogramos) => kilogramos * 2.20462;

    public double GramosAOnzas(double gramos) => gramos * 0.03527396;

    public double ToneladasAKilogramos(double toneladas) => toneladas * 1000;

    public double LibrasAOnzas(double libras) => libras * 16;

    public double MiligramosAGramos(double miligramos) => miligramos / 1000;
}
