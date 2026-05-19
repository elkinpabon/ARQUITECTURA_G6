using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class Factura
{
    [Key]
    public int IdFactura { get; set; }

    public DateTime Fecha { get; set; }

    public decimal Subtotal { get; set; }

    public decimal Iva { get; set; }

    public decimal Total { get; set; }

    [JsonIgnore]
    public ICollection<DetalleFactura> Detalles { get; set; } = new List<DetalleFactura>();
}
