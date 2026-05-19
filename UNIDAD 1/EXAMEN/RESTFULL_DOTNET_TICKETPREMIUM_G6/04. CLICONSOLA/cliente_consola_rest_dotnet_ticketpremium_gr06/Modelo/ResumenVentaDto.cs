namespace cliente_consola_rest_dotnet_ticketpremium_gr06.Modelo;

public class ResumenVentaDto
{
    public string Localidad { get; set; } = string.Empty;

    public int Vendidos { get; set; }

    public decimal TotalRecaudado { get; set; }
}
