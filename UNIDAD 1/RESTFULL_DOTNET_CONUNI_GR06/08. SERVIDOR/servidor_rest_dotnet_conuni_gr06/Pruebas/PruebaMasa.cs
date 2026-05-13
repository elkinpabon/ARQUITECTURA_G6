using System.Globalization;
using System.Net.Http.Json;
using Ec.Edu.Monster.Utils;

static class PruebaMasa
{
    static readonly string baseUrl = ConstantesPruebas.DireccionServicio + "/conversion/masa";
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE CONVERSION DE MASA ===");
        Console.WriteLine();

        Probar("kilogramosALibras",     1,   2.20462,  0.001);
        Probar("gramosAOnzas",          100, 3.527396, 0.001);
        Probar("toneladasAKilogramos",  2,   2000,     0.001);
        Probar("librasAOnzas",          1,   16,       0.001);
        Probar("miligramosAGramos",     500, 0.5,      0.001);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS MASA: OK={ok} FAIL={fail} ---");
        Console.WriteLine();
    }

    static void Probar(string operacion, double valor, double esperado, double tolerancia)
    {
        try
        {
            using var client = new HttpClient();
            var response = client.GetAsync($"{baseUrl}/{operacion}?valor={valor.ToString(CultureInfo.InvariantCulture)}").Result;
            var json = response.Content.ReadFromJsonAsync<Dictionary<string, object>>().Result;

            double obtenido = ((System.Text.Json.JsonElement)json!["valor"]!).GetDouble();
            bool casoOk = Math.Abs(obtenido - esperado) <= tolerancia;

            if (casoOk) { ok++; Console.WriteLine($"[OK] {operacion}({valor}) = {obtenido:F4}"); }
            else
            {
                fail++;
                Console.WriteLine($"[FAIL] {operacion}({valor})");
                Console.WriteLine($"  Esperado: {esperado} +/- {tolerancia}");
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
