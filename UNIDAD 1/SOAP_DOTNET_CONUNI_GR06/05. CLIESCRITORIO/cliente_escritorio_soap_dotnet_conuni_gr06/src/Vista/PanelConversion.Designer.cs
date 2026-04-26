namespace Ec.Edu.Monster.Vista;

partial class PanelConversion
{
    private System.ComponentModel.IContainer components = null;
    private System.Windows.Forms.Panel panelEncabezado;
    private System.Windows.Forms.Button btnVolver;
    private System.Windows.Forms.Label lblTituloCategoria;
    private System.Windows.Forms.Label lblEncabezado;
    private System.Windows.Forms.Label lblDescripcion;
    private System.Windows.Forms.ComboBox comboOperacion;
    private System.Windows.Forms.TextBox campoValor;
    private System.Windows.Forms.Button btnConvertir;
    private System.Windows.Forms.Label lblResultado;

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
        panelEncabezado = new Panel();
        lblTituloCategoria = new Label();
        btnVolver = new Button();
        lblEncabezado = new Label();
        lblDescripcion = new Label();
        comboOperacion = new ComboBox();
        campoValor = new TextBox();
        btnConvertir = new Button();
        lblResultado = new Label();
        panelEncabezado.SuspendLayout();
        SuspendLayout();
        // 
        // panelEncabezado
        // 
        panelEncabezado.Controls.Add(lblTituloCategoria);
        panelEncabezado.Controls.Add(btnVolver);
        panelEncabezado.Dock = DockStyle.Top;
        panelEncabezado.Location = new Point(0, 0);
        panelEncabezado.Name = "panelEncabezado";
        panelEncabezado.Padding = new Padding(24, 20, 24, 20);
        panelEncabezado.Size = new Size(1180, 74);
        panelEncabezado.TabIndex = 0;
        // 
        // lblTituloCategoria
        // 
        lblTituloCategoria.AutoSize = true;
        lblTituloCategoria.Location = new Point(140, 26);
        lblTituloCategoria.Name = "lblTituloCategoria";
        lblTituloCategoria.Size = new Size(43, 19);
        lblTituloCategoria.TabIndex = 1;
        lblTituloCategoria.Text = "Titulo";
        // 
        // btnVolver
        // 
        btnVolver.Location = new Point(24, 20);
        btnVolver.Name = "btnVolver";
        btnVolver.Size = new Size(96, 32);
        btnVolver.TabIndex = 0;
        btnVolver.Text = "Volver al Menu";
        btnVolver.UseVisualStyleBackColor = true;
        // 
        // lblEncabezado
        // 
        lblEncabezado.AutoSize = true;
        lblEncabezado.Location = new Point(392, 164);
        lblEncabezado.Name = "lblEncabezado";
        lblEncabezado.Size = new Size(91, 19);
        lblEncabezado.TabIndex = 1;
        lblEncabezado.Text = "Conversiones";
        lblEncabezado.Click += lblEncabezado_Click;
        // 
        // lblDescripcion
        // 
        lblDescripcion.AutoSize = true;
        lblDescripcion.Location = new Point(392, 232);
        lblDescripcion.Name = "lblDescripcion";
        lblDescripcion.Size = new Size(171, 19);
        lblDescripcion.TabIndex = 2;
        lblDescripcion.Text = "Selecciona una conversion.";
        lblDescripcion.Click += lblDescripcion_Click;
        // 
        // comboOperacion
        // 
        comboOperacion.DropDownStyle = ComboBoxStyle.DropDownList;
        comboOperacion.FormattingEnabled = true;
        comboOperacion.Location = new Point(392, 268);
        comboOperacion.Name = "comboOperacion";
        comboOperacion.Size = new Size(340, 27);
        comboOperacion.TabIndex = 3;
        // 
        // campoValor
        // 
        campoValor.Location = new Point(392, 328);
        campoValor.Name = "campoValor";
        campoValor.Size = new Size(340, 26);
        campoValor.TabIndex = 4;
        // 
        // btnConvertir
        // 
        btnConvertir.Location = new Point(495, 380);
        btnConvertir.Name = "btnConvertir";
        btnConvertir.Size = new Size(340, 36);
        btnConvertir.TabIndex = 5;
        btnConvertir.Text = "Convertir";
        btnConvertir.UseVisualStyleBackColor = true;
        // 
        // lblResultado
        // 
        lblResultado.BorderStyle = BorderStyle.FixedSingle;
        lblResultado.Location = new Point(392, 436);
        lblResultado.Name = "lblResultado";
        lblResultado.Size = new Size(520, 48);
        lblResultado.TabIndex = 6;
        lblResultado.Text = " ";
        lblResultado.TextAlign = ContentAlignment.MiddleLeft;
        // 
        // PanelConversion
        // 
        BackColor = Color.White;
        Controls.Add(lblResultado);
        Controls.Add(btnConvertir);
        Controls.Add(campoValor);
        Controls.Add(comboOperacion);
        Controls.Add(lblDescripcion);
        Controls.Add(lblEncabezado);
        Controls.Add(panelEncabezado);
        Name = "PanelConversion";
        Size = new Size(1180, 620);
        panelEncabezado.ResumeLayout(false);
        panelEncabezado.PerformLayout();
        ResumeLayout(false);
        PerformLayout();
    }
}
