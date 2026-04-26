using Ec.Edu.Monster.Modelo;
using Ec.Edu.Monster.Servicio;
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
        vista.MostrarMensaje("Ingrese sus credenciales para usar el servicio REST.");

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
            vista.MostrarResultado(new Resultado { Exito = false, Mensaje = $"No se pudo conectar al servidor REST: {ex.Message}" });
        }
    }

    private bool IniciarSesion()
    {
        while (true)
        {
            var usuario = LeerTextoObligatorio("Usuario");
            var contrasena = LeerTextoObligatorio("Contrasena", ocultar: true);

            var resultado = autenticacion.Autenticar(usuario, contrasena);
            if (resultado.Exito)
            {
                vista.MostrarResultado(resultado);
                return true;
            }

            vista.MostrarResultado(resultado);
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
            "1" => FormatearLongitud(valor, longitud.MetrosAPies(valor), "metros", "pies"),
            "2" => FormatearLongitud(valor, longitud.KilometrosAMillas(valor), "kilometros", "millas"),
            "3" => FormatearLongitud(valor, longitud.CentimetrosAPulgadas(valor), "centimetros", "pulgadas"),
            "4" => FormatearLongitud(valor, longitud.YardasAMetros(valor), "yardas", "metros"),
            "5" => FormatearLongitud(valor, longitud.MilimetrosAPulgadas(valor), "milimetros", "pulgadas"),
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
            "1" => FormatearMasa(valor, masa.KilogramosALibras(valor), "kilogramos", "libras"),
            "2" => FormatearMasa(valor, masa.GramosAOnzas(valor), "gramos", "onzas"),
            "3" => FormatearMasa(valor, masa.ToneladasAKilogramos(valor), "toneladas", "kilogramos"),
            "4" => FormatearMasa(valor, masa.LibrasAOnzas(valor), "libras", "onzas"),
            "5" => FormatearMasa(valor, masa.MiligramosAGramos(valor), "miligramos", "gramos"),
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
            "1" => FormatearTemperatura(valor, temperatura.CelsiusAFahrenheit(valor), "celsius", "fahrenheit"),
            "2" => FormatearTemperatura(valor, temperatura.FahrenheitACelsius(valor), "fahrenheit", "celsius"),
            "3" => FormatearTemperatura(valor, temperatura.CelsiusAKelvin(valor), "celsius", "kelvin"),
            "4" => FormatearTemperatura(valor, temperatura.KelvinACelsius(valor), "kelvin", "celsius"),
            "5" => FormatearTemperatura(valor, temperatura.FahrenheitAKelvin(valor), "fahrenheit", "kelvin"),
            _ => new Resultado { Exito = false, Mensaje = "Conversion no valida" }
        };
    }

    private Resultado FormatearLongitud(double entrada, Resultado resultado, string origen, string destino)
    {
        return new Resultado
        {
            Exito = resultado.Exito,
            Valor = resultado.Valor,
            Mensaje = resultado.Exito ? $"{entrada:0.##} {origen} = {resultado.Valor:0.000} {destino}" : resultado.Mensaje
        };
    }

    private Resultado FormatearMasa(double entrada, Resultado resultado, string origen, string destino)
    {
        return new Resultado
        {
            Exito = resultado.Exito,
            Valor = resultado.Valor,
            Mensaje = resultado.Exito ? $"{entrada:0.##} {origen} = {resultado.Valor:0.000} {destino}" : resultado.Mensaje
        };
    }

    private Resultado FormatearTemperatura(double entrada, Resultado resultado, string origen, string destino)
    {
        return new Resultado
        {
            Exito = resultado.Exito,
            Valor = resultado.Valor,
            Mensaje = resultado.Exito ? $"{entrada:0.##} {origen} = {resultado.Valor:0.000} {destino}" : resultado.Mensaje
        };
    }

    private string LeerTextoObligatorio(string etiqueta, bool ocultar = false)
    {
        while (true)
        {
            var texto = ocultar ? LeerContrasena($"{etiqueta}: ") : LeerLinea($"{etiqueta}: ");
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
