using cliente_movil_rest_dotnet_ticketpremium_gr06.Controlador;
using Microsoft.Extensions.DependencyInjection;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06.Vista;

public partial class Principal : ContentPage
{
	public Principal()
	{
		InitializeComponent();
		BindingContext = MauiProgram.Services.GetRequiredService<PrincipalControlador>();
	}
}
