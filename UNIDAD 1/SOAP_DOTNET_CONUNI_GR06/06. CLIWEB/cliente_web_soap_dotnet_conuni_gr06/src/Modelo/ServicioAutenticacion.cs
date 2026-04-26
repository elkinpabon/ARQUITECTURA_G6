namespace Ec.Edu.Monster.Modelo;

public class ServicioAutenticacion
{
    private readonly ClienteSoap cliente = new();

    public bool Autenticar(string usuario, string contrasena)
    {
        return cliente.Ejecutar(servicio => servicio.IniciarSesion(usuario, contrasena));
    }
}
