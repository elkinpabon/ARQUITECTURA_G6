namespace Ec.Edu.Monster.Vista;

public class VistaConsola
{
    public void MostrarEncabezado(string titulo)
    {
        Console.WriteLine();
        Console.WriteLine(new string('=', 48));
        Console.WriteLine(titulo);
        Console.WriteLine(new string('=', 48));
    }

    public void MostrarMensaje(string mensaje) => Console.WriteLine(mensaje);

    public void MostrarResultado(Modelo.Resultado resultado)
    {
        if (!resultado.Exito)
        {
            Console.WriteLine($"[ERROR] {resultado.Mensaje}");
            return;
        }

        if (!string.IsNullOrWhiteSpace(resultado.Mensaje))
        {
            Console.WriteLine($"[OK] {resultado.Mensaje}");
            return;
        }

        Console.WriteLine($"[OK] Resultado: {resultado.Valor:0.000}");
    }

    public void MostrarOpcion(int numero, string texto) => Console.WriteLine($"{numero}. {texto}");

    public void MostrarLineaVacia() => Console.WriteLine();
}
