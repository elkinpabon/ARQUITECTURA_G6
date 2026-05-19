using CLIESCRITORIO.Controlador;
using CLIESCRITORIO.Servicio;
using CLIESCRITORIO.Util;
using CLIESCRITORIO.Vista;

namespace CLIESCRITORIO;

public class MainForm : Form
{
    private readonly BancoController _ctrl;
    private readonly Action _onLogout;
    private readonly List<ClienteResumen> _clientes = new();
    private List<CuentaResumen> _cuentas = new();

    private ComboBox cboClientes = null!;
    private ComboBox cboCuenta = null!;
    private DataGridView dgvCuentas = null!;
    private Label lblTotal = null!;
    private Label lblEstado = null!;
    private Label lblUser = null!;
    private TextBox txtMonto = null!;
    private ComboBox cboMoneda = null!;
    private Button btnDepositar = null!;
    private Button btnRetirar = null!;
    private Button btnSaldo = null!;
    private Button btnTransferir = null!;
    private Button btnMov = null!;
    private Button btnActualizar = null!;
    private Button btnRegCli = null!;
    private Button btnRegCta = null!;
    private Button btnDelCta = null!;
    private Button btnVer = null!;

    public MainForm(BancoController ctrl, Action onLogout)
    {
        _ctrl = ctrl;
        _onLogout = onLogout;
        InitializeComponent();
        Load += (_, _) => OnLoaded();
    }

