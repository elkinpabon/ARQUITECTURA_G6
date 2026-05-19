using System.ComponentModel;
using System.Drawing;
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

        ReorganizarLayout();
    }

    private void ReorganizarLayout()
    {
        SuspendLayout();
        panelCabecera.SuspendLayout();
        panelTarjetas.SuspendLayout();

        panelCabecera.Controls.Clear();
        panelCabecera.Padding = new Padding(16, 12, 16, 12);
        panelCabecera.Height = 88;

        var cabecera = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 4,
            RowCount = 1,
            BackColor = Paleta.AZUL,
            Margin = new Padding(0),
            Padding = new Padding(0)
        };
        cabecera.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 48F));
        cabecera.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100F));
        cabecera.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));
        cabecera.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));

        picLogo.Dock = DockStyle.Fill;
        picLogo.Margin = new Padding(0, 4, 16, 4);
        cabecera.Controls.Add(picLogo, 0, 0);

        lblTitulo.Dock = DockStyle.Fill;
        lblTitulo.TextAlign = ContentAlignment.MiddleLeft;
        lblTitulo.Margin = new Padding(0, 0, 16, 0);
        cabecera.Controls.Add(lblTitulo, 1, 0);

        lblSaludo.Dock = DockStyle.Fill;
        lblSaludo.TextAlign = ContentAlignment.MiddleRight;
        lblSaludo.Margin = new Padding(0, 0, 16, 0);
        cabecera.Controls.Add(lblSaludo, 2, 0);

        btnCerrarSesion.Dock = DockStyle.Fill;
        btnCerrarSesion.Margin = new Padding(0);
        cabecera.Controls.Add(btnCerrarSesion, 3, 0);

        panelCabecera.Controls.Add(cabecera);

        panelTarjetas.Controls.Clear();
        panelTarjetas.Dock = DockStyle.Fill;
        panelTarjetas.Padding = new Padding(20, 24, 20, 24);
        panelTarjetas.ColumnStyles.Clear();
        panelTarjetas.ColumnCount = 3;
        panelTarjetas.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 33.33333F));
        panelTarjetas.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 33.33333F));
        panelTarjetas.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 33.33333F));
        panelTarjetas.RowStyles.Clear();
        panelTarjetas.RowCount = 1;
        panelTarjetas.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));

        ConfigurarTarjetaLayout(tarjetaLongitud, tituloLongitud, descripcionLongitud, () => CategoriaSeleccionada?.Invoke("longitud"));
        ConfigurarTarjetaLayout(tarjetaMasa, tituloMasa, descripcionMasa, () => CategoriaSeleccionada?.Invoke("masa"));
        ConfigurarTarjetaLayout(tarjetaTemperatura, tituloTemperatura, descripcionTemperatura, () => CategoriaSeleccionada?.Invoke("temperatura"));

        panelTarjetas.Controls.Add(tarjetaLongitud, 0, 0);
        panelTarjetas.Controls.Add(tarjetaMasa, 1, 0);
        panelTarjetas.Controls.Add(tarjetaTemperatura, 2, 0);

        panelTarjetas.ResumeLayout(true);
        panelCabecera.ResumeLayout(true);
        ResumeLayout(true);
    }

    private static void ConfigurarTarjetaLayout(Panel tarjeta, Label titulo, Label descripcion, Action accion)
    {
        tarjeta.Controls.Clear();
        tarjeta.BackColor = Paleta.AZUL;
        tarjeta.Cursor = Cursors.Hand;
        tarjeta.Dock = DockStyle.Fill;
        tarjeta.Margin = new Padding(0, 0, 12, 0);
        tarjeta.MinimumSize = new Size(220, 180);

        var contenido = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 1,
            RowCount = 4,
            BackColor = Paleta.AZUL,
            Padding = new Padding(18)
        };
        contenido.RowStyles.Add(new RowStyle(SizeType.Percent, 46F));
        contenido.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        contenido.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        contenido.RowStyles.Add(new RowStyle(SizeType.Percent, 54F));

        titulo.Dock = DockStyle.Fill;
        titulo.TextAlign = ContentAlignment.MiddleCenter;
        titulo.Margin = new Padding(0);
        descripcion.Dock = DockStyle.Fill;
        descripcion.TextAlign = ContentAlignment.MiddleCenter;
        descripcion.Margin = new Padding(0);

        contenido.Controls.Add(titulo, 0, 1);
        contenido.Controls.Add(descripcion, 0, 2);

        tarjeta.Click += (_, _) => accion();
        contenido.Click += (_, _) => accion();
        titulo.Click += (_, _) => accion();
        descripcion.Click += (_, _) => accion();

        tarjeta.Controls.Add(contenido);
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
        var rutaPrincipal = Path.Combine(AppContext.BaseDirectory, "src", "Recursos", "img", archivo);
        if (File.Exists(rutaPrincipal))
        {
            return rutaPrincipal;
        }

        var rutaSecundaria = Path.Combine(AppContext.BaseDirectory, "src", "img", archivo);
        if (File.Exists(rutaSecundaria))
        {
            return rutaSecundaria;
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

    private void descripcionMasa_Click(object sender, EventArgs e)
    {

    }

    private void descripcionTemperatura_Click(object sender, EventArgs e)
    {

    }
}
