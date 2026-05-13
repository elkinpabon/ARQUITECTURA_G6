using Ec.Edu.Monster.Controlador;

namespace Ec.Edu.Monster.Pruebas;

static class PruebaTemperatura
{
    static readonly CONUNI servicio = new();
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE CONVERSION DE TEMPERATURA ===");
        Console.WriteLine();

        Probar("celsiusAFahrenheit", 0, 32);
        Probar("fahrenheitACelsius", 32, 0);
        Probar("celsiusAKelvin", 0, 273.15);
        Probar("kelvinACelsius", 273.15, 0);
        Probar("fahrenheitAKelvin", 32, 273.15);
        Probar("kelvinAFahrenheit", 273.15, 32);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS TEMPERATURA: OK={ok} FAIL={fail} ---");
        Console.WriteLine();
    }

    static void Probar(string operacion, double valor, double esperado)
    {
        try
        {
            double obtenido = operacion switch
            {
                "celsiusAFahrenheit" => servicio.CelsiusAFahrenheit(valor),
                "fahrenheitACelsius" => servicio.FahrenheitACelsius(valor),
                "celsiusAKelvin" => servicio.CelsiusAKelvin(valor),
                "kelvinACelsius" => servicio.KelvinACelsius(valor),
                "fahrenheitAKelvin" => servicio.FahrenheitAKelvin(valor),
                "kelvinAFahrenheit" => servicio.KelvinAFahrenheit(valor),
                _ => throw new ArgumentOutOfRangeException(nameof(operacion))
            };

            bool casoOk = Math.Abs(obtenido - esperado) <= ConstantesPruebas.Tolerancia;

            if (casoOk)
            {
                ok++;
                Console.WriteLine($"[OK] {operacion}({valor}) = {obtenido:F4}");
            }
            else
            {
                fail++;
                Console.WriteLine($"[FAIL] {operacion}({valor})");
                Console.WriteLine($"  Esperado: {esperado} +/- {ConstantesPruebas.Tolerancia}");
                Console.WriteLine($"  Obtenido: {obtenido}");
            }
        }
        catch (Exception e)
        {
            fail++;
            Console.WriteLine($"[FAIL] {operacion}({valor}) -> Error: {e.Message}");
        }
    }
}
