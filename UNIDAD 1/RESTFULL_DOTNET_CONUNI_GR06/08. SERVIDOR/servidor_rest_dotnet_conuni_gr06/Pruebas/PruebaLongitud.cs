using System.Globalization;
using System.Net.Http.Json;
using Ec.Edu.Monster.Utils;

static class PruebaLongitud
{
    static readonly string baseUrl = ConstantesPruebas.DireccionServicio + "/conversion/longitud";
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE CONVERSION DE LONGITUD ===");
        Console.WriteLine();

        Probar("metrosAPies",            10,    32.8084, 0.001);
        Probar("kilometrosAMillas",      5,     3.106855, 0.001);
        Probar("centimetrosAPulgadas",   10,    3.93701, 0.001);
        Probar("yardasAMetros",          10,    9.144,   0.001);
        Probar("milimetrosAPulgadas",    25.4,  1.0,     0.001);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS LONGITUD: OK={ok} FAIL={fail} ---");
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
