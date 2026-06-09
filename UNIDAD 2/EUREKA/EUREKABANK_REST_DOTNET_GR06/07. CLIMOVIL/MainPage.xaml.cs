using CLIMOVIL.Services;
using System.Globalization;

namespace CLIMOVIL;

public partial class MainPage : ContentPage
{
    private readonly ApiClient _api;
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
        _api = new ApiClient();
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
            var ok = await _api.IniciarSesion(usuario, clave);
            if (!ok)
            {
                lblError.Text = "Usuario o clave inválidos";
                return;
            }

            _usuario = usuario;
            var cliente = await _api.ClienteDeUsuario(usuario);
            _isAdmin = string.IsNullOrEmpty(cliente);
            _clienteCodigo = _isAdmin ? string.Empty : cliente;

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
                    if (!CuentaCargada(cuenta))
                    {
                        await DisplayAlert("Operación", "Seleccione una cuenta válida.", "OK");
                        return;
                    }

                    resultado = await _api.ConsultarSaldo(cuenta);
                    lblActionResult.Text = resultado.Exitoso ? $"Saldo actual: {resultado.Saldo:F2}" : resultado.Mensaje;
                    break;

                case "retirar":
                {
                    if (!CuentaCargada(cuenta))
                    {
                        await DisplayAlert("Operación", "Seleccione una cuenta válida.", "OK");
                        return;
                    }

                    var montoRetiro = await TryGetMontoAsync();
                    if (montoRetiro is null) return;
                    resultado = await _api.Retirar(cuenta, montoRetiro, GetCurrencyCode());
                    lblActionResult.Text = FormatResultado(resultado);
                    break;
                }

                case "depositar":
                {
                    if (!CuentaCargada(cuenta))
                    {
                        await DisplayAlert("Operación", "Seleccione una cuenta válida.", "OK");
                        return;
                    }

                    var montoDeposito = await TryGetMontoAsync();
                    if (montoDeposito is null) return;
                    resultado = await _api.Depositar(cuenta, montoDeposito, GetCurrencyCode());
                    lblActionResult.Text = FormatResultado(resultado);
                    break;
                }

