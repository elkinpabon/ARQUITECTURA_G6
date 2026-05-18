using System.Drawing;
using System.ComponentModel;
using System.IO;
using System.Windows.Forms;

namespace Ec.Edu.Monster.Vista;

public partial class PanelMenu : UserControl
{
    public event Action<string>? CategoriaSeleccionada;
    public event Action? CerrarSesionSolicitada;

    public PanelMenu()
    {
        InitializeComponent();
        ConstruirInterfaz();
    }

    private void ConstruirInterfaz()
    {
        BackColor = Paleta.GRIS_FONDO;
        panelCabecera.BackColor = Paleta.AZUL;
        lblTitulo.Font = new Font("SansSerif", 16, FontStyle.Bold);
        lblTitulo.ForeColor = Color.White;
        lblSaludo.Font = Paleta.SUBTITULO;
        lblSaludo.ForeColor = Paleta.AMARILLO;
        btnCerrarSesion.BackColor = Paleta.AMARILLO;
        btnCerrarSesion.ForeColor = Paleta.AZUL;
        btnCerrarSesion.FlatStyle = FlatStyle.Flat;
        btnCerrarSesion.FlatAppearance.BorderSize = 0;

        if (!EsModoDisenio())
        {
            picLogo.Image = CargarImagen(Ruta("moster.png"));
        }

        picLogo.SizeMode = PictureBoxSizeMode.Zoom;
        ConfigurarTarjeta(tarjetaLongitud, tituloLongitud, descripcionLongitud, () => CategoriaSeleccionada?.Invoke("longitud"));
        ConfigurarTarjeta(tarjetaMasa, tituloMasa, descripcionMasa, () => CategoriaSeleccionada?.Invoke("masa"));
        ConfigurarTarjeta(tarjetaTemperatura, tituloTemperatura, descripcionTemperatura, () => CategoriaSeleccionada?.Invoke("temperatura"));
        lblSaludo.Text = "Bienvenido";
        btnCerrarSesion.Click += (_, _) => CerrarSesionSolicitada?.Invoke();
    }

    private void ConfigurarTarjeta(Panel tarjeta, Label titulo, Label descripcion, Action accion)
    {
        tarjeta.BackColor = Paleta.AZUL;
        tarjeta.Cursor = Cursors.Hand;
        titulo.ForeColor = Color.White;
        titulo.Font = new Font("SansSerif", 18, FontStyle.Bold);
        descripcion.ForeColor = Color.FromArgb(0xD0, 0xDA, 0xE8);
        descripcion.Font = Paleta.SUBTITULO;
        tarjeta.Click += (_, _) => accion();
        titulo.Click += (_, _) => accion();
        descripcion.Click += (_, _) => accion();
    }

    private static string Ruta(string archivo)
    {
        var rutaPrincipal = Path.Combine(AppContext.BaseDirectory, "src", "img", archivo);
        if (File.Exists(rutaPrincipal))
        {
            return rutaPrincipal;
        }

        return Path.Combine(AppContext.BaseDirectory, "img", archivo);
    }

    private static Image? CargarImagen(string ruta)
    {
        return File.Exists(ruta) ? Image.FromFile(ruta) : null;
    }

    private static bool EsModoDisenio() =>
        LicenseManager.UsageMode == LicenseUsageMode.Designtime;

    private void tituloTemperatura_Click(object sender, EventArgs e)
    {

    }
}
