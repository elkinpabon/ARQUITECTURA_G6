package ec.edu.monster.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(name = "WSRetiro", targetNamespace = "http://ws.monster.edu.ec/")
@XmlSeeAlso(ObjectFactory.class)
public interface WSRetiro {
    @WebMethod @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "retirar", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.ws.Retirar")
    @ResponseWrapper(localName = "retirarResponse", targetNamespace = "http://ws.monster.edu.ec/", className = "ec.edu.monster.ws.RetirarResponse")
    Resultado retirar(@WebParam(name = "cuenta", targetNamespace = "") String cuenta,
            @WebParam(name = "monto", targetNamespace = "") String monto,
            @WebParam(name = "moneda", targetNamespace = "") String moneda);
}
