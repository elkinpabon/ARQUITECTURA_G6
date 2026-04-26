using Ec.Edu.Monster.Controlador;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace Ec.Edu.Monster.Vista;

public partial class PanelLogin : UserControl
{
    public event Action? LoginExitoso;

    private ControladorEscritorio? controlador;

    public PanelLogin()
    {
        InitializeComponent();
        ConstruirInterfaz();
    }

    public PanelLogin(ControladorEscritorio controlador)
        : this()
    {
        this.controlador = controlador;
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
            panelImagen.BackgroundImageLayout = ImageLayout.Stretch;
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
    }

    private void Login()
    {
        if (controlador is null)
        {
            lblError.Text = "Control no inicializado.";
            return;
        }

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

    private void panelImagen_Paint(object sender, PaintEventArgs e)
    {
    }

    private void panelFormulario_Paint(object sender, PaintEventArgs e)
    {
    }
}
