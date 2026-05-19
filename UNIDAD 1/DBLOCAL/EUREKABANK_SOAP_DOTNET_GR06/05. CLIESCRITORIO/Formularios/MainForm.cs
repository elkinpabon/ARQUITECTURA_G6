using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using CLIESCRITORIO.Servicio;

namespace CLIESCRITORIO.Formularios
{
    public class MainForm : Form
    {
        private readonly SoapClient _soapClient;
        private readonly string _usuario;
        private readonly bool _isAdmin;
        private readonly string _clienteCodigo;

        private readonly List<ClienteResumen> _clientes = new();
        private List<CuentaResumen> _cuentas = new();

        private ComboBox cboClientes = null!;
        private ComboBox cboCuenta = null!;
        private DataGridView dgvCuentas = null!;
        private Label lblTotal = null!;
        private Label lblEstado = null!;
        private TextBox txtMonto = null!;
        private ComboBox cboMoneda = null!;

        public MainForm(SoapClient soapClient, string usuario, bool isAdmin, string clienteCodigo)
        {
            _soapClient = soapClient;
            _usuario = usuario;
            _isAdmin = isAdmin;
            _clienteCodigo = clienteCodigo;
            InitializeComponent();
            Load += (_, _) => OnLoaded();
        }

        private void InitializeComponent()
        {
            Text = $"EUREKABANK GR06 - {_usuario}";
            Size = new Size(980, 700);
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
            root.RowStyles.Add(new RowStyle(SizeType.Absolute, 78));
            root.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));
            root.RowStyles.Add(new RowStyle(SizeType.Absolute, 180));

