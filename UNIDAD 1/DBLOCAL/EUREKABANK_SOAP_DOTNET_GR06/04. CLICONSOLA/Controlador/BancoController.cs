using CLICONSOLA.Servicio;
using CLICONSOLA.Util;

namespace CLICONSOLA.Controlador
{
    public class BancoController
    {
        public bool LoggedIn => !string.IsNullOrEmpty(Sesion.Usuario);
        public bool IsAdmin => Sesion.EsAdmin;
        public string CurrentUser => Sesion.Usuario;
        public string ClienteAsignado => Sesion.ClienteAsignado;

        public bool Login(string usuario, string clave)
        {
            try
            {
                bool valido = SoapClient.IniciarSesion(usuario, clave);
                if (valido)
                {
                    Sesion.Usuario = usuario;
                    Sesion.EsAdmin = usuario.Equals("admin", StringComparison.OrdinalIgnoreCase);
                    Sesion.ClienteAsignado = SoapClient.ClienteDeUsuario(usuario);
                    return true;
                }
                return false;
            }
            catch
            {
                return false;
            }
        }

        public void Logout()
        {
            Sesion.Usuario = string.Empty;
            Sesion.EsAdmin = false;
            Sesion.ClienteAsignado = string.Empty;
            Sesion.CuentasCargadas.Clear();
        }

        public Resultado Depositar(string cuenta, string monto, string moneda)
        {
            if (!IsAdmin)
                return new Resultado { Exitoso = false, Mensaje = "Solo administradores pueden depositar desde consola." };

            return SoapClient.Depositar(cuenta, monto, moneda);
        }

        public Resultado Retirar(string cuenta, string monto, string moneda)
        {
            if (!IsAdmin && !PerteneceACliente(cuenta))
                return new Resultado { Exitoso = false, Mensaje = "La cuenta no pertenece al usuario." };

            return SoapClient.Retirar(cuenta, monto, moneda);
        }

        public Resultado ConsultarSaldo(string cuenta)
        {
            if (!IsAdmin && !PerteneceACliente(cuenta))
                return new Resultado { Exitoso = false, Mensaje = "La cuenta no pertenece al usuario." };

            return SoapClient.ConsultarSaldo(cuenta);
        }

        public Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            if (!IsAdmin && !PerteneceACliente(origen))
                return new Resultado { Exitoso = false, Mensaje = "La cuenta origen no pertenece al usuario." };

            return SoapClient.Transferir(origen, destino, monto, moneda);
        }

        public List<CuentaResumen> ListarCuentas()
        {
            if (IsAdmin)
            {
                var todas = SoapClient.ListarCuentasPorCliente("");
                Sesion.CuentasCargadas = todas;
                return todas;
            }

            var cuentas = SoapClient.ListarCuentasPorCliente(ClienteAsignado);
            Sesion.CuentasCargadas = cuentas;
            return cuentas;
        }

        public List<ClienteResumen> ListarClientes()
        {
            if (!IsAdmin)
                return new List<ClienteResumen>();

            return SoapClient.ListarClientes();
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
        {
            if (!IsAdmin)
                return new Resultado { Exitoso = false, Mensaje = "Solo administradores pueden registrar clientes." };

            return SoapClient.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            if (!IsAdmin)
                return new Resultado { Exitoso = false, Mensaje = "Solo administradores pueden registrar cuentas." };

            return SoapClient.RegistrarCuenta(cliente, moneda);
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            if (!IsAdmin)
                return new Resultado { Exitoso = false, Mensaje = "Solo administradores pueden eliminar cuentas." };

            return SoapClient.EliminarCuenta(cuenta);
        }

        public List<MovimientoModel> ListarMovimientos(string cuenta)
        {
            if (!IsAdmin && !PerteneceACliente(cuenta))
                return new List<MovimientoModel>();

            return SoapClient.ListarMovimientos(cuenta);
        }

        private bool PerteneceACliente(string cuenta)
        {
            if (Sesion.CuentasCargadas.Count == 0)
            {
                ListarCuentas();
            }
            return Sesion.CuentasCargadas.Any(c => c.CodigoCuenta.Equals(cuenta, StringComparison.OrdinalIgnoreCase));
        }
    }
}
