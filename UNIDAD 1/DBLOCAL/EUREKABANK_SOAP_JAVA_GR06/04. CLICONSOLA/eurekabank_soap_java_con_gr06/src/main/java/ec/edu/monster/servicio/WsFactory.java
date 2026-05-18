package ec.edu.monster.servicio;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.ws.WSCuenta;
import ec.edu.monster.ws.WSCuenta_Service;
import ec.edu.monster.ws.WSLogin;
import ec.edu.monster.ws.WSLogin_Service;
import ec.edu.monster.ws.WSMovimiento;
import ec.edu.monster.ws.WSMovimiento_Service;
import jakarta.xml.ws.BindingProvider;

/**
 * Crea los puertos SOAP y les fija el endpoint definido en
 * {@link ServidorConfig} (archivo servidor.properties).
 */
public final class WsFactory {

    private WsFactory() { }

    private static <T> T conEndpoint(T port, String endpoint) {
        ((BindingProvider) port).getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
        return port;
    }

    public static WSLogin login() {
        return conEndpoint(new WSLogin_Service().getWSLoginPort(),
                ServidorConfig.endpointLogin());
    }

    public static WSCuenta cuenta() {
        return conEndpoint(new WSCuenta_Service().getWSCuentaPort(),
                ServidorConfig.endpointCuenta());
    }

    public static WSMovimiento movimiento() {
        return conEndpoint(new WSMovimiento_Service().getWSMovimientoPort(),
                ServidorConfig.endpointMovimiento());
    }
}
