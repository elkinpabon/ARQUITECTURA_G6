namespace CLIMOVIL.Services;

public static class Constantes
{
    // "localhost" solo sirve si la app corre en la MISMA maquina (MAUI Windows).
    // En CELULAR FISICO (otra maquina) usa la IP LAN del PC servidor + abre el 5010 en el firewall.
    // Ejemplo celular fisico: public static string IpServidor { get; set; } = "192.168.1.102";
    public static string IpServidor { get; set; } = "localhost";
    public static string BaseUrl => $"http://{IpServidor}:5010";
}
