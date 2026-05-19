using SERVIDOR.Modelo;
using SERVIDOR.Persistencia;

namespace SERVIDOR.Servicio
{
    public class LoginService
    {
        private readonly UsuarioDAO usuarioDAO = new UsuarioDAO();

        public bool Login(string usuario, string clave)
        {
            if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(clave))
                return false;

            Usuario? u = usuarioDAO.BuscarPorUsuario(usuario.Trim());
            if (u == null) return false;
            if (!"ACTIVO".Equals(u.Estado, StringComparison.OrdinalIgnoreCase)) return false;

            string hash = SeguridadUtil.Sha1(clave);
            return hash.Equals(u.Clave, StringComparison.OrdinalIgnoreCase);
        }

        public string ClienteDeUsuario(string usuario)
        {
            if (string.IsNullOrWhiteSpace(usuario)) return string.Empty;

            Usuario? u = usuarioDAO.BuscarPorUsuario(usuario.Trim());
            if (u == null || string.IsNullOrEmpty(u.ClienteCodigo)) return string.Empty;
            return u.ClienteCodigo;
        }
    }
}
