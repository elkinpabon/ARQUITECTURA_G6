using CLICONSOLA.Config;
using CLICONSOLA.Controlador;
using CLICONSOLA.Vista;

Console.WriteLine("========================================");
Console.WriteLine("     EUREKA BANK - Console Client");
Console.WriteLine("========================================");
Console.WriteLine();
Console.Write("URL del servidor (default: http://localhost:5000): ");
string input = Console.ReadLine() ?? string.Empty;

if (!string.IsNullOrWhiteSpace(input))
{
    ServidorConfig.BaseUrl = input;
}

Console.WriteLine($"Conectando a: {ServidorConfig.BaseUrl}");
Console.WriteLine();

var controller = new BancoController();
ConsolaApp.Ejecutar(controller);
