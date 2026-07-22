package ec.edu.monster.cliweb.ws;
import jakarta.jws.*;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.*;
@WebService(name = "WSDeposito", targetNamespace = "http://ws.monster.edu.ec/") @XmlSeeAlso(ObjectFactory.class)
public interface WSDeposito {
    @WebMethod @WebResult(targetNamespace = "") @RequestWrapper(localName = "depositar", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.Depositar") @ResponseWrapper(localName = "depositarResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.cliweb.ws.DepositarResponse")
    Resultado depositar(@WebParam(name = "cuenta", targetNamespace = "") String cuenta, @WebParam(name = "monto", targetNamespace = "") String monto, @WebParam(name = "moneda", targetNamespace = "") String moneda);
}
