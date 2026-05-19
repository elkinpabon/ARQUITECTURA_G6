namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;

public class AutenticacionResponse
{
    public bool Exito { get; set; }

    public string Mensaje { get; set; } = string.Empty;

    public int? IdUsuario { get; set; }

    public string NombreCompleto { get; set; } = string.Empty;

    public string Usuario { get; set; } = string.Empty;
}
