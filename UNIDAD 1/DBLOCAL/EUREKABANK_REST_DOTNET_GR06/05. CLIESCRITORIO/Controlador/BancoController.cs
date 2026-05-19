using CLIESCRITORIO.Services;

namespace CLIESCRITORIO.Controlador;

public class BancoController
{
    private readonly ApiClient _api;
    private readonly Sesion _sesion = new();

    public BancoController(ApiClient? api = null)
    {
        _api = api ?? new ApiClient();
    }

    public Sesion Sesion => _sesion;

    public bool Login(string usuario, string clave)
    {
        if (!_api.IniciarSesion(usuario, clave).GetAwaiter().GetResult())
        {
            return false;
        }

        _sesion.Usuario = usuario;
        _sesion.Admin = string.Equals(usuario, "monster", StringComparison.OrdinalIgnoreCase);
        _sesion.ClienteAsignado = _sesion.Admin ? string.Empty : _api.ClienteDeUsuario(usuario).GetAwaiter().GetResult();
        _sesion.Cuentas = new List<CuentaResumen>();

        if (!_sesion.Admin)
        {
            CargarCuentas(_sesion.ClienteAsignado);
        }

        return true;
    }

    public void Logout()
    {
        _sesion.Usuario = null;
        _sesion.Admin = false;
        _sesion.ClienteAsignado = string.Empty;
        _sesion.Cuentas = new List<CuentaResumen>();
    }

    public List<ClienteResumen> ListarClientes() => _api.ListarClientes().GetAwaiter().GetResult();

    public void CargarCuentas(string criterio)
    {
        var c = _sesion.Admin ? criterio : _sesion.ClienteAsignado;
        _sesion.Cuentas = _api.ListarCuentas(c).GetAwaiter().GetResult() ?? new List<CuentaResumen>();
    }

    public List<CuentaResumen> GetCuentas() => _sesion.Cuentas;

    public double SaldoTotal() => _sesion.Cuentas.Sum(c => c.Saldo);

    private static Resultado Denegado(string msg) => new() { Exitoso = false, Mensaje = msg };

    public Resultado ConsultarSaldo(string cuenta)
    {
        if (!_sesion.CuentaPropia(cuenta)) return Denegado($"No tienes acceso a la cuenta {cuenta}.");
        return _api.ConsultarSaldo(cuenta).GetAwaiter().GetResult();
    }

    public Resultado Depositar(string cuenta, string monto, string moneda)
    {
        if (!_sesion.Admin) return Denegado("Solo el administrador puede depositar. Usa transferencia.");
        if (!_sesion.CuentaPropia(cuenta)) return Denegado($"No tienes acceso a la cuenta {cuenta}.");
        return _api.Depositar(cuenta, monto, moneda).GetAwaiter().GetResult();
    }

    public Resultado Retirar(string cuenta, string monto, string moneda)
    {
        if (!_sesion.CuentaPropia(cuenta)) return Denegado($"No tienes acceso a la cuenta {cuenta}.");
        return _api.Retirar(cuenta, monto, moneda).GetAwaiter().GetResult();
    }

    public Resultado Transferir(string origen, string destino, string monto, string moneda)
    {
        if (!_sesion.CuentaPropia(origen)) return Denegado($"No tienes acceso a la cuenta de origen {origen}.");
        return _api.Transferir(origen, destino, monto, moneda).GetAwaiter().GetResult();
    }

    public List<MovimientoModel> Movimientos(string cuenta)
    {
        if (!_sesion.CuentaPropia(cuenta)) return new List<MovimientoModel>();
        return _api.ListarMovimientos(cuenta).GetAwaiter().GetResult();
    }

    public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
    {
        if (!_sesion.Admin) return Denegado("Solo el administrador puede registrar clientes.");
        return _api.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email).GetAwaiter().GetResult();
    }

    public Resultado RegistrarCuenta(string cliente, string moneda)
    {
        if (!_sesion.Admin) return Denegado("Solo el administrador puede registrar cuentas.");
        return _api.RegistrarCuenta(cliente, moneda).GetAwaiter().GetResult();
    }

    public Resultado EliminarCuenta(string cuenta)
    {
        if (!_sesion.Admin) return Denegado("Solo el administrador puede eliminar cuentas.");
        return _api.EliminarCuenta(cuenta).GetAwaiter().GetResult();
    }
}
