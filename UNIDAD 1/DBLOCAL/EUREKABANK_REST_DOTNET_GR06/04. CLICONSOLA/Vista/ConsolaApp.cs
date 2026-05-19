using CLICONSOLA.Controlador;
using CLICONSOLA.Models;
using CLICONSOLA.Util;
using System.Globalization;

namespace CLICONSOLA.Vista;

public static class ConsolaApp
{
    private static BancoController _controller = null!;

    public static async Task Run(BancoController controller)
    {
        _controller = controller;

        while (true)
        {
            if (!_controller.LoggedIn)
            {
                await MostrarLogin();
            }
            else
            {
                await MostrarMenuPrincipal();
            }
        }
    }

    private static async Task MostrarLogin()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        EUREKA BANK - LOGIN");
        Console.WriteLine("========================================");
        Console.WriteLine();
        Console.WriteLine("Escriba 0 en Usuario para salir.");
        Console.Write("Usuario: ");
        string usuario = Console.ReadLine() ?? string.Empty;

        if (usuario == "0")
        {
            Environment.Exit(0);
        }

        if (string.IsNullOrWhiteSpace(usuario))
        {
            MostrarError("El usuario no puede estar vacio.");
            return;
        }

        Console.Write("Clave: ");
        string clave = LeerClave();

        bool exitoso = await _controller.Login(usuario, clave);

