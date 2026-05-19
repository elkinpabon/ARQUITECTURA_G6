using CLIMOVIL.Services;
using System.Globalization;

namespace CLIMOVIL;

public partial class MainPage : ContentPage
{
    private readonly SoapClientService _soap;
    private readonly List<ClienteResumen> _clientes = new();
    private readonly List<CuentaResumen> _cuentas = new();
    private readonly List<MovimientoModel> _movimientos = new();

    private string _usuario = string.Empty;
    private string _clienteCodigo = string.Empty;
    private bool _isAdmin;
    private string _currentAction = string.Empty;

    public MainPage()
    {
        InitializeComponent();
        _soap = new SoapClientService();
        pkrActionMoneda.ItemsSource = new[] { "Soles", "Dólares" };
        pkrActionMoneda.SelectedIndex = 0;
        ShowLogin();
    }

    private async void OnLoginClicked(object sender, EventArgs e)
    {
        var usuario = txtUsuario.Text?.Trim() ?? string.Empty;
        var clave = txtClave.Text?.Trim() ?? string.Empty;

        if (string.IsNullOrWhiteSpace(usuario) || string.IsNullOrWhiteSpace(clave))
        {
            lblError.Text = "Ingrese usuario y clave";
            return;
        }

        try
        {
            var ok = await Task.Run(() => _soap.IniciarSesion(usuario, clave));
            if (!ok)
            {
                lblError.Text = "Usuario o clave inválidos";
                return;
            }

            _usuario = usuario;
            _isAdmin = string.Equals(usuario, "monster", StringComparison.OrdinalIgnoreCase);
            _clienteCodigo = _isAdmin ? string.Empty : await Task.Run(() => _soap.ClienteDeUsuario(usuario));

            ShowDashboard();
            ApplyRole();

            if (_isAdmin)
            {
                await LoadClientsAsync();
                await LoadAccountsAsync(string.Empty, clearIfEmpty: true);
            }
            else
            {
                await LoadAccountsAsync(_clienteCodigo, clearIfEmpty: true);
            }

            lblError.Text = string.Empty;
        }
        catch (Exception ex)
        {
            lblError.Text = $"Error: {ex.Message}";
        }
    }

    private async void OnLoadAccountsClicked(object sender, EventArgs e)
    {
        if (!_isAdmin)
        {
            return;
        }

        var cliente = GetSelectedClientCode();
        if (string.IsNullOrWhiteSpace(cliente))
        {
            await DisplayAlert("Cuentas", "Seleccione un cliente.", "OK");
            return;
        }

        await LoadAccountsAsync(cliente, clearIfEmpty: false);
    }

    private async void OnConsultarSaldoClicked(object sender, EventArgs e) => await PrepareActionAsync("consultar");
    private async void OnRetirarClicked(object sender, EventArgs e) => await PrepareActionAsync("retirar");
    private async void OnDepositarClicked(object sender, EventArgs e)
    {
        if (!_isAdmin)
        {
            await DisplayAlert("Depósito", "Solo el administrador puede depositar.", "OK");
            return;
        }

        await PrepareActionAsync("depositar");
    }
    private async void OnTransferirClicked(object sender, EventArgs e) => await PrepareActionAsync("transferir");

    private async void OnActionClicked(object sender, EventArgs e)
    {
        try
        {
            var cuenta = GetActionAccount();
            if (string.IsNullOrWhiteSpace(cuenta))
            {
                await DisplayAlert("Operación", "Ingrese una cuenta.", "OK");
                return;
            }

            Resultado? resultado = null;
            switch (_currentAction)
            {
                case "consultar":
                {
                    resultado = await Task.Run(() => _soap.ConsultarSaldo(cuenta));
                    lblActionResult.Text = resultado.Exitoso
                        ? $"Saldo actual: {resultado.Saldo:F2}"
                        : resultado.Mensaje;
                    break;
                }

                case "retirar":
                {
                    var montoRetiro = await TryGetMontoAsync();
                    if (montoRetiro is null) return;
                    resultado = await Task.Run(() => _soap.Retirar(cuenta, montoRetiro, GetCurrencyCode()));
                    lblActionResult.Text = FormatResultado(resultado);
                    break;
                }

                case "depositar":
                {
                    var montoDeposito = await TryGetMontoAsync();
                    if (montoDeposito is null) return;
                    resultado = await Task.Run(() => _soap.Depositar(cuenta, montoDeposito, GetCurrencyCode()));
                    lblActionResult.Text = FormatResultado(resultado);
                    break;
                }

                case "transferir":
                {
                    var montoTransferencia = await TryGetMontoAsync();
                    if (montoTransferencia is null) return;
                    var destino = await DisplayPromptAsync("Transferir", "Cuenta destino", "Enviar", "Cancelar", "Cuenta destino");
                    if (string.IsNullOrWhiteSpace(destino)) return;
                    resultado = await Task.Run(() => _soap.Transferir(cuenta, destino.Trim(), montoTransferencia, GetCurrencyCode()));
                    lblActionResult.Text = FormatResultado(resultado);
                    break;
                }

                default:
                    await DisplayAlert("Operación", "Acción inválida.", "OK");
                    return;
            }

            lblActionResult.TextColor = resultado != null && resultado.Exitoso ? Color.FromArgb("#166534") : Color.FromArgb("#B91C1C");
            if (resultado is { Exitoso: true } && _currentAction != "consultar")
            {
                await RefreshAccountsAsync();
            }
        }
        catch (Exception ex)
        {
            lblActionResult.Text = $"Error: {ex.Message}";
            lblActionResult.TextColor = Color.FromArgb("#B91C1C");
        }
    }

