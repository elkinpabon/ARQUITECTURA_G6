namespace Ec.Edu.Monster.Utilidades;

public static class ConstantesSoap
{
    public const string IpServidor = "10.25.36.189";
    public const string PuertoServidor = "5000";

    public static string DireccionServicio => $"http://{IpServidor}:{PuertoServidor}/CONUNI.svc";
}
