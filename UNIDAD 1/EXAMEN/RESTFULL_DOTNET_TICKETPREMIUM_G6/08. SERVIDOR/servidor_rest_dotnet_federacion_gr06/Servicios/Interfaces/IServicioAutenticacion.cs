using servidor_rest_dotnet_federacion_gr06.Modelos;

namespace servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

public interface IServicioAutenticacion
{
    Task<AutenticacionResponse> RegistrarAsync(RegistroUsuarioRequest request, CancellationToken cancellationToken = default);

    Task<AutenticacionResponse> IniciarSesionAsync(LoginUsuarioRequest request, CancellationToken cancellationToken = default);
}