                case "transferir":
                {
                    if (!CuentaCargada(cuenta))
                    {
                        await DisplayAlert("Operación", "Seleccione una cuenta válida.", "OK");
                        return;
                    }

                    var montoTransferencia = await TryGetMontoAsync();
                    if (montoTransferencia is null) return;
                    var destino = await DisplayPromptAsync("Transferir", "Cuenta destino", "Enviar", "Cancelar", "Cuenta destino");
                    destino = NormalizeText(destino);
                    if (string.IsNullOrWhiteSpace(destino)) return;
                    if (destino.Equals(cuenta, StringComparison.OrdinalIgnoreCase))
                    {
                        await DisplayAlert("Transferir", "La cuenta origen y destino no pueden ser iguales.", "OK");
                        return;
                    }

                    resultado = await _api.Transferir(cuenta, destino.Trim(), montoTransferencia, GetCurrencyCode());
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

            var resultado = await _api.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
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

            var resultado = await _api.RegistrarCuenta(cliente.Trim(), moneda);
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

            var resultado = await _api.EliminarCuenta(cuenta.Trim());
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
        var clients = await _api.ListarClientes();
        _clientes.AddRange(clients);
        pkrClientes.ItemsSource = _clientes;
        pkrClientes.SelectedIndex = _clientes.Count > 0 ? 0 : -1;
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
        var cuentas = await _api.ListarCuentas(cliente);
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

    private async Task LoadMovimientosAsync(string cuenta)
    {
        _movimientos.Clear();
        var movimientos = await _api.ListarMovimientos(cuenta);
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

    private string GetSelectedClientCode()
    {
        if (pkrClientes.SelectedItem is ClienteResumen cliente)
        {
            return cliente.Codigo;
        }

        return string.Empty;
    }

    private string GetSelectedAccountCode()
    {
        if (pkrCuentas.SelectedItem is CuentaResumen cuenta)
        {
            return cuenta.CodigoCuenta;
        }

        return string.Empty;
    }

    private string GetActionAccount() => txtActionCuenta.Text?.Trim() ?? string.Empty;

    private string GetCurrencyCode() => pkrActionMoneda.SelectedIndex == 1 ? "02" : "01";

    private string FormatMoney(double value) => value.ToString("N2", CultureInfo.CurrentCulture);

    private string FormatResultado(Resultado r)
    {
        var text = r.Exitoso
            ? string.IsNullOrWhiteSpace(r.Mensaje) ? "Operación exitosa" : r.Mensaje
            : string.IsNullOrWhiteSpace(r.Mensaje) ? "Operación fallida" : r.Mensaje;
        return r.Saldo != 0 ? $"{text} | Saldo: {FormatMoney(r.Saldo)}" : text;
    }

    private async Task PrepareActionAsync(string action)
    {
        _currentAction = action;
        frmAction.IsVisible = true;
        frmList.IsVisible = false;

        switch (action)
        {
            case "consultar":
                lblActionTitle.Text = "Consultar saldo";
                lblActionHint.Text = "Ingrese o seleccione la cuenta";
                txtActionCuenta.Placeholder = "Cuenta";
                txtActionCuenta.Text = string.Empty;
                txtActionMonto.IsVisible = false;
                pkrActionMoneda.IsVisible = false;
                btnAction.Text = "Consultar";
                break;
            case "retirar":
                lblActionTitle.Text = "Retirar";
                lblActionHint.Text = "Cuenta, monto y moneda";
                txtActionCuenta.Placeholder = "Cuenta";
                txtActionMonto.Placeholder = "Monto";
                txtActionMonto.IsVisible = true;
                pkrActionMoneda.IsVisible = true;
                btnAction.Text = "Retirar";
                break;
            case "depositar":
                lblActionTitle.Text = "Depositar";
                lblActionHint.Text = "Solo administrador";
                txtActionCuenta.Placeholder = "Cuenta";
                txtActionMonto.Placeholder = "Monto";
                txtActionMonto.IsVisible = true;
                pkrActionMoneda.IsVisible = true;
                btnAction.Text = "Depositar";
                break;
            case "transferir":
                lblActionTitle.Text = "Transferir";
                lblActionHint.Text = "Cuenta origen, monto y moneda";
                txtActionCuenta.Placeholder = "Cuenta origen";
                txtActionMonto.Placeholder = "Monto";
                txtActionMonto.IsVisible = true;
                pkrActionMoneda.IsVisible = true;
                btnAction.Text = "Transferir";
                break;
        }

        lblActionResult.Text = string.Empty;
        lblActionResult.TextColor = Color.FromArgb("#0F172A");
    }

    private async Task<string?> TryGetMontoAsync()
    {
        var monto = txtActionMonto.Text?.Trim() ?? string.Empty;
        if (string.IsNullOrWhiteSpace(monto))
        {
            await DisplayAlert("Operación", "Ingrese un monto.", "OK");
            return null;
        }

        var normalized = monto.Replace(',', '.');
        if (!decimal.TryParse(normalized, NumberStyles.Number, CultureInfo.InvariantCulture, out var valor) || valor <= 0)
        {
            await DisplayAlert("Operación", "Ingrese un monto válido mayor que cero.", "OK");
            return null;
        }

        return valor.ToString(CultureInfo.InvariantCulture);
    }

    private async Task<string?> PromptRequiredAsync(string title, string text, Keyboard? keyboard = null)
    {
        var value = await DisplayPromptAsync(title, text, "OK", "Cancelar", keyboard: keyboard ?? Keyboard.Default);
        if (string.IsNullOrWhiteSpace(value))
        {
            return null;
        }

        return value.Trim();
    }

    private async Task<string?> SelectMonedaAsync()
    {
        var result = await DisplayActionSheet("Moneda", "Cancelar", null, "Soles", "Dólares");
        return result switch
        {
            "Soles" => "01",
            "Dólares" => "02",
            _ => null
        };
    }

    private string? NormalizeText(string? value)
        => string.IsNullOrWhiteSpace(value) ? null : value.Trim();

    private bool CuentaCargada(string cuenta)
        => _cuentas.Any(x => x.CodigoCuenta.Equals(cuenta, StringComparison.OrdinalIgnoreCase));
}
