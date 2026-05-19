using cliente_consola_rest_dotnet_ticketpremium_gr06.Servicios;
using cliente_consola_rest_dotnet_ticketpremium_gr06.Modelo;
using System.Text.RegularExpressions;

const string apiBaseUrl = "https://localhost:44348/";

using var servicio = new ClienteTicketPremiumService(apiBaseUrl);

Console.Title = "TicketPremium - Partidos Ecuatorianos";

var usuarioActual = await MostrarAutenticacionAsync(servicio);

while (true)
{
    Console.Clear();
    MostrarEncabezado();
    Console.WriteLine($"Sesion: {usuarioActual}");
    Console.WriteLine();
    Console.WriteLine("1. Ver partidos disponibles");
    Console.WriteLine("2. Ver localidades de un partido");
    Console.WriteLine("3. Comprar boletos");
    Console.WriteLine("4. Ver reporte de ventas");
    Console.WriteLine("5. Cerrar sesion");
    Console.WriteLine("0. Salir");
    var opcion = LeerOpcion("Seleccione una opcion: ", new HashSet<string> { "0", "1", "2", "3", "4", "5" });

    try
    {
        switch (opcion)
        {
            case "1":
                await MostrarPartidosAsync(servicio);
                break;
            case "2":
                await MostrarLocalidadesAsync(servicio);
                break;
            case "3":
                await ComprarBoletosAsync(servicio);
                break;
            case "4":
                await MostrarReporteAsync(servicio);
                break;
            case "5":
                usuarioActual = await MostrarAutenticacionAsync(servicio);
                break;
            case "0":
                return;
            default:
                Console.WriteLine("Opcion invalida.");
                break;
        }
    }
    catch (Exception ex)
    {
        Console.ForegroundColor = ConsoleColor.Red;
        Console.WriteLine(ex.Message);
        Console.ResetColor();
    }

    Console.WriteLine();
    Console.Write("Presione ENTER para continuar...");
    Console.ReadLine();
}

static void MostrarEncabezado()
{
    Console.ForegroundColor = ConsoleColor.Cyan;
    Console.WriteLine("===========================================");
    Console.WriteLine("  TICKETPREMIUM - EVENTOS ECUATORIANOS  ");
    Console.WriteLine("===========================================");
    Console.ResetColor();
}

static async Task<string> MostrarAutenticacionAsync(ClienteTicketPremiumService servicio)
{
    while (true)
    {
        Console.Clear();
        MostrarEncabezado();
        Console.WriteLine("1. Iniciar sesion");
        Console.WriteLine("2. Registrarse");
        Console.WriteLine("0. Salir");
        var opcion = LeerOpcion("Seleccione una opcion: ", new HashSet<string> { "0", "1", "2" });
        try
        {
            switch (opcion)
            {
                case "1":
                    return await IniciarSesionAsync(servicio);
                case "2":
                    await RegistrarUsuarioAsync(servicio);
                    break;
                case "0":
                    Environment.Exit(0);
                    break;
                default:
                    Console.WriteLine("Opcion invalida.");
                    break;
            }
        }
        catch (Exception ex)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine(ex.Message);
            Console.ResetColor();
            Console.WriteLine("Presione ENTER para continuar...");
            Console.ReadLine();
        }
    }
}

static async Task<string> IniciarSesionAsync(ClienteTicketPremiumService servicio)
{
    var usuario = PedirUsuario("Usuario: ");
    var password = LeerContrasenaOculta("Contrasena: ");

    var respuesta = await servicio.IniciarSesionAsync(new AutenticacionRequest
    {
        Usuario = usuario,
        Password = password
    });

    if (!respuesta.Exito)
    {
        throw new InvalidOperationException(respuesta.Mensaje);
    }

    Console.WriteLine(respuesta.Mensaje);
    Console.WriteLine("Presione ENTER para continuar...");
    Console.ReadLine();
    return respuesta.NombreCompleto;
}

