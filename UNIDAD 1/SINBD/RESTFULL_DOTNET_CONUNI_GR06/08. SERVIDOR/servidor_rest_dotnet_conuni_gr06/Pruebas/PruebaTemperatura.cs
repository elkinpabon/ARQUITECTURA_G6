using System.Globalization;
using System.Net.Http.Json;
using Ec.Edu.Monster.Utils;

static class PruebaTemperatura
{
    static readonly string baseUrl = ConstantesPruebas.DireccionServicio + "/conversion/temperatura";
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE CONVERSION DE TEMPERATURA ===");
        Console.WriteLine();

        Probar("celsiusAFahrenheit",   0,     32,    0.001);
        Probar("fahrenheitACelsius",   32,    0,     0.001);
        Probar("celsiusAKelvin",       0,     273.15, 0.001);
        Probar("kelvinACelsius",       273.15, 0,     0.001);
        Probar("fahrenheitAKelvin",    32,    273.15, 0.001);
        Probar("kelvinAFahrenheit",    273.15, 32,    0.001);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS TEMPERATURA: OK={ok} FAIL={fail} ---");
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
