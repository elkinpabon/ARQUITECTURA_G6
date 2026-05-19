namespace Ec.Edu.Monster.Utils;

public static class ConstantesPruebas
{
    public static string IpServidor { get; set; } = "192.168.1.54";
    public static int PuertoServidor { get; set; } = 5000;

    public static string DireccionServicio => $"http://{IpServidor}:{PuertoServidor}/api/conuni";
}
