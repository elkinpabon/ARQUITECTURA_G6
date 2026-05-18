using CLIESCRITORIO.Services;

namespace CLIESCRITORIO;

public partial class MainForm : Form
{
    private readonly ApiClient _api;
    private readonly string _user;
    private readonly bool _admin;
    private readonly string _cliente;
    private TabControl _tabs = null!;

    public MainForm(ApiClient api, string user, bool admin, string cliente)
    {
        _api = api; _user = user; _admin = admin; _cliente = cliente;
        InitializeComponent();
    }

    private void InitializeComponent()
    {
        Text = $"EurekaBank - {_user} ({(_admin ? "ADMIN" : "CLIENTE")})";
        Size = new(900, 600); StartPosition = FormStartPosition.CenterScreen; MinimizeBox = false; MaximizeBox = false;

        var ms = new MenuStrip();
        var ma = new ToolStripMenuItem("Archivo");
        var ms2 = new ToolStripMenuItem("Salir");
        ms2.Click += (s, e) => Close();
        ma.DropDownItems.Add(ms2);
        ms.Items.Add(ma);
        MainMenuStrip = ms; Controls.Add(ms);

        _tabs = new TabControl { Dock = DockStyle.Fill, Location = new(0, 24) };

        if (_admin) { AddTab("Depositar", DoDepositar); AddTab("Retirar", DoRetirar); AddTab("Consultar Saldo", DoSaldo); AddTab("Transferir", DoTransferir); AddTab("Listar Cuentas", DoCuentas); AddTab("Listar Clientes", DoClientes); AddTab("Registrar Cliente", DoRegCliente); AddTab("Registrar Cuenta", DoRegCuenta); AddTab("Eliminar Cuenta", DoElimCuenta); AddTab("Movimientos", DoMovimientos); }
        else { AddTab("Retirar", DoRetirar); AddTab("Consultar Saldo", DoSaldo); AddTab("Transferir", DoTransferir); AddTab("Mis Cuentas", DoMisCuentas); AddTab("Movimientos", DoMovimientos); }

        Controls.Add(_tabs);
    }

    private void AddTab(string title, Action<TabPage> setup)
    {
        var tab = new TabPage(title);
        setup(tab);
        _tabs.TabPages.Add(tab);
    }

