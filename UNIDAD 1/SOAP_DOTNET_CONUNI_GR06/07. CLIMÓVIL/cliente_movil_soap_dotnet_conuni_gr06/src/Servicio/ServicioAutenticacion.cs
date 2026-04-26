namespace Ec.Edu.Monster.Servicio;

public class ServicioAutenticacion
{
    private readonly Utilidades.ClienteSoap cliente = new();

    public bool Autenticar(string usuario, string contrasena)
    {
        return cliente.Ejecutar(servicio => servicio.IniciarSesion(usuario, contrasena));
    }
}
