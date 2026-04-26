namespace Ec.Edu.Monster.Modelo;

public class ServicioMasa
{
    private readonly ClienteSoap cliente = new();

    public double KilogramosALibras(double kilogramos) => cliente.Ejecutar(servicio => servicio.KilogramosALibras(kilogramos));

    public double GramosAOnzas(double gramos) => cliente.Ejecutar(servicio => servicio.GramosAOnzas(gramos));

    public double ToneladasAKilogramos(double toneladas) => cliente.Ejecutar(servicio => servicio.ToneladasAKilogramos(toneladas));

    public double LibrasAOnzas(double libras) => cliente.Ejecutar(servicio => servicio.LibrasAOnzas(libras));

    public double MiligramosAGramos(double miligramos) => cliente.Ejecutar(servicio => servicio.MiligramosAGramos(miligramos));
}
