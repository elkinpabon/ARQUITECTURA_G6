using cliente_movil_rest_dotnet_ticketpremium_gr06.Vista;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06;

public partial class AppShell : Shell
{
	public AppShell()
	{
		InitializeComponent();
        Routing.RegisterRoute(nameof(Registro), typeof(Registro));
        Routing.RegisterRoute(nameof(Principal), typeof(Principal));
    }
}
