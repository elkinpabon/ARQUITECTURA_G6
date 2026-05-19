namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class ResumenVentaDto
{
    public string Partido { get; set; } = string.Empty;

    public DateTime Fecha { get; set; }

    public string Localidad { get; set; } = string.Empty;

    public int Vendidos { get; set; }

    public decimal TotalRecaudado { get; set; }
}
