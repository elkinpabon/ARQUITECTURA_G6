using CLICONSOLA.Controlador;

namespace CLICONSOLA.Vista;

public static class ConsolaApp
{
    public static async Task Run(BancoController ctrl)
    {
        Console.WriteLine("=== EurekaBank REST - Consola ===");
        Console.Write("URL servidor [http://localhost:5010]: ");
        string url = Console.ReadLine()?.Trim() ?? "http://localhost:5010";
        if (!string.IsNullOrEmpty(url)) ctrl = new BancoController(url);

        while (true)
        {
            Console.Write("\nUsuario: ");
            string usuario = Console.ReadLine()?.Trim() ?? "";
            Console.Write("Clave: ");
            string clave = ReadPassword();

            if (await ctrl.Login(usuario, clave))
            {
                Console.WriteLine($"\nBienvenido {ctrl.CurrentUser} ({(ctrl.IsAdmin ? "ADMIN" : "CLIENTE")})");
                await MenuPrincipal(ctrl);
            }
            else
            {
                Console.WriteLine("Usuario o clave incorrectos");
            }
        }
    }

    static async Task MenuPrincipal(BancoController ctrl)
    {
        while (ctrl.LoggedIn)
        {
            Console.WriteLine("\n--- Menu ---");
            if (ctrl.IsAdmin)
            {
                Console.WriteLine("1.Depositar 2.Retirar 3.Saldo 4.Transferir 5.Cuentas 6.Clientes 7.Reg.Cliente 8.Reg.Cuenta 9.Elim.Cuenta 10.Movimientos 0.Salir");
            }
            else
            {
                Console.WriteLine("1.Retirar 2.Saldo 3.Transferir 4.Mis Cuentas 5.Movimientos 0.Salir");
            }
            Console.Write("Opcion: ");
            string? op = Console.ReadLine()?.Trim();

            try
            {
                switch (op)
                {
                    case "0": ctrl.Logout(); return;
                    case "1" when ctrl.IsAdmin: await DoDepositar(ctrl); break;
                    case "1": await DoRetirar(ctrl); break;
                    case "2" when ctrl.IsAdmin: await DoRetirar(ctrl); break;
                    case "2": await DoSaldo(ctrl); break;
                    case "3" when ctrl.IsAdmin: await DoSaldo(ctrl); break;
                    case "3": await DoTransferir(ctrl); break;
                    case "4" when ctrl.IsAdmin: await DoTransferir(ctrl); break;
                    case "4": await DoMisCuentas(ctrl); break;
                    case "5" when ctrl.IsAdmin: await DoCuentas(ctrl); break;
                    case "5": await DoMovimientos(ctrl); break;
                    case "6" when ctrl.IsAdmin: await DoClientes(ctrl); break;
                    case "7" when ctrl.IsAdmin: await DoRegCliente(ctrl); break;
                    case "8" when ctrl.IsAdmin: await DoRegCuenta(ctrl); break;
                    case "9" when ctrl.IsAdmin: await DoElimCuenta(ctrl); break;
                    case "10" when ctrl.IsAdmin: await DoMovimientos(ctrl); break;
                }
            }
            catch (Exception ex) { Console.WriteLine($"Error: {ex.Message}"); }
        }
    }

    static async Task DoDepositar(BancoController c) { Console.Write("Cuenta: "); string cu = Console.ReadLine()!; Console.Write("Monto: "); string mo = Console.ReadLine()!; Console.Write("Moneda(01/02): "); string me = Console.ReadLine()!; var r = await c.Depositar(cu, mo, me); Console.WriteLine(r.Exitoso ? $"OK: {r.Mensaje} Saldo:{r.Saldo:F2}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoRetirar(BancoController c) { Console.Write("Cuenta: "); string cu = Console.ReadLine()!; Console.Write("Monto: "); string mo = Console.ReadLine()!; Console.Write("Moneda(01/02): "); string me = Console.ReadLine()!; var r = await c.Retirar(cu, mo, me); Console.WriteLine(r.Exitoso ? $"OK: {r.Mensaje} Saldo:{r.Saldo:F2}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoSaldo(BancoController c) { Console.Write("Cuenta: "); string cu = Console.ReadLine()!; var r = await c.ConsultarSaldo(cu); Console.WriteLine(r.Exitoso ? $"Saldo: {r.Saldo:F2}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoTransferir(BancoController c) { Console.Write("Origen: "); string o = Console.ReadLine()!; Console.Write("Destino: "); string d = Console.ReadLine()!; Console.Write("Monto: "); string m = Console.ReadLine()!; Console.Write("Moneda(01/02): "); string me = Console.ReadLine()!; var r = await c.Transferir(o, d, m, me); Console.WriteLine(r.Exitoso ? $"OK: {r.Mensaje} Saldo:{r.Saldo:F2}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoCuentas(BancoController c) { Console.Write("Cliente/DNI: "); string cl = Console.ReadLine()!; var list = await c.ListarCuentas(cl); list.ForEach(x => Console.WriteLine($"{x.CodigoCuenta} | {x.Moneda} | {x.Saldo:F2} | {x.Estado} | {x.NombreCliente}")); }
    static async Task DoMisCuentas(BancoController c) { var list = await c.ListarCuentas(c.ClienteAsignado); list.ForEach(x => Console.WriteLine($"{x.CodigoCuenta} | {x.Moneda} | {x.Saldo:F2} | {x.Estado}")); }
    static async Task DoClientes(BancoController c) { var list = await c.ListarClientes(); list.ForEach(x => Console.WriteLine($"{x.Codigo} | {x.Dni} | {x.Nombre}")); }
    static async Task DoRegCliente(BancoController c) { Console.Write("Paterno: "); string p = Console.ReadLine()!; Console.Write("Materno: "); string m = Console.ReadLine()!; Console.Write("Nombre: "); string n = Console.ReadLine()!; Console.Write("DNI: "); string d = Console.ReadLine()!; Console.Write("Ciudad: "); string ci = Console.ReadLine()!; Console.Write("Direccion: "); string di = Console.ReadLine()!; Console.Write("Telefono: "); string t = Console.ReadLine()!; Console.Write("Email: "); string e = Console.ReadLine()!; var r = await c.RegistrarCliente(p, m, n, d, ci, di, t, e); Console.WriteLine(r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoRegCuenta(BancoController c) { Console.Write("Cliente: "); string cl = Console.ReadLine()!; Console.Write("Moneda(01/02): "); string me = Console.ReadLine()!; var r = await c.RegistrarCuenta(cl, me); Console.WriteLine(r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoElimCuenta(BancoController c) { Console.Write("Cuenta: "); string cu = Console.ReadLine()!; var r = await c.EliminarCuenta(cu); Console.WriteLine(r.Exitoso ? $"OK: {r.Mensaje}" : $"ERROR: {r.Mensaje}"); }
    static async Task DoMovimientos(BancoController c) { Console.Write("Cuenta: "); string cu = Console.ReadLine()!; var list = await c.ListarMovimientos(cu); list.ForEach(x => Console.WriteLine($"{x.FechaMovimiento} | {x.TipoDescripcion} | {x.ImporteMovimiento:F2}")); }

    static string ReadPassword()
    {
        var pw = "";
        while (true) { var k = Console.ReadKey(true); if (k.Key == ConsoleKey.Enter) break; if (k.Key == ConsoleKey.Backspace && pw.Length > 0) pw = pw[..^1]; else if (k.KeyChar != 0) pw += k.KeyChar; }
        Console.WriteLine(); return pw;
    }
}
