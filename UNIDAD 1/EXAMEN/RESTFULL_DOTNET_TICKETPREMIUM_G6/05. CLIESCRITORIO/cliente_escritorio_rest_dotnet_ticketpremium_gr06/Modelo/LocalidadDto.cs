namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;

public class LocalidadDto
{
    public string CodigoLocalidad { get; set; } = string.Empty;

    public int Disponibilidad { get; set; }

    public decimal Precio { get; set; }

    public string Descripcion => $"{CodigoLocalidad} - Q{Precio:N2} ({Disponibilidad})";
}
