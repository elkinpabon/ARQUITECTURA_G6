using Ec.Edu.Monster.Controlador;

namespace Ec.Edu.Monster.Pruebas;

static class PruebaAutenticacion
{
    static readonly CONUNI servicio = new();
    static int ok = 0, fail = 0;

    public static void Ejecutar()
    {
        Console.WriteLine("=== PRUEBAS DE AUTENTICACION ===");
        Console.WriteLine();

        Probar("MONSTER", "MONSTER9", true, ConstantesPruebas.MensajeExitoAutenticacion);
        Probar("INVALIDO", "MONSTER9", false, ConstantesPruebas.MensajeErrorAutenticacion);
        Probar("MONSTER", "12345", false, ConstantesPruebas.MensajeErrorAutenticacion);
        Probar("", "", false, ConstantesPruebas.MensajeErrorAutenticacion);
        Probar("monster", "MONSTER9", true, ConstantesPruebas.MensajeExitoAutenticacion);
        Probar("MONSTER", "monster9", false, ConstantesPruebas.MensajeErrorAutenticacion);

        Console.WriteLine();
        Console.WriteLine($"--- RESULTADOS AUTENTICACION: OK={ok} FAIL={fail} ---");
        Console.WriteLine();
    }

    static void Probar(string usuario, string contrasena, bool esperaExito, string esperaMensaje)
    {
        try
        {
            bool exito = servicio.IniciarSesion(usuario, contrasena);
            string mensaje = exito ? ConstantesPruebas.MensajeExitoAutenticacion : ConstantesPruebas.MensajeErrorAutenticacion;
            bool casoOk = exito == esperaExito && mensaje == esperaMensaje;

            if (casoOk)
            {
                ok++;
                Console.WriteLine($"[OK] {{ usuario = {usuario}, contrasena = {contrasena} }} -> {esperaMensaje}");
            }
            else
            {
                fail++;
                Console.WriteLine($"[FAIL] {{ usuario = {usuario}, contrasena = {contrasena} }}");
                Console.WriteLine($"  Esperado: exito={esperaExito} mensaje={esperaMensaje}");
                Console.WriteLine($"  Obtenido: exito={exito} mensaje={mensaje}");
            }
        }
        catch (Exception e)
        {
            fail++;
            Console.WriteLine($"[FAIL] {{ usuario = {usuario}, contrasena = {contrasena} }} -> Error: {e.Message}");
        }
    }
}
