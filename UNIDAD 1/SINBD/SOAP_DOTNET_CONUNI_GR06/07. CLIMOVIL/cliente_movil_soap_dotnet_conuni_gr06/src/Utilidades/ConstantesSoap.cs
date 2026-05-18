namespace Ec.Edu.Monster.Utilidades;

public static class ConstantesSoap
{
    public const string IpServidor = "192.168.1.54";
    public const string PuertoServidor = "5000";

    public static string DireccionServicio => $"http://{IpServidor}:{PuertoServidor}/CONUNI.svc";
}
