using Ec.Edu.Monster.Controladores;

namespace Ec.Edu.Monster.Conuni;

public partial class ActividadPrincipal : ContentPage
{
    private readonly AplicacionControlador controlador = new();
    private bool contrasenaVisible;

    public ActividadPrincipal()
    {
        InitializeComponent();
    }

    private void BotonContrasena_Clicked(object sender, EventArgs e)
    {
        contrasenaVisible = !contrasenaVisible;
        EntradaContrasena.IsPassword = !contrasenaVisible;
        BotonContrasena.Text = contrasenaVisible ? "Ocultar" : "Mostrar";
    }

    private async void Ingresar_Clicked(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(EntradaUsuario.Text) || string.IsNullOrWhiteSpace(EntradaContrasena.Text))
            {
                await DisplayAlert("CONUNI", "Completa usuario y contrasena", "OK");
                return;
            }

            var resultado = controlador.IniciarSesion(EntradaUsuario.Text.Trim(), EntradaContrasena.Text);
            if (!resultado.Exito)
            {
                await DisplayAlert("CONUNI", resultado.Mensaje, "OK");
                return;
            }

            await Navigation.PushAsync(new ActividadMenu(EntradaUsuario.Text.Trim()));
        }
        catch (Exception ex)
        {
            await DisplayAlert("CONUNI", $"Error inesperado: {ex.Message}", "OK");
        }
    }
}
