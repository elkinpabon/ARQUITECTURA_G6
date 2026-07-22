package ec.edu.monster.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(name = "WSTransferencia", targetNamespace = "http://ws.monster.edu.ec/")
@XmlSeeAlso(ObjectFactory.class)
public interface WSTransferencia {
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "transferir", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.ws.Transferir")
    @ResponseWrapper(localName = "transferirResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.ws.TransferirResponse")
    Resultado transferir(@WebParam(name = "origen", targetNamespace = "") String origen,
            @WebParam(name = "destino", targetNamespace = "") String destino,
            @WebParam(name = "monto", targetNamespace = "") String monto,
            @WebParam(name = "moneda", targetNamespace = "") String moneda);
}
