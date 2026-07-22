package ec.edu.monster.cliweb.ws;

import ec.edu.monster.cliweb.config.ServidorConfig;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;

public class WSCuenta_Service extends Service {

    private static final QName QNAME = new QName("http://ws.monster.edu.ec/", "WSCuenta");

    public WSCuenta_Service() {
        this(wsdl(), QNAME);
    }

    public WSCuenta_Service(URL wsdlLocation) {
        super(wsdlLocation, QNAME);
    }

    public WSCuenta_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSCuenta getWSCuentaPort() {
        return super.getPort(new QName("http://ws.monster.edu.ec/", "WSCuentaPort"), WSCuenta.class);
    }

    private static URL wsdl() {
        try {
            return new URL(ServidorConfig.wsdlCuenta());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
