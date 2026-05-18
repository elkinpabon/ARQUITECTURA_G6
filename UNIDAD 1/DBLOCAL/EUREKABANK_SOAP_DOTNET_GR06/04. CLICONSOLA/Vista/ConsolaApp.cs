using CLICONSOLA.Controlador;
using CLICONSOLA.Util;

namespace CLICONSOLA.Vista
{
    public static class ConsolaApp
    {
        private static BancoController _controller = null!;

        public static void Ejecutar(BancoController controller)
        {
            _controller = controller;

            while (true)
            {
                if (!_controller.LoggedIn)
                {
                    MostrarLogin();
                }
                else
                {
                    MostrarMenuPrincipal();
                }
            }
        }

        private static void MostrarLogin()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        EUREKA BANK - LOGIN");
            Console.WriteLine("========================================");
            Console.WriteLine();
            Console.Write("Usuario: ");
            string usuario = Console.ReadLine() ?? string.Empty;

            if (string.IsNullOrWhiteSpace(usuario))
                return;

            Console.Write("Clave: ");
            string clave = LeerClave();

            bool exitoso = _controller.Login(usuario, clave);

            if (exitoso)
            {
                Console.WriteLine();
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine($"Bienvenido {_controller.CurrentUser}{(_controller.IsAdmin ? " (Administrador)" : "")}");
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
                else
                {
                    clave += key.KeyChar;
                    Console.Write("*");
                }
            }
            return clave;
        }

