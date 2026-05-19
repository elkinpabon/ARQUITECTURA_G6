using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class PartidoFutbol
{
    [Key]
    public int Codigo { get; set; }

    [Required]
    [MaxLength(100)]
    public string EquipoLocal { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    public string EquipoVisita { get; set; } = string.Empty;

    public DateTime Fecha { get; set; }

    [Required]
    [MaxLength(150)]
    public string Lugar { get; set; } = string.Empty;

    [JsonIgnore]
    public ICollection<LocalidadPartido> Localidades { get; set; } = new List<LocalidadPartido>();
}
