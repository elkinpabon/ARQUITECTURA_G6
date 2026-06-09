package ec.edu.monster.cliweb.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "WSCuenta", targetNamespace = "http://ws.monster.edu.ec/")
public interface WSCuenta {

    @WebMethod(operationName = "depositar")
    Resultado depositar(@WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "monto") String monto,
            @WebParam(name = "moneda") String moneda);

    @WebMethod(operationName = "retirar")
    Resultado retirar(@WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "monto") String monto,
            @WebParam(name = "moneda") String moneda);

    @WebMethod(operationName = "consultarSaldo")
    Resultado consultarSaldo(@WebParam(name = "cuenta") String cuenta);

    @WebMethod(operationName = "transferir")
    Resultado transferir(@WebParam(name = "origen") String origen,
            @WebParam(name = "destino") String destino,
            @WebParam(name = "monto") String monto,
            @WebParam(name = "moneda") String moneda);

    @WebMethod(operationName = "listarCuentasPorCliente")
    List<CuentaResumen> listarCuentasPorCliente(
            @WebParam(name = "cliente") String cliente);

    @WebMethod(operationName = "listarClientes")
    List<ClienteResumen> listarClientes();

    @WebMethod(operationName = "registrarCliente")
    Resultado registrarCliente(@WebParam(name = "paterno") String paterno,
            @WebParam(name = "materno") String materno,
            @WebParam(name = "nombre") String nombre,
            @WebParam(name = "dni") String dni,
            @WebParam(name = "ciudad") String ciudad,
            @WebParam(name = "direccion") String direccion,
            @WebParam(name = "telefono") String telefono,
            @WebParam(name = "email") String email);

    @WebMethod(operationName = "registrarCuenta")
    Resultado registrarCuenta(@WebParam(name = "cliente") String cliente,
            @WebParam(name = "moneda") String moneda);

    @WebMethod(operationName = "eliminarCuenta")
    Resultado eliminarCuenta(@WebParam(name = "cuenta") String cuenta);
}
