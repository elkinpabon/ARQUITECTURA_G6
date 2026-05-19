namespace CLICONSOLA.Config;

public static class Constantes
{
    public static string IpServidor { get; set; } = "192.168.1.54";
    public static string BaseUrl => $"http://{IpServidor}:5010";
}
