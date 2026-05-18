using SERVIDOR.Data;
using SERVIDOR.Models;

namespace SERVIDOR.Services;

public class LoginService
{
    private readonly UsuarioDAO _usuarioDAO;

    public LoginService(UsuarioDAO usuarioDAO)
    {
        _usuarioDAO = usuarioDAO;
    }

    public bool Login(string usuario, string clave)
    {
        if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(clave))
            return false;

        var u = _usuarioDAO.BuscarPorUsuario(usuario.Trim());
        if (u == null)
            return false;
        if (!"ACTIVO".Equals(u.Estado, StringComparison.OrdinalIgnoreCase))
            return false;

        var hash = SeguridadUtil.Sha1(clave);
        return hash.Equals(u.Clave, StringComparison.OrdinalIgnoreCase);
    }

    public string ClienteDeUsuario(string usuario)
    {
        if (string.IsNullOrWhiteSpace(usuario))
            return string.Empty;

        var u = _usuarioDAO.BuscarPorUsuario(usuario.Trim());
        if (u == null || u.ClienteCodigo == null)
            return string.Empty;

        return u.ClienteCodigo;
    }
}
