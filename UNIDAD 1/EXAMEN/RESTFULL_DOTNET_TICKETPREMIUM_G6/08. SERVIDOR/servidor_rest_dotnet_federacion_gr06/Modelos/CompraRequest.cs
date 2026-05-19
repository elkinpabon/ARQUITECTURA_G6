using System.ComponentModel.DataAnnotations;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class CompraRequest
{
    [Required]
    public int CodigoPartido { get; set; }

    [Required]
    [MaxLength(50)]
    public string CodigoLocalidad { get; set; } = string.Empty;

    [Range(1, int.MaxValue)]
    public int Cantidad { get; set; }
}
