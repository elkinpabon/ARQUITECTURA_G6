using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Utils;

namespace Ec.Edu.Monster.Servicio;

public class ServicioAutenticacion
{
    private readonly ClienteRest cliente = new();

    public Resultado Autenticar(string usuario, string contrasena)
    {
        return cliente.IniciarSesion(usuario, contrasena);
    }
}
