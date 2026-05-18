using System.Globalization;
using System.Net.Http.Json;
using Ec.Edu.Monster.Utils;

static class PruebaAutenticacion
{
    static readonly string url = ConstantesPruebas.DireccionServicio + "/login";
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE AUTENTICACION ===");
        Console.WriteLine();

        Probar(new { usuario = "MONSTER",   contrasena = "MONSTER9" }, true,  "Sesion iniciada");
        Probar(new { usuario = "INVALIDO",  contrasena = "MONSTER9" }, false, "Credenciales invalidas");
        Probar(new { usuario = "MONSTER",   contrasena = "12345"    }, false, "Credenciales invalidas");
        Probar(new { usuario = "",          contrasena = ""          }, false, "Credenciales invalidas");
        Probar(new { usuario = "monster",   contrasena = "MONSTER9" }, true,  "Sesion iniciada");
        Probar(new { usuario = "MONSTER",   contrasena = "monster9" }, false, "Credenciales invalidas");

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS AUTENTICACION: OK={ok} FAIL={fail} ---");
        Console.WriteLine();
    }

    static void Probar(object body, bool esperaExito, string esperaMensaje)
    {
        try
        {
            using var client = new HttpClient();
            var response = client.PostAsJsonAsync(url, body).Result;
            var json = response.Content.ReadFromJsonAsync<Dictionary<string, object>>().Result;

            var element = (System.Text.Json.JsonElement)json!["exito"]!;
            bool exito = element.GetBoolean();
            string mensaje = ((System.Text.Json.JsonElement)json["mensaje"]!).GetString() ?? "";
            bool casoOk = exito == esperaExito && mensaje == esperaMensaje;

            if (casoOk) { ok++; Console.WriteLine($"[OK] {body} -> {esperaMensaje}"); }
            else
            {
                fail++;
                Console.WriteLine($"[FAIL] {body}");
                Console.WriteLine($"  Esperado: exito={esperaExito} mensaje={esperaMensaje}");
                Console.WriteLine($"  Obtenido: exito={exito} mensaje={mensaje}");
            }
        }
        catch (Exception e)
        {
            fail++;
            Console.WriteLine($"[FAIL] {body} -> Error: {e.Message}");
        }
    }
}