            var top = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(30, 41, 59), Padding = new Padding(18, 12, 18, 12) };
            var brand = new Label
            {
                Text = "EUREKABANK GR06",
                AutoSize = true,
                Font = new Font("Segoe UI", 18F, FontStyle.Bold),
                ForeColor = Color.FromArgb(56, 189, 248),
                Location = new Point(0, 6)
            };
            var user = new Label
            {
                AutoSize = true,
                Text = $"Usuario: {_usuario}  ·  {(_isAdmin ? "ADMIN" : "CLIENTE")}",
                ForeColor = Color.FromArgb(226, 232, 240),
                Location = new Point(0, 38)
            };
            var btnSalir = new Button
            {
                Text = "Cerrar sesion",
                Width = 130,
                Height = 34,
                Anchor = AnchorStyles.Top | AnchorStyles.Right,
                BackColor = Color.FromArgb(56, 189, 248),
                ForeColor = Color.FromArgb(4, 38, 58),
                FlatStyle = FlatStyle.Flat,
                Location = new Point(800, 20)
            };
            btnSalir.Click += (_, _) => Close();
            top.Controls.AddRange(new Control[] { brand, user, btnSalir });

            var center = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(30, 41, 59), Padding = new Padding(14) };

            var adminRow = new Panel { Dock = DockStyle.Top, Height = 56, Visible = _isAdmin };
            var lblCliente = new Label { Text = "Cliente registrado", AutoSize = true, ForeColor = Color.WhiteSmoke, Location = new Point(0, 18) };
            cboClientes = new ComboBox { Width = 360, DropDownStyle = ComboBoxStyle.DropDownList, Location = new Point(130, 14), BackColor = Color.FromArgb(15, 23, 42), ForeColor = Color.WhiteSmoke };
            var btnVer = new Button { Text = "Ver cuentas", Width = 120, Height = 32, Location = new Point(500, 12), BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var btnRegCli = new Button { Text = "Registrar cliente", Width = 135, Height = 32, Location = new Point(630, 12), BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var btnRegCta = new Button { Text = "Registrar cuenta", Width = 135, Height = 32, Location = new Point(770, 12), BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            adminRow.Controls.AddRange(new Control[] { lblCliente, cboClientes, btnVer, btnRegCli, btnRegCta });

            var adminRow2 = new Panel { Dock = DockStyle.Top, Height = 52, Visible = _isAdmin };
            var btnDelCta = new Button { Text = "Eliminar cuenta", Width = 135, Height = 32, Location = new Point(630, 8), BackColor = Color.FromArgb(248, 113, 113), ForeColor = Color.FromArgb(63, 13, 13), FlatStyle = FlatStyle.Flat };
            adminRow2.Controls.Add(btnDelCta);

            dgvCuentas = new DataGridView
            {
                Dock = DockStyle.Fill,
                ReadOnly = true,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                BackgroundColor = Color.FromArgb(15, 23, 42),
                ForeColor = Color.WhiteSmoke,
                BorderStyle = BorderStyle.None,
                RowHeadersVisible = false
            };
            dgvCuentas.DefaultCellStyle.BackColor = Color.FromArgb(15, 23, 42);
            dgvCuentas.DefaultCellStyle.ForeColor = Color.WhiteSmoke;
            dgvCuentas.DefaultCellStyle.SelectionBackColor = Color.FromArgb(56, 189, 248);
            dgvCuentas.DefaultCellStyle.SelectionForeColor = Color.FromArgb(4, 38, 58);
            dgvCuentas.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(56, 189, 248);
            dgvCuentas.ColumnHeadersDefaultCellStyle.ForeColor = Color.FromArgb(4, 38, 58);
            dgvCuentas.EnableHeadersVisualStyles = false;

            lblTotal = new Label { AutoSize = true, Dock = DockStyle.Bottom, Height = 24, Text = "Saldo total: 0.00", ForeColor = Color.FromArgb(56, 189, 248), Font = new Font("Segoe UI", 11F, FontStyle.Bold) };
            center.Controls.Add(dgvCuentas);
            center.Controls.Add(lblTotal);
            center.Controls.Add(adminRow2);
            center.Controls.Add(adminRow);

            var bottom = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(30, 41, 59), Padding = new Padding(14) };
            var row1 = new TableLayoutPanel { Dock = DockStyle.Top, Height = 82, ColumnCount = 6 };
            row1.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 80));
            row1.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 32F));
            row1.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 80));
            row1.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 32F));
            row1.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 80));
            row1.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 36F));

            row1.Controls.Add(new Label { Text = "Cuenta", AutoSize = true, ForeColor = Color.WhiteSmoke, Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleLeft }, 0, 0);
            cboCuenta = new ComboBox { Dock = DockStyle.Fill, DropDownStyle = ComboBoxStyle.DropDownList, BackColor = Color.FromArgb(15, 23, 42), ForeColor = Color.WhiteSmoke };
            row1.Controls.Add(cboCuenta, 1, 0);
            row1.Controls.Add(new Label { Text = "Monto", AutoSize = true, ForeColor = Color.WhiteSmoke, Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleLeft }, 2, 0);
            txtMonto = new TextBox { Dock = DockStyle.Fill, BackColor = Color.FromArgb(15, 23, 42), ForeColor = Color.WhiteSmoke, BorderStyle = BorderStyle.FixedSingle };
            row1.Controls.Add(txtMonto, 3, 0);
            row1.Controls.Add(new Label { Text = "Moneda", AutoSize = true, ForeColor = Color.WhiteSmoke, Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleLeft }, 4, 0);
            cboMoneda = new ComboBox { Dock = DockStyle.Fill, DropDownStyle = ComboBoxStyle.DropDownList, BackColor = Color.FromArgb(15, 23, 42), ForeColor = Color.WhiteSmoke };
            cboMoneda.Items.AddRange(new object[] { "Dólares (02)", "Soles (01)" });
            cboMoneda.SelectedIndex = 0;
            row1.Controls.Add(cboMoneda, 5, 0);

            var row2 = new FlowLayoutPanel { Dock = DockStyle.Bottom, Height = 72, FlowDirection = FlowDirection.LeftToRight, WrapContents = true, Padding = new Padding(0, 10, 0, 0) };
            var btnDepositar = new Button { Text = "Depositar", Width = 125, Height = 34, BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var btnRetirar = new Button { Text = "Retirar", Width = 125, Height = 34, BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var btnSaldo = new Button { Text = "Consultar saldo", Width = 140, Height = 34, BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var btnTransferir = new Button { Text = "Transferir", Width = 125, Height = 34, BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var btnMov = new Button { Text = "Movimientos", Width = 125, Height = 34, BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            row2.Controls.AddRange(new Control[] { btnDepositar, btnRetirar, btnSaldo, btnTransferir, btnMov });
            if (!_isAdmin) btnDepositar.Visible = false;

            lblEstado = new Label { Dock = DockStyle.Bottom, Height = 24, ForeColor = Color.FromArgb(148, 163, 184), Text = "Listo" };

            bottom.Controls.Add(lblEstado);
            bottom.Controls.Add(row2);
            bottom.Controls.Add(row1);

            root.Controls.Add(top, 0, 0);
            root.Controls.Add(center, 0, 1);
            root.Controls.Add(bottom, 0, 2);
            Controls.Add(root);

            btnVer.Click += (_, _) => LoadSelectedClientAccounts();
            btnRegCli.Click += (_, _) => RegistrarCliente();
            btnRegCta.Click += (_, _) => RegistrarCuenta();
            btnDelCta.Click += (_, _) => EliminarCuenta();
            btnDepositar.Click += (_, _) => HacerDeposito();
            btnRetirar.Click += (_, _) => HacerRetiro();
            btnSaldo.Click += (_, _) => ConsultarSaldo();
            btnTransferir.Click += (_, _) => Transferir();
            btnMov.Click += (_, _) => VerMovimientos();
        }

        private void OnLoaded()
        {
            if (_isAdmin)
            {
                CargarClientes();
                SetEstado("Seleccione un cliente y presione 'Ver cuentas'.");
            }
            else
            {
                CargarCuentas(_clienteCodigo);
                SetEstado("Cuentas cargadas.");
            }
        }

        private void SetEstado(string texto) => lblEstado.Text = texto;

        private void CargarClientes()
        {
            try
            {
                _clientes.Clear();
                _clientes.AddRange(_soapClient.ListarClientes());
                cboClientes.Items.Clear();
                foreach (var c in _clientes)
                    cboClientes.Items.Add($"{c.Codigo} · DNI {c.Dni} · {c.Nombre}");
                if (cboClientes.Items.Count > 0) cboClientes.SelectedIndex = 0;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void LoadSelectedClientAccounts()
        {
            if (!_isAdmin)
                return;

            if (cboClientes.SelectedItem == null)
            {
                MessageBox.Show("Seleccione un cliente.", "EUREKABANK", MessageBoxButtons.OK, MessageBoxIcon.Information);
                return;
            }

            var code = ((string)cboClientes.SelectedItem).Split('·')[0].Trim();
            CargarCuentas(code);
            SetEstado($"Cuentas cargadas para cliente {code}.");
        }

        private void CargarCuentas(string cliente)
        {
            try
            {
                _cuentas = _soapClient.ListarCuentasPorCliente(cliente) ?? new List<CuentaResumen>();
                dgvCuentas.DataSource = null;
                dgvCuentas.DataSource = _cuentas;

                cboCuenta.Items.Clear();
                double total = 0;
                foreach (var c in _cuentas)
                {
                    cboCuenta.Items.Add(c.CodigoCuenta);
                    total += c.Saldo;
                }

                if (cboCuenta.Items.Count > 0)
                    cboCuenta.SelectedIndex = 0;

                lblTotal.Text = $"Saldo total: {total:F2}";
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private string? CuentaSeleccionada() => cboCuenta.SelectedItem?.ToString();

        private string MonedaSeleccionada() => cboMoneda.SelectedIndex == 1 ? "01" : "02";

        private void RefreshAfterOperation()
        {
            if (_isAdmin)
                LoadSelectedClientAccounts();
            else
                CargarCuentas(_clienteCodigo);
        }

        private void HacerDeposito()
        {
            if (!_isAdmin) return;
            var cuenta = CuentaSeleccionada();
            if (string.IsNullOrWhiteSpace(cuenta)) return;
            var monto = txtMonto.Text.Trim();
            if (string.IsNullOrWhiteSpace(monto)) return;

            try
            {
                var r = _soapClient.Depositar(cuenta, monto, MonedaSeleccionada());
                MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
                RefreshAfterOperation();
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void HacerRetiro()
        {
            var cuenta = CuentaSeleccionada();
            if (string.IsNullOrWhiteSpace(cuenta)) return;
            var monto = txtMonto.Text.Trim();
            if (string.IsNullOrWhiteSpace(monto)) return;

            try
            {
                var r = _soapClient.Retirar(cuenta, monto, MonedaSeleccionada());
                MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
                RefreshAfterOperation();
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void ConsultarSaldo()
        {
            var cuenta = CuentaSeleccionada();
            if (string.IsNullOrWhiteSpace(cuenta)) return;

            try
            {
                var r = _soapClient.ConsultarSaldo(cuenta);
                MessageBox.Show(r.Exitoso ? $"Saldo: {r.Saldo:F2}" : r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void Transferir()
        {
            var cuenta = CuentaSeleccionada();
            if (string.IsNullOrWhiteSpace(cuenta)) return;
            var monto = txtMonto.Text.Trim();
            if (string.IsNullOrWhiteSpace(monto)) return;

            var dlg = BuildPrompt("Transferir", "Cuenta destino", string.Empty);
            if (dlg == null) return;

            try
            {
                var r = _soapClient.Transferir(cuenta, dlg[0], monto, MonedaSeleccionada());
                MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
                RefreshAfterOperation();
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void VerMovimientos()
        {
            var cuenta = CuentaSeleccionada();
            if (string.IsNullOrWhiteSpace(cuenta)) return;

            try
            {
                var list = _soapClient.ListarMovimientos(cuenta);
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
                    BorderStyle = BorderStyle.None
                };
                dlg.Controls.Add(grid);
                dlg.ShowDialog(this);
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void RegistrarCliente()
        {
            var input = BuildPrompt("Registrar cliente", "Paterno", "Materno", "Nombre", "DNI", "Ciudad", "Direccion", "Telefono", "Email");
            if (input == null) return;

            try
            {
                var r = _soapClient.RegistrarCliente(input[0], input[1], input[2], input[3], input[4], input[5], input[6], input[7]);
                MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
                CargarClientes();
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void RegistrarCuenta()
        {
            var input = BuildPrompt("Registrar cuenta", "Codigo cliente", "Moneda (01/02)");
            if (input == null) return;

            try
            {
                var r = _soapClient.RegistrarCuenta(input[0], string.IsNullOrWhiteSpace(input[1]) ? "01" : input[1]);
                MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
                CargarClientes();
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        private void EliminarCuenta()
        {
            var input = BuildPrompt("Eliminar cuenta", "Cuenta");
            if (input == null) return;

            try
            {
                var r = _soapClient.EliminarCuenta(input[0]);
                MessageBox.Show(r.Mensaje, r.Exitoso ? "OK" : "Error", MessageBoxButtons.OK, r.Exitoso ? MessageBoxIcon.Information : MessageBoxIcon.Error);
                RefreshAfterOperation();
            }
            catch (Exception ex) { MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error); }
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
                table.Controls.Add(new Label { Text = labels[i], Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleLeft, ForeColor = Color.WhiteSmoke }, 0, i);
                boxes[i] = new TextBox { Dock = DockStyle.Fill, BackColor = Color.FromArgb(15, 23, 42), ForeColor = Color.WhiteSmoke };
                table.Controls.Add(boxes[i], 1, i);
            }

            var buttons = new FlowLayoutPanel { Dock = DockStyle.Fill, FlowDirection = FlowDirection.RightToLeft, Height = 40 };
            var ok = new Button { Text = "Aceptar", Width = 100, BackColor = Color.FromArgb(56, 189, 248), ForeColor = Color.FromArgb(4, 38, 58), FlatStyle = FlatStyle.Flat };
            var cancel = new Button { Text = "Cancelar", Width = 100, DialogResult = DialogResult.Cancel, FlatStyle = FlatStyle.Flat };
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
}
