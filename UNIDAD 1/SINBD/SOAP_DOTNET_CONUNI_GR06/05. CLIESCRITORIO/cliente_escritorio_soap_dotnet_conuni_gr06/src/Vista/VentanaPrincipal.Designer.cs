namespace Ec.Edu.Monster.Vista;

partial class VentanaPrincipal
{
    private System.ComponentModel.IContainer components = null;
    private System.Windows.Forms.Panel contenedorPantallas;

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
        contenedorPantallas = new System.Windows.Forms.Panel();
        SuspendLayout();
        // 
        // contenedorPantallas
        // 
        contenedorPantallas.BackColor = System.Drawing.Color.White;
        contenedorPantallas.Dock = System.Windows.Forms.DockStyle.Fill;
        contenedorPantallas.Location = new System.Drawing.Point(0, 0);
        contenedorPantallas.Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
        contenedorPantallas.Name = "contenedorPantallas";
        contenedorPantallas.Size = new System.Drawing.Size(1180, 620);
        contenedorPantallas.TabIndex = 0;
        // 
        // VentanaPrincipal
        // 
        AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
        AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
        ClientSize = new System.Drawing.Size(1180, 620);
        Controls.Add(contenedorPantallas);
        Margin = new System.Windows.Forms.Padding(4, 3, 4, 3);
        Name = "VentanaPrincipal";
        StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
        Text = "CONUNI - Cliente de escritorio";
        ResumeLayout(false);
    }
}
