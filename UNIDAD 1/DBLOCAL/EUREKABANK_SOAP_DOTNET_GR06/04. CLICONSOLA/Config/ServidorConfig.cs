namespace CLICONSOLA.Config
{
    public static class ServidorConfig
    {
        private static string _baseUrl = Constantes.BaseUrl;

        public static string BaseUrl
        {
            get => _baseUrl;
            set
            {
                Constantes.BaseUrl = value;
                _baseUrl = Constantes.BaseUrl;
            }
        }

        public static string WsLoginUrl => $"{_baseUrl}/WSLogin.asmx";
        public static string WsCuentaUrl => $"{_baseUrl}/WSCuenta.asmx";
        public static string WsMovimientoUrl => $"{_baseUrl}/WSMovimiento.asmx";
    }
}
