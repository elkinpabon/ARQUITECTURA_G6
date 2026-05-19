namespace CLIESCRITORIO.Config
{
    public static class Constantes
    {
        public static string IpServidor { get; set; } = "192.168.1.54";
        public static int PuertoServidor { get; set; } = 5000;

        public static string BaseUrl
        {
            get => $"http://{IpServidor}:{PuertoServidor}";
            set
            {
                if (string.IsNullOrWhiteSpace(value)) return;
                var text = value.Trim();
                if (!text.Contains("://", StringComparison.Ordinal))
                    text = "http://" + text;
                if (Uri.TryCreate(text, UriKind.Absolute, out var uri))
                {
                    IpServidor = uri.Host;
                    PuertoServidor = uri.IsDefaultPort ? 5000 : uri.Port;
                }
            }
        }
    }
}
