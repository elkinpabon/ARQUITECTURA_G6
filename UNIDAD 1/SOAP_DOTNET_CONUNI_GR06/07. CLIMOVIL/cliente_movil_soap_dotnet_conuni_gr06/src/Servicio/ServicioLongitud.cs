namespace Ec.Edu.Monster.Servicio;

public class ServicioLongitud
{
    private readonly Utilidades.ClienteSoap cliente = new();

    public double MetrosAPies(double metros) => cliente.Ejecutar(servicio => servicio.MetrosAPies(metros));

    public double KilometrosAMillas(double kilometros) => cliente.Ejecutar(servicio => servicio.KilometrosAMillas(kilometros));

    public double CentimetrosAPulgadas(double centimetros) => cliente.Ejecutar(servicio => servicio.CentimetrosAPulgadas(centimetros));

    public double YardasAMetros(double yardas) => cliente.Ejecutar(servicio => servicio.YardasAMetros(yardas));

    public double MilimetrosAPulgadas(double milimetros) => cliente.Ejecutar(servicio => servicio.MilimetrosAPulgadas(milimetros));
}
