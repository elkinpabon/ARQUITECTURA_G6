using System.ServiceModel;

namespace Ec.Edu.Monster.Contrato;

[ServiceContract(Namespace = "http://conuni.monster.edu.ec/soap")]
public interface IConuniServicio
{
    [OperationContract]
    bool IniciarSesion(string usuario, string contrasena);

    [OperationContract]
    double MetrosAPies(double metros);

    [OperationContract]
    double KilometrosAMillas(double kilometros);

    [OperationContract]
    double CentimetrosAPulgadas(double centimetros);

    [OperationContract]
    double YardasAMetros(double yardas);

    [OperationContract]
    double MilimetrosAPulgadas(double milimetros);

    [OperationContract]
    double KilogramosALibras(double kilogramos);

    [OperationContract]
    double GramosAOnzas(double gramos);

    [OperationContract]
    double ToneladasAKilogramos(double toneladas);

    [OperationContract]
    double LibrasAOnzas(double libras);

    [OperationContract]
    double MiligramosAGramos(double miligramos);

    [OperationContract]
    double CelsiusAFahrenheit(double celsius);

    [OperationContract]
    double FahrenheitACelsius(double fahrenheit);

    [OperationContract]
    double CelsiusAKelvin(double celsius);

    [OperationContract]
    double KelvinACelsius(double kelvin);

    [OperationContract]
    double FahrenheitAKelvin(double fahrenheit);

    [OperationContract]
    double KelvinAFahrenheit(double kelvin);
}
