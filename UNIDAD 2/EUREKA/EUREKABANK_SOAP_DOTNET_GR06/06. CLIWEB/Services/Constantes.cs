namespace CLIWEB.Services
{
    public static class Constantes
    {
        // Local (misma maquina) para pruebas. Para conectar desde OTRA maquina,
        // reemplaza "localhost" por la IP LAN del PC servidor.
        // Ejemplo otra maquina: public static string IpServidor { get; set; } = "192.168.1.102";
        public static string IpServidor { get; set; } = "localhost";

        public static string BaseUrl
        {
            get => $"http://{IpServidor}:5000";
        }
    }
}
