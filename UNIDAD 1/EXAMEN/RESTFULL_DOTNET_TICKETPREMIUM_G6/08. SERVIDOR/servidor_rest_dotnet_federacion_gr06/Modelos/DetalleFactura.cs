using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class DetalleFactura
{
    [Key]
    public int IdDetalle { get; set; }

    public int IdFactura { get; set; }

    public int CodigoPartido { get; set; }

    [Required]
    [MaxLength(50)]
    public string Localidad { get; set; } = string.Empty;

    public int Cantidad { get; set; }

    public decimal PrecioUnitario { get; set; }

    public decimal Total { get; set; }

    [JsonIgnore]
    public Factura? Factura { get; set; }
}
