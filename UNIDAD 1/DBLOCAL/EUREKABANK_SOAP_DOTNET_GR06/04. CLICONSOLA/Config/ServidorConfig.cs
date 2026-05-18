namespace CLICONSOLA.Config
{
    public static class ServidorConfig
    {
        private static string _baseUrl = "http://localhost:5000";

        public static string BaseUrl
        {
            get => _baseUrl;
            set => _baseUrl = value.TrimEnd('/');
        }

        public static string WsLoginUrl => $"{_baseUrl}/WSLogin.asmx";
        public static string WsCuentaUrl => $"{_baseUrl}/WSCuenta.asmx";
        public static string WsMovimientoUrl => $"{_baseUrl}/WSMovimiento.asmx";
    }
}
