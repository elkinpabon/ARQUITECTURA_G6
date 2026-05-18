using Ec.Edu.Monster.Controlador;
using System.Windows.Forms;

namespace Ec.Edu.Monster;

public static class Aplicacion
{
    public static void Main()
    {
        ApplicationConfiguration.Initialize();
        Application.Run(new Vista.VentanaPrincipal(new ControladorEscritorio()));
    }
}