        if (exitoso)
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine($"Bienvenido {_controller.CurrentUser}{(_controller.IsAdmin ? " (Administrador)" : string.Empty)}");
            Console.ResetColor();
            Console.WriteLine("Presione cualquier tecla para continuar...");
            Console.ReadKey();
        }
        else
        {
            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Credenciales incorrectas o error de conexion.");
            Console.ResetColor();
            Console.WriteLine("Presione cualquier tecla para intentar de nuevo...");
            Console.ReadKey();
        }
    }

    private static string LeerClave()
    {
        string clave = string.Empty;
        while (true)
        {
            ConsoleKeyInfo key = Console.ReadKey(true);
            if (key.Key == ConsoleKey.Enter)
            {
                Console.WriteLine();
                break;
            }
            if (key.Key == ConsoleKey.Backspace)
            {
                if (clave.Length > 0)
                {
                    clave = clave.Substring(0, clave.Length - 1);
                    Console.Write("\b \b");
                }
            }
            else if (!char.IsControl(key.KeyChar))
            {
                clave += key.KeyChar;
                Console.Write("*");
            }
        }

        return clave;
    }

    private static string LeerTextoObligatorio(string etiqueta)
    {
        while (true)
        {
            Console.Write($"{etiqueta}: ");
            string valor = Console.ReadLine()?.Trim() ?? string.Empty;
            if (!string.IsNullOrWhiteSpace(valor))
            {
                return valor;
            }

            MostrarError($"{etiqueta} no puede estar vacio.");
        }
    }

    private static string LeerOpcion(string etiqueta, params string[] permitidas)
    {
        while (true)
        {
            Console.Write($"{etiqueta}: ");
            string valor = Console.ReadLine()?.Trim() ?? string.Empty;
            if (Array.Exists(permitidas, x => x == valor))
            {
                return valor;
            }

            MostrarError($"Opcion invalida. Usa: {string.Join(", ", permitidas)}");
        }
    }

    private static decimal LeerDecimalPositivo(string etiqueta)
    {
        while (true)
        {
            Console.Write($"{etiqueta}: ");
            string texto = Console.ReadLine()?.Trim() ?? string.Empty;
            if (decimal.TryParse(texto.Replace(',', '.'), NumberStyles.Number, CultureInfo.InvariantCulture, out decimal valor) && valor > 0)
            {
                return valor;
            }

            MostrarError("Ingresa un numero valido mayor que cero.");
        }
    }

    private static string LeerMoneda()
    {
        Console.WriteLine("Moneda del monto: 1) Dolares  2) Soles");
        return LeerOpcion("Opcion [1]", "1", "2") == "2" ? "01" : "02";
    }

    private static async Task MostrarMenuPrincipal()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine($"  EUREKA BANK - {_controller.CurrentUser}");
        Console.WriteLine($"  Rol: {(_controller.IsAdmin ? "Administrador" : "Cliente")}");
        Console.WriteLine("========================================");
        Console.WriteLine();

        if (_controller.IsAdmin)
        {
            Console.WriteLine("1. Depositar");
            Console.WriteLine("2. Retirar");
            Console.WriteLine("3. Consultar Saldo");
            Console.WriteLine("4. Transferir");
            Console.WriteLine("5. Listar Cuentas");
            Console.WriteLine("6. Listar Clientes");
            Console.WriteLine("7. Registrar Cliente");
            Console.WriteLine("8. Registrar Cuenta");
            Console.WriteLine("9. Eliminar Cuenta");
            Console.WriteLine("10. Movimientos");
            Console.WriteLine("0. Salir (Logout)");
        }
        else
        {
            Console.WriteLine("1. Retirar");
            Console.WriteLine("2. Consultar Saldo");
            Console.WriteLine("3. Transferir");
            Console.WriteLine("4. Mis Cuentas");
            Console.WriteLine("5. Movimientos");
            Console.WriteLine("0. Salir (Logout)");
        }

        Console.WriteLine();
        Console.Write("Seleccione una opcion: ");
        string opcion = Console.ReadLine() ?? string.Empty;

        if (_controller.IsAdmin)
        {
            await EjecutarOpcionAdmin(opcion);
        }
        else
        {
            await EjecutarOpcionCliente(opcion);
        }
    }

    private static async Task EjecutarOpcionAdmin(string opcion)
    {
        switch (opcion)
        {
            case "1":
                await OpcionDepositar();
                break;
            case "2":
                await OpcionRetirar();
                break;
            case "3":
                await OpcionConsultarSaldo();
                break;
            case "4":
                await OpcionTransferir();
                break;
            case "5":
                await OpcionListarCuentas();
                break;
            case "6":
                await OpcionListarClientes();
                break;
            case "7":
                await OpcionRegistrarCliente();
                break;
            case "8":
                await OpcionRegistrarCuenta();
                break;
            case "9":
                await OpcionEliminarCuenta();
                break;
            case "10":
                await OpcionMovimientos();
                break;
            case "0":
                _controller.Logout();
                break;
            default:
                MostrarError("Opcion no valida.");
                break;
        }
    }

    private static async Task EjecutarOpcionCliente(string opcion)
    {
        switch (opcion)
        {
            case "1":
                await OpcionRetirar();
                break;
            case "2":
                await OpcionConsultarSaldo();
                break;
            case "3":
                await OpcionTransferir();
                break;
            case "4":
                await OpcionListarCuentas();
                break;
            case "5":
                await OpcionMovimientos();
                break;
            case "0":
                _controller.Logout();
                break;
            default:
                MostrarError("Opcion no valida.");
                break;
        }
    }

    private static async Task OpcionDepositar()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        DEPOSITAR");
        Console.WriteLine("========================================");
        string cuenta = LeerTextoObligatorio("Cuenta");
        decimal monto = LeerDecimalPositivo("Monto");
        string moneda = LeerMoneda();

        var resultado = await _controller.Depositar(cuenta, monto.ToString(CultureInfo.InvariantCulture), moneda);
        MostrarResultado(resultado);
    }

    private static async Task OpcionRetirar()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        RETIRAR");
        Console.WriteLine("========================================");
        string cuenta = LeerTextoObligatorio("Cuenta");
        if (!await PerteneceACliente(_controller, cuenta))
        {
            MostrarError("La cuenta no pertenece al usuario.");
            return;
        }
        decimal monto = LeerDecimalPositivo("Monto");
        string moneda = LeerMoneda();

        var resultado = await _controller.Retirar(cuenta, monto.ToString(CultureInfo.InvariantCulture), moneda);
        MostrarResultado(resultado);
    }

    private static async Task OpcionConsultarSaldo()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        CONSULTAR SALDO");
        Console.WriteLine("========================================");
        string cuenta = LeerTextoObligatorio("Cuenta");
        if (!await PerteneceACliente(_controller, cuenta))
        {
            MostrarError("La cuenta no pertenece al usuario.");
            return;
        }

        var resultado = await _controller.ConsultarSaldo(cuenta);
        MostrarResultado(resultado);
    }

    private static async Task OpcionTransferir()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        TRANSFERIR");
        Console.WriteLine("========================================");
        string origen = LeerTextoObligatorio("Cuenta origen");
        string destino = LeerTextoObligatorio("Cuenta destino");
        if (!await PerteneceACliente(_controller, origen))
        {
            MostrarError("La cuenta origen no pertenece al usuario.");
            return;
        }

        decimal monto = LeerDecimalPositivo("Monto");
        string moneda = LeerMoneda();

        if (origen.Equals(destino, StringComparison.OrdinalIgnoreCase))
        {
            MostrarError("La cuenta origen y destino no pueden ser iguales.");
            return;
        }

        var resultado = await _controller.Transferir(origen, destino, monto.ToString(CultureInfo.InvariantCulture), moneda);
        MostrarResultado(resultado);
    }

    private static async Task OpcionListarCuentas()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        LISTAR CUENTAS");
        Console.WriteLine("========================================");
        Console.WriteLine();

        string cliente = _controller.IsAdmin ? ReadOptional("Cliente/DNI") : _controller.ClienteAsignado;
        var cuentas = await _controller.ListarCuentas(cliente);

        if (cuentas.Count == 0)
        {
            Console.WriteLine("No se encontraron cuentas.");
        }
        else
        {
            Console.WriteLine($"{"Codigo",-15} {"Moneda",-12} {"Saldo",12} {"Estado",-10} {"Cliente"}");
            Console.WriteLine(new string('-', 80));
            foreach (var c in cuentas)
            {
                Console.WriteLine($"{c.CodigoCuenta,-15} {Moneda.Nombre(c.Moneda),-12} {c.Saldo,12:F2} {c.Estado,-10} {c.NombreCliente}");
            }

            Console.WriteLine();
            Console.WriteLine($"Total: {cuentas.Count} cuenta(s)");
        }

        Console.WriteLine();
        Console.WriteLine("Presione cualquier tecla para continuar...");
        Console.ReadKey();
    }

    private static async Task OpcionListarClientes()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        LISTAR CLIENTES");
        Console.WriteLine("========================================");
        Console.WriteLine();

        var clientes = await _controller.ListarClientes();

        if (clientes.Count == 0)
        {
            Console.WriteLine("No se encontraron clientes.");
        }
        else
        {
            Console.WriteLine($"{"Codigo",-12} {"DNI",-12} {"Nombre"}");
            Console.WriteLine(new string('-', 60));
            foreach (var c in clientes)
            {
                Console.WriteLine($"{c.Codigo,-12} {c.Dni,-12} {c.Nombre}");
            }

            Console.WriteLine();
            Console.WriteLine($"Total: {clientes.Count} cliente(s)");
        }

        Console.WriteLine();
        Console.WriteLine("Presione cualquier tecla para continuar...");
        Console.ReadKey();
    }

    private static async Task OpcionRegistrarCliente()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        REGISTRAR CLIENTE");
        Console.WriteLine("========================================");
        string paterno = ReadOptional("Apellido Paterno");
        string materno = ReadOptional("Apellido Materno");
        string nombre = ReadOptional("Nombre");
        string dni = ReadOptional("DNI");
        string ciudad = ReadOptional("Ciudad");
        string direccion = ReadOptional("Direccion");
        string telefono = ReadOptional("Telefono");
        string email = ReadOptional("Email");

        var resultado = await _controller.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
        MostrarResultado(resultado);
    }

    private static async Task OpcionRegistrarCuenta()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        REGISTRAR CUENTA");
        Console.WriteLine("========================================");
        string cliente = LeerTextoObligatorio("Codigo Cliente");
        string moneda = LeerMoneda();

        var resultado = await _controller.RegistrarCuenta(cliente, moneda);
        MostrarResultado(resultado);
    }

    private static async Task OpcionEliminarCuenta()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        ELIMINAR CUENTA");
        Console.WriteLine("========================================");
        string cuenta = LeerTextoObligatorio("Codigo Cuenta");

        var resultado = await _controller.EliminarCuenta(cuenta);
        MostrarResultado(resultado);
    }

    private static async Task OpcionMovimientos()
    {
        Console.Clear();
        Console.WriteLine("========================================");
        Console.WriteLine("        MOVIMIENTOS");
        Console.WriteLine("========================================");
        string cuenta = LeerTextoObligatorio("Cuenta");
        if (!await PerteneceACliente(_controller, cuenta))
        {
            MostrarError("La cuenta no pertenece al usuario.");
            return;
        }

        var movimientos = await _controller.ListarMovimientos(cuenta);

        Console.WriteLine();
        if (movimientos.Count == 0)
        {
            Console.WriteLine("No se encontraron movimientos.");
        }
        else
        {
            Console.WriteLine($"{"Nro",-6} {"Fecha",-12} {"Tipo",-15} {"Importe",12}");
            Console.WriteLine(new string('-', 60));
            foreach (var m in movimientos)
            {
                Console.WriteLine($"{m.NumeroMovimiento,-6} {m.FechaMovimiento,-12} {m.TipoDescripcion,-15} {m.ImporteMovimiento,12:F2}");
            }

            Console.WriteLine();
            Console.WriteLine($"Total: {movimientos.Count} movimiento(s)");
        }

        Console.WriteLine();
        Console.WriteLine("Presione cualquier tecla para continuar...");
        Console.ReadKey();
    }

    private static async Task<bool> PerteneceACliente(BancoController c, string cuenta)
    {
        if (c.IsAdmin)
        {
            return true;
        }

        var cuentas = await c.ListarCuentas(c.ClienteAsignado);
        return cuentas.Any(x => x.CodigoCuenta.Equals(cuenta, StringComparison.OrdinalIgnoreCase));
    }

    private static void MostrarResultado(Resultado resultado)
    {
        Console.WriteLine();
        if (resultado.Exitoso)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine($"EXITO: {resultado.Mensaje}");
            if (resultado.Saldo > 0)
            {
                Console.WriteLine($"Saldo actual: {resultado.Saldo:F2}");
            }
            Console.ResetColor();
        }
        else
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"ERROR: {resultado.Mensaje}");
            Console.ResetColor();
        }

        Console.WriteLine();
        Console.WriteLine("Presione cualquier tecla para continuar...");
        Console.ReadKey();
    }

    private static void MostrarError(string mensaje)
    {
        Console.WriteLine();
        Console.ForegroundColor = ConsoleColor.Yellow;
        Console.WriteLine(mensaje);
        Console.ResetColor();
        Console.WriteLine("Presione cualquier tecla para continuar...");
        Console.ReadKey();
    }

    private static string ReadOptional(string etiqueta)
    {
        Console.Write($"{etiqueta}: ");
        return Console.ReadLine()?.Trim() ?? string.Empty;
    }
}
