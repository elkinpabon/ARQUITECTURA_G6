namespace Ec.Edu.Monster.Vista;

partial class PanelMenu
{
    private System.ComponentModel.IContainer components = null;
    private System.Windows.Forms.Panel panelCabecera;
    private System.Windows.Forms.PictureBox picLogo;
    private System.Windows.Forms.Label lblTitulo;
    private System.Windows.Forms.Label lblSaludo;
    private System.Windows.Forms.Button btnCerrarSesion;
    private System.Windows.Forms.TableLayoutPanel panelTarjetas;
    private System.Windows.Forms.Panel tarjetaLongitud;
    private System.Windows.Forms.Label tituloLongitud;
    private System.Windows.Forms.Label descripcionLongitud;
    private System.Windows.Forms.Panel tarjetaMasa;
    private System.Windows.Forms.Label tituloMasa;
    private System.Windows.Forms.Label descripcionMasa;
    private System.Windows.Forms.Panel tarjetaTemperatura;
    private System.Windows.Forms.Label tituloTemperatura;
    private System.Windows.Forms.Label descripcionTemperatura;

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
        panelCabecera = new Panel();
        btnCerrarSesion = new Button();
        lblSaludo = new Label();
        lblTitulo = new Label();
        picLogo = new PictureBox();
        panelTarjetas = new TableLayoutPanel();
        tarjetaLongitud = new Panel();
        descripcionLongitud = new Label();
        tituloLongitud = new Label();
        tarjetaMasa = new Panel();
        descripcionMasa = new Label();
        tituloMasa = new Label();
        tarjetaTemperatura = new Panel();
        descripcionTemperatura = new Label();
        tituloTemperatura = new Label();
        panelCabecera.SuspendLayout();
        ((System.ComponentModel.ISupportInitialize)picLogo).BeginInit();
        panelTarjetas.SuspendLayout();
        tarjetaLongitud.SuspendLayout();
        tarjetaMasa.SuspendLayout();
        tarjetaTemperatura.SuspendLayout();
        SuspendLayout();
        // 
        // panelCabecera
        // 
        panelCabecera.Controls.Add(btnCerrarSesion);
        panelCabecera.Controls.Add(lblSaludo);
        panelCabecera.Controls.Add(lblTitulo);
        panelCabecera.Controls.Add(picLogo);
        panelCabecera.Dock = DockStyle.Top;
        panelCabecera.Location = new Point(0, 0);
        panelCabecera.Name = "panelCabecera";
        panelCabecera.Padding = new Padding(12);
        panelCabecera.Size = new Size(1180, 72);
        panelCabecera.TabIndex = 0;
        // 
        // btnCerrarSesion
        // 
        btnCerrarSesion.Anchor = AnchorStyles.Top | AnchorStyles.Right;
        btnCerrarSesion.Location = new Point(1044, 16);
        btnCerrarSesion.Name = "btnCerrarSesion";
        btnCerrarSesion.Size = new Size(124, 32);
        btnCerrarSesion.TabIndex = 3;
        btnCerrarSesion.Text = "Cerrar Sesion";
        btnCerrarSesion.UseVisualStyleBackColor = true;
        // 
        // lblSaludo
        // 
        lblSaludo.AutoSize = true;
        lblSaludo.Location = new Point(942, 22);
        lblSaludo.Name = "lblSaludo";
        lblSaludo.Size = new Size(76, 19);
        lblSaludo.TabIndex = 2;
        lblSaludo.Text = "Bienvenido";
        // 
        // lblTitulo
        // 
        lblTitulo.AutoSize = true;
        lblTitulo.Location = new Point(60, 22);
        lblTitulo.Name = "lblTitulo";
        lblTitulo.Size = new Size(169, 19);
        lblTitulo.TabIndex = 1;
        lblTitulo.Text = "Cliente Escritorio CONUNI";
        // 
        // picLogo
        // 
        picLogo.Location = new Point(12, 16);
        picLogo.Name = "picLogo";
        picLogo.Size = new Size(36, 36);
        picLogo.TabIndex = 0;
        picLogo.TabStop = false;
        // 
        // panelTarjetas
        // 
        panelTarjetas.ColumnCount = 3;
        panelTarjetas.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 33.33333F));
        panelTarjetas.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 33.33333F));
        panelTarjetas.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 33.33333F));
        panelTarjetas.Controls.Add(tarjetaLongitud, 0, 0);
        panelTarjetas.Controls.Add(tarjetaMasa, 1, 0);
        panelTarjetas.Controls.Add(tarjetaTemperatura, 2, 0);
        panelTarjetas.Dock = DockStyle.Top;
        panelTarjetas.Location = new Point(0, 72);
        panelTarjetas.Name = "panelTarjetas";
        panelTarjetas.Padding = new Padding(20, 24, 20, 24);
        panelTarjetas.RowCount = 1;
        panelTarjetas.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));
        panelTarjetas.Size = new Size(1180, 260);
        panelTarjetas.TabIndex = 1;
        // 
        // tarjetaLongitud
        // 
        tarjetaLongitud.Controls.Add(descripcionLongitud);
        tarjetaLongitud.Controls.Add(tituloLongitud);
        tarjetaLongitud.Dock = DockStyle.Fill;
        tarjetaLongitud.Location = new Point(23, 27);
        tarjetaLongitud.Margin = new Padding(3, 3, 12, 3);
        tarjetaLongitud.Name = "tarjetaLongitud";
        tarjetaLongitud.Size = new Size(365, 206);
        tarjetaLongitud.TabIndex = 0;
        // 
        // descripcionLongitud
        // 
        descripcionLongitud.AutoSize = true;
        descripcionLongitud.Location = new Point(74, 108);
        descripcionLongitud.Name = "descripcionLongitud";
        descripcionLongitud.Size = new Size(196, 19);
        descripcionLongitud.TabIndex = 1;
        descripcionLongitud.Text = "Metros, pies, millas, pulgadas...";
        // 
        // tituloLongitud
        // 
        tituloLongitud.AutoSize = true;
        tituloLongitud.Location = new Point(152, 58);
        tituloLongitud.Name = "tituloLongitud";
        tituloLongitud.Size = new Size(64, 19);
        tituloLongitud.TabIndex = 0;
        tituloLongitud.Text = "Longitud";
        // 
        // tarjetaMasa
        // 
        tarjetaMasa.Controls.Add(descripcionMasa);
        tarjetaMasa.Controls.Add(tituloMasa);
        tarjetaMasa.Dock = DockStyle.Fill;
        tarjetaMasa.Location = new Point(403, 27);
        tarjetaMasa.Margin = new Padding(3, 3, 12, 3);
        tarjetaMasa.Name = "tarjetaMasa";
        tarjetaMasa.Size = new Size(365, 206);
        tarjetaMasa.TabIndex = 1;
        // 
        // descripcionMasa
        // 
        descripcionMasa.AutoSize = true;
        descripcionMasa.Location = new Point(83, 108);
        descripcionMasa.Name = "descripcionMasa";
        descripcionMasa.Size = new Size(167, 19);
        descripcionMasa.TabIndex = 1;
        descripcionMasa.Text = "Kilogramos, libras, onzas...";
        descripcionMasa.Click += descripcionMasa_Click;
        // 
        // tituloMasa
        // 
        tituloMasa.AutoSize = true;
        tituloMasa.Location = new Point(150, 58);
        tituloMasa.Name = "tituloMasa";
        tituloMasa.Size = new Size(42, 19);
        tituloMasa.TabIndex = 0;
        tituloMasa.Text = "Masa";
        // 
        // tarjetaTemperatura
        // 
        tarjetaTemperatura.Controls.Add(descripcionTemperatura);
        tarjetaTemperatura.Controls.Add(tituloTemperatura);
        tarjetaTemperatura.Dock = DockStyle.Fill;
        tarjetaTemperatura.Location = new Point(783, 27);
        tarjetaTemperatura.Name = "tarjetaTemperatura";
        tarjetaTemperatura.Size = new Size(374, 206);
        tarjetaTemperatura.TabIndex = 2;
        // 
        // descripcionTemperatura
        // 
        descripcionTemperatura.AutoSize = true;
        descripcionTemperatura.Location = new Point(81, 108);
        descripcionTemperatura.Name = "descripcionTemperatura";
        descripcionTemperatura.Size = new Size(166, 19);
        descripcionTemperatura.TabIndex = 1;
        descripcionTemperatura.Text = "Celsius, Fahrenheit, Kelvin";
        descripcionTemperatura.Click += descripcionTemperatura_Click;
        // 
        // tituloTemperatura
        // 
        tituloTemperatura.AutoSize = true;
        tituloTemperatura.Location = new Point(127, 58);
        tituloTemperatura.Name = "tituloTemperatura";
        tituloTemperatura.Size = new Size(86, 19);
        tituloTemperatura.TabIndex = 0;
        tituloTemperatura.Text = "Temperatura";
        tituloTemperatura.Click += tituloTemperatura_Click;
        // 
        // PanelMenu
        // 
        BackColor = Color.FromArgb(242, 244, 247);
        Controls.Add(panelTarjetas);
        Controls.Add(panelCabecera);
        Name = "PanelMenu";
        Size = new Size(1180, 620);
        panelCabecera.ResumeLayout(false);
        panelCabecera.PerformLayout();
        ((System.ComponentModel.ISupportInitialize)picLogo).EndInit();
        panelTarjetas.ResumeLayout(false);
        tarjetaLongitud.ResumeLayout(false);
        tarjetaLongitud.PerformLayout();
        tarjetaMasa.ResumeLayout(false);
        tarjetaMasa.PerformLayout();
        tarjetaTemperatura.ResumeLayout(false);
        tarjetaTemperatura.PerformLayout();
        ResumeLayout(false);
    }
}