static async Task RegistrarUsuarioAsync(ClienteTicketPremiumService servicio)
{
    Console.WriteLine();
    Console.WriteLine("REGISTRO DE USUARIO");
    var nombre = PedirNombreCompleto("Nombre completo: ");
    var usuario = PedirUsuario("Usuario: ");
    var correo = PedirCorreo("Correo: ");
    var password = LeerContrasenaOculta("Contrasena: ");
    var confirmar = LeerContrasenaOculta("Confirmar contrasena: ");

    var respuesta = await servicio.RegistrarUsuarioAsync(new RegistroUsuarioRequest
    {
        NombreCompleto = nombre,
        Usuario = usuario,
        Correo = correo,
        Password = password,
        ConfirmarPassword = confirmar
    });

    if (!respuesta.Exito)
    {
        Console.WriteLine(respuesta.Mensaje);
        Console.WriteLine("Presione ENTER para continuar...");
        Console.ReadLine();
        return;
    }

    Console.WriteLine(respuesta.Mensaje);
    Console.WriteLine("Presione ENTER para continuar...");
    Console.ReadLine();
}

static async Task MostrarPartidosAsync(ClienteTicketPremiumService servicio)
{
    var partidos = await servicio.ObtenerPartidosAsync();
    Console.WriteLine();
    Console.WriteLine("PARTIDOS ECUATORIANOS DISPONIBLES");
    Console.WriteLine("COD | FECHA                | LOCAL               | VISITA              | LUGAR");
    Console.WriteLine("----+----------------------+---------------------+---------------------+---------------------");

    foreach (var partido in partidos)
    {
        Console.WriteLine($"{partido.Codigo,3} | {partido.Fecha:dd/MM/yyyy HH:mm} | {partido.EquipoLocal,-19} | {partido.EquipoVisita,-19} | {partido.Lugar}");
    }
}

static async Task MostrarLocalidadesAsync(ClienteTicketPremiumService servicio)
{
    var codigoPartido = PedirEntero("Codigo del partido: ");
    var localidades = await servicio.ObtenerLocalidadesAsync(codigoPartido);

    Console.WriteLine();
    Console.WriteLine("LOCALIDADES DISPONIBLES");
    Console.WriteLine("LOCALIDAD        | DISPONIBLE | PRECIO");
    Console.WriteLine("-----------------+------------+--------");

    foreach (var localidad in localidades)
    {
        Console.WriteLine($"{localidad.CodigoLocalidad,-16} | {localidad.Disponibilidad,10} | {localidad.Precio,6:N2}");
    }
}

static async Task ComprarBoletosAsync(ClienteTicketPremiumService servicio)
{
    var codigoPartido = PedirEntero("Codigo del partido: ");
    var codigoLocalidad = PedirCodigoLocalidad("Codigo de localidad (GENERAL, TRIBUNA, PALCO, VIP): ");
    var cantidad = PedirEntero("Cantidad: ");

    var respuesta = await servicio.ComprarAsync(new CompraRequest
    {
        CodigoPartido = codigoPartido,
        CodigoLocalidad = codigoLocalidad,
        Cantidad = cantidad
    });

    Console.ForegroundColor = ConsoleColor.Green;
    Console.WriteLine();
    Console.WriteLine("COMPRA REGISTRADA");
    Console.WriteLine($"Factura: {respuesta.IdFactura}");
    Console.WriteLine($"Subtotal: {respuesta.Subtotal:N2}");
    Console.WriteLine($"IVA:      {respuesta.Iva:N2}");
    Console.WriteLine($"Total:    {respuesta.Total:N2}");
    Console.WriteLine($"Restante: {respuesta.DisponibilidadRestante}");
    Console.ResetColor();
}

