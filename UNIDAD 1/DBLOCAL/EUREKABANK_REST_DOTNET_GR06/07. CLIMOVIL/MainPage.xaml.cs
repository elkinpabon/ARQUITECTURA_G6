using CLIMOVIL.Services;

namespace CLIMOVIL;

public partial class MainPage : ContentPage
{
    private readonly ApiClient _api;
    private string _usuario = "", _clienteCodigo = "";
    private bool _isAdmin = false;
    private string _currentAction = "";

    public MainPage()
    {
        InitializeComponent();
        _api = new ApiClient("http://10.0.2.2:5010");
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        _usuario = txtUsuario.Text?.Trim() ?? "";
        string clave = txtClave.Text?.Trim() ?? "";
        if (string.IsNullOrEmpty(_usuario) || string.IsNullOrEmpty(clave)) { lblError.Text = "Ingrese datos"; return; }
        try
        {
            bool ok = await Task.Run(() => _api.IniciarSesion(_usuario, clave));
            if (ok)
            {
                _clienteCodigo = await Task.Run(() => _api.ClienteDeUsuario(_usuario));
                _isAdmin = string.IsNullOrEmpty(_clienteCodigo);
                lblUser.Text = $"{_usuario} ({(_isAdmin ? "ADMIN" : "CLIENTE")})";
                frmLogin.IsVisible = false; frmMenu.IsVisible = true; lblError.Text = "";
            }
            else lblError.Text = "Credenciales incorrectas";
        }
        catch (Exception ex) { lblError.Text = $"Error: {ex.Message}"; }
    }

    private void OnLogoutClicked(object sender, EventArgs e)
    {
        _usuario = ""; _isAdmin = false; _clienteCodigo = "";
        txtUsuario.Text = ""; txtClave.Text = "";
        frmMenu.IsVisible = false; frmLogin.IsVisible = true; HideAll();
    }

    private void OnSaldoClicked(object sender, EventArgs e) { _currentAction = "saldo"; ShowAction("Consultar Saldo", "Cuenta", false, false, "Consultar"); }
    private void OnRetirarClicked(object sender, EventArgs e) { _currentAction = "retirar"; ShowAction("Retirar", "Cuenta", true, true, "Retirar"); txtCampo2.Placeholder = "Monto"; txtCampo3.Placeholder = "Moneda(01/02)"; txtCampo3.Text = "01"; }
    private void OnTransferirClicked(object sender, EventArgs e) { _currentAction = "transferir"; ShowAction("Transferir", "Origen", true, true, "Transferir"); txtCampo2.Placeholder = "Destino"; txtCampo3.Placeholder = "Monto"; }
    private void OnCuentasClicked(object sender, EventArgs e) { _currentAction = "cuentas"; ShowAction("Mis Cuentas", "Cliente", false, false, "Listar"); txtCampo1.Text = _clienteCodigo; }
    private void OnMovimientosClicked(object sender, EventArgs e) { _currentAction = "movimientos"; ShowAction("Movimientos", "Cuenta", false, false, "Listar"); }

    private void ShowAction(string title, string ph, bool f2, bool f3, string btn)
    {
        HideAll(); frmAction.IsVisible = true;
        lblActionTitle.Text = title; txtCampo1.Placeholder = ph; txtCampo1.Text = "";
        txtCampo2.IsVisible = f2; txtCampo3.IsVisible = f3;
        btnAction.Text = btn; lblActionResult.Text = "";
    }

    private async void OnActionClicked(object sender, EventArgs e)
    {
        try
        {
            switch (_currentAction)
            {
                case "saldo":
                    var rs = await Task.Run(() => _api.ConsultarSaldo(txtCampo1.Text.Trim()));
                    lblActionResult.Text = rs.Exitoso ? $"Saldo: {rs.Saldo:F2}" : $"ERROR: {rs.Mensaje}";
                    break;
                case "retirar":
                    var rr = await Task.Run(() => _api.Retirar(txtCampo1.Text.Trim(), txtCampo2.Text.Trim(), txtCampo3.Text.Trim()));
                    lblActionResult.Text = rr.Exitoso ? $"OK: {rr.Mensaje} Saldo:{rr.Saldo:F2}" : $"ERROR: {rr.Mensaje}";
                    break;
                case "transferir":
                    var rt = await Task.Run(() => _api.Transferir(txtCampo1.Text.Trim(), txtCampo2.Text.Trim(), txtCampo3.Text.Trim(), "01"));
                    lblActionResult.Text = rt.Exitoso ? $"OK: {rt.Mensaje} Saldo:{rt.Saldo:F2}" : $"ERROR: {rt.Mensaje}";
                    break;
                case "cuentas":
                    var lc = await Task.Run(() => _api.ListarCuentas(txtCampo1.Text.Trim()));
                    HideAll(); frmList.IsVisible = true; lblListTitle.Text = "Mis Cuentas";
                    cvList.ItemsSource = lc.Select(c => new { DisplayText = $"{c.CodigoCuenta} | {c.Moneda} | S/. {c.Saldo:F2} | {c.Estado}" }).ToList();
                    return;
                case "movimientos":
                    var lm = await Task.Run(() => _api.ListarMovimientos(txtCampo1.Text.Trim()));
                    HideAll(); frmList.IsVisible = true; lblListTitle.Text = $"Movimientos - {txtCampo1.Text}";
                    cvList.ItemsSource = lm.Select(m => new { DisplayText = $"{m.FechaMovimiento} | {m.TipoDescripcion} | {m.ImporteMovimiento:F2}" }).ToList();
                    return;
            }
            lblActionResult.TextColor = lblActionResult.Text.StartsWith("ERROR") ? Colors.Red : Colors.Green;
        }
        catch (Exception ex) { lblActionResult.Text = $"Error: {ex.Message}"; lblActionResult.TextColor = Colors.Red; }
    }

    private void OnVolverClicked(object sender, EventArgs e) { HideAll(); frmMenu.IsVisible = true; }
    private void HideAll() { frmAction.IsVisible = false; frmList.IsVisible = false; }
}
