using System.Collections.Generic;
using System.ServiceModel;
using SERVIDOR.Modelo;

namespace SERVIDOR.Controlador
{
    [ServiceContract(Namespace = "http://ws.monster.edu.ec/")]
    public interface IWSCuenta
    {
        [OperationContract]
        Resultado Depositar(string cuenta, string monto, string moneda);

        [OperationContract]
        Resultado Retirar(string cuenta, string monto, string moneda);

        [OperationContract]
        Resultado ConsultarSaldo(string cuenta);

        [OperationContract]
        Resultado Transferir(string origen, string destino, string monto, string moneda);

        [OperationContract]
        List<CuentaResumen> ListarCuentasPorCliente(string cliente);

        [OperationContract]
        List<ClienteResumen> ListarClientes();

        [OperationContract]
        Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni,
            string ciudad, string direccion, string telefono, string email);

        [OperationContract]
        Resultado RegistrarCuenta(string cliente, string moneda);

        [OperationContract]
        Resultado EliminarCuenta(string cuenta);
    }
}
