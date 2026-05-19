namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Vista;

partial class FrmPrincipal
{
    private System.ComponentModel.IContainer components = null;
    private TabControl TabsMain;
    private TabPage TabPartidos;
    private TabPage TabCompra;
    private TabPage TabReporte;
    private Panel PanelTop;
    private Label LblTitulo;
    private Label LblUsuarioActual;
    private DataGridView GridPartidos;
    private DataGridView GridLocalidades;
    private DataGridView GridReporte;
    private BindingSource BindingPartidos;
    private BindingSource BindingLocalidades;
    private BindingSource BindingReporte;
    private ComboBox CboPartidos;
    private ComboBox CboLocalidades;
    private ComboBox CboReporte;
    private NumericUpDown NudCantidad;
    private Button BtnComprar;
    private Button BtnRefrescarPartidos;
    private Button BtnCargarReporte;
    private Label LblEstado;

    protected override void Dispose(bool disposing)
    {
        if (disposing && (components != null))
        {
            components.Dispose();
        }

        base.Dispose(disposing);
    }

    private void InitializeComponent()
    {
        components = new System.ComponentModel.Container();
        TabsMain = new TabControl();
        TabPartidos = new TabPage();
        GridPartidos = new DataGridView();
        BtnRefrescarPartidos = new Button();
        TabCompra = new TabPage();
        GridLocalidades = new DataGridView();
        BtnComprar = new Button();
        NudCantidad = new NumericUpDown();
        CboLocalidades = new ComboBox();
        CboPartidos = new ComboBox();
        LblEstado = new Label();
        TabReporte = new TabPage();
        GridReporte = new DataGridView();
        BtnCargarReporte = new Button();
        CboReporte = new ComboBox();
        PanelTop = new Panel();
        LblTitulo = new Label();
        LblUsuarioActual = new Label();
        BindingPartidos = new BindingSource(components);
        BindingLocalidades = new BindingSource(components);
        BindingReporte = new BindingSource(components);
        TabsMain.SuspendLayout();
        TabPartidos.SuspendLayout();
        ((System.ComponentModel.ISupportInitialize)GridPartidos).BeginInit();
        TabCompra.SuspendLayout();
        ((System.ComponentModel.ISupportInitialize)GridLocalidades).BeginInit();
        ((System.ComponentModel.ISupportInitialize)NudCantidad).BeginInit();
        TabReporte.SuspendLayout();
        ((System.ComponentModel.ISupportInitialize)GridReporte).BeginInit();
        PanelTop.SuspendLayout();
        SuspendLayout();
        // 
        // PanelTop
        // 
        PanelTop.BackColor = Color.FromArgb(15, 23, 42);
        PanelTop.Controls.Add(LblUsuarioActual);
        PanelTop.Controls.Add(LblTitulo);
        PanelTop.Dock = DockStyle.Top;
        PanelTop.Location = new Point(0, 0);
        PanelTop.Name = "PanelTop";
        PanelTop.Size = new Size(1184, 72);
        PanelTop.TabIndex = 0;
        // 
        // LblTitulo
        // 
        LblTitulo.AutoSize = true;
        LblTitulo.Font = new Font("Segoe UI Semibold", 18F, FontStyle.Bold, GraphicsUnit.Point);
        LblTitulo.ForeColor = Color.White;
        LblTitulo.Location = new Point(20, 18);
        LblTitulo.Name = "LblTitulo";
        LblTitulo.Size = new Size(187, 32);
        LblTitulo.TabIndex = 0;
        LblTitulo.Text = "TicketPremium";
        // 
        // LblUsuarioActual
        // 
        LblUsuarioActual.Anchor = AnchorStyles.Top | AnchorStyles.Right;
        LblUsuarioActual.AutoSize = true;
        LblUsuarioActual.Font = new Font("Segoe UI", 10F, FontStyle.Regular, GraphicsUnit.Point);
        LblUsuarioActual.ForeColor = Color.FromArgb(191, 219, 254);
        LblUsuarioActual.Location = new Point(998, 24);
        LblUsuarioActual.Name = "LblUsuarioActual";
        LblUsuarioActual.Size = new Size(111, 19);
        LblUsuarioActual.TabIndex = 1;
        LblUsuarioActual.Text = "Sesión activa";
        // 
        // TabsMain
        // 
        TabsMain.Controls.Add(TabPartidos);
        TabsMain.Controls.Add(TabCompra);
        TabsMain.Controls.Add(TabReporte);
        TabsMain.Dock = DockStyle.Fill;
        TabsMain.Font = new Font("Segoe UI", 10F, FontStyle.Regular, GraphicsUnit.Point);
        TabsMain.Location = new Point(0, 72);
        TabsMain.Name = "TabsMain";
        TabsMain.Padding = new Point(14, 6);
        TabsMain.SelectedIndex = 0;
        TabsMain.Size = new Size(1184, 689);
        TabsMain.TabIndex = 1;
        // 
        // TabPartidos
        // 
        TabPartidos.BackColor = Color.White;
        TabPartidos.Controls.Add(BtnRefrescarPartidos);
        TabPartidos.Controls.Add(GridPartidos);
        TabPartidos.Location = new Point(4, 31);
        TabPartidos.Name = "TabPartidos";
        TabPartidos.Padding = new Padding(16);
        TabPartidos.Size = new Size(1176, 654);
        TabPartidos.TabIndex = 0;
        TabPartidos.Text = "Partidos";
        // 
        // BtnRefrescarPartidos
        // 
        BtnRefrescarPartidos.Anchor = AnchorStyles.Top | AnchorStyles.Right;
        BtnRefrescarPartidos.BackColor = Color.FromArgb(37, 99, 235);
        BtnRefrescarPartidos.FlatAppearance.BorderSize = 0;
        BtnRefrescarPartidos.FlatStyle = FlatStyle.Flat;
        BtnRefrescarPartidos.ForeColor = Color.White;
        BtnRefrescarPartidos.Location = new Point(1019, 18);
        BtnRefrescarPartidos.Name = "BtnRefrescarPartidos";
        BtnRefrescarPartidos.Size = new Size(133, 36);
        BtnRefrescarPartidos.TabIndex = 1;
        BtnRefrescarPartidos.Text = "Refrescar";
        BtnRefrescarPartidos.UseVisualStyleBackColor = false;
        BtnRefrescarPartidos.Click += BtnRefrescarPartidos_Click;
        // 
        // GridPartidos
        // 
        GridPartidos.Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
        GridPartidos.BackgroundColor = Color.White;
        GridPartidos.BorderStyle = BorderStyle.None;
        GridPartidos.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.AutoSize;
        GridPartidos.Location = new Point(16, 72);
        GridPartidos.Name = "GridPartidos";
        GridPartidos.ReadOnly = true;
        GridPartidos.RowHeadersVisible = false;
        GridPartidos.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
        GridPartidos.Size = new Size(1136, 566);
        GridPartidos.TabIndex = 0;
        GridPartidos.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
        GridPartidos.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(15, 23, 42);
        GridPartidos.ColumnHeadersDefaultCellStyle.ForeColor = Color.White;
        GridPartidos.EnableHeadersVisualStyles = false;
        GridPartidos.SelectionChanged += GridPartidos_SelectionChanged;
        // 
        // TabCompra
        // 
        TabCompra.BackColor = Color.White;
        TabCompra.Controls.Add(LblEstado);
        TabCompra.Controls.Add(BtnComprar);
        TabCompra.Controls.Add(NudCantidad);
        TabCompra.Controls.Add(CboLocalidades);
        TabCompra.Controls.Add(CboPartidos);
        TabCompra.Controls.Add(GridLocalidades);
        TabCompra.Location = new Point(4, 31);
        TabCompra.Name = "TabCompra";
        TabCompra.Padding = new Padding(16);
        TabCompra.Size = new Size(1176, 654);
        TabCompra.TabIndex = 1;
        TabCompra.Text = "Compra";
        // 
        // CboPartidos
        // 
        CboPartidos.DropDownStyle = ComboBoxStyle.DropDownList;
        CboPartidos.FormattingEnabled = true;
        CboPartidos.Location = new Point(16, 18);
        CboPartidos.Name = "CboPartidos";
        CboPartidos.Size = new Size(260, 25);
        CboPartidos.TabIndex = 0;
        CboPartidos.SelectedIndexChanged += CboPartidos_SelectedIndexChanged;
        // 
        // CboLocalidades
        // 
        CboLocalidades.DropDownStyle = ComboBoxStyle.DropDownList;
        CboLocalidades.FormattingEnabled = true;
        CboLocalidades.Location = new Point(292, 18);
        CboLocalidades.Name = "CboLocalidades";
        CboLocalidades.Size = new Size(260, 25);
        CboLocalidades.TabIndex = 1;
        // 
        // NudCantidad
        // 
        NudCantidad.Location = new Point(568, 19);
        NudCantidad.Minimum = new decimal(new int[] { 1, 0, 0, 0 });
        NudCantidad.Name = "NudCantidad";
        NudCantidad.Size = new Size(88, 25);
        NudCantidad.TabIndex = 2;
        NudCantidad.Value = new decimal(new int[] { 1, 0, 0, 0 });
        // 
        // BtnComprar
        // 
        BtnComprar.BackColor = Color.FromArgb(16, 185, 129);
        BtnComprar.FlatAppearance.BorderSize = 0;
        BtnComprar.FlatStyle = FlatStyle.Flat;
        BtnComprar.ForeColor = Color.White;
        BtnComprar.Location = new Point(672, 14);
        BtnComprar.Name = "BtnComprar";
        BtnComprar.Size = new Size(160, 34);
        BtnComprar.TabIndex = 3;
        BtnComprar.Text = "Comprar boletos";
        BtnComprar.UseVisualStyleBackColor = false;
        BtnComprar.Click += BtnComprar_Click;
        // 
        // LblEstado
        // 
        LblEstado.Anchor = AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
        LblEstado.ForeColor = Color.FromArgb(15, 23, 42);
        LblEstado.Location = new Point(16, 604);
        LblEstado.Name = "LblEstado";
        LblEstado.Size = new Size(1136, 28);
        LblEstado.TabIndex = 5;
        // 
        // GridLocalidades
        // 
        GridLocalidades.Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
        GridLocalidades.BackgroundColor = Color.White;
        GridLocalidades.BorderStyle = BorderStyle.None;
        GridLocalidades.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.AutoSize;
        GridLocalidades.Location = new Point(16, 64);
        GridLocalidades.Name = "GridLocalidades";
        GridLocalidades.ReadOnly = true;
        GridLocalidades.RowHeadersVisible = false;
        GridLocalidades.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
        GridLocalidades.Size = new Size(1136, 522);
        GridLocalidades.TabIndex = 4;
        GridLocalidades.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
        GridLocalidades.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(15, 23, 42);
        GridLocalidades.ColumnHeadersDefaultCellStyle.ForeColor = Color.White;
        GridLocalidades.EnableHeadersVisualStyles = false;
        // 
        // TabReporte
        // 
        TabReporte.BackColor = Color.White;
        TabReporte.Controls.Add(BtnCargarReporte);
        TabReporte.Controls.Add(CboReporte);
        TabReporte.Controls.Add(GridReporte);
        TabReporte.Location = new Point(4, 31);
        TabReporte.Name = "TabReporte";
        TabReporte.Padding = new Padding(16);
        TabReporte.Size = new Size(1176, 654);
        TabReporte.TabIndex = 2;
        TabReporte.Text = "Reporte";
        // 
        // CboReporte
        // 
        CboReporte.DropDownStyle = ComboBoxStyle.DropDownList;
        CboReporte.FormattingEnabled = true;
        CboReporte.Location = new Point(16, 18);
        CboReporte.Name = "CboReporte";
        CboReporte.Size = new Size(260, 25);
        CboReporte.TabIndex = 0;
        // 
        // BtnCargarReporte
        // 
        BtnCargarReporte.BackColor = Color.FromArgb(37, 99, 235);
        BtnCargarReporte.FlatAppearance.BorderSize = 0;
        BtnCargarReporte.FlatStyle = FlatStyle.Flat;
        BtnCargarReporte.ForeColor = Color.White;
        BtnCargarReporte.Location = new Point(292, 14);
        BtnCargarReporte.Name = "BtnCargarReporte";
        BtnCargarReporte.Size = new Size(160, 34);
        BtnCargarReporte.TabIndex = 1;
        BtnCargarReporte.Text = "Cargar";
        BtnCargarReporte.UseVisualStyleBackColor = false;
        BtnCargarReporte.Click += BtnCargarReporte_Click;
        // 
        // GridReporte
        // 
        GridReporte.Anchor = AnchorStyles.Top | AnchorStyles.Bottom | AnchorStyles.Left | AnchorStyles.Right;
        GridReporte.BackgroundColor = Color.White;
        GridReporte.BorderStyle = BorderStyle.None;
        GridReporte.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.AutoSize;
        GridReporte.Location = new Point(16, 64);
        GridReporte.Name = "GridReporte";
        GridReporte.ReadOnly = true;
        GridReporte.RowHeadersVisible = false;
        GridReporte.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
        GridReporte.Size = new Size(1136, 574);
        GridReporte.TabIndex = 2;
        GridReporte.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
        GridReporte.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(15, 23, 42);
        GridReporte.ColumnHeadersDefaultCellStyle.ForeColor = Color.White;
        GridReporte.EnableHeadersVisualStyles = false;
        // 
        // FrmPrincipal
        // 
        AutoScaleDimensions = new SizeF(7F, 15F);
        AutoScaleMode = AutoScaleMode.Font;
        BackColor = Color.FromArgb(248, 250, 252);
        ClientSize = new Size(1184, 761);
        Controls.Add(TabsMain);
        Controls.Add(PanelTop);
        Font = new Font("Segoe UI", 9F, FontStyle.Regular, GraphicsUnit.Point);
        MinimumSize = new Size(1024, 720);
        Name = "FrmPrincipal";
        StartPosition = FormStartPosition.CenterScreen;
        Text = "TicketPremium - Partidos Ecuatorianos";
        TabsMain.ResumeLayout(false);
        TabPartidos.ResumeLayout(false);
        ((System.ComponentModel.ISupportInitialize)GridPartidos).EndInit();
        TabCompra.ResumeLayout(false);
        ((System.ComponentModel.ISupportInitialize)GridLocalidades).EndInit();
        ((System.ComponentModel.ISupportInitialize)NudCantidad).EndInit();
        TabReporte.ResumeLayout(false);
        ((System.ComponentModel.ISupportInitialize)GridReporte).EndInit();
        PanelTop.ResumeLayout(false);
        PanelTop.PerformLayout();
        ResumeLayout(false);
    }
}
