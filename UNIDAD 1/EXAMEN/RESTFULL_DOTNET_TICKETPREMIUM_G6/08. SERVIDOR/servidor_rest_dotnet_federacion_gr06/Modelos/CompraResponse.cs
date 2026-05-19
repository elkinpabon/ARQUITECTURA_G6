namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class CompraResponse
{
    public int IdFactura { get; set; }

    public int CodigoPartido { get; set; }

    public string CodigoLocalidad { get; set; } = string.Empty;

    public int Cantidad { get; set; }

    public decimal Subtotal { get; set; }

    public decimal Iva { get; set; }

    public decimal Total { get; set; }

    public int DisponibilidadRestante { get; set; }

    public DateTime Fecha { get; set; }
}
