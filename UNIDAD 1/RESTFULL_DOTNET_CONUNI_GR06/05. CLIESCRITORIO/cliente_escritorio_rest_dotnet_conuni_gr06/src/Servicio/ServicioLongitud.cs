using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utils;

namespace Ec.Edu.Monster.Servicio;

public class ServicioLongitud
{
    private readonly ClienteRest cliente = new();

    public Resultado MetrosAPies(double metros) => cliente.Convertir("longitud", "metrosAPies", metros);

    public Resultado KilometrosAMillas(double kilometros) => cliente.Convertir("longitud", "kilometrosAMillas", kilometros);

    public Resultado CentimetrosAPulgadas(double centimetros) => cliente.Convertir("longitud", "centimetrosAPulgadas", centimetros);

    public Resultado YardasAMetros(double yardas) => cliente.Convertir("longitud", "yardasAMetros", yardas);

    public Resultado MilimetrosAPulgadas(double milimetros) => cliente.Convertir("longitud", "milimetrosAPulgadas", milimetros);
}
