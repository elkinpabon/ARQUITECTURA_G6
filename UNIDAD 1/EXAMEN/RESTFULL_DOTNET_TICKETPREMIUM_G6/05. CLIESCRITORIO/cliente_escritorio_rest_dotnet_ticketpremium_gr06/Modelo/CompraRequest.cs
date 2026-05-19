namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;

public class CompraRequest
{
    public int CodigoPartido { get; set; }

    public string CodigoLocalidad { get; set; } = string.Empty;

    public int Cantidad { get; set; }
}