    private async void OnMovimientosClicked(object sender, EventArgs e)
    {
        var cuenta = GetActionAccount();
        if (string.IsNullOrWhiteSpace(cuenta))
        {
            cuenta = await DisplayPromptAsync("Movimientos", "Cuenta", "Buscar", "Cancelar", "Cuenta");
        }

        if (string.IsNullOrWhiteSpace(cuenta))
        {
            return;
        }

        await LoadMovimientosAsync(cuenta.Trim());
    }

    private async void OnClientesClicked(object sender, EventArgs e)
    {
        if (!_isAdmin)
        {
            return;
        }

        await LoadClientsAsync();
        lblListTitle.Text = "Clientes";
        lblListSubTitle.Text = "Listado general de clientes";
        cvList.ItemsSource = _clientes;
        ShowList();
    }

    private async void OnRegistrarClienteClicked(object sender, EventArgs e)
    {
        if (!_isAdmin)
        {
            return;
        }

        try
        {
            var paterno = await PromptRequiredAsync("Registrar cliente", "Apellido paterno");
            if (paterno is null) return;
            var materno = await PromptRequiredAsync("Registrar cliente", "Apellido materno");
            if (materno is null) return;
            var nombre = await PromptRequiredAsync("Registrar cliente", "Nombre");
            if (nombre is null) return;
            var dni = await PromptRequiredAsync("Registrar cliente", "DNI", Keyboard.Numeric);
            if (dni is null) return;
            var ciudad = await PromptRequiredAsync("Registrar cliente", "Ciudad");
            if (ciudad is null) return;
            var direccion = await PromptRequiredAsync("Registrar cliente", "Dirección");
            if (direccion is null) return;
            var telefono = await PromptRequiredAsync("Registrar cliente", "Teléfono", Keyboard.Telephone);
            if (telefono is null) return;
            var email = await PromptRequiredAsync("Registrar cliente", "Email", Keyboard.Email);
            if (email is null) return;

            var resultado = await Task.Run(() => _soap.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email));
            await DisplayAlert("Registrar cliente", FormatResultado(resultado), "OK");
            await LoadClientsAsync();
        }
        catch (Exception ex)
        {
            await DisplayAlert("Registrar cliente", ex.Message, "OK");
        }
    }

    private async void OnRegistrarCuentaClicked(object sender, EventArgs e)
    {
        if (!_isAdmin)
        {
            return;
        }

        try
        {
            var cliente = GetSelectedClientCode();
            if (string.IsNullOrWhiteSpace(cliente))
            {
                cliente = await PromptRequiredAsync("Registrar cuenta", "Código de cliente");
            }

            if (string.IsNullOrWhiteSpace(cliente)) return;

            var moneda = await SelectMonedaAsync();
            if (moneda is null) return;

            var resultado = await Task.Run(() => _soap.RegistrarCuenta(cliente.Trim(), moneda));
            await DisplayAlert("Registrar cuenta", FormatResultado(resultado), "OK");
            await RefreshAccountsAsync();
        }
        catch (Exception ex)
        {
            await DisplayAlert("Registrar cuenta", ex.Message, "OK");
        }
    }

    private async void OnEliminarCuentaClicked(object sender, EventArgs e)
    {
        if (!_isAdmin)
        {
            return;
        }

        try
        {
            var cuenta = GetSelectedAccountCode();
            if (string.IsNullOrWhiteSpace(cuenta))
            {
                cuenta = await PromptRequiredAsync("Eliminar cuenta", "Código de cuenta");
            }

            if (string.IsNullOrWhiteSpace(cuenta)) return;

            if (!await DisplayAlert("Eliminar cuenta", $"Eliminar la cuenta {cuenta.Trim()}.", "Eliminar", "Cancelar"))
            {
                return;
            }

            var resultado = await Task.Run(() => _soap.EliminarCuenta(cuenta.Trim()));
            await DisplayAlert("Eliminar cuenta", FormatResultado(resultado), "OK");
            await RefreshAccountsAsync();
        }
        catch (Exception ex)
        {
            await DisplayAlert("Eliminar cuenta", ex.Message, "OK");
        }
    }

    private void OnLogoutClicked(object sender, EventArgs e)
    {
        _usuario = string.Empty;
        _clienteCodigo = string.Empty;
        _isAdmin = false;
        _currentAction = string.Empty;
        _clientes.Clear();
        _cuentas.Clear();
        _movimientos.Clear();

        txtUsuario.Text = string.Empty;
        txtClave.Text = string.Empty;
        lblError.Text = string.Empty;
        pkrClientes.ItemsSource = null;
        pkrCuentas.ItemsSource = null;
        cvCuentas.ItemsSource = null;
        cvList.ItemsSource = null;

        ShowLogin();
    }

    private void OnCancelarActionClicked(object sender, EventArgs e)
    {
        frmAction.IsVisible = false;
        _currentAction = string.Empty;
    }

    private void OnListBackClicked(object sender, EventArgs e)
    {
        frmList.IsVisible = false;
    }

    private void ShowLogin()
    {
        frmLogin.IsVisible = true;
        frmSession.IsVisible = false;
        frmMenu.IsVisible = false;
        frmAction.IsVisible = false;
        frmList.IsVisible = false;
        lblSessionUser.Text = string.Empty;
        lblSessionRole.Text = string.Empty;
    }

    private void ShowDashboard()
    {
        frmLogin.IsVisible = false;
        frmSession.IsVisible = true;
        frmMenu.IsVisible = true;
        frmAction.IsVisible = false;
        frmList.IsVisible = false;
    }

    private void ShowList()
    {
        frmAction.IsVisible = false;
        frmList.IsVisible = true;
    }

    private void ApplyRole()
    {
        lblSessionUser.Text = $"Usuario: {_usuario}";
        lblSessionRole.Text = _isAdmin
            ? "Rol: ADMIN · puede administrar clientes y cuentas"
            : $"Rol: CLIENTE · código {_clienteCodigo}";

        pnlClients.IsVisible = _isAdmin;
        btnDeposit.IsVisible = _isAdmin;
        btnClientes.IsVisible = _isAdmin;
        btnRegCliente.IsVisible = _isAdmin;
        btnRegCuenta.IsVisible = _isAdmin;
        btnEliminarCuenta.IsVisible = _isAdmin;
    }

    private async Task LoadClientsAsync()
    {
        _clientes.Clear();
        var clients = await Task.Run(() => _soap.ListarClientes());
        _clientes.AddRange(clients);
        pkrClientes.ItemsSource = _clientes;
        pkrClientes.SelectedIndex = -1;
    }

    private async Task LoadAccountsAsync(string cliente, bool clearIfEmpty)
    {
        if (string.IsNullOrWhiteSpace(cliente))
        {
            if (clearIfEmpty)
            {
                _cuentas.Clear();
                pkrCuentas.ItemsSource = null;
                cvCuentas.ItemsSource = null;
                lblAccountsSummary.Text = _isAdmin ? "Seleccione un cliente" : "Sin cuentas";
            }

            return;
        }

        _cuentas.Clear();
        var cuentas = await Task.Run(() => _soap.ListarCuentasPorCliente(cliente));
        _cuentas.AddRange(cuentas);
        pkrCuentas.ItemsSource = null;
        pkrCuentas.ItemsSource = _cuentas;
        pkrCuentas.SelectedIndex = _cuentas.Count > 0 ? 0 : -1;
        cvCuentas.ItemsSource = _cuentas;
        lblAccountsSummary.Text = _cuentas.Count == 0
            ? "Sin cuentas"
            : $"Cuentas: {_cuentas.Count} | Total: {FormatMoney(_cuentas.Sum(x => x.Saldo))}";
    }

    private async Task RefreshAccountsAsync()
    {
        var cliente = _isAdmin ? GetSelectedClientCode() : _clienteCodigo;
        if (string.IsNullOrWhiteSpace(cliente))
        {
            return;
        }

        await LoadAccountsAsync(cliente, clearIfEmpty: true);
    }

    private Task PrepareActionAsync(string action)
    {
        _currentAction = action;
        frmList.IsVisible = false;
        frmAction.IsVisible = true;

        txtActionCuenta.Text = GetSelectedAccountCode();
        txtActionMonto.Text = string.Empty;
        pkrActionMoneda.SelectedIndex = 0;
        lblActionResult.Text = string.Empty;
        lblActionResult.TextColor = Color.FromArgb("#0F172A");

        switch (action)
        {
            case "consultar":
                lblActionTitle.Text = "Consultar saldo";
                lblActionHint.Text = "Ingrese o seleccione la cuenta";
                btnAction.Text = "Consultar";
                txtActionCuenta.Placeholder = "Cuenta";
                txtActionMonto.IsVisible = false;
                pkrActionMoneda.IsVisible = false;
                break;
            case "retirar":
                lblActionTitle.Text = "Retirar";
                lblActionHint.Text = "Cuenta, monto y moneda";
                btnAction.Text = "Retirar";
                txtActionCuenta.Placeholder = "Cuenta";
                txtActionMonto.IsVisible = true;
                pkrActionMoneda.IsVisible = true;
                break;
            case "depositar":
                lblActionTitle.Text = "Depositar";
                lblActionHint.Text = "Solo administrador";
                btnAction.Text = "Depositar";
                txtActionCuenta.Placeholder = "Cuenta";
                txtActionMonto.IsVisible = true;
                pkrActionMoneda.IsVisible = true;
                break;
            case "transferir":
                lblActionTitle.Text = "Transferir";
                lblActionHint.Text = "Cuenta origen, monto y moneda";
                btnAction.Text = "Transferir";
                txtActionCuenta.Placeholder = "Cuenta origen";
                txtActionMonto.IsVisible = true;
                pkrActionMoneda.IsVisible = true;
                break;
        }

        return Task.CompletedTask;
    }

    private async Task LoadMovimientosAsync(string cuenta)
    {
        _movimientos.Clear();
        var movimientos = await Task.Run(() => _soap.ListarMovimientos(cuenta));
        _movimientos.AddRange(movimientos);

        lblListTitle.Text = $"Movimientos - {cuenta}";
        if (_movimientos.Count == 0)
        {
            lblListSubTitle.Text = "Sin movimientos";
        }
        else
        {
            var ingresos = _movimientos.Where(x => x.EsIngreso).Sum(x => x.ImporteMovimiento);
            var egresos = _movimientos.Where(x => !x.EsIngreso).Sum(x => x.ImporteMovimiento);
            lblListSubTitle.Text = $"Créditos + {FormatMoney(ingresos)} | Débitos - {FormatMoney(egresos)} | Neto {FormatMoney(ingresos - egresos)}";
        }

        cvList.ItemsSource = _movimientos;
        ShowList();
    }

    private async Task<string?> TryGetMontoAsync()
    {
        var monto = NormalizeNumber(txtActionMonto.Text);
        if (string.IsNullOrWhiteSpace(monto))
        {
            await DisplayAlert("Operación", "Ingrese un monto.", "OK");
            return null;
        }

        return monto;
    }

    private string GetActionAccount()
        => NormalizeText(txtActionCuenta.Text) ?? GetSelectedAccountCode();

    private string GetSelectedClientCode()
        => (pkrClientes.SelectedItem as ClienteResumen)?.Codigo ?? string.Empty;

    private string GetSelectedAccountCode()
        => (pkrCuentas.SelectedItem as CuentaResumen)?.CodigoCuenta ?? string.Empty;

    private async Task<string?> PromptRequiredAsync(string title, string placeholder, Keyboard? keyboard = null)
        => await DisplayPromptAsync(title, placeholder, "OK", "Cancelar", placeholder, -1, keyboard ?? Keyboard.Default, string.Empty);

    private async Task<string?> SelectMonedaAsync()
    {
        var moneda = await DisplayActionSheet("Moneda", "Cancelar", null, "Soles", "Dólares");
        if (string.IsNullOrWhiteSpace(moneda) || moneda == "Cancelar") return null;
        return moneda == "Dólares" ? "02" : "01";
    }

    private string GetCurrencyCode() => pkrActionMoneda.SelectedIndex == 1 ? "02" : "01";

    private string FormatResultado(Resultado r)
    {
        var text = r.Exitoso
            ? string.IsNullOrWhiteSpace(r.Mensaje) ? "Operación exitosa" : r.Mensaje
            : string.IsNullOrWhiteSpace(r.Mensaje) ? "Operación fallida" : r.Mensaje;
        return r.Saldo != 0 ? $"{text} | Saldo: {FormatMoney(r.Saldo)}" : text;
    }

    private string FormatMoney(double value) => value.ToString("N2", CultureInfo.CurrentCulture);

    private string? NormalizeText(string? value)
        => string.IsNullOrWhiteSpace(value) ? null : value.Trim();

    private string NormalizeNumber(string? value)
        => string.IsNullOrWhiteSpace(value) ? string.Empty : value.Trim().Replace(',', '.');
}
