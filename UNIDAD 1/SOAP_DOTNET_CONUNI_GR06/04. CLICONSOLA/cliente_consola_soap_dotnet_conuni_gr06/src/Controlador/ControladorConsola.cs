using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Vista;
using System.Globalization;

namespace Ec.Edu.Monster.Controlador;

public class ControladorConsola
{
    private readonly VistaConsola vista = new();
    private readonly ServicioAutenticacion autenticacion = new();
    private readonly ServicioLongitud longitud = new();
    private readonly ServicioMasa masa = new();
    private readonly ServicioTemperatura temperatura = new();

    public void Ejecutar()
    {
        vista.MostrarEncabezado("CONUNI - Cliente de consola");
        vista.MostrarMensaje("Ingrese sus credenciales para usar el servicio SOAP.");

        try
        {
            if (!IniciarSesion())
            {
                return;
            }

            MenuConversiones();
        }
        catch (Exception ex)
        {
            vista.MostrarResultado(new Resultado { Exito = false, Mensaje = $"No se pudo conectar al servidor SOAP: {ex.Message}" });
        }
    }

    private bool IniciarSesion()
    {
        while (true)
        {
            var usuario = LeerTextoObligatorio("Usuario");

            var contrasena = LeerTextoObligatorio("Contrasena");

            try
            {
                var exito = autenticacion.Autenticar(usuario, contrasena);
                if (exito)
                {
                    vista.MostrarResultado(new Resultado { Exito = true, Mensaje = "Sesion iniciada" });
                    return true;
                }

                vista.MostrarResultado(new Resultado { Exito = false, Mensaje = "Credenciales invalidas. Intenta otra vez." });
            }
            catch (Exception ex)
            {
                vista.MostrarResultado(new Resultado { Exito = false, Mensaje = $"No se pudo conectar al servidor SOAP: {ex.Message}" });
                return false;
            }
        }
    }

    private void MenuConversiones()
    {
        while (true)
        {
            vista.MostrarLineaVacia();
            vista.MostrarEncabezado("Menu de conversiones");
            vista.MostrarOpcion(1, "Longitud");
            vista.MostrarOpcion(2, "Masa");
            vista.MostrarOpcion(3, "Temperatura");
            vista.MostrarOpcion(0, "Salir");

            var opcion = LeerOpcion("Opcion", "0", "1", "2", "3");
            if (opcion == "0")
            {
                vista.MostrarMensaje("Sesion finalizada.");
                return;
            }

            var valor = LeerValor("Valor");

            Resultado resultado = opcion switch
            {
                "1" => ElegirLongitud(valor),
                "2" => ElegirMasa(valor),
                "3" => ElegirTemperatura(valor),
                _ => new Resultado { Exito = false, Mensaje = "Opcion no valida" }
            };

            vista.MostrarResultado(resultado);
        }
    }

    private Resultado ElegirLongitud(double valor)
    {
        vista.MostrarEncabezado("Conversion de longitud");
        vista.MostrarOpcion(1, "Metros a Pies");
        vista.MostrarOpcion(2, "Kilometros a Millas");
        vista.MostrarOpcion(3, "Centimetros a Pulgadas");
        vista.MostrarOpcion(4, "Yardas a Metros");
        vista.MostrarOpcion(5, "Milimetros a Pulgadas");

        return LeerOpcion("Conversion", "1", "2", "3", "4", "5") switch
        {
            "1" => new Resultado { Exito = true, Valor = longitud.MetrosAPies(valor), Mensaje = $"{valor:0.##} metros = {longitud.MetrosAPies(valor):0.00} pies" },
            "2" => new Resultado { Exito = true, Valor = longitud.KilometrosAMillas(valor), Mensaje = $"{valor:0.##} kilometros = {longitud.KilometrosAMillas(valor):0.00} millas" },
            "3" => new Resultado { Exito = true, Valor = longitud.CentimetrosAPulgadas(valor), Mensaje = $"{valor:0.##} centimetros = {longitud.CentimetrosAPulgadas(valor):0.00} pulgadas" },
            "4" => new Resultado { Exito = true, Valor = longitud.YardasAMetros(valor), Mensaje = $"{valor:0.##} yardas = {longitud.YardasAMetros(valor):0.00} metros" },
            "5" => new Resultado { Exito = true, Valor = longitud.MilimetrosAPulgadas(valor), Mensaje = $"{valor:0.##} milimetros = {longitud.MilimetrosAPulgadas(valor):0.00} pulgadas" },
            _ => new Resultado { Exito = false, Mensaje = "Conversion no valida" }
        };
    }

