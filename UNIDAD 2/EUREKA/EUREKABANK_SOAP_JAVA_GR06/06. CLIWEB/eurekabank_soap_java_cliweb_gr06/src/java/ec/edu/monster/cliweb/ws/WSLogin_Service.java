package ec.edu.monster.cliweb.ws;

import ec.edu.monster.cliweb.config.ServidorConfig;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;

public class WSLogin_Service extends Service {

    private static final QName QNAME = new QName("http://ws.monster.edu.ec/", "WSLogin");

    public WSLogin_Service() {
        this(wsdl(), QNAME);
    }

    public WSLogin_Service(URL wsdlLocation) {
        super(wsdlLocation, QNAME);
    }

    public WSLogin_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSLogin getWSLoginPort() {
        return super.getPort(new QName("http://ws.monster.edu.ec/", "WSLoginPort"), WSLogin.class);
    }

    private static URL wsdl() {
        try {
            return new URL(ServidorConfig.wsdlLogin());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
