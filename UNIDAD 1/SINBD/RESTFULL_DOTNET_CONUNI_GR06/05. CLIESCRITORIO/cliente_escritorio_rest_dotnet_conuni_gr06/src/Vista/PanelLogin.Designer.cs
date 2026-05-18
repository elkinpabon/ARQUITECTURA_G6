namespace Ec.Edu.Monster.Vista;

partial class PanelLogin
{
    private System.ComponentModel.IContainer components = null;
    private System.Windows.Forms.Panel panelImagen;
    private System.Windows.Forms.Panel panelFormulario;
    private System.Windows.Forms.PictureBox picLogo;
    private System.Windows.Forms.Label lblTitulo;
    private System.Windows.Forms.Label lblSubtitulo;
    private System.Windows.Forms.Label lblUsuario;
    private System.Windows.Forms.TextBox campoUsuario;
    private System.Windows.Forms.Label lblContrasena;
    private System.Windows.Forms.TextBox campoContrasena;
    private System.Windows.Forms.Button botonMostrar;
    private System.Windows.Forms.Button botonIngresar;
    private System.Windows.Forms.Label lblError;

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
        panelImagen = new Panel();
        panelFormulario = new Panel();
        lblError = new Label();
        botonIngresar = new Button();
        botonMostrar = new Button();
        campoContrasena = new TextBox();
        lblContrasena = new Label();
        campoUsuario = new TextBox();
        lblUsuario = new Label();
        lblSubtitulo = new Label();
        lblTitulo = new Label();
        picLogo = new PictureBox();
        panelFormulario.SuspendLayout();
        ((System.ComponentModel.ISupportInitialize)picLogo).BeginInit();
        SuspendLayout();
        // 
        // panelImagen
        // 
        panelImagen.BackColor = Color.FromArgb(31, 58, 95);
        panelImagen.Dock = DockStyle.Left;
        panelImagen.Location = new Point(0, 0);
        panelImagen.Name = "panelImagen";
        panelImagen.Size = new Size(568, 620);
        panelImagen.TabIndex = 0;
        panelImagen.Paint += panelImagen_Paint;
        // 
        // panelFormulario
        // 
        panelFormulario.BackColor = Color.White;
        panelFormulario.Controls.Add(lblError);
        panelFormulario.Controls.Add(botonIngresar);
        panelFormulario.Controls.Add(botonMostrar);
        panelFormulario.Controls.Add(campoContrasena);
        panelFormulario.Controls.Add(lblContrasena);
        panelFormulario.Controls.Add(campoUsuario);
        panelFormulario.Controls.Add(lblUsuario);
        panelFormulario.Controls.Add(lblSubtitulo);
        panelFormulario.Controls.Add(lblTitulo);
        panelFormulario.Controls.Add(picLogo);
        panelFormulario.Dock = DockStyle.Fill;
        panelFormulario.Location = new Point(568, 0);
        panelFormulario.Name = "panelFormulario";
        panelFormulario.Padding = new Padding(36, 24, 36, 24);
        panelFormulario.Size = new Size(612, 620);
        panelFormulario.TabIndex = 1;
        panelFormulario.Paint += panelFormulario_Paint;
        // 
        // lblError
        // 
        lblError.Location = new Point(214, 556);
        lblError.Name = "lblError";
        lblError.Size = new Size(359, 28);
        lblError.TabIndex = 9;
        lblError.Text = " ";
        lblError.TextAlign = ContentAlignment.MiddleCenter;
        // 
        // botonIngresar
        // 
        botonIngresar.Location = new Point(214, 521);
        botonIngresar.Name = "botonIngresar";
        botonIngresar.Size = new Size(359, 33);
        botonIngresar.TabIndex = 8;
        botonIngresar.Text = "Ingresar";
        botonIngresar.UseVisualStyleBackColor = true;
        // 
        // botonMostrar
        // 
        botonMostrar.Location = new Point(488, 455);
        botonMostrar.Name = "botonMostrar";
        botonMostrar.Size = new Size(85, 25);
        botonMostrar.TabIndex = 7;
        botonMostrar.Text = "Mostrar";
        botonMostrar.UseVisualStyleBackColor = true;
        // 
        // campoContrasena
        // 
        campoContrasena.Location = new Point(214, 454);
        campoContrasena.Name = "campoContrasena";
        campoContrasena.Size = new Size(251, 26);
        campoContrasena.TabIndex = 6;
        // 
        // lblContrasena
        // 
        lblContrasena.AutoSize = true;
        lblContrasena.Location = new Point(214, 416);
        lblContrasena.Name = "lblContrasena";
        lblContrasena.Size = new Size(82, 19);
        lblContrasena.TabIndex = 5;
        lblContrasena.Text = "Contrasena:";
        // 
        // campoUsuario
        // 
        campoUsuario.Location = new Point(214, 363);
        campoUsuario.Name = "campoUsuario";
        campoUsuario.Size = new Size(331, 26);
        campoUsuario.TabIndex = 4;
        // 
        // lblUsuario
        // 
        lblUsuario.AutoSize = true;
        lblUsuario.Location = new Point(214, 325);
        lblUsuario.Name = "lblUsuario";
        lblUsuario.Size = new Size(59, 19);
        lblUsuario.TabIndex = 3;
        lblUsuario.Text = "Usuario:";
        // 
        // lblSubtitulo
        // 
        lblSubtitulo.AutoSize = true;
        lblSubtitulo.Location = new Point(214, 263);
        lblSubtitulo.Name = "lblSubtitulo";
        lblSubtitulo.Size = new Size(154, 19);
        lblSubtitulo.TabIndex = 2;
        lblSubtitulo.Text = "Ingresa tus credenciales";
        // 
        // lblTitulo
        // 
        lblTitulo.AutoSize = true;
        lblTitulo.Location = new Point(214, 192);
        lblTitulo.Name = "lblTitulo";
        lblTitulo.Size = new Size(88, 19);
        lblTitulo.TabIndex = 1;
        lblTitulo.Text = "Iniciar Sesion";
        // 
        // picLogo
        // 
        picLogo.Location = new Point(350, 75);
        picLogo.Name = "picLogo";
        picLogo.Size = new Size(81, 70);
        picLogo.TabIndex = 0;
        picLogo.TabStop = false;
        // 
        // PanelLogin
        // 
        BackColor = Color.White;
        Controls.Add(panelFormulario);
        Controls.Add(panelImagen);
        Name = "PanelLogin";
        Size = new Size(1180, 620);
        panelFormulario.ResumeLayout(false);
        panelFormulario.PerformLayout();
        ((System.ComponentModel.ISupportInitialize)picLogo).EndInit();
        ResumeLayout(false);
    }
}
