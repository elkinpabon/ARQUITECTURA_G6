using Ec.Edu.Monster.Controlador;

namespace Ec.Edu.Monster.Vista;

public partial class ActividadPrincipal : ContentPage
{
    private readonly AplicacionControlador controlador = new();
    private bool contrasenaVisible;

    public ActividadPrincipal()
    {
        InitializeComponent();
        BotonContrasena.Clicked += BotonContrasena_Clicked;
        BotonIngresar.Clicked += Ingresar_Clicked;
    }

    private void BotonContrasena_Clicked(object? sender, EventArgs e)
    {
        contrasenaVisible = !contrasenaVisible;
        EntradaContrasena.IsPassword = !contrasenaVisible;
        BotonContrasena.Text = contrasenaVisible ? "Ocultar" : "Mostrar";
    }

    private async void Ingresar_Clicked(object? sender, EventArgs e)
    {
        try
        {
            BotonIngresar.IsEnabled = false;
            IndicadorCargando.IsVisible = true;
            IndicadorCargando.IsRunning = true;
            EtiquetaEstado.Text = "Validando...";
            await Task.Yield();

            if (string.IsNullOrWhiteSpace(EntradaUsuario.Text) || string.IsNullOrWhiteSpace(EntradaContrasena.Text))
            {
                EtiquetaEstado.Text = "";
                await DisplayAlert("CONUNI", "Completa usuario y contrasena", "OK");
                return;
            }

            var resultado = await controlador.IniciarSesionAsync(EntradaUsuario.Text.Trim(), EntradaContrasena.Text);
            if (!resultado.Exito)
            {
                EtiquetaEstado.Text = "";
                await DisplayAlert("CONUNI", resultado.Mensaje, "OK");
                return;
            }

            EtiquetaEstado.Text = "";
            await Navigation.PushAsync(new ActividadMenu(EntradaUsuario.Text.Trim()));
        }
        catch (Exception ex)
        {
            EtiquetaEstado.Text = "";
            await DisplayAlert("CONUNI", $"Error al ingresar: {ex.Message}", "OK");
        }
        finally
        {
            IndicadorCargando.IsRunning = false;
            IndicadorCargando.IsVisible = false;
            BotonIngresar.IsEnabled = true;
        }
    }
}