    private void DoDepositar(TabPage t)
    {
        var lblCuenta = new Label { Text = "Cuenta:", Location = new(20, 20), AutoSize = true };
        var txtCuenta = new TextBox { Location = new(120, 17), Width = 150 };
        var lblMonto = new Label { Text = "Monto:", Location = new(20, 55), AutoSize = true };
        var txtMonto = new TextBox { Location = new(120, 52), Width = 150 };
        var lblMoneda = new Label { Text = "Moneda (01/02):", Location = new(20, 90), AutoSize = true };
        var txtMoneda = new TextBox { Location = new(120, 87), Width = 60, Text = "01" };
        var btnDepositar = new Button { Text = "Depositar", Location = new(120, 120) };
        var lblResultado = new Label { Location = new(20, 160), AutoSize = true };
        btnDepositar.Click += async (s, e) => { try { var r = await _api.Depositar(txtCuenta.Text.Trim(), txtMonto.Text.Trim(), txtMoneda.Text.Trim()); lblResultado.Text = r.Exitoso ? $"OK: {r.Mensaje} - Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblCuenta, txtCuenta, lblMonto, txtMonto, lblMoneda, txtMoneda, btnDepositar, lblResultado });
    }

    private void DoRetirar(TabPage t)
    {
        var lblCuenta = new Label { Text = "Cuenta:", Location = new(20, 20), AutoSize = true };
        var txtCuenta = new TextBox { Location = new(120, 17), Width = 150 };
        var lblMonto = new Label { Text = "Monto:", Location = new(20, 55), AutoSize = true };
        var txtMonto = new TextBox { Location = new(120, 52), Width = 150 };
        var lblMoneda = new Label { Text = "Moneda (01/02):", Location = new(20, 90), AutoSize = true };
        var txtMoneda = new TextBox { Location = new(120, 87), Width = 60, Text = "01" };
        var btnRetirar = new Button { Text = "Retirar", Location = new(120, 120) };
        var lblResultado = new Label { Location = new(20, 160), AutoSize = true };
        btnRetirar.Click += async (s, e) => { try { var r = await _api.Retirar(txtCuenta.Text.Trim(), txtMonto.Text.Trim(), txtMoneda.Text.Trim()); lblResultado.Text = r.Exitoso ? $"OK: {r.Mensaje} - Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblCuenta, txtCuenta, lblMonto, txtMonto, lblMoneda, txtMoneda, btnRetirar, lblResultado });
    }

    private void DoSaldo(TabPage t)
    {
        var lblCuenta = new Label { Text = "Cuenta:", Location = new(20, 20), AutoSize = true };
        var txtCuenta = new TextBox { Location = new(120, 17), Width = 150 };
        var btnConsultar = new Button { Text = "Consultar", Location = new(120, 50) };
        var lblResultado = new Label { Location = new(20, 90), AutoSize = true, Font = new("Arial", 12, FontStyle.Bold) };
        btnConsultar.Click += async (s, e) => { try { var r = await _api.ConsultarSaldo(txtCuenta.Text.Trim()); lblResultado.Text = r.Exitoso ? $"Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblCuenta, txtCuenta, btnConsultar, lblResultado });
    }

    private void DoTransferir(TabPage t)
    {
        var lblOrigen = new Label { Text = "Origen:", Location = new(20, 20), AutoSize = true };
        var txtOrigen = new TextBox { Location = new(120, 17), Width = 150 };
        var lblDestino = new Label { Text = "Destino:", Location = new(20, 55), AutoSize = true };
        var txtDestino = new TextBox { Location = new(120, 52), Width = 150 };
        var lblMonto = new Label { Text = "Monto:", Location = new(20, 90), AutoSize = true };
        var txtMonto = new TextBox { Location = new(120, 87), Width = 150 };
        var btnTransferir = new Button { Text = "Transferir", Location = new(120, 120) };
        var lblResultado = new Label { Location = new(20, 160), AutoSize = true };
        btnTransferir.Click += async (s, e) => { try { var r = await _api.Transferir(txtOrigen.Text.Trim(), txtDestino.Text.Trim(), txtMonto.Text.Trim(), "01"); lblResultado.Text = r.Exitoso ? $"OK: {r.Mensaje} - Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblOrigen, txtOrigen, lblDestino, txtDestino, lblMonto, txtMonto, btnTransferir, lblResultado });
    }

    private void DoCuentas(TabPage t)
    {
        var lblCliente = new Label { Text = "Cliente/DNI:", Location = new(20, 20), AutoSize = true };
        var txtCliente = new TextBox { Location = new(100, 17), Width = 120 };
        var btnListar = new Button { Text = "Listar", Location = new(230, 15) };
        var dgv = new DataGridView { Location = new(10, 50), Size = new(850, 400), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
        btnListar.Click += async (s, e) => { try { var list = await _api.ListarCuentas(txtCliente.Text.Trim()); dgv.DataSource = new System.ComponentModel.BindingList<CuentaResumen>(list); } catch (Exception ex) { MessageBox.Show($"Error: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); } };
        t.Controls.AddRange(new Control[] { lblCliente, txtCliente, btnListar, dgv });
    }

    private void DoMisCuentas(TabPage t)
    {
        var btnListar = new Button { Text = "Listar Mis Cuentas", Location = new(20, 15) };
        var dgv = new DataGridView { Location = new(10, 50), Size = new(850, 400), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
        btnListar.Click += async (s, e) => { try { var list = await _api.ListarCuentas(_cliente); dgv.DataSource = new System.ComponentModel.BindingList<CuentaResumen>(list); } catch (Exception ex) { MessageBox.Show($"Error: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); } };
        t.Controls.AddRange(new Control[] { btnListar, dgv });
    }

    private void DoClientes(TabPage t)
    {
        var btnListar = new Button { Text = "Listar Todos", Location = new(20, 15) };
        var dgv = new DataGridView { Location = new(10, 50), Size = new(850, 400), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
        btnListar.Click += async (s, e) => { try { var list = await _api.ListarClientes(); dgv.DataSource = new System.ComponentModel.BindingList<ClienteResumen>(list); } catch (Exception ex) { MessageBox.Show($"Error: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); } };
        t.Controls.AddRange(new Control[] { btnListar, dgv });
    }

    private void DoRegCliente(TabPage t)
    {
        var lblPaterno = new Label { Text = "Paterno:", Location = new(20, 20), AutoSize = true };
        var txtPaterno = new TextBox { Location = new(120, 17), Width = 180 };
        var lblMaterno = new Label { Text = "Materno:", Location = new(20, 55), AutoSize = true };
        var txtMaterno = new TextBox { Location = new(120, 52), Width = 180 };
        var lblNombre = new Label { Text = "Nombre:", Location = new(20, 90), AutoSize = true };
        var txtNombre = new TextBox { Location = new(120, 87), Width = 180 };
        var lblDni = new Label { Text = "DNI:", Location = new(20, 125), AutoSize = true };
        var txtDni = new TextBox { Location = new(120, 122), Width = 100 };
        var lblCiudad = new Label { Text = "Ciudad:", Location = new(20, 160), AutoSize = true };
        var txtCiudad = new TextBox { Location = new(120, 157), Width = 180 };
        var lblDireccion = new Label { Text = "Direccion:", Location = new(20, 195), AutoSize = true };
        var txtDireccion = new TextBox { Location = new(120, 192), Width = 180 };
        var lblTelefono = new Label { Text = "Telefono:", Location = new(20, 230), AutoSize = true };
        var txtTelefono = new TextBox { Location = new(120, 227), Width = 150 };
        var lblEmail = new Label { Text = "Email:", Location = new(20, 265), AutoSize = true };
        var txtEmail = new TextBox { Location = new(120, 262), Width = 180 };
        var btnRegistrar = new Button { Text = "Registrar", Location = new(120, 300) };
        var lblResultado = new Label { Location = new(20, 340), AutoSize = true };
        btnRegistrar.Click += async (s, e) => { try { var r = await _api.RegistrarCliente(txtPaterno.Text.Trim(), txtMaterno.Text.Trim(), txtNombre.Text.Trim(), txtDni.Text.Trim(), txtCiudad.Text.Trim(), txtDireccion.Text.Trim(), txtTelefono.Text.Trim(), txtEmail.Text.Trim()); lblResultado.Text = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblPaterno, txtPaterno, lblMaterno, txtMaterno, lblNombre, txtNombre, lblDni, txtDni, lblCiudad, txtCiudad, lblDireccion, txtDireccion, lblTelefono, txtTelefono, lblEmail, txtEmail, btnRegistrar, lblResultado });
    }

    private void DoRegCuenta(TabPage t)
    {
        var lblCliente = new Label { Text = "Cliente:", Location = new(20, 20), AutoSize = true };
        var txtCliente = new TextBox { Location = new(120, 17), Width = 120 };
        var lblMoneda = new Label { Text = "Moneda (01/02):", Location = new(20, 55), AutoSize = true };
        var txtMoneda = new TextBox { Location = new(120, 52), Width = 60, Text = "01" };
        var btnRegistrar = new Button { Text = "Registrar", Location = new(120, 85) };
        var lblResultado = new Label { Location = new(20, 125), AutoSize = true };
        btnRegistrar.Click += async (s, e) => { try { var r = await _api.RegistrarCuenta(txtCliente.Text.Trim(), txtMoneda.Text.Trim()); lblResultado.Text = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblCliente, txtCliente, lblMoneda, txtMoneda, btnRegistrar, lblResultado });
    }

    private void DoElimCuenta(TabPage t)
    {
        var lblCuenta = new Label { Text = "Cuenta:", Location = new(20, 20), AutoSize = true };
        var txtCuenta = new TextBox { Location = new(120, 17), Width = 150 };
        var btnEliminar = new Button { Text = "Eliminar", Location = new(120, 50) };
        var lblResultado = new Label { Location = new(20, 90), AutoSize = true };
        btnEliminar.Click += async (s, e) => { try { var r = await _api.EliminarCuenta(txtCuenta.Text.Trim()); lblResultado.Text = r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"; lblResultado.ForeColor = r.Exitoso ? Color.Green : Color.Red; } catch (Exception ex) { lblResultado.Text = $"Error: {ex.Message}"; lblResultado.ForeColor = Color.Red; } };
        t.Controls.AddRange(new Control[] { lblCuenta, txtCuenta, btnEliminar, lblResultado });
    }

    private void DoMovimientos(TabPage t)
    {
        var lblCuenta = new Label { Text = "Cuenta:", Location = new(20, 20), AutoSize = true };
        var txtCuenta = new TextBox { Location = new(120, 17), Width = 150 };
        var btnListar = new Button { Text = "Listar", Location = new(280, 15) };
        var dgv = new DataGridView { Location = new(10, 50), Size = new(850, 400), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
        btnListar.Click += async (s, e) => { try { var list = await _api.ListarMovimientos(txtCuenta.Text.Trim()); dgv.DataSource = new System.ComponentModel.BindingList<MovimientoModel>(list); } catch (Exception ex) { MessageBox.Show($"Error: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); } };
        t.Controls.AddRange(new Control[] { lblCuenta, txtCuenta, btnListar, dgv });
    }
}
