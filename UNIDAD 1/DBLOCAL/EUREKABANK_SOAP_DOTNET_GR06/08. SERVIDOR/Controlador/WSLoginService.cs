using SERVIDOR.Servicio;

namespace SERVIDOR.Controlador
{
    public class WSLoginService : IWSLogin
    {
        private readonly LoginService loginService = new LoginService();

        public bool IniciarSesion(string usuario, string clave)
        {
            return loginService.Login(usuario, clave);
        }

        public string ClienteDeUsuario(string usuario)
        {
            return loginService.ClienteDeUsuario(usuario);
        }
    }
}