    private void InitializeComponent()
    {
        Text = $"EUREKABANK GR06 - {_ctrl.Sesion.Usuario}";
        Size = new Size(900, 640);
        StartPosition = FormStartPosition.CenterScreen;
        FormBorderStyle = FormBorderStyle.FixedDialog;
        MaximizeBox = false;
        MinimizeBox = false;
        BackColor = Color.FromArgb(15, 23, 42);
        ForeColor = Color.WhiteSmoke;
        Font = new Font("Segoe UI", 9.5F, FontStyle.Regular, GraphicsUnit.Point);

        var root = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 1,
            RowCount = 3,
            Padding = new Padding(18)
        };
        root.RowStyles.Add(new RowStyle(SizeType.Absolute, 76));
        root.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));
        root.RowStyles.Add(new RowStyle(SizeType.Absolute, 180));

        var top = new Panel
        {
            Dock = DockStyle.Fill,
            BackColor = Color.FromArgb(30, 41, 59),
            Padding = new Padding(18, 12, 18, 12)
        };

        var brand = new FlowLayoutPanel
        {
            Dock = DockStyle.Left,
            AutoSize = true,
            WrapContents = false,
            FlowDirection = FlowDirection.LeftToRight,
            BackColor = Color.Transparent
        };
        brand.Controls.Add(UiImages.Logo("moster.png", 40));
        brand.Controls.Add(new Label
        {
            Text = "EUREKABANK GR06",
            AutoSize = true,
            Font = new Font("Segoe UI", 18F, FontStyle.Bold),
            ForeColor = Color.FromArgb(56, 189, 248),
            Padding = new Padding(10, 4, 0, 0)
        });

        lblUser = new Label
        {
            AutoSize = true,
            Text = $"Usuario: {_ctrl.Sesion.Usuario}{(_ctrl.Sesion.Admin ? "  [ADMIN]" : string.Empty)}",
            ForeColor = Color.FromArgb(226, 232, 240),
            Dock = DockStyle.Left,
            Padding = new Padding(12, 22, 0, 0)
        };

        var topRight = new FlowLayoutPanel
        {
            Dock = DockStyle.Right,
            AutoSize = true,
            WrapContents = false,
            FlowDirection = FlowDirection.LeftToRight,
            BackColor = Color.Transparent
        };

        btnActualizar = MakeButton("Actualizar saldos");
        var btnSalir = MakeButton("Cerrar sesión", Color.FromArgb(56, 189, 248), Color.FromArgb(4, 38, 58));
        topRight.Controls.Add(btnActualizar);
        topRight.Controls.Add(btnSalir);

        top.Controls.Add(topRight);
        top.Controls.Add(lblUser);
        top.Controls.Add(brand);

        var center = new Panel
        {
            Dock = DockStyle.Fill,
            BackColor = Color.FromArgb(30, 41, 59),
            Padding = new Padding(14)
        };

        var adminRow = new FlowLayoutPanel
        {
            Dock = DockStyle.Top,
            Height = 64,
            AutoSize = false,
            WrapContents = true,
            BackColor = Color.Transparent,
            Visible = _ctrl.Sesion.Admin
        };

        adminRow.Controls.Add(new Label
        {
            Text = "Cliente registrado",
            AutoSize = true,
            ForeColor = Color.WhiteSmoke,
            Padding = new Padding(0, 12, 4, 0)
        });

        cboClientes = new ComboBox
        {
            Width = 360,
            DropDownStyle = ComboBoxStyle.DropDownList,
            BackColor = Color.FromArgb(15, 23, 42),
            ForeColor = Color.WhiteSmoke,
            FlatStyle = FlatStyle.Flat
        };
        adminRow.Controls.Add(cboClientes);

        btnVer = MakeButton("Ver cuentas");
        btnRegCli = MakeButton("Registrar cliente");
        btnRegCta = MakeButton("Registrar cuenta");
        btnDelCta = MakeButton("Eliminar cuenta", Color.FromArgb(248, 113, 113), Color.FromArgb(63, 13, 13));
        adminRow.Controls.Add(btnVer);
        adminRow.Controls.Add(btnRegCli);
        adminRow.Controls.Add(btnRegCta);
        adminRow.Controls.Add(btnDelCta);

        dgvCuentas = new DataGridView
        {
            Dock = DockStyle.Fill,
            ReadOnly = true,
            AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
            BackgroundColor = Color.FromArgb(15, 23, 42),
            ForeColor = Color.WhiteSmoke,
            BorderStyle = BorderStyle.None,
            RowHeadersVisible = false,
            AllowUserToAddRows = false,
            AllowUserToDeleteRows = false,
            AutoGenerateColumns = false
        };
        dgvCuentas.DefaultCellStyle.BackColor = Color.FromArgb(15, 23, 42);
        dgvCuentas.DefaultCellStyle.ForeColor = Color.WhiteSmoke;
        dgvCuentas.DefaultCellStyle.SelectionBackColor = Color.FromArgb(56, 189, 248);
        dgvCuentas.DefaultCellStyle.SelectionForeColor = Color.FromArgb(4, 38, 58);
        dgvCuentas.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(56, 189, 248);
        dgvCuentas.ColumnHeadersDefaultCellStyle.ForeColor = Color.FromArgb(4, 38, 58);
        dgvCuentas.EnableHeadersVisualStyles = false;
        dgvCuentas.Columns.Add(new DataGridViewTextBoxColumn { HeaderText = "Cuenta", DataPropertyName = nameof(CuentaResumen.CodigoCuenta) });
        dgvCuentas.Columns.Add(new DataGridViewTextBoxColumn { HeaderText = "Saldo", DataPropertyName = nameof(CuentaResumen.Saldo) });
        dgvCuentas.Columns.Add(new DataGridViewTextBoxColumn { HeaderText = "Moneda", DataPropertyName = nameof(CuentaResumen.Moneda) });
        dgvCuentas.Columns.Add(new DataGridViewTextBoxColumn { HeaderText = "Estado", DataPropertyName = nameof(CuentaResumen.Estado) });

        lblTotal = new Label
        {
            AutoSize = false,
            Dock = DockStyle.Bottom,
            Height = 24,
            Text = "Saldo total: 0.00",
            ForeColor = Color.FromArgb(56, 189, 248),
            Font = new Font("Segoe UI", 11F, FontStyle.Bold)
        };

        center.Controls.Add(dgvCuentas);
        center.Controls.Add(lblTotal);
        center.Controls.Add(adminRow);

        var bottom = new Panel
        {
            Dock = DockStyle.Fill,
            BackColor = Color.FromArgb(30, 41, 59),
            Padding = new Padding(14)
        };

        var row1 = new TableLayoutPanel
        {
            Dock = DockStyle.Top,
            Height = 82,
            ColumnCount = 6
        };
        row1.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 80));
        row1.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 32F));
        row1.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 80));
        row1.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 32F));
        row1.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 80));
        row1.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 36F));

        row1.Controls.Add(MakeTextLabel("Cuenta"), 0, 0);
        cboCuenta = MakeComboBox();
        row1.Controls.Add(cboCuenta, 1, 0);
        row1.Controls.Add(MakeTextLabel("Monto"), 2, 0);
        txtMonto = MakeTextBox();
        row1.Controls.Add(txtMonto, 3, 0);
        row1.Controls.Add(MakeTextLabel("Moneda"), 4, 0);
        cboMoneda = MakeComboBox();
        cboMoneda.Items.AddRange(new object[] { "Dólares (02)", "Soles (01)" });
        cboMoneda.SelectedIndex = 0;
        row1.Controls.Add(cboMoneda, 5, 0);

        var row2 = new FlowLayoutPanel
        {
            Dock = DockStyle.Top,
            Height = 72,
            FlowDirection = FlowDirection.LeftToRight,
            WrapContents = true,
            Padding = new Padding(0, 10, 0, 0)
        };

        btnDepositar = MakeButton("Depositar");
        btnRetirar = MakeButton("Retirar");
        btnSaldo = MakeButton("Consultar saldo");
        btnTransferir = MakeButton("Transferir");
        btnMov = MakeButton("Movimientos");
        if (!_ctrl.Sesion.Admin)
        {
            btnDepositar.Visible = false;
        }

        row2.Controls.AddRange(new Control[] { btnDepositar, btnRetirar, btnSaldo, btnTransferir, btnMov });

        lblEstado = new Label
        {
            Dock = DockStyle.Bottom,
            Height = 24,
            ForeColor = Color.FromArgb(148, 163, 184),
            Text = "Listo"
        };

        bottom.Controls.Add(lblEstado);
        bottom.Controls.Add(row2);
        bottom.Controls.Add(row1);

        btnSalir.Click += (_, _) =>
        {
            _ctrl.Logout();
            Close();
            _onLogout?.Invoke();
        };
        btnActualizar.Click += (_, _) => RefreshCurrent();
        btnVer.Click += (_, _) => LoadSelectedClientAccounts();
        btnRegCli.Click += (_, _) => RegistrarCliente();
        btnRegCta.Click += (_, _) => RegistrarCuenta();
        btnDelCta.Click += (_, _) => EliminarCuenta();
        btnDepositar.Click += (_, _) => HacerDeposito();
        btnRetirar.Click += (_, _) => HacerRetiro();
        btnSaldo.Click += (_, _) => ConsultarSaldo();
        btnTransferir.Click += (_, _) => Transferir();
        btnMov.Click += (_, _) => VerMovimientos();

        root.Controls.Add(top, 0, 0);
        root.Controls.Add(center, 0, 1);
        root.Controls.Add(bottom, 0, 2);
        Controls.Add(root);
    }

    private void OnLoaded()
    {
        if (_ctrl.Sesion.Admin)
        {
            CargarClientes();
            SetEstado("Seleccione un cliente y presione 'Ver cuentas'.");
        }
        else
        {
            CargarCuentas(_ctrl.Sesion.ClienteAsignado);
            SetEstado("Cuentas cargadas.");
        }
    }

    private static Button MakeButton(string text, Color? back = null, Color? fore = null)
    {
        return new Button
        {
            Text = text,
            Width = 130,
            Height = 34,
            BackColor = back ?? Color.FromArgb(56, 189, 248),
            ForeColor = fore ?? Color.FromArgb(4, 38, 58),
            FlatStyle = FlatStyle.Flat,
            Margin = new Padding(4, 6, 4, 4)
        };
    }

    private static Label MakeTextLabel(string text)
    {
        return new Label
        {
            Text = text,
            AutoSize = true,
            ForeColor = Color.WhiteSmoke,
            Dock = DockStyle.Fill,
            TextAlign = ContentAlignment.MiddleLeft
        };
    }

    private static ComboBox MakeComboBox()
    {
        return new ComboBox
        {
            Dock = DockStyle.Fill,
            DropDownStyle = ComboBoxStyle.DropDownList,
            BackColor = Color.FromArgb(15, 23, 42),
            ForeColor = Color.WhiteSmoke,
            FlatStyle = FlatStyle.Flat
        };
    }

    private static TextBox MakeTextBox()
    {
        return new TextBox
        {
            Dock = DockStyle.Fill,
            BackColor = Color.FromArgb(15, 23, 42),
            ForeColor = Color.WhiteSmoke,
            BorderStyle = BorderStyle.FixedSingle
        };
    }

    private void SetEstado(string texto) => lblEstado.Text = texto;

    private void CargarClientes()
    {
        try
        {
            _clientes.Clear();
            _clientes.AddRange(_ctrl.ListarClientes());
            cboClientes.Items.Clear();

            foreach (var c in _clientes)
            {
                cboClientes.Items.Add($"{c.Codigo}  ·  DNI {c.Dni}  ·  {c.Nombre}");
            }

            if (cboClientes.Items.Count > 0)
            {
                cboClientes.SelectedIndex = 0;
            }
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void LoadSelectedClientAccounts()
    {
        if (!_ctrl.Sesion.Admin)
        {
            return;
        }

        if (cboClientes.SelectedItem == null)
        {
            MessageBox.Show("Seleccione un cliente.", "EUREKABANK", MessageBoxButtons.OK, MessageBoxIcon.Information);
            return;
        }

        var code = cboClientes.SelectedItem.ToString()!.Split('·')[0].Trim();
        CargarCuentas(code);
        SetEstado($"Cuentas cargadas para cliente {code}.");
    }

    private void RefreshCurrent()
    {
        if (_ctrl.Sesion.Admin)
        {
            if (cboClientes.SelectedItem != null)
            {
                LoadSelectedClientAccounts();
            }
            else
            {
                CargarClientes();
            }
        }
        else
        {
            CargarCuentas(_ctrl.Sesion.ClienteAsignado);
        }
    }

    private void CargarCuentas(string cliente)
    {
        try
        {
            _ctrl.CargarCuentas(cliente);
            _cuentas = _ctrl.GetCuentas();

            dgvCuentas.Rows.Clear();
            cboCuenta.Items.Clear();

            double total = 0;
            foreach (var c in _cuentas)
            {
                dgvCuentas.Rows.Add(c.CodigoCuenta, c.Saldo.ToString("N2"), Moneda.Nombre(c.Moneda), c.Estado);
                cboCuenta.Items.Add(c.CodigoCuenta);
                total += c.Saldo;
            }

            if (cboCuenta.Items.Count > 0)
            {
                cboCuenta.SelectedIndex = 0;
            }

            lblTotal.Text = $"Saldo total: {total:N2}";
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private string? CuentaSeleccionada() => cboCuenta.SelectedItem?.ToString();

    private string MonedaSeleccionada() => cboMoneda.SelectedIndex == 1 ? "01" : "02";

    private void ShowResult(Resultado r)
    {
        MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK,
            r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
        RefreshCurrent();
    }

    private void HacerDeposito()
    {
        if (!_ctrl.Sesion.Admin) return;
        var cuenta = CuentaSeleccionada();
        if (string.IsNullOrWhiteSpace(cuenta)) return;
        var monto = txtMonto.Text.Trim();
        if (string.IsNullOrWhiteSpace(monto)) return;

        try
        {
            ShowResult(_ctrl.Depositar(cuenta, monto, MonedaSeleccionada()));
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void HacerRetiro()
    {
        var cuenta = CuentaSeleccionada();
        if (string.IsNullOrWhiteSpace(cuenta)) return;
        var monto = txtMonto.Text.Trim();
        if (string.IsNullOrWhiteSpace(monto)) return;

        try
        {
            ShowResult(_ctrl.Retirar(cuenta, monto, MonedaSeleccionada()));
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void ConsultarSaldo()
    {
        var cuenta = CuentaSeleccionada();
        if (string.IsNullOrWhiteSpace(cuenta)) return;

        try
        {
            var r = _ctrl.ConsultarSaldo(cuenta);
            MessageBox.Show(r.Exitoso ? $"Saldo: {r.Saldo:N2}" : r.Mensaje,
                r.Exitoso ? "OK" : "Error",
                MessageBoxButtons.OK,
                r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void Transferir()
    {
        var cuenta = CuentaSeleccionada();
        if (string.IsNullOrWhiteSpace(cuenta)) return;
        var monto = txtMonto.Text.Trim();
        if (string.IsNullOrWhiteSpace(monto)) return;

        var prompt = BuildPrompt("Transferir", "Cuenta destino");
        if (prompt == null) return;

        try
        {
            ShowResult(_ctrl.Transferir(cuenta, prompt[0], monto, MonedaSeleccionada()));
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void VerMovimientos()
    {
        var cuenta = CuentaSeleccionada();
        if (string.IsNullOrWhiteSpace(cuenta)) return;

        try
        {
            var list = _ctrl.Movimientos(cuenta);
            var dlg = new Form
            {
                Text = $"Movimientos - {cuenta}",
                Size = new Size(940, 520),
                StartPosition = FormStartPosition.CenterParent,
                BackColor = Color.FromArgb(15, 23, 42),
                ForeColor = Color.WhiteSmoke
            };

            var grid = new DataGridView
            {
                Dock = DockStyle.Fill,
                DataSource = list,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                BackgroundColor = Color.FromArgb(15, 23, 42),
                ForeColor = Color.WhiteSmoke,
                RowHeadersVisible = false,
                BorderStyle = BorderStyle.None,
                AllowUserToAddRows = false,
                AllowUserToDeleteRows = false
            };
            dlg.Controls.Add(grid);
            dlg.ShowDialog(this);
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void RegistrarCliente()
    {
        var input = BuildPrompt("Registrar cliente", "Paterno", "Materno", "Nombre", "DNI", "Ciudad", "Direccion", "Telefono", "Email");
        if (input == null) return;

        try
        {
            var r = _ctrl.RegistrarCliente(input[0], input[1], input[2], input[3], input[4], input[5], input[6], input[7]);
            MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK,
                r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
            CargarClientes();
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void RegistrarCuenta()
    {
        var input = BuildPrompt("Registrar cuenta", "Codigo cliente", "Moneda (01/02)");
        if (input == null) return;

        try
        {
            var r = _ctrl.RegistrarCuenta(input[0], string.IsNullOrWhiteSpace(input[1]) ? "01" : input[1]);
            MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK,
                r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
            CargarClientes();
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private void EliminarCuenta()
    {
        var input = BuildPrompt("Eliminar cuenta", "Cuenta");
        if (input == null) return;

        try
        {
            var r = _ctrl.EliminarCuenta(input[0]);
            MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK,
                r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
            RefreshCurrent();
        }
        catch (Exception ex)
        {
            MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    private static string[]? BuildPrompt(string title, params string[] labels)
    {
        using var form = new Form
        {
            Text = title,
            StartPosition = FormStartPosition.CenterParent,
            FormBorderStyle = FormBorderStyle.FixedDialog,
            Size = new Size(420, 80 + labels.Length * 42 + 60),
            MaximizeBox = false,
            MinimizeBox = false,
            BackColor = Color.FromArgb(30, 41, 59),
            ForeColor = Color.WhiteSmoke,
            Padding = new Padding(12)
        };

        var table = new TableLayoutPanel { Dock = DockStyle.Fill, ColumnCount = 2, RowCount = labels.Length + 1 };
        table.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 130));
        table.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100F));

        var boxes = new TextBox[labels.Length];
        for (var i = 0; i < labels.Length; i++)
        {
            table.RowStyles.Add(new RowStyle(SizeType.Absolute, 34));
            table.Controls.Add(new Label
            {
                Text = labels[i],
                Dock = DockStyle.Fill,
                TextAlign = ContentAlignment.MiddleLeft,
                ForeColor = Color.WhiteSmoke
            }, 0, i);
            boxes[i] = new TextBox
            {
                Dock = DockStyle.Fill,
                BackColor = Color.FromArgb(15, 23, 42),
                ForeColor = Color.WhiteSmoke
            };
            table.Controls.Add(boxes[i], 1, i);
        }

        var buttons = new FlowLayoutPanel
        {
            Dock = DockStyle.Fill,
            FlowDirection = FlowDirection.RightToLeft,
            Height = 40
        };
        var ok = new Button
        {
            Text = "Aceptar",
            Width = 100,
            BackColor = Color.FromArgb(56, 189, 248),
            ForeColor = Color.FromArgb(4, 38, 58),
            FlatStyle = FlatStyle.Flat
        };
        var cancel = new Button
        {
            Text = "Cancelar",
            Width = 100,
            DialogResult = DialogResult.Cancel,
            FlatStyle = FlatStyle.Flat
        };
        buttons.Controls.Add(ok);
        buttons.Controls.Add(cancel);
        table.RowStyles.Add(new RowStyle(SizeType.Absolute, 44));
        table.Controls.Add(buttons, 0, labels.Length);
        table.SetColumnSpan(buttons, 2);

        form.Controls.Add(table);
        form.AcceptButton = ok;
        form.CancelButton = cancel;

        ok.Click += (_, _) => form.DialogResult = DialogResult.OK;

        return form.ShowDialog() == DialogResult.OK ? boxes.Select(b => b.Text.Trim()).ToArray() : null;
    }
}
