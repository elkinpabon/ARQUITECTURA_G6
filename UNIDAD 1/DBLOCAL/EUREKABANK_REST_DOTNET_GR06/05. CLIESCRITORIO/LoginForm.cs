using CLIESCRITORIO.Services;

namespace CLIESCRITORIO;

public partial class LoginForm : Form
{
    private readonly ApiClient _api;
    public LoginForm() { _api = new ApiClient(); InitializeComponent(); }

    private void InitializeComponent()
    {
        Text = "EurekaBank REST - Login"; Size = new(350, 200); StartPosition = FormStartPosition.CenterScreen; FormBorderStyle = FormBorderStyle.FixedDialog; MaximizeBox = false;
        var lblU = new Label { Text = "Usuario:", Location = new(20, 30), AutoSize = true };
        var txtU = new TextBox { Name = "txtUsuario", Location = new(120, 27), Width = 180 };
        var lblC = new Label { Text = "Clave:", Location = new(20, 65), AutoSize = true };
        var txtC = new TextBox { Name = "txtClave", Location = new(120, 62), Width = 180, PasswordChar = '*' };
        var btnL = new Button { Text = "Iniciar Sesion", Location = new(120, 100), Width = 100 };
        var btnX = new Button { Text = "Cancelar", Location = new(230, 100), Width = 80 };
        btnL.Click += async (s, e) =>
        {
            string u = txtU.Text.Trim(), c = txtC.Text.Trim();
            if (string.IsNullOrEmpty(u) || string.IsNullOrEmpty(c)) { MessageBox.Show("Ingrese datos", "Error", MessageBoxButtons.OK, MessageBoxIcon.Warning); return; }
            try
            {
                bool ok = await _api.IniciarSesion(u, c);
                if (ok)
                {
                    string cc = await _api.ClienteDeUsuario(u);
                    Hide();
                    new MainForm(_api, u, string.IsNullOrEmpty(cc), cc).ShowDialog();
                    Close();
                }
                else MessageBox.Show("Credenciales incorrectas", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            catch (Exception ex) { MessageBox.Show($"Error: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        };
        btnX.Click += (s, e) => Close();
        Controls.AddRange(new Control[] { lblU, txtU, lblC, txtC, btnL, btnX });
        AcceptButton = btnL; CancelButton = btnX;
    }
}