        private static void MostrarMenuPrincipal()
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
                EjecutarOpcionAdmin(opcion);
            }
            else
            {
                EjecutarOpcionCliente(opcion);
            }
        }

        private static void EjecutarOpcionAdmin(string opcion)
        {
            switch (opcion)
            {
                case "1":
                    OpcionDepositar();
                    break;
                case "2":
                    OpcionRetirar();
                    break;
                case "3":
                    OpcionConsultarSaldo();
                    break;
                case "4":
                    OpcionTransferir();
                    break;
                case "5":
                    OpcionListarCuentas();
                    break;
                case "6":
                    OpcionListarClientes();
                    break;
                case "7":
                    OpcionRegistrarCliente();
                    break;
                case "8":
                    OpcionRegistrarCuenta();
                    break;
                case "9":
                    OpcionEliminarCuenta();
                    break;
                case "10":
                    OpcionMovimientos();
                    break;
                case "0":
                    _controller.Logout();
                    break;
                default:
                    MostrarError("Opcion no valida.");
                    break;
            }
        }

        private static void EjecutarOpcionCliente(string opcion)
        {
            switch (opcion)
            {
                case "1":
                    OpcionRetirar();
                    break;
                case "2":
                    OpcionConsultarSaldo();
                    break;
                case "3":
                    OpcionTransferir();
                    break;
                case "4":
                    OpcionListarCuentas();
                    break;
                case "5":
                    OpcionMovimientos();
                    break;
                case "0":
                    _controller.Logout();
                    break;
                default:
                    MostrarError("Opcion no valida.");
                    break;
            }
        }

        private static void OpcionDepositar()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        DEPOSITAR");
            Console.WriteLine("========================================");
            Console.Write("Cuenta: ");
            string cuenta = Console.ReadLine() ?? string.Empty;
            Console.Write("Monto: ");
            string monto = Console.ReadLine() ?? string.Empty;
            Console.Write("Moneda (01=Soles, 02=Dolares): ");
            string moneda = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.Depositar(cuenta, monto, moneda);
            MostrarResultado(resultado);
        }

        private static void OpcionRetirar()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        RETIRAR");
            Console.WriteLine("========================================");
            Console.Write("Cuenta: ");
            string cuenta = Console.ReadLine() ?? string.Empty;
            Console.Write("Monto: ");
            string monto = Console.ReadLine() ?? string.Empty;
            Console.Write("Moneda (01=Soles, 02=Dolares): ");
            string moneda = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.Retirar(cuenta, monto, moneda);
            MostrarResultado(resultado);
        }

        private static void OpcionConsultarSaldo()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        CONSULTAR SALDO");
            Console.WriteLine("========================================");
            Console.Write("Cuenta: ");
            string cuenta = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.ConsultarSaldo(cuenta);
            MostrarResultado(resultado);
        }

        private static void OpcionTransferir()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        TRANSFERIR");
            Console.WriteLine("========================================");
            Console.Write("Cuenta origen: ");
            string origen = Console.ReadLine() ?? string.Empty;
            Console.Write("Cuenta destino: ");
            string destino = Console.ReadLine() ?? string.Empty;
            Console.Write("Monto: ");
            string monto = Console.ReadLine() ?? string.Empty;
            Console.Write("Moneda (01=Soles, 02=Dolares): ");
            string moneda = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.Transferir(origen, destino, monto, moneda);
            MostrarResultado(resultado);
        }

        private static void OpcionListarCuentas()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        LISTAR CUENTAS");
            Console.WriteLine("========================================");
            Console.WriteLine();

            var cuentas = _controller.ListarCuentas();

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

        private static void OpcionListarClientes()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        LISTAR CLIENTES");
            Console.WriteLine("========================================");
            Console.WriteLine();

            var clientes = _controller.ListarClientes();

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

        private static void OpcionRegistrarCliente()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        REGISTRAR CLIENTE");
            Console.WriteLine("========================================");
            Console.Write("Apellido Paterno: ");
            string paterno = Console.ReadLine() ?? string.Empty;
            Console.Write("Apellido Materno: ");
            string materno = Console.ReadLine() ?? string.Empty;
            Console.Write("Nombre: ");
            string nombre = Console.ReadLine() ?? string.Empty;
            Console.Write("DNI: ");
            string dni = Console.ReadLine() ?? string.Empty;
            Console.Write("Ciudad: ");
            string ciudad = Console.ReadLine() ?? string.Empty;
            Console.Write("Direccion: ");
            string direccion = Console.ReadLine() ?? string.Empty;
            Console.Write("Telefono: ");
            string telefono = Console.ReadLine() ?? string.Empty;
            Console.Write("Email: ");
            string email = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
            MostrarResultado(resultado);
        }

        private static void OpcionRegistrarCuenta()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        REGISTRAR CUENTA");
            Console.WriteLine("========================================");
            Console.Write("Codigo Cliente: ");
            string cliente = Console.ReadLine() ?? string.Empty;
            Console.Write("Moneda (01=Soles, 02=Dolares): ");
            string moneda = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.RegistrarCuenta(cliente, moneda);
            MostrarResultado(resultado);
        }

        private static void OpcionEliminarCuenta()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        ELIMINAR CUENTA");
            Console.WriteLine("========================================");
            Console.Write("Codigo Cuenta: ");
            string cuenta = Console.ReadLine() ?? string.Empty;

            var resultado = _controller.EliminarCuenta(cuenta);
            MostrarResultado(resultado);
        }

        private static void OpcionMovimientos()
        {
            Console.Clear();
            Console.WriteLine("========================================");
            Console.WriteLine("        MOVIMIENTOS");
            Console.WriteLine("========================================");
            Console.Write("Cuenta: ");
            string cuenta = Console.ReadLine() ?? string.Empty;

            var movimientos = _controller.ListarMovimientos(cuenta);

            Console.WriteLine();
            if (movimientos.Count == 0)
            {
                Console.WriteLine("No se encontraron movimientos.");
            }
            else
            {
                Console.WriteLine($"{"Nro",-6} {"Fecha",-12} {"Tipo",-15} {"Importe",12} {"Moneda"}");
                Console.WriteLine(new string('-', 70));
                foreach (var m in movimientos)
                {
                    Console.WriteLine($"{m.NumeroMovimiento,-6} {m.FechaMovimiento,-12} {m.TipoDescripcion,-15} {m.ImporteMovimiento,12:F2} {Moneda.Nombre(m.MonedaOrigen)}");
                }
                Console.WriteLine();
                Console.WriteLine($"Total: {movimientos.Count} movimiento(s)");
            }

            Console.WriteLine();
            Console.WriteLine("Presione cualquier tecla para continuar...");
            Console.ReadKey();
        }

        private static void MostrarResultado(Servicio.Resultado resultado)
        {
            Console.WriteLine();
            if (resultado.Exitoso)
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine($"EXITO: {resultado.Mensaje}");
                if (resultado.Saldo > 0)
                    Console.WriteLine($"Saldo actual: {resultado.Saldo:F2}");
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
    }
}
