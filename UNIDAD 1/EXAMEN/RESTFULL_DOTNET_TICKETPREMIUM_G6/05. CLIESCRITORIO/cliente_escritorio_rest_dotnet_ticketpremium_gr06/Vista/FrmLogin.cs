using cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_escritorio_rest_dotnet_ticketpremium_gr06.Controlador;

namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Vista;

public partial class FrmLogin : Form
{
    private const string ApiBaseUrl = "https://localhost:44348/";

    private readonly ClienteTicketPremiumControlador _controlador = new(ApiBaseUrl);

    public string UsuarioActual { get; private set; } = string.Empty;

    public FrmLogin()
    {
        InitializeComponent();
        Text = "TicketPremium - Partidos Ecuatorianos";
    }

    private async void BtnLogin_Click(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(TxtUsuarioLogin.Text) || string.IsNullOrWhiteSpace(TxtPasswordLogin.Text))
            {
                LblLoginEstado.Text = "Complete usuario y contraseña.";
                return;
            }

            var respuesta = await _controlador.IniciarSesionAsync(new AutenticacionRequest
            {
                Usuario = TxtUsuarioLogin.Text.Trim(),
                Password = TxtPasswordLogin.Text
            });

            if (!respuesta.Exito)
            {
                LblLoginEstado.Text = respuesta.Mensaje;
                return;
            }

            UsuarioActual = respuesta.NombreCompleto;
            DialogResult = DialogResult.OK;
            Close();
        }
        catch (Exception ex)
        {
            LblLoginEstado.Text = ex.Message;
        }
    }

    private async void BtnRegistro_Click(object sender, EventArgs e)
    {
        try
        {
            if (string.IsNullOrWhiteSpace(TxtNombreRegistro.Text) ||
                string.IsNullOrWhiteSpace(TxtUsuarioRegistro.Text) ||
                string.IsNullOrWhiteSpace(TxtCorreoRegistro.Text) ||
                string.IsNullOrWhiteSpace(TxtPasswordRegistro.Text) ||
                string.IsNullOrWhiteSpace(TxtConfirmarRegistro.Text))
            {
                LblRegistroEstado.Text = "Complete todos los campos.";
                return;
            }

            var respuesta = await _controlador.RegistrarUsuarioAsync(new RegistroUsuarioRequest
            {
                NombreCompleto = TxtNombreRegistro.Text.Trim(),
                Usuario = TxtUsuarioRegistro.Text.Trim(),
                Correo = TxtCorreoRegistro.Text.Trim(),
                Password = TxtPasswordRegistro.Text,
                ConfirmarPassword = TxtConfirmarRegistro.Text
            });

            if (!respuesta.Exito)
            {
                LblRegistroEstado.Text = respuesta.Mensaje;
                return;
            }

            LblRegistroEstado.Text = respuesta.Mensaje;
        }
        catch (Exception ex)
        {
            LblRegistroEstado.Text = ex.Message;
        }
    }

    protected override void OnFormClosed(FormClosedEventArgs e)
    {
        _controlador.Dispose();
        base.OnFormClosed(e);
    }
}
