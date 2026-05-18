using Ec.Edu.Monster.Controlador;

namespace Ec.Edu.Monster.Pruebas;

static class PruebaMasa
{
    static readonly CONUNI servicio = new();
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE CONVERSION DE MASA ===");
        Console.WriteLine();

        Probar("kilogramosALibras", 1, 2.20462);
        Probar("gramosAOnzas", 100, 3.527396);
        Probar("toneladasAKilogramos", 2, 2000);
        Probar("librasAOnzas", 1, 16);
        Probar("miligramosAGramos", 500, 0.5);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS MASA: OK={ok} FAIL={fail} ---");
        Console.WriteLine();
    }

    static void Probar(string operacion, double valor, double esperado)
    {
        try
        {
            double obtenido = operacion switch
            {
                "kilogramosALibras" => servicio.KilogramosALibras(valor),
                "gramosAOnzas" => servicio.GramosAOnzas(valor),
                "toneladasAKilogramos" => servicio.ToneladasAKilogramos(valor),
                "librasAOnzas" => servicio.LibrasAOnzas(valor),
                "miligramosAGramos" => servicio.MiligramosAGramos(valor),
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
