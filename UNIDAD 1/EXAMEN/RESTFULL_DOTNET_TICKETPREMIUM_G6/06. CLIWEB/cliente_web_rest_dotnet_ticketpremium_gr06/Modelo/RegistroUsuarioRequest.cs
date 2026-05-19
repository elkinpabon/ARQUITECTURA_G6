namespace cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;

public class RegistroUsuarioRequest
{
    public string NombreCompleto { get; set; } = string.Empty;

    public string Usuario { get; set; } = string.Empty;

    public string Correo { get; set; } = string.Empty;

    public string Password { get; set; } = string.Empty;

    public string ConfirmarPassword { get; set; } = string.Empty;
}
