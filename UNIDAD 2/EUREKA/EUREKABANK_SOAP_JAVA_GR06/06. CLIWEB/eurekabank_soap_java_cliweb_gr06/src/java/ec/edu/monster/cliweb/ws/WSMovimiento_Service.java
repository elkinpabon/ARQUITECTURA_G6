package ec.edu.monster.cliweb.ws;

import ec.edu.monster.cliweb.config.ServidorConfig;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;

public class WSMovimiento_Service extends Service {

    private static final QName QNAME = new QName("http://ws.monster.edu.ec/", "WSMovimiento");

    public WSMovimiento_Service() {
        this(wsdl(), QNAME);
    }

    public WSMovimiento_Service(URL wsdlLocation) {
        super(wsdlLocation, QNAME);
    }

    public WSMovimiento_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSMovimiento getWSMovimientoPort() {
        return super.getPort(new QName("http://ws.monster.edu.ec/", "WSMovimientoPort"), WSMovimiento.class);
    }

    private static URL wsdl() {
        try {
            return new URL(ServidorConfig.wsdlMovimiento());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
