namespace cliente_consola_rest_dotnet_ticketpremium_gr06.Vista;

public static class ConsolaVista
{
    public static void MostrarEncabezado()
    {
        Console.ForegroundColor = ConsoleColor.Cyan;
        Console.WriteLine("===========================================");
        Console.WriteLine("       TICKETPREMIUM - CLIENTE CONSOLA      ");
        Console.WriteLine("===========================================");
        Console.ResetColor();
    }
}
