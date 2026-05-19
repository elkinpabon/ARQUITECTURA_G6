namespace CLICONSOLA.Config
{
    public static class ServidorConfig
    {
        public static string BaseUrl => Constantes.BaseUrl;

        public static string WsLoginUrl => $"{BaseUrl}/WSLogin.asmx";
        public static string WsCuentaUrl => $"{BaseUrl}/WSCuenta.asmx";
        public static string WsMovimientoUrl => $"{BaseUrl}/WSMovimiento.asmx";
    }
}
