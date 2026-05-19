namespace cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;

public class CompraViewModel
{
    public PartidoDto? Partido { get; set; }

    public IReadOnlyList<LocalidadDto> Localidades { get; set; } = Array.Empty<LocalidadDto>();

    public string CodigoLocalidad { get; set; } = string.Empty;

    public int Cantidad { get; set; } = 1;

    public CompraResponse? Resultado { get; set; }

    public string? Mensaje { get; set; }
}
