namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06;

static class Program
{
    /// <summary>
    ///  The main entry point for the application.
    /// </summary>
    [STAThread]
    static void Main()
    {
        ApplicationConfiguration.Initialize();
        using var login = new Vista.FrmLogin();

        if (login.ShowDialog() == DialogResult.OK)
        {
            Application.Run(new Vista.FrmPrincipal(login.UsuarioActual));
        }
    }    
}
