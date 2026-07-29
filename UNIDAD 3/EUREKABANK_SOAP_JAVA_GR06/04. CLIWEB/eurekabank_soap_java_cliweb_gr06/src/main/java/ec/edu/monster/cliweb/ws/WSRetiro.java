package ec.edu.monster.cliweb.ws;
import jakarta.jws.*;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.*;
@WebService(name = "WSRetiro", targetNamespace = "http://ws.monster.edu.ec/") @XmlSeeAlso(ObjectFactory.class)
public interface WSRetiro {
    @WebMethod @WebResult(targetNamespace = "") @RequestWrapper(localName = "retirar", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.Retirar") @ResponseWrapper(localName = "retirarResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.RetirarResponse")
    Resultado retirar(@WebParam(name = "cuenta", targetNamespace = "") String cuenta, @WebParam(name = "monto", targetNamespace = "") String monto, @WebParam(name = "moneda", targetNamespace = "") String moneda);
}
