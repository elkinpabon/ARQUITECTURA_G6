using cliente_movil_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_movil_rest_dotnet_ticketpremium_gr06.Servicios;
using Microsoft.Extensions.DependencyInjection;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06.Vista;

public partial class Registro : ContentPage
{
    private readonly ITicketPremiumApiService _servicio = MauiProgram.Services.GetRequiredService<ITicketPremiumApiService>();

    public Registro()
    {
        InitializeComponent();
    }

    private async void OnRegisterClicked(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(TxtNombre.Text) ||
                string.IsNullOrWhiteSpace(TxtUsuario.Text) ||
                string.IsNullOrWhiteSpace(TxtCorreo.Text) ||
                string.IsNullOrWhiteSpace(TxtPassword.Text) ||
                string.IsNullOrWhiteSpace(TxtConfirmar.Text))
            {
                LblMensaje.Text = "Complete todos los campos.";
                return;
            }

            var respuesta = await _servicio.RegistrarUsuarioAsync(new RegistroUsuarioRequest
            {
                NombreCompleto = TxtNombre.Text?.Trim() ?? string.Empty,
                Usuario = TxtUsuario.Text?.Trim() ?? string.Empty,
                Correo = TxtCorreo.Text?.Trim() ?? string.Empty,
                Password = TxtPassword.Text ?? string.Empty,
                ConfirmarPassword = TxtConfirmar.Text ?? string.Empty
            });

            LblMensaje.Text = respuesta.Mensaje;
            if (respuesta.Exito)
            {
                await Shell.Current.GoToAsync("//InicioSesion");
            }
        }
        catch (Exception ex)
        {
            LblMensaje.Text = ex.Message;
        }
    }

    private async void OnBackClicked(object sender, EventArgs e)
    {
        await Shell.Current.GoToAsync("//InicioSesion");
    }
}
