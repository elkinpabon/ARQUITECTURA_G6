using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utils;

namespace Ec.Edu.Monster.Servicio;

public class ServicioMasa
{
    private readonly ClienteRest cliente = new();

    public Resultado KilogramosALibras(double kilogramos) => cliente.Convertir("masa", "kilogramosALibras", kilogramos);
    public Resultado GramosAOnzas(double gramos) => cliente.Convertir("masa", "gramosAOnzas", gramos);
    public Resultado ToneladasAKilogramos(double toneladas) => cliente.Convertir("masa", "toneladasAKilogramos", toneladas);
    public Resultado LibrasAOnzas(double libras) => cliente.Convertir("masa", "librasAOnzas", libras);
    public Resultado MiligramosAGramos(double miligramos) => cliente.Convertir("masa", "miligramosAGramos", miligramos);
}
