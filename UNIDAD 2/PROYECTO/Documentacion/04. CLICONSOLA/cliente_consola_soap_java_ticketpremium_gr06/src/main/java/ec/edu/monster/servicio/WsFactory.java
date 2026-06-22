package ec.edu.monster.servicio;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.ws.WSFederacion;
import ec.edu.monster.ws.WSFederacion_Service;
import jakarta.xml.ws.BindingProvider;
import java.net.URL;

/** Crea el puerto SOAP usando el WSDL configurado. */
public final class WsFactory {

    private WsFactory() { }

    private static <T> T endpoint(T port, String address) {
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
        return port;
    }

    public static WSFederacion federacion() {
        try {
            return endpoint(new WSFederacion_Service(new URL(ServidorConfig.wsdlFederacion())).getWSFederacionPort(),
                    ServidorConfig.endpointFederacion());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a WSFederacion: " + e.getMessage(), e);
        }
    }
}
