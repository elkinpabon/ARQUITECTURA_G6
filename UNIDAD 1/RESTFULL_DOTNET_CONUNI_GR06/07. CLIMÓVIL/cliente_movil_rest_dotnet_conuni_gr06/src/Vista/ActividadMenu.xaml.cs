namespace Ec.Edu.Monster.Vista;

public partial class ActividadMenu : ContentPage
{
    public ActividadMenu(string usuario)
    {
        InitializeComponent();
        EtiquetaUsuario.Text = $"Hola, {usuario}";
    }

    private async void Salir_Clicked(object sender, EventArgs e)
    {
        await Navigation.PopToRootAsync();
    }

    private async void Longitud_Tapped(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new ActividadConversor("longitud"));
    }

    private async void Masa_Tapped(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new ActividadConversor("masa"));
    }

    private async void Temperatura_Tapped(object sender, EventArgs e)
    {
        await Navigation.PushAsync(new ActividadConversor("temperatura"));
    }
}
