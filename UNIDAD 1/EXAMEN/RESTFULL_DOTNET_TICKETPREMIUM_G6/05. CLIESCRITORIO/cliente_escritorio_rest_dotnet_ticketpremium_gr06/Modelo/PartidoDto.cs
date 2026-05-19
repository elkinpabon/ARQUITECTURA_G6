namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;

public class PartidoDto
{
    public int Codigo { get; set; }

    public string EquipoLocal { get; set; } = string.Empty;

    public string EquipoVisita { get; set; } = string.Empty;

    public DateTime Fecha { get; set; }

    public string Lugar { get; set; } = string.Empty;

    public string Descripcion => $"{Codigo} - {EquipoLocal} vs {EquipoVisita}";
}
