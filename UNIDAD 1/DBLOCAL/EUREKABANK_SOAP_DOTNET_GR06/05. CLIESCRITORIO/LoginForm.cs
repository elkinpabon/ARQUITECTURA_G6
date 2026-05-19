using CLIESCRITORIO.Controlador;
using CLIESCRITORIO.Vista;

namespace CLIESCRITORIO;

public class LoginForm : Form
{
    private readonly BancoController _ctrl;
    private bool _returnedToLogin;

    public LoginForm()
    {
        _ctrl = new BancoController();
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        Text = "EUREKABANK GR06 - Iniciar sesion";
        Size = new Size(1120, 700);
        StartPosition = FormStartPosition.CenterScreen;
        FormBorderStyle = FormBorderStyle.Sizable;
        MaximizeBox = true;
        MinimizeBox = true;
        MinimumSize = new Size(980, 620);
        BackColor = Color.FromArgb(15, 23, 42);
        ForeColor = Color.WhiteSmoke;
        Font = new Font("Segoe UI", 10F, FontStyle.Regular, GraphicsUnit.Point);

        var split = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 2,
            RowCount = 1,
            Padding = new Padding(20)
        };
        split.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 52F));
        split.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 48F));

        var art = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 1,
            RowCount = 2,
            BackColor = Color.FromArgb(11, 18, 32),
            Padding = new Padding(0)
        };
        art.RowStyles.Add(new RowStyle(SizeType.Percent, 78F));
        art.RowStyles.Add(new RowStyle(SizeType.Absolute, 132F));

        var artImage = new PictureBox
        {
            Dock = DockStyle.Fill,
            Image = UiImages.Load("login.jpg"),
            SizeMode = PictureBoxSizeMode.Zoom,
            BackColor = Color.FromArgb(11, 18, 32),
            Margin = new Padding(0)
        };

        var artWrap = new Panel
        {
            Dock = DockStyle.Fill,
            BackColor = Color.FromArgb(30, 41, 59),
            Padding = new Padding(18, 12, 18, 12)
        };

        var artText = new FlowLayoutPanel
        {
            Dock = DockStyle.Fill,
            FlowDirection = FlowDirection.TopDown,
            WrapContents = false,
            BackColor = Color.Transparent,
            Padding = new Padding(4, 2, 4, 0)
        };
        artText.Controls.Add(UiImages.Logo("moster.png", 58));
        artText.Controls.Add(new Label
        {
            Text = "EUREKABANK GR06",
            AutoSize = true,
            Font = new Font("Segoe UI", 20F, FontStyle.Bold),
            ForeColor = Color.White,
            Margin = new Padding(0, 12, 0, 0)
        });
        artText.Controls.Add(new Label
        {
            Text = "Banca SOAP · Cliente Escritorio .NET",
            AutoSize = true,
            Font = new Font("Segoe UI", 10F, FontStyle.Regular),
            ForeColor = Color.FromArgb(203, 213, 225),
            Margin = new Padding(0, 4, 0, 0)
        });
        artWrap.Controls.Add(artText);

        art.Controls.Add(artImage, 0, 0);
        art.Controls.Add(artWrap, 0, 1);

        var login = new Panel
        {
            Dock = DockStyle.Fill,
            BackColor = Color.FromArgb(30, 41, 59),
            Padding = new Padding(34)
        };
        var lblTitle = new Label
        {
            Text = "Iniciar sesion",
            AutoSize = true,
            Font = new Font("Segoe UI", 18F, FontStyle.Bold),
            ForeColor = Color.White
        };
        var lblSub = new Label
        {
            Text = "Ingresa tus credenciales para continuar",
            AutoSize = true,
            Top = 42,
            ForeColor = Color.FromArgb(148, 163, 184)
        };
        var lblUsuario = new Label { Text = "Usuario", AutoSize = true, Top = 94, ForeColor = Color.WhiteSmoke };
        var txtUsuario = new TextBox
        {
            Name = "txtUsuario",
            Width = 280,
            Top = 120,
            BackColor = Color.FromArgb(15, 23, 42),
            ForeColor = Color.WhiteSmoke,
            BorderStyle = BorderStyle.FixedSingle
        };
        var lblClave = new Label { Text = "Clave", AutoSize = true, Top = 168, ForeColor = Color.WhiteSmoke };
        var txtClave = new TextBox
        {
            Name = "txtClave",
            Width = 280,
            Top = 194,
            PasswordChar = '*',
            BackColor = Color.FromArgb(15, 23, 42),
            ForeColor = Color.WhiteSmoke,
            BorderStyle = BorderStyle.FixedSingle
        };
        var btnLogin = new Button
        {
            Name = "btnLogin",
            Text = "Ingresar",
            Width = 280,
            Height = 42,
            Top = 252,
            FlatStyle = FlatStyle.Flat,
            BackColor = Color.FromArgb(56, 189, 248),
            ForeColor = Color.FromArgb(4, 38, 58)
        };
        var hint = new Label
        {
            Text = "Prueba: monster / monster9",
            AutoSize = true,
            Top = 314,
            ForeColor = Color.FromArgb(100, 116, 139)
        };

        void LoginAction()
        {
            var usuario = txtUsuario.Text.Trim();
            var clave = txtClave.Text.Trim();

            if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(clave))
            {
                MessageBox.Show("Ingrese usuario y clave", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            try
            {
                if (!_ctrl.Login(usuario, clave))
                {
                    MessageBox.Show("Usuario o clave incorrectos", "Error de autenticacion", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                txtClave.Clear();
                _returnedToLogin = false;
                Hide();

                using var mainForm = new MainForm(_ctrl, () =>
                {
                    _returnedToLogin = true;
                    Show();
                    Activate();
                });

                mainForm.ShowDialog(this);

                if (!_returnedToLogin)
                {
                    Close();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Error conectando al servidor:\n{ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        btnLogin.Click += (_, _) => LoginAction();
        txtClave.KeyDown += (_, e) => { if (e.KeyCode == Keys.Enter) LoginAction(); };
        txtUsuario.KeyDown += (_, e) => { if (e.KeyCode == Keys.Enter) LoginAction(); };

        login.Controls.AddRange(new Control[] { lblTitle, lblSub, lblUsuario, txtUsuario, lblClave, txtClave, btnLogin, hint });
        lblSub.Location = new Point(0, 42);
        lblUsuario.Location = new Point(0, 94);
        txtUsuario.Location = new Point(0, 120);
        lblClave.Location = new Point(0, 168);
        txtClave.Location = new Point(0, 194);
        btnLogin.Location = new Point(0, 252);
        hint.Location = new Point(0, 314);

        split.Controls.Add(art, 0, 0);
        split.Controls.Add(login, 1, 0);
        Controls.Add(split);

        AcceptButton = btnLogin;
    }
}