static async Task MostrarReporteAsync(ClienteTicketPremiumService servicio)
{
    var codigoPartido = PedirEntero("Codigo del partido: ");
    var resumen = await servicio.ObtenerReporteAsync(codigoPartido);

    Console.WriteLine();
    Console.WriteLine("REPORTE DE VENTAS POR PARTIDO");
    Console.WriteLine("LOCALIDAD        | VENDIDOS | TOTAL");
    Console.WriteLine("-----------------+----------+--------");

    foreach (var item in resumen)
    {
        Console.WriteLine($"{item.Localidad,-16} | {item.Vendidos,8} | {item.TotalRecaudado,6:N2}");
    }
}

static int PedirEntero(string mensaje)
{
    while (true)
    {
        Console.Write(mensaje);
        if (int.TryParse(Console.ReadLine(), out var valor) && valor > 0)
        {
            return valor;
        }

        Console.WriteLine("Ingrese un numero valido.");
    }
}

static string PedirNombreCompleto(string mensaje)
{
    while (true)
    {
        Console.Write(mensaje);
        var valor = Console.ReadLine()?.Trim() ?? string.Empty;
        if (EsNombreValido(valor))
        {
            return valor;
        }

        Console.WriteLine("El nombre solo puede contener letras y espacios.");
    }
}

static string PedirUsuario(string mensaje)
{
    while (true)
    {
        Console.Write(mensaje);
        var valor = Console.ReadLine()?.Trim() ?? string.Empty;
        if (EsUsuarioValido(valor))
        {
            return valor;
        }

        Console.WriteLine("El usuario solo puede contener letras, numeros y guion bajo.");
    }
}

static string PedirCorreo(string mensaje)
{
    while (true)
    {
        Console.Write(mensaje);
        var valor = Console.ReadLine()?.Trim() ?? string.Empty;
        if (EsCorreoValido(valor))
        {
            return valor;
        }

        Console.WriteLine("Ingrese un correo valido.");
    }
}

static string PedirCodigoLocalidad(string mensaje)
{
    var permitidos = new HashSet<string>(StringComparer.OrdinalIgnoreCase)
    {
        "GENERAL", "TRIBUNA", "PALCO", "VIP"
    };

    while (true)
    {
        Console.Write(mensaje);
        var valor = Console.ReadLine()?.Trim() ?? string.Empty;
        if (permitidos.Contains(valor))
        {
            return valor.ToUpperInvariant();
        }

        Console.WriteLine("Codigo de localidad invalido. Use GENERAL, TRIBUNA, PALCO o VIP.");
    }
}

static string LeerContrasenaOculta(string mensaje)
{
    Console.Write(mensaje);

    var password = string.Empty;
    while (true)
    {
        var key = Console.ReadKey(intercept: true);

        if (key.Key == ConsoleKey.Enter)
        {
            Console.WriteLine();
            return password;
        }

        if (key.Key == ConsoleKey.Backspace)
        {
            if (password.Length > 0)
            {
                password = password[..^1];
                Console.Write("\b \b");
            }

            continue;
        }

        if (char.IsControl(key.KeyChar))
        {
            continue;
        }

        password += key.KeyChar;
        Console.Write('*');
    }
}

static string LeerOpcion(string mensaje, IReadOnlySet<string> opcionesValidas)
{
    while (true)
    {
        Console.Write(mensaje);
        var opcion = Console.ReadLine()?.Trim() ?? string.Empty;
        if (opcionesValidas.Contains(opcion))
        {
            return opcion;
        }

        Console.WriteLine("Opcion invalida. Intente de nuevo.");
    }
}

static bool EsNombreValido(string valor)
{
    if (string.IsNullOrWhiteSpace(valor))
    {
        return false;
    }

    return Regex.IsMatch(valor, @"^[\p{L}\s]+$");
}

static bool EsUsuarioValido(string valor)
{
    if (string.IsNullOrWhiteSpace(valor))
    {
        return false;
    }

    return Regex.IsMatch(valor, @"^[A-Za-z0-9_]{3,20}$");
}

static bool EsCorreoValido(string valor)
{
    return !string.IsNullOrWhiteSpace(valor) && Regex.IsMatch(valor, @"^[^\s@]+@[^\s@]+\.[^\s@]+$");
}
