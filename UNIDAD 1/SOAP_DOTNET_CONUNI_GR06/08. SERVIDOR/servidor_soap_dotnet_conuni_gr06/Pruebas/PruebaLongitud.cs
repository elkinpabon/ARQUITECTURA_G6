using Ec.Edu.Monster.Controlador;

namespace Ec.Edu.Monster.Pruebas;

static class PruebaLongitud
{
    static readonly CONUNI servicio = new();
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE CONVERSION DE LONGITUD ===");
        Console.WriteLine();

        Probar("metrosAPies", 10, 32.8084);
        Probar("kilometrosAMillas", 5, 3.106855);
        Probar("centimetrosAPulgadas", 10, 3.93701);
        Probar("yardasAMetros", 10, 9.144);
        Probar("milimetrosAPulgadas", 25.4, 1.0);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS LONGITUD: OK={ok} FAIL={fail} ---");
        Console.WriteLine();
    }

    static void Probar(string operacion, double valor, double esperado)
    {
        try
        {
            double obtenido = operacion switch
            {
                "metrosAPies" => servicio.MetrosAPies(valor),
                "kilometrosAMillas" => servicio.KilometrosAMillas(valor),
                "centimetrosAPulgadas" => servicio.CentimetrosAPulgadas(valor),
                "yardasAMetros" => servicio.YardasAMetros(valor),
                "milimetrosAPulgadas" => servicio.MilimetrosAPulgadas(valor),
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
