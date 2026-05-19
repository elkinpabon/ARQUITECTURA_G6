using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using cliente_movil_rest_dotnet_ticketpremium_gr06.Controlador;
using cliente_movil_rest_dotnet_ticketpremium_gr06.Servicios;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06;

public static class MauiProgram
{
	public static IServiceProvider Services { get; private set; } = default!;

	public static MauiApp CreateMauiApp()
	{
		var builder = MauiApp.CreateBuilder();
		builder
			.UseMauiApp<App>()
			.ConfigureFonts(fonts =>
			{
				fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
				fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
			});

        builder.Services.AddSingleton(new HttpClient { BaseAddress = new Uri("https://localhost:44348/") });
		builder.Services.AddSingleton<ITicketPremiumApiService, TicketPremiumApiService>();
        builder.Services.AddSingleton<PrincipalControlador>();

#if DEBUG
		builder.Logging.AddDebug();
#endif

		var app = builder.Build();
		Services = app.Services;

		return app;
	}
}
