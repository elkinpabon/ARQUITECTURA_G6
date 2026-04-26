using Ec.Edu.Monster.Controlador;
using System.Windows.Forms;

namespace Ec.Edu.Monster.Vista;

public partial class VentanaPrincipal : Form
{
    private readonly ControladorEscritorio controlador;

    public VentanaPrincipal(ControladorEscritorio controlador)
    {
        this.controlador = controlador;
        InitializeComponent();
        MostrarLogin();
    }

    private void MostrarLogin()
    {
        var login = new PanelLogin(controlador);
        login.Dock = DockStyle.Fill;
        login.LoginExitoso += () => MostrarMenu();
        contenedorPantallas.Controls.Clear();
        contenedorPantallas.Controls.Add(login);
    }

    private void MostrarMenu()
    {
        var menu = new PanelMenu();
        menu.Dock = DockStyle.Fill;
        menu.CategoriaSeleccionada += categoria => MostrarConversion(categoria);
        menu.CerrarSesionSolicitada += () => MostrarLogin();
        contenedorPantallas.Controls.Clear();
        contenedorPantallas.Controls.Add(menu);
    }

    private void MostrarConversion(string categoria)
    {
        var conversion = new PanelConversion(controlador, categoria);
        conversion.Dock = DockStyle.Fill;
        conversion.VolverSolicitado += () => MostrarMenu();
        contenedorPantallas.Controls.Clear();
        contenedorPantallas.Controls.Add(conversion);
    }
}
