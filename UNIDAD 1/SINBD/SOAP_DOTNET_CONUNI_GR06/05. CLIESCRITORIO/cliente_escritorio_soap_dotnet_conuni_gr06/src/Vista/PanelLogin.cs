using Ec.Edu.Monster.Controlador;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace Ec.Edu.Monster.Vista;

public partial class PanelLogin : UserControl
{
    public event Action? LoginExitoso;

    private readonly ControladorEscritorio controlador;

    public PanelLogin(ControladorEscritorio controlador)
    {
        this.controlador = controlador;
        InitializeComponent();
        ConstruirInterfaz();
    }

    private void ConstruirInterfaz()
    {
        campoUsuario.PlaceholderText = "Usuario";
        campoContrasena.PlaceholderText = "Contrasena";
        campoContrasena.UseSystemPasswordChar = true;
        lblError.Text = " ";

        botonMostrar.Text = "Mostrar";
        botonMostrar.Click += (_, _) => campoContrasena.UseSystemPasswordChar = !campoContrasena.UseSystemPasswordChar;

        botonIngresar.Text = "Ingresar";
        botonIngresar.BackColor = Paleta.AZUL;
        botonIngresar.ForeColor = Color.White;
        botonIngresar.FlatStyle = FlatStyle.Flat;
        botonIngresar.FlatAppearance.BorderSize = 0;
        botonIngresar.Click += (_, _) => Login();

        lblError.ForeColor = Paleta.ROJO_ERROR_FG;

        if (!EsModoDisenio())
        {
            panelImagen.BackgroundImage = CargarImagen(Ruta("login.jpg"));
            panelImagen.BackgroundImageLayout = ImageLayout.Zoom;
            picLogo.Image = CargarImagen(Ruta("moster.png"));
        }

        picLogo.SizeMode = PictureBoxSizeMode.Zoom;

        lblTitulo.Font = Paleta.TITULO;
        lblTitulo.ForeColor = Paleta.AZUL;
        lblSubtitulo.Font = Paleta.SUBTITULO;
        lblSubtitulo.ForeColor = Paleta.TEXTO_SUAVE;
        lblUsuario.Font = Paleta.ETIQUETA;
        lblUsuario.ForeColor = Paleta.AZUL;
        lblContrasena.Font = Paleta.ETIQUETA;
        lblContrasena.ForeColor = Paleta.AZUL;
        campoUsuario.Font = Paleta.CAMPO;
        campoContrasena.Font = Paleta.CAMPO;
        botonMostrar.FlatStyle = FlatStyle.Flat;
        botonMostrar.FlatAppearance.BorderSize = 0;
        botonIngresar.FlatStyle = FlatStyle.Flat;
        botonIngresar.FlatAppearance.BorderSize = 0;

        ReorganizarLayout();
    }

    private void ReorganizarLayout()
    {
        panelFormulario.AutoScroll = true;

        var layout = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 2,
            RowCount = 9,
            Padding = new Padding(8, 8, 8, 8)
        };

        layout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100F));
        layout.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 104F));

        for (var i = 0; i < 9; i++)
        {
            layout.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        }

        picLogo.Anchor = AnchorStyles.None;
        picLogo.Size = new Size(64, 64);
        picLogo.Margin = new Padding(0, 8, 0, 20);
        layout.Controls.Add(picLogo, 0, 0);
        layout.SetColumnSpan(picLogo, 2);

        lblTitulo.TextAlign = ContentAlignment.MiddleCenter;
        lblTitulo.Dock = DockStyle.Fill;
        lblTitulo.Margin = new Padding(0, 0, 0, 6);
        layout.Controls.Add(lblTitulo, 0, 1);
        layout.SetColumnSpan(lblTitulo, 2);

        lblSubtitulo.TextAlign = ContentAlignment.MiddleCenter;
        lblSubtitulo.Dock = DockStyle.Fill;
        lblSubtitulo.Margin = new Padding(0, 0, 0, 16);
        layout.Controls.Add(lblSubtitulo, 0, 2);
        layout.SetColumnSpan(lblSubtitulo, 2);

        lblUsuario.Dock = DockStyle.Fill;
        lblUsuario.Margin = new Padding(0, 0, 0, 4);
        layout.Controls.Add(lblUsuario, 0, 3);
        layout.SetColumnSpan(lblUsuario, 2);

        campoUsuario.Dock = DockStyle.Fill;
        campoUsuario.Margin = new Padding(0, 0, 0, 12);
        layout.Controls.Add(campoUsuario, 0, 4);
        layout.SetColumnSpan(campoUsuario, 2);

        lblContrasena.Dock = DockStyle.Fill;
        lblContrasena.Margin = new Padding(0, 0, 0, 4);
        layout.Controls.Add(lblContrasena, 0, 5);
        layout.SetColumnSpan(lblContrasena, 2);

        var filaContrasena = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 2,
            Margin = new Padding(0, 0, 0, 12)
        };
        filaContrasena.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100F));
        filaContrasena.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 104F));

        campoContrasena.Dock = DockStyle.Fill;
        campoContrasena.Margin = new Padding(0);
        botonMostrar.Dock = DockStyle.Fill;
        botonMostrar.Margin = new Padding(8, 0, 0, 0);
        filaContrasena.Controls.Add(campoContrasena, 0, 0);
        filaContrasena.Controls.Add(botonMostrar, 1, 0);

        layout.Controls.Add(filaContrasena, 0, 6);
        layout.SetColumnSpan(filaContrasena, 2);

        botonIngresar.Dock = DockStyle.Fill;
        botonIngresar.Margin = new Padding(0, 0, 0, 10);
        layout.Controls.Add(botonIngresar, 0, 7);
        layout.SetColumnSpan(botonIngresar, 2);

        lblError.Dock = DockStyle.Fill;
        lblError.TextAlign = ContentAlignment.MiddleCenter;
        lblError.Margin = new Padding(0);
        layout.Controls.Add(lblError, 0, 8);
        layout.SetColumnSpan(lblError, 2);

        panelFormulario.Controls.Clear();
        panelFormulario.Controls.Add(layout);
    }

    private void Login()
    {
        var resultado = controlador.IniciarSesion(campoUsuario.Text.Trim(), campoContrasena.Text);
        if (resultado.Exito)
        {
            lblError.Text = " ";
            LoginExitoso?.Invoke();
            return;
        }

        lblError.Text = resultado.Mensaje;
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

    private void panelImagen_Paint(object sender, PaintEventArgs e)
    {

    }

    private void panelFormulario_Paint(object sender, PaintEventArgs e)
    {

    }
}
