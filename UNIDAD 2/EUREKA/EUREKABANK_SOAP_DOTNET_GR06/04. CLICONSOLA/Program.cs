using CLICONSOLA.Config;
using CLICONSOLA.Controlador;
using CLICONSOLA.Vista;

Console.WriteLine("========================================");
Console.WriteLine("     EUREKA BANK - Console Client");
Console.WriteLine("========================================");
Console.WriteLine();
Console.WriteLine($"Conectando a: {ServidorConfig.BaseUrl}");
Console.WriteLine();

var controller = new BancoController();
ConsolaApp.Ejecutar(controller);
