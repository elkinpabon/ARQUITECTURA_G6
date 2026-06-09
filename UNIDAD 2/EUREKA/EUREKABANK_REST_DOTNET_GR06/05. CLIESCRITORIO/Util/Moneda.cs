namespace CLIESCRITORIO.Util;

public static class Moneda
{
    public static string Nombre(string codigo) => codigo == "01" ? "Soles" : "Dólares";
}
