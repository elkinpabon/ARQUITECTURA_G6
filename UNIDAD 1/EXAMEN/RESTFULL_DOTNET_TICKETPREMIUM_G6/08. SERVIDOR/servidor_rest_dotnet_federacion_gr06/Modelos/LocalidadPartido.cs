using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class LocalidadPartido
{
    [Key]
    public int Id { get; set; }

    public int CodigoPartido { get; set; }

    [Required]
    [MaxLength(50)]
    public string CodigoLocalidad { get; set; } = string.Empty;

    public int Disponibilidad { get; set; }

    public decimal Precio { get; set; }

    [JsonIgnore]
    public PartidoFutbol? Partido { get; set; }
}
