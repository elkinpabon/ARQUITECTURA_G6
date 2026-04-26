using Ec.Edu.Monster.Conuni;

namespace Ec.Edu.Monster;

public partial class App : Application
{
    public App()
    {
        InitializeComponent();
        MainPage = new NavigationPage(new ActividadPrincipal());
    }
}
