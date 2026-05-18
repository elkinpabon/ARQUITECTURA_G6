using CLICONSOLA.Models;
using CLICONSOLA.Services;

namespace CLICONSOLA.Controlador;

public class BancoController
{
    private readonly ApiClient _api;
    public bool LoggedIn { get; private set; }
    public bool IsAdmin { get; private set; }
    public string CurrentUser { get; private set; } = "";
    public string ClienteAsignado { get; private set; } = "";

    public BancoController(string baseUrl = "http://localhost:5010")
    {
        _api = new ApiClient(baseUrl);
    }

    public async Task<bool> Login(string usuario, string clave)
    {
        bool ok = await _api.IniciarSesion(usuario, clave);
        if (ok)
        {
            LoggedIn = true;
            CurrentUser = usuario;
            ClienteAsignado = await _api.ClienteDeUsuario(usuario);
            IsAdmin = string.IsNullOrEmpty(ClienteAsignado);
        }
        return ok;
    }

    public void Logout() { LoggedIn = false; IsAdmin = false; CurrentUser = ""; ClienteAsignado = ""; }

    public async Task<Resultado> Depositar(string cuenta, string monto, string moneda) => await _api.Depositar(cuenta, monto, moneda);
    public async Task<Resultado> Retirar(string cuenta, string monto, string moneda) => await _api.Retirar(cuenta, monto, moneda);
    public async Task<Resultado> ConsultarSaldo(string cuenta) => await _api.ConsultarSaldo(cuenta);
    public async Task<Resultado> Transferir(string origen, string destino, string monto, string moneda) => await _api.Transferir(origen, destino, monto, moneda);
    public async Task<List<CuentaResumen>> ListarCuentas(string cliente) => await _api.ListarCuentasPorCliente(cliente);
    public async Task<List<ClienteResumen>> ListarClientes() => await _api.ListarClientes();
    public async Task<Resultado> RegistrarCliente(string p, string m, string n, string d, string c, string dir, string t, string e) => await _api.RegistrarCliente(p, m, n, d, c, dir, t, e);
    public async Task<Resultado> RegistrarCuenta(string cliente, string moneda) => await _api.RegistrarCuenta(cliente, moneda);
    public async Task<Resultado> EliminarCuenta(string cuenta) => await _api.EliminarCuenta(cuenta);
    public async Task<List<MovimientoModel>> ListarMovimientos(string cuenta) => await _api.ListarMovimientos(cuenta);
}
