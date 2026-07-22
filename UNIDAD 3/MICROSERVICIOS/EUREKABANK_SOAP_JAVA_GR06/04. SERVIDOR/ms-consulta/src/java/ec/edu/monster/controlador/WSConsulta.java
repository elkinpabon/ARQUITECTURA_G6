package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ClienteResumen;
import ec.edu.monster.modelo.CuentaResumen;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.ConsultaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/** Fachada SOAP del microservicio ms-consulta. */
@WebService(serviceName = "WSConsulta", targetNamespace = "http://ws.monster.edu.ec/")
public class WSConsulta {

    private final ConsultaService consultaService = new ConsultaService();

    @WebMethod(operationName = "consultarSaldo")
    public Resultado consultarSaldo(@WebParam(name = "cuenta") String cuenta) {
        return consultaService.consultarSaldo(cuenta);
    }

    @WebMethod(operationName = "listarCuentasPorCliente")
    public List<CuentaResumen> listarCuentasPorCliente(@WebParam(name = "cliente") String cliente) {
        return consultaService.listarCuentasPorCliente(cliente);
    }

    @WebMethod(operationName = "listarClientes")
    public List<ClienteResumen> listarClientes() {
        return consultaService.listarClientes();
    }

    @WebMethod(operationName = "registrarCliente")
    public Resultado registrarCliente(@WebParam(name = "paterno") String paterno,
                                       @WebParam(name = "materno") String materno,
                                       @WebParam(name = "nombre") String nombre,
                                       @WebParam(name = "dni") String dni,
                                       @WebParam(name = "ciudad") String ciudad,
                                       @WebParam(name = "direccion") String direccion,
                                       @WebParam(name = "telefono") String telefono,
                                       @WebParam(name = "email") String email) {
        return consultaService.registrarCliente(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
    }

    @WebMethod(operationName = "registrarCuenta")
    public Resultado registrarCuenta(@WebParam(name = "cliente") String cliente,
                                      @WebParam(name = "moneda") String moneda) {
        return consultaService.registrarCuenta(cliente, moneda);
    }

    @WebMethod(operationName = "eliminarCuenta")
    public Resultado eliminarCuenta(@WebParam(name = "cuenta") String cuenta) {
        return consultaService.eliminarCuenta(cuenta);
    }
}
