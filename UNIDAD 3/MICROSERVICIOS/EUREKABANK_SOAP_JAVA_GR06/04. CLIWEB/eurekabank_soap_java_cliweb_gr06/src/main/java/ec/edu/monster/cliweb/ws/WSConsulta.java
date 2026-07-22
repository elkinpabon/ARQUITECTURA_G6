package ec.edu.monster.cliweb.ws;

import jakarta.jws.*;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;
import java.util.List;

@WebService(name = "WSConsulta", targetNamespace = "http://ws.monster.edu.ec/")
@XmlSeeAlso(ObjectFactory.class)
public interface WSConsulta {
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "consultarSaldo", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.ConsultarSaldo")
    @ResponseWrapper(localName = "consultarSaldoResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.ConsultarSaldoResponse")
    Resultado consultarSaldo(@WebParam(name = "cuenta", targetNamespace = "") String cuenta);
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "listarCuentasPorCliente", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.ListarCuentasPorCliente")
    @ResponseWrapper(localName = "listarCuentasPorClienteResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.ListarCuentasPorClienteResponse")
    List<CuentaResumen> listarCuentasPorCliente(@WebParam(name = "cliente", targetNamespace = "") String cliente);
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "listarClientes", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.ListarClientes")
    @ResponseWrapper(localName = "listarClientesResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.ListarClientesResponse")
    List<ClienteResumen> listarClientes();
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "registrarCliente", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.RegistrarCliente")
    @ResponseWrapper(localName = "registrarClienteResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.RegistrarClienteResponse")
    Resultado registrarCliente(@WebParam(name = "paterno", targetNamespace = "") String paterno, @WebParam(name = "materno", targetNamespace = "") String materno, @WebParam(name = "nombre", targetNamespace = "") String nombre, @WebParam(name = "dni", targetNamespace = "") String dni, @WebParam(name = "ciudad", targetNamespace = "") String ciudad, @WebParam(name = "direccion", targetNamespace = "") String direccion, @WebParam(name = "telefono", targetNamespace = "") String telefono, @WebParam(name = "email", targetNamespace = "") String email);
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "registrarCuenta", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.RegistrarCuenta")
    @ResponseWrapper(localName = "registrarCuentaResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.RegistrarCuentaResponse")
    Resultado registrarCuenta(@WebParam(name = "cliente", targetNamespace = "") String cliente, @WebParam(name = "moneda", targetNamespace = "") String moneda);
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "eliminarCuenta", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.EliminarCuenta")
    @ResponseWrapper(localName = "eliminarCuentaResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.EliminarCuentaResponse")
    Resultado eliminarCuenta(@WebParam(name = "cuenta", targetNamespace = "") String cuenta);
}
