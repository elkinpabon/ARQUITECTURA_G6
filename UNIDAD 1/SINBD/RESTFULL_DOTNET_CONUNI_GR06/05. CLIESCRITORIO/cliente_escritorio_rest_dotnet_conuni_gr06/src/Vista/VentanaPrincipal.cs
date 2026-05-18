using Ec.Edu.Monster.Controlador;
using System.ComponentModel;
using System.Windows.Forms;

namespace Ec.Edu.Monster.Vista;

public partial class VentanaPrincipal : Form
{
    private ControladorEscritorio? controlador;

    public VentanaPrincipal()
    {
        InitializeComponent();
    }

    public VentanaPrincipal(ControladorEscritorio controlador)
        : this()
    {
        this.controlador = controlador;
        MostrarLogin();
    }

    protected override void OnLoad(EventArgs e)
    {
        base.OnLoad(e);
        if (controlador is null && !EsModoDisenio())
        {
            controlador = new ControladorEscritorio();
            MostrarLogin();
        }
    }

    private void MostrarLogin()
    {
        if (controlador is null)
        {
            return;
        }

        var login = new PanelLogin(controlador);
        login.Dock = DockStyle.Fill;
        login.LoginExitoso += MostrarMenu;
        contenedorPantallas.Controls.Clear();
        contenedorPantallas.Controls.Add(login);
    }

    private void MostrarMenu()
    {
        var menu = new PanelMenu();
        menu.Dock = DockStyle.Fill;
        menu.CategoriaSeleccionada += MostrarConversion;
        menu.CerrarSesionSolicitada += MostrarLogin;
        contenedorPantallas.Controls.Clear();
        contenedorPantallas.Controls.Add(menu);
    }

    private void MostrarConversion(string categoria)
    {
        if (controlador is null)
        {
            return;
        }

        var conversion = new PanelConversion(controlador, categoria);
        conversion.Dock = DockStyle.Fill;
        conversion.VolverSolicitado += MostrarMenu;
        contenedorPantallas.Controls.Clear();
        contenedorPantallas.Controls.Add(conversion);
    }

    private static bool EsModoDisenio() =>
        LicenseManager.UsageMode == LicenseUsageMode.Designtime;
}
