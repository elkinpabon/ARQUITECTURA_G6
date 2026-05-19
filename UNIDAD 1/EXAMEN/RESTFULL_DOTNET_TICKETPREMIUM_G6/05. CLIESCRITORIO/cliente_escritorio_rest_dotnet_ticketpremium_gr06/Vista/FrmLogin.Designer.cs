namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Vista;

partial class FrmLogin
{
    private System.ComponentModel.IContainer components = null;
    private SplitContainer MainSplit;
    private Panel PanelBrand;
    private Label LblBrandTitle;
    private Label LblBrandSubtitle;
    private Label LblBrandNote;
    private TabControl TabsAuth;
    private TabPage TabLogin;
    private TabPage TabRegister;
    private TextBox TxtUsuarioLogin;
    private TextBox TxtPasswordLogin;
    private Button BtnLogin;
    private Label LblLoginEstado;
    private TextBox TxtNombreRegistro;
    private TextBox TxtUsuarioRegistro;
    private TextBox TxtCorreoRegistro;
    private TextBox TxtPasswordRegistro;
    private TextBox TxtConfirmarRegistro;
    private Button BtnRegistro;
    private Label LblRegistroEstado;

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
        MainSplit = new SplitContainer();
        PanelBrand = new Panel();
        LblBrandNote = new Label();
        LblBrandSubtitle = new Label();
        LblBrandTitle = new Label();
        TabsAuth = new TabControl();
        TabLogin = new TabPage();
        LblLoginEstado = new Label();
        BtnLogin = new Button();
        TxtPasswordLogin = new TextBox();
        TxtUsuarioLogin = new TextBox();
        TabRegister = new TabPage();
        LblRegistroEstado = new Label();
        BtnRegistro = new Button();
        TxtConfirmarRegistro = new TextBox();
        TxtPasswordRegistro = new TextBox();
        TxtCorreoRegistro = new TextBox();
        TxtUsuarioRegistro = new TextBox();
        TxtNombreRegistro = new TextBox();
        ((System.ComponentModel.ISupportInitialize)MainSplit).BeginInit();
        MainSplit.Panel1.SuspendLayout();
        MainSplit.Panel2.SuspendLayout();
        MainSplit.SuspendLayout();
        PanelBrand.SuspendLayout();
        TabsAuth.SuspendLayout();
        TabLogin.SuspendLayout();
        TabRegister.SuspendLayout();
        SuspendLayout();
        // 
        // MainSplit
        // 
        MainSplit.Dock = DockStyle.Fill;
        MainSplit.FixedPanel = FixedPanel.Panel1;
        MainSplit.IsSplitterFixed = true;
        MainSplit.Location = new Point(0, 0);
        MainSplit.Margin = new Padding(0);
        MainSplit.Name = "MainSplit";
        // 
        // MainSplit.Panel1
        // 
        MainSplit.Panel1.BackColor = Color.FromArgb(15, 23, 42);
        MainSplit.Panel1.Controls.Add(PanelBrand);
        // 
        // MainSplit.Panel2
        // 
        MainSplit.Panel2.BackColor = Color.FromArgb(248, 250, 252);
        MainSplit.Panel2.Controls.Add(TabsAuth);
        MainSplit.Size = new Size(1040, 680);
        MainSplit.SplitterDistance = 360;
        MainSplit.SplitterWidth = 1;
        MainSplit.TabIndex = 0;
        // 
        // PanelBrand
        // 
        PanelBrand.Dock = DockStyle.Fill;
        PanelBrand.Padding = new Padding(28);
        PanelBrand.Controls.Add(LblBrandNote);
        PanelBrand.Controls.Add(LblBrandSubtitle);
        PanelBrand.Controls.Add(LblBrandTitle);
        // 
        // LblBrandTitle
        // 
        LblBrandTitle.AutoSize = true;
        LblBrandTitle.Font = new Font("Segoe UI Semibold", 28F, FontStyle.Bold, GraphicsUnit.Point);
        LblBrandTitle.ForeColor = Color.White;
        LblBrandTitle.Location = new Point(28, 32);
        LblBrandTitle.Name = "LblBrandTitle";
        LblBrandTitle.Size = new Size(251, 51);
        LblBrandTitle.TabIndex = 0;
        LblBrandTitle.Text = "TicketPremium";
        // 
        // LblBrandSubtitle
        // 
        LblBrandSubtitle.AutoSize = false;
        LblBrandSubtitle.Font = new Font("Segoe UI", 12F, FontStyle.Regular, GraphicsUnit.Point);
        LblBrandSubtitle.ForeColor = Color.FromArgb(203, 213, 225);
        LblBrandSubtitle.Location = new Point(32, 104);
        LblBrandSubtitle.Name = "LblBrandSubtitle";
        LblBrandSubtitle.Size = new Size(260, 120);
        LblBrandSubtitle.TabIndex = 1;
        LblBrandSubtitle.Text = "Acceso seguro para consultar partidos, comprar boletos y revisar ventas.";
        // 
        // LblBrandNote
        // 
        LblBrandNote.AutoSize = true;
        LblBrandNote.Font = new Font("Segoe UI Semibold", 10F, FontStyle.Bold, GraphicsUnit.Point);
        LblBrandNote.ForeColor = Color.FromArgb(191, 219, 254);
        LblBrandNote.Location = new Point(32, 250);
        LblBrandNote.Name = "LblBrandNote";
        LblBrandNote.Size = new Size(212, 19);
        LblBrandNote.TabIndex = 2;
        LblBrandNote.Text = "Inicia sesion o crea una cuenta";
        // 
        // TabsAuth
        // 
        TabsAuth.Controls.Add(TabLogin);
        TabsAuth.Controls.Add(TabRegister);
        TabsAuth.Dock = DockStyle.Fill;
        TabsAuth.Font = new Font("Segoe UI", 10F, FontStyle.Regular, GraphicsUnit.Point);
        TabsAuth.Location = new Point(0, 0);
        TabsAuth.Name = "TabsAuth";
        TabsAuth.Padding = new Point(14, 6);
        TabsAuth.SelectedIndex = 0;
        TabsAuth.Size = new Size(676, 680);
        TabsAuth.TabIndex = 0;
        // 
        // TabLogin
        // 
        TabLogin.BackColor = Color.White;
        TabLogin.Controls.Add(LblLoginEstado);
        TabLogin.Controls.Add(BtnLogin);
        TabLogin.Controls.Add(TxtPasswordLogin);
        TabLogin.Controls.Add(TxtUsuarioLogin);
        TabLogin.Location = new Point(4, 31);
        TabLogin.Name = "TabLogin";
        TabLogin.Padding = new Padding(24);
        TabLogin.Size = new Size(668, 645);
        TabLogin.TabIndex = 0;
        TabLogin.Text = "Ingresar";
        // 
        // TxtUsuarioLogin
        // 
        TxtUsuarioLogin.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtUsuarioLogin.BorderStyle = BorderStyle.FixedSingle;
        TxtUsuarioLogin.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtUsuarioLogin.Location = new Point(32, 86);
        TxtUsuarioLogin.Name = "TxtUsuarioLogin";
        TxtUsuarioLogin.PlaceholderText = "Usuario";
        TxtUsuarioLogin.Size = new Size(580, 27);
        TxtUsuarioLogin.TabIndex = 0;
        // 
        // TxtPasswordLogin
        // 
        TxtPasswordLogin.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtPasswordLogin.BorderStyle = BorderStyle.FixedSingle;
        TxtPasswordLogin.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtPasswordLogin.Location = new Point(32, 138);
        TxtPasswordLogin.Name = "TxtPasswordLogin";
        TxtPasswordLogin.PlaceholderText = "Contraseña";
        TxtPasswordLogin.Size = new Size(580, 27);
        TxtPasswordLogin.TabIndex = 1;
        TxtPasswordLogin.UseSystemPasswordChar = true;
        // 
        // BtnLogin
        // 
        BtnLogin.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        BtnLogin.BackColor = Color.FromArgb(37, 99, 235);
        BtnLogin.FlatAppearance.BorderSize = 0;
        BtnLogin.FlatStyle = FlatStyle.Flat;
        BtnLogin.Font = new Font("Segoe UI Semibold", 11F, FontStyle.Bold, GraphicsUnit.Point);
        BtnLogin.ForeColor = Color.White;
        BtnLogin.Location = new Point(32, 190);
        BtnLogin.Name = "BtnLogin";
        BtnLogin.Size = new Size(580, 42);
        BtnLogin.TabIndex = 2;
        BtnLogin.Text = "Iniciar sesión";
        BtnLogin.UseVisualStyleBackColor = false;
        BtnLogin.Click += BtnLogin_Click;
        // 
        // LblLoginEstado
        // 
        LblLoginEstado.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        LblLoginEstado.Font = new Font("Segoe UI", 9.5F, FontStyle.Regular, GraphicsUnit.Point);
        LblLoginEstado.ForeColor = Color.FromArgb(15, 23, 42);
        LblLoginEstado.Location = new Point(32, 250);
        LblLoginEstado.Name = "LblLoginEstado";
        LblLoginEstado.Size = new Size(580, 48);
        LblLoginEstado.TabIndex = 3;
        // 
        // TabRegister
        // 
        TabRegister.BackColor = Color.White;
        TabRegister.Controls.Add(LblRegistroEstado);
        TabRegister.Controls.Add(BtnRegistro);
        TabRegister.Controls.Add(TxtConfirmarRegistro);
        TabRegister.Controls.Add(TxtPasswordRegistro);
        TabRegister.Controls.Add(TxtCorreoRegistro);
        TabRegister.Controls.Add(TxtUsuarioRegistro);
        TabRegister.Controls.Add(TxtNombreRegistro);
        TabRegister.Location = new Point(4, 31);
        TabRegister.Name = "TabRegister";
        TabRegister.Padding = new Padding(24);
        TabRegister.Size = new Size(668, 645);
        TabRegister.TabIndex = 1;
        TabRegister.Text = "Registrar";
        // 
        // TxtNombreRegistro
        // 
        TxtNombreRegistro.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtNombreRegistro.BorderStyle = BorderStyle.FixedSingle;
        TxtNombreRegistro.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtNombreRegistro.Location = new Point(32, 42);
        TxtNombreRegistro.Name = "TxtNombreRegistro";
        TxtNombreRegistro.PlaceholderText = "Nombre completo";
        TxtNombreRegistro.Size = new Size(580, 27);
        TxtNombreRegistro.TabIndex = 0;
        // 
        // TxtUsuarioRegistro
        // 
        TxtUsuarioRegistro.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtUsuarioRegistro.BorderStyle = BorderStyle.FixedSingle;
        TxtUsuarioRegistro.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtUsuarioRegistro.Location = new Point(32, 94);
        TxtUsuarioRegistro.Name = "TxtUsuarioRegistro";
        TxtUsuarioRegistro.PlaceholderText = "Usuario";
        TxtUsuarioRegistro.Size = new Size(580, 27);
        TxtUsuarioRegistro.TabIndex = 1;
        // 
        // TxtCorreoRegistro
        // 
        TxtCorreoRegistro.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtCorreoRegistro.BorderStyle = BorderStyle.FixedSingle;
        TxtCorreoRegistro.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtCorreoRegistro.Location = new Point(32, 146);
        TxtCorreoRegistro.Name = "TxtCorreoRegistro";
        TxtCorreoRegistro.PlaceholderText = "Correo";
        TxtCorreoRegistro.Size = new Size(580, 27);
        TxtCorreoRegistro.TabIndex = 2;
        // 
        // TxtPasswordRegistro
        // 
        TxtPasswordRegistro.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtPasswordRegistro.BorderStyle = BorderStyle.FixedSingle;
        TxtPasswordRegistro.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtPasswordRegistro.Location = new Point(32, 198);
        TxtPasswordRegistro.Name = "TxtPasswordRegistro";
        TxtPasswordRegistro.PlaceholderText = "Contraseña";
        TxtPasswordRegistro.Size = new Size(580, 27);
        TxtPasswordRegistro.TabIndex = 3;
        TxtPasswordRegistro.UseSystemPasswordChar = true;
        // 
        // TxtConfirmarRegistro
        // 
        TxtConfirmarRegistro.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        TxtConfirmarRegistro.BorderStyle = BorderStyle.FixedSingle;
        TxtConfirmarRegistro.Font = new Font("Segoe UI", 11F, FontStyle.Regular, GraphicsUnit.Point);
        TxtConfirmarRegistro.Location = new Point(32, 250);
        TxtConfirmarRegistro.Name = "TxtConfirmarRegistro";
        TxtConfirmarRegistro.PlaceholderText = "Confirmar contraseña";
        TxtConfirmarRegistro.Size = new Size(580, 27);
        TxtConfirmarRegistro.TabIndex = 4;
        TxtConfirmarRegistro.UseSystemPasswordChar = true;
        // 
        // BtnRegistro
        // 
        BtnRegistro.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        BtnRegistro.BackColor = Color.FromArgb(16, 185, 129);
        BtnRegistro.FlatAppearance.BorderSize = 0;
        BtnRegistro.FlatStyle = FlatStyle.Flat;
        BtnRegistro.Font = new Font("Segoe UI Semibold", 11F, FontStyle.Bold, GraphicsUnit.Point);
        BtnRegistro.ForeColor = Color.White;
        BtnRegistro.Location = new Point(32, 302);
        BtnRegistro.Name = "BtnRegistro";
        BtnRegistro.Size = new Size(580, 42);
        BtnRegistro.TabIndex = 5;
        BtnRegistro.Text = "Crear cuenta";
        BtnRegistro.UseVisualStyleBackColor = false;
        BtnRegistro.Click += BtnRegistro_Click;
        // 
        // LblRegistroEstado
        // 
        LblRegistroEstado.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right;
        LblRegistroEstado.Font = new Font("Segoe UI", 9.5F, FontStyle.Regular, GraphicsUnit.Point);
        LblRegistroEstado.ForeColor = Color.FromArgb(15, 23, 42);
        LblRegistroEstado.Location = new Point(32, 360);
        LblRegistroEstado.Name = "LblRegistroEstado";
        LblRegistroEstado.Size = new Size(580, 52);
        LblRegistroEstado.TabIndex = 6;
        // 
        // FrmLogin
        // 
        AutoScaleDimensions = new SizeF(7F, 15F);
        AutoScaleMode = AutoScaleMode.Font;
        BackColor = Color.FromArgb(248, 250, 252);
        ClientSize = new Size(1040, 680);
        Controls.Add(MainSplit);
        Font = new Font("Segoe UI", 9F, FontStyle.Regular, GraphicsUnit.Point);
        FormBorderStyle = FormBorderStyle.FixedSingle;
        MaximizeBox = false;
        MinimumSize = new Size(980, 640);
        Name = "FrmLogin";
        StartPosition = FormStartPosition.CenterScreen;
        Text = "TicketPremium - Acceso";
        MainSplit.Panel1.ResumeLayout(false);
        MainSplit.Panel2.ResumeLayout(false);
        ((System.ComponentModel.ISupportInitialize)MainSplit).EndInit();
        MainSplit.ResumeLayout(false);
        PanelBrand.ResumeLayout(false);
        PanelBrand.PerformLayout();
        TabsAuth.ResumeLayout(false);
        TabLogin.ResumeLayout(false);
        TabLogin.PerformLayout();
        TabRegister.ResumeLayout(false);
        TabRegister.PerformLayout();
        ResumeLayout(false);
    }
}
