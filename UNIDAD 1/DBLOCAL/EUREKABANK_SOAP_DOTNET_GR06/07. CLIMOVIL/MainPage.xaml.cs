using CLIMOVIL.Services;

namespace CLIMOVIL;

public partial class MainPage : ContentPage
{
    private readonly SoapClientService _soap;
    private string _usuario = "";
    private bool _isAdmin = false;
    private string _clienteCodigo = "";
    private string _currentAction = "";

    public MainPage()
    {
        InitializeComponent();
        _soap = new SoapClientService("http://10.0.2.2:5000");
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        _usuario = txtUsuario.Text?.Trim() ?? "";
        string clave = txtClave.Text?.Trim() ?? "";

        if (string.IsNullOrEmpty(_usuario) || string.IsNullOrEmpty(clave))
        {
            lblError.Text = "Ingrese usuario y clave";
            return;
        }

        try
        {
            bool success = await Task.Run(() => _soap.IniciarSesion(_usuario, clave));
            if (success)
            {
                _clienteCodigo = await Task.Run(() => _soap.ClienteDeUsuario(_usuario));
                _isAdmin = string.IsNullOrEmpty(_clienteCodigo);
                lblUser.Text = $"{_usuario} ({(_isAdmin ? "ADMIN" : "CLIENTE")})";
                frmLogin.IsVisible = false;
                frmMenu.IsVisible = true;
                lblError.Text = "";
            }
            else
            {
                lblError.Text = "Usuario o clave incorrectos";
            }
        }
        catch (Exception ex)
        {
            lblError.Text = $"Error: {ex.Message}";
        }
    }

    private void OnLogoutClicked(object sender, EventArgs e)
    {
        _usuario = "";
        _isAdmin = false;
        _clienteCodigo = "";
        txtUsuario.Text = "";
        txtClave.Text = "";
        frmMenu.IsVisible = false;
        frmLogin.IsVisible = true;
        HideAll();
    }

    private async void OnConsultarSaldoClicked(object sender, EventArgs e)
    {
        _currentAction = "consultar";
        HideAll();
        frmAction.IsVisible = true;
        lblActionTitle.Text = "Consultar Saldo";
        txtCampo1.Placeholder = "Codigo de cuenta";
        txtCampo1.Text = "";
        txtCampo2.IsVisible = false;
        txtCampo3.IsVisible = false;
        btnAction.Text = "Consultar";
        lblActionResult.Text = "";
    }

    private async void OnRetirarClicked(object sender, EventArgs e)
    {
        _currentAction = "retirar";
        HideAll();
        frmAction.IsVisible = true;
        lblActionTitle.Text = "Retirar";
        txtCampo1.Placeholder = "Cuenta";
        txtCampo2.Placeholder = "Monto";
        txtCampo3.Placeholder = "Moneda (01/02)";
        txtCampo1.Text = ""; txtCampo2.Text = ""; txtCampo3.Text = "01";
        txtCampo1.IsVisible = true; txtCampo2.IsVisible = true; txtCampo3.IsVisible = true;
        btnAction.Text = "Retirar";
        lblActionResult.Text = "";
    }

    private async void OnTransferirClicked(object sender, EventArgs e)
    {
        _currentAction = "transferir";
        HideAll();
        frmAction.IsVisible = true;
        lblActionTitle.Text = "Transferir";
        txtCampo1.Placeholder = "Cuenta origen";
        txtCampo2.Placeholder = "Cuenta destino";
        txtCampo3.Placeholder = "Monto";
        txtCampo1.Text = ""; txtCampo2.Text = ""; txtCampo3.Text = "";
        txtCampo1.IsVisible = true; txtCampo2.IsVisible = true; txtCampo3.IsVisible = true;
        btnAction.Text = "Transferir";
        lblActionResult.Text = "";
    }

    private async void OnMisCuentasClicked(object sender, EventArgs e)
    {
        try
        {
            var cuentas = await Task.Run(() => _soap.ListarCuentasPorCliente(_clienteCodigo));
            HideAll();
            frmList.IsVisible = true;
            lblListTitle.Text = "Mis Cuentas";
            cvList.ItemsSource = cuentas.Select(c => new { DisplayText = $"{c.CodigoCuenta} | {c.Moneda} | S/. {c.Saldo:F2} | {c.Estado}" }).ToList();
        }
        catch (Exception ex)
        {
            await DisplayAlert("Error", ex.Message, "OK");
        }
    }

    private async void OnMovimientosClicked(object sender, EventArgs e)
    {
        _currentAction = "movimientos";
        HideAll();
        frmAction.IsVisible = true;
        lblActionTitle.Text = "Movimientos";
        txtCampo1.Placeholder = "Cuenta";
        txtCampo1.Text = "";
        txtCampo2.IsVisible = false; txtCampo3.IsVisible = false;
        btnAction.Text = "Listar";
        lblActionResult.Text = "";
    }

    private async void OnActionClicked(object sender, EventArgs e)
    {
        try
        {
            Resultado r;
            switch (_currentAction)
            {
                case "consultar":
                    r = await Task.Run(() => _soap.ConsultarSaldo(txtCampo1.Text.Trim()));
                    lblActionResult.Text = r.Exitoso ? $"Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}";
                    break;
                case "retirar":
                    r = await Task.Run(() => _soap.Retirar(txtCampo1.Text.Trim(), txtCampo2.Text.Trim(), txtCampo3.Text.Trim()));
                    lblActionResult.Text = r.Exitoso ? $"OK: {r.Mensaje} - Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}";
                    break;
                case "transferir":
                    r = await Task.Run(() => _soap.Transferir(txtCampo1.Text.Trim(), txtCampo2.Text.Trim(), txtCampo3.Text.Trim(), "01"));
                    lblActionResult.Text = r.Exitoso ? $"OK: {r.Mensaje} - Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}";
                    break;
                case "movimientos":
                    var movs = await Task.Run(() => _soap.ListarMovimientos(txtCampo1.Text.Trim()));
                    HideAll();
                    frmList.IsVisible = true;
                    lblListTitle.Text = $"Movimientos - {txtCampo1.Text}";
                    cvList.ItemsSource = movs.Select(m => new { DisplayText = $"{m.FechaMovimiento} | {m.TipoDescripcion} | {m.ImporteMovimiento:F2}" }).ToList();
                    return;
            }
            lblActionResult.TextColor = lblActionResult.Text.StartsWith("ERROR") ? Colors.Red : Colors.Green;
        }
        catch (Exception ex)
        {
            lblActionResult.Text = $"Error: {ex.Message}";
            lblActionResult.TextColor = Colors.Red;
        }
    }

    private void OnVolverClicked(object sender, EventArgs e)
    {
        HideAll();
        frmMenu.IsVisible = true;
    }

    private void HideAll()
    {
        frmAction.IsVisible = false;
        frmResult.IsVisible = false;
        frmList.IsVisible = false;
    }
}
