using CoreWCF;
using Ec.Edu.Monster.Contrato;
using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;

namespace Ec.Edu.Monster.Controlador;

[ServiceBehavior(InstanceContextMode = InstanceContextMode.PerCall)]
public class CONUNI : IConuniServicio
{
    private readonly ServicioAutenticacion autenticacion = new();
    private readonly ServicioLongitud longitud = new();
    private readonly ServicioMasa masa = new();
    private readonly ServicioTemperatura temperatura = new();

    public bool IniciarSesion(string usuario, string contrasena)
    {
        return autenticacion.Validar(new Credencial
        {
            Usuario = usuario,
            Contrasena = contrasena
        });
    }

    public double MetrosAPies(double metros) => longitud.MetrosAPies(metros);

    public double KilometrosAMillas(double kilometros) => longitud.KilometrosAMillas(kilometros);

    public double CentimetrosAPulgadas(double centimetros) => longitud.CentimetrosAPulgadas(centimetros);

    public double YardasAMetros(double yardas) => longitud.YardasAMetros(yardas);

    public double MilimetrosAPulgadas(double milimetros) => longitud.MilimetrosAPulgadas(milimetros);

    public double KilogramosALibras(double kilogramos) => masa.KilogramosALibras(kilogramos);

    public double GramosAOnzas(double gramos) => masa.GramosAOnzas(gramos);

    public double ToneladasAKilogramos(double toneladas) => masa.ToneladasAKilogramos(toneladas);

    public double LibrasAOnzas(double libras) => masa.LibrasAOnzas(libras);

    public double MiligramosAGramos(double miligramos) => masa.MiligramosAGramos(miligramos);

    public double CelsiusAFahrenheit(double celsius) => temperatura.CelsiusAFahrenheit(celsius);

    public double FahrenheitACelsius(double fahrenheit) => temperatura.FahrenheitACelsius(fahrenheit);

    public double CelsiusAKelvin(double celsius) => temperatura.CelsiusAKelvin(celsius);

    public double KelvinACelsius(double kelvin) => temperatura.KelvinACelsius(kelvin);

    public double FahrenheitAKelvin(double fahrenheit) => temperatura.FahrenheitAKelvin(fahrenheit);

    public double KelvinAFahrenheit(double kelvin) => temperatura.KelvinAFahrenheit(kelvin);
}