    private Resultado ElegirMasa(double valor)
    {
        vista.MostrarEncabezado("Conversion de masa");
        vista.MostrarOpcion(1, "Kilogramos a Libras");
        vista.MostrarOpcion(2, "Gramos a Onzas");
        vista.MostrarOpcion(3, "Toneladas a Kilogramos");
        vista.MostrarOpcion(4, "Libras a Onzas");
        vista.MostrarOpcion(5, "Miligramos a Gramos");

        return LeerOpcion("Conversion", "1", "2", "3", "4", "5") switch
        {
            "1" => new Resultado { Exito = true, Valor = masa.KilogramosALibras(valor), Mensaje = $"{valor:0.##} kilogramos = {masa.KilogramosALibras(valor):0.00} libras" },
            "2" => new Resultado { Exito = true, Valor = masa.GramosAOnzas(valor), Mensaje = $"{valor:0.##} gramos = {masa.GramosAOnzas(valor):0.00} onzas" },
            "3" => new Resultado { Exito = true, Valor = masa.ToneladasAKilogramos(valor), Mensaje = $"{valor:0.##} toneladas = {masa.ToneladasAKilogramos(valor):0.00} kilogramos" },
            "4" => new Resultado { Exito = true, Valor = masa.LibrasAOnzas(valor), Mensaje = $"{valor:0.##} libras = {masa.LibrasAOnzas(valor):0.00} onzas" },
            "5" => new Resultado { Exito = true, Valor = masa.MiligramosAGramos(valor), Mensaje = $"{valor:0.##} miligramos = {masa.MiligramosAGramos(valor):0.00} gramos" },
            _ => new Resultado { Exito = false, Mensaje = "Conversion no valida" }
        };
    }

    private Resultado ElegirTemperatura(double valor)
    {
        vista.MostrarEncabezado("Conversion de temperatura");
        vista.MostrarOpcion(1, "Celsius a Fahrenheit");
        vista.MostrarOpcion(2, "Fahrenheit a Celsius");
        vista.MostrarOpcion(3, "Celsius a Kelvin");
        vista.MostrarOpcion(4, "Kelvin a Celsius");
        vista.MostrarOpcion(5, "Fahrenheit a Kelvin");

        return LeerOpcion("Conversion", "1", "2", "3", "4", "5") switch
        {
            "1" => new Resultado { Exito = true, Valor = temperatura.CelsiusAFahrenheit(valor), Mensaje = $"{valor:0.##} celsius = {temperatura.CelsiusAFahrenheit(valor):0.00} fahrenheit" },
            "2" => new Resultado { Exito = true, Valor = temperatura.FahrenheitACelsius(valor), Mensaje = $"{valor:0.##} fahrenheit = {temperatura.FahrenheitACelsius(valor):0.00} celsius" },
            "3" => new Resultado { Exito = true, Valor = temperatura.CelsiusAKelvin(valor), Mensaje = $"{valor:0.##} celsius = {temperatura.CelsiusAKelvin(valor):0.00} kelvin" },
            "4" => new Resultado { Exito = true, Valor = temperatura.KelvinACelsius(valor), Mensaje = $"{valor:0.##} kelvin = {temperatura.KelvinACelsius(valor):0.00} celsius" },
            "5" => new Resultado { Exito = true, Valor = temperatura.FahrenheitAKelvin(valor), Mensaje = $"{valor:0.##} fahrenheit = {temperatura.FahrenheitAKelvin(valor):0.00} kelvin" },
            _ => new Resultado { Exito = false, Mensaje = "Conversion no valida" }
        };
    }

    private string LeerTextoObligatorio(string etiqueta)
    {
        while (true)
        {
            var texto = etiqueta.Equals("Contrasena", StringComparison.OrdinalIgnoreCase)
                ? LeerContrasena($"{etiqueta}: ")
                : LeerLinea($"{etiqueta}: ");

            if (!string.IsNullOrWhiteSpace(texto))
            {
                return texto;
            }

            vista.MostrarResultado(new Resultado { Exito = false, Mensaje = $"{etiqueta} no puede estar vacio" });
        }
    }

    private string LeerLinea(string etiqueta)
    {
        Console.Write(etiqueta);
        return (Console.ReadLine() ?? string.Empty).Trim();
    }

    private string LeerContrasena(string etiqueta)
    {
        Console.Write(etiqueta);
        var contrasena = string.Empty;

        while (true)
        {
            var tecla = Console.ReadKey(intercept: true);

            if (tecla.Key == ConsoleKey.Enter)
            {
                Console.WriteLine();
                return contrasena;
            }

            if (tecla.Key == ConsoleKey.Backspace)
            {
                if (contrasena.Length > 0)
                {
                    contrasena = contrasena[..^1];
                    Console.Write("\b \b");
                }

                continue;
            }

            if (tecla.Key == ConsoleKey.Escape)
            {
                Console.WriteLine();
                return string.Empty;
            }

            if (!char.IsControl(tecla.KeyChar))
            {
                contrasena += tecla.KeyChar;
                Console.Write('*');
            }
        }
    }

    private string LeerOpcion(string etiqueta, params string[] permitidas)
    {
        while (true)
        {
            Console.Write($"{etiqueta}: ");
            var texto = Console.ReadLine()?.Trim() ?? string.Empty;
            if (Array.Exists(permitidas, valor => valor == texto))
            {
                return texto;
            }

            vista.MostrarResultado(new Resultado { Exito = false, Mensaje = $"Opcion invalida. Usa: {string.Join(", ", permitidas)}" });
        }
    }

    private double LeerValor(string etiqueta)
    {
        while (true)
        {
            Console.Write($"{etiqueta}: ");
            var texto = Console.ReadLine() ?? string.Empty;
            if (double.TryParse(texto.Replace(',', '.'), NumberStyles.Any, CultureInfo.InvariantCulture, out var valor))
            {
                return valor;
            }

            vista.MostrarResultado(new Resultado { Exito = false, Mensaje = "Valor invalido. Ingresa un numero valido." });
        }
    }
}
