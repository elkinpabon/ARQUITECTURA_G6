using Ec.Edu.Monster.Controlador;
using System.Windows.Forms;

namespace Ec.Edu.Monster;

internal static class Program
{
    [STAThread]
    private static void Main()
    {
        ApplicationConfiguration.Initialize();
        Application.Run(new Vista.VentanaPrincipal(new ControladorEscritorio()));
    }
}
