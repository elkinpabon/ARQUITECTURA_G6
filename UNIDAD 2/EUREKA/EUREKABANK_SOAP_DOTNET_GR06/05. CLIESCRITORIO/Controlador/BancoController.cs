using CLIESCRITORIO.Servicio;

namespace CLIESCRITORIO.Controlador
{
    public class BancoController
    {
        private readonly SoapClient _soapClient;
        private readonly Sesion _sesion = new();

        public BancoController(SoapClient? soapClient = null)
        {
            _soapClient = soapClient ?? new SoapClient();
        }

        public Sesion Sesion => _sesion;

        public bool Login(string usuario, string clave)
        {
            if (!_soapClient.IniciarSesion(usuario, clave))
            {
                return false;
            }

            _sesion.Usuario = usuario;
            _sesion.Admin = string.Equals(usuario, "monster", StringComparison.OrdinalIgnoreCase);
            _sesion.ClienteAsignado = _sesion.Admin ? string.Empty : _soapClient.ClienteDeUsuario(usuario);
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

        public List<ClienteResumen> ListarClientes() => _soapClient.ListarClientes();

        public void CargarCuentas(string criterio)
        {
            var c = _sesion.Admin ? criterio : _sesion.ClienteAsignado;
            _sesion.Cuentas = _soapClient.ListarCuentasPorCliente(c) ?? new List<CuentaResumen>();
        }

        public List<CuentaResumen> GetCuentas() => _sesion.Cuentas;

        public double SaldoTotal() => _sesion.Cuentas.Sum(c => c.Saldo);

        private static Resultado Denegado(string msg) => new()
        {
            Exitoso = false,
            Mensaje = msg
        };

        public Resultado ConsultarSaldo(string cuenta)
        {
            if (!_sesion.CuentaPropia(cuenta))
            {
                return Denegado($"No tienes acceso a la cuenta {cuenta}.");
            }

            return _soapClient.ConsultarSaldo(cuenta);
        }

        public Resultado Depositar(string cuenta, string monto, string moneda)
        {
            if (!_sesion.Admin)
            {
                return Denegado("Solo el administrador puede depositar. Usa transferencia.");
            }

            if (!_sesion.CuentaPropia(cuenta))
            {
                return Denegado($"No tienes acceso a la cuenta {cuenta}.");
            }

            return _soapClient.Depositar(cuenta, monto, moneda);
        }

        public Resultado Retirar(string cuenta, string monto, string moneda)
        {
            if (!_sesion.CuentaPropia(cuenta))
            {
                return Denegado($"No tienes acceso a la cuenta {cuenta}.");
            }

            return _soapClient.Retirar(cuenta, monto, moneda);
        }

        public Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            if (!_sesion.CuentaPropia(origen))
            {
                return Denegado($"No tienes acceso a la cuenta de origen {origen}.");
            }

            return _soapClient.Transferir(origen, destino, monto, moneda);
        }

        public List<MovimientoModel> Movimientos(string cuenta)
        {
            if (!_sesion.CuentaPropia(cuenta))
            {
                return new List<MovimientoModel>();
            }

            return _soapClient.ListarMovimientos(cuenta);
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre,
            string dni, string ciudad, string direccion, string telefono, string email)
        {
            if (!_sesion.Admin)
            {
                return Denegado("Solo el administrador puede registrar clientes.");
            }

            return _soapClient.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            if (!_sesion.Admin)
            {
                return Denegado("Solo el administrador puede registrar cuentas.");
            }

            return _soapClient.RegistrarCuenta(cliente, moneda);
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            if (!_sesion.Admin)
            {
                return Denegado("Solo el administrador puede eliminar cuentas.");
            }

            return _soapClient.EliminarCuenta(cuenta);
        }
    }
}
