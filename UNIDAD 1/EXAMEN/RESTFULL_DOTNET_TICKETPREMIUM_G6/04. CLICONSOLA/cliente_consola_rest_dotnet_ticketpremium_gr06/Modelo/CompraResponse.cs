namespace cliente_consola_rest_dotnet_ticketpremium_gr06.Modelo;

public class CompraResponse
{
    public int IdFactura { get; set; }

    public decimal Subtotal { get; set; }

    public decimal Iva { get; set; }

    public decimal Total { get; set; }

    public int DisponibilidadRestante { get; set; }
}
