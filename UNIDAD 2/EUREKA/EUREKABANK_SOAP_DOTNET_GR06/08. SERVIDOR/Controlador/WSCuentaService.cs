using SERVIDOR.Modelo;
using SERVIDOR.Servicio;

namespace SERVIDOR.Controlador
{
    public class WSCuentaService : IWSCuenta
    {
        private readonly CuentaService cuentaService = new CuentaService();

        public Resultado Depositar(string cuenta, string monto, string moneda)
        {
            return cuentaService.Depositar(cuenta, monto, moneda);
        }

        public Resultado Retirar(string cuenta, string monto, string moneda)
        {
            return cuentaService.Retirar(cuenta, monto, moneda);
        }

        public Resultado ConsultarSaldo(string cuenta)
        {
            return cuentaService.ConsultarSaldo(cuenta);
        }

        public Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            return cuentaService.Transferir(origen, destino, monto, moneda);
        }

        public List<CuentaResumen> ListarCuentasPorCliente(string cliente)
        {
            return cuentaService.ListarCuentasPorCliente(cliente);
        }

        public List<ClienteResumen> ListarClientes()
        {
            return cuentaService.ListarClientes();
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni,
            string ciudad, string direccion, string telefono, string email)
        {
            return cuentaService.RegistrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            return cuentaService.RegistrarCuenta(cliente, moneda);
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            return cuentaService.EliminarCuenta(cuenta);
        }
    }
}
