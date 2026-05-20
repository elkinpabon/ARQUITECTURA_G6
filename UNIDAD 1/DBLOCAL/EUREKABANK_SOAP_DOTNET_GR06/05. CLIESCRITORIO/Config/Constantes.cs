namespace CLIESCRITORIO.Config
{
    public static class Constantes
    {
        public static string IpServidor { get; set; } = "10.94.162.189";

        public static string BaseUrl
        {
            get => $"http://{IpServidor}:5000";
        }
    }
}
