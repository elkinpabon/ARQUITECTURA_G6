namespace cliente_web_rest_dotnet_ticketpremium_gr06.Modelo;

public class ReporteViewModel
{
    public IReadOnlyList<PartidoDto> Partidos { get; set; } = Array.Empty<PartidoDto>();

    public int CodigoPartido { get; set; }

    public IReadOnlyList<ResumenVentaDto> Registros { get; set; } = Array.Empty<ResumenVentaDto>();
}
