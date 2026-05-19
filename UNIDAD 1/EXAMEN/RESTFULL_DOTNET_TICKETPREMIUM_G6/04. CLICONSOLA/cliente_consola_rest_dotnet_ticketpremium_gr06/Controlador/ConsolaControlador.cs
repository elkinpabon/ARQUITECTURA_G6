using cliente_consola_rest_dotnet_ticketpremium_gr06.Servicios;

namespace cliente_consola_rest_dotnet_ticketpremium_gr06.Controlador;

public sealed class ConsolaControlador
{
    public ConsolaControlador(ClienteTicketPremiumService servicio)
    {
        Servicio = servicio;
    }

    public ClienteTicketPremiumService Servicio { get; }
}
