using cliente_movil_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_movil_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.Extensions.DependencyInjection;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06.Vista;

public partial class InicioSesion : ContentPage
{
    private readonly ITicketPremiumApiService _servicio = MauiProgram.Services.GetRequiredService<ITicketPremiumApiService>();

    public InicioSesion()
    {
        InitializeComponent();
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(TxtUsuario.Text) || string.IsNullOrWhiteSpace(TxtPassword.Text))
            {
                LblMensaje.Text = "Complete usuario y contrasena.";
                return;
            }

            var respuesta = await _servicio.IniciarSesionAsync(new AutenticacionRequest
            {
                Usuario = TxtUsuario.Text?.Trim() ?? string.Empty,
                Password = TxtPassword.Text ?? string.Empty
            });

            LblMensaje.Text = respuesta.Mensaje;
            if (respuesta.Exito)
            {
                await Shell.Current.GoToAsync(nameof(Principal));
            }
        }
        catch (Exception ex)
        {
            LblMensaje.Text = ex.Message;
        }
    }

    private async void OnRegisterClicked(object sender, EventArgs e)
    {
        await Shell.Current.GoToAsync(nameof(Registro));
    }
}
