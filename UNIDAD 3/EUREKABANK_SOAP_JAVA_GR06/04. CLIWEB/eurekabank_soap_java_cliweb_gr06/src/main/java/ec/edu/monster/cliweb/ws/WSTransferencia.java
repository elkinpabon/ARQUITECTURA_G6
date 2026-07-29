package ec.edu.monster.cliweb.ws;
import jakarta.jws.*;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.*;
@WebService(name = "WSTransferencia", targetNamespace = "http://ws.monster.edu.ec/") @XmlSeeAlso(ObjectFactory.class)
public interface WSTransferencia {
    @WebMethod @WebResult(targetNamespace = "") @RequestWrapper(localName = "transferir", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.Transferir") @ResponseWrapper(localName = "transferirResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.TransferirResponse")
    Resultado transferir(@WebParam(name = "origen", targetNamespace = "") String origen, @WebParam(name = "destino", targetNamespace = "") String destino, @WebParam(name = "monto", targetNamespace = "") String monto, @WebParam(name = "moneda", targetNamespace = "") String moneda);
}
