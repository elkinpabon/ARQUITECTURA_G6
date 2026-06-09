namespace SERVIDOR.Models;

public class Resultado
{
    public bool Exitoso { get; set; }
    public string Mensaje { get; set; } = string.Empty;
    public double Saldo { get; set; }

    public Resultado() { }

    public Resultado(bool exitoso, string mensaje)
    {
        Exitoso = exitoso;
        Mensaje = mensaje;
    }

    public Resultado(bool exitoso, string mensaje, double saldo)
    {
        Exitoso = exitoso;
        Mensaje = mensaje;
        Saldo = saldo;
    }

    public static Resultado Ok(string mensaje, double saldo) =>
        new Resultado(true, mensaje, saldo);

    public static Resultado Error(string mensaje) =>
        new Resultado(false, mensaje);
}
