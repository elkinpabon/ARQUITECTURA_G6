namespace Ec.Edu.Monster.Conuni;

public partial class ActividadMenu : ContentPage
{
    private readonly string usuario;

    public ActividadMenu(string usuario)
    {
        InitializeComponent();
        this.usuario = usuario;
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
