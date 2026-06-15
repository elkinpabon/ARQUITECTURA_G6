package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.config.ServidorConfig;
import ec.edu.monster.cliweb.ws.WSCuenta;
import ec.edu.monster.cliweb.ws.WSCuenta_Service;
import ec.edu.monster.cliweb.ws.WSLogin;
import ec.edu.monster.cliweb.ws.WSLogin_Service;
import ec.edu.monster.cliweb.ws.WSMovimiento;
import ec.edu.monster.cliweb.ws.WSMovimiento_Service;
import jakarta.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Crea los puertos SOAP usando el WSDL y endpoint definidos en
 * {@link ServidorConfig} (un solo archivo para cambiar el host/IP).
 * No depende de la URL "horneada" por wsimport.
 */
public final class WsFactory {

    private WsFactory() { }

    private static <T> T endpoint(T port, String address) {
        ((BindingProvider) port).getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
        return port;
    }

    public static WSLogin login() {
        try {
            WSLogin p = new WSLogin_Service(
                    new URL(ServidorConfig.wsdlLogin())).getWSLoginPort();
            return endpoint(p, ServidorConfig.epLogin());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a WSLogin: "
                    + e.getMessage(), e);
        }
    }

    public static WSCuenta cuenta() {
        try {
            WSCuenta p = new WSCuenta_Service(
                    new URL(ServidorConfig.wsdlCuenta())).getWSCuentaPort();
            return endpoint(p, ServidorConfig.epCuenta());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a WSCuenta: "
                    + e.getMessage(), e);
        }
    }

    public static WSMovimiento movimiento() {
        try {
            WSMovimiento p = new WSMovimiento_Service(
                    new URL(ServidorConfig.wsdlMovimiento())).getWSMovimientoPort();
            return endpoint(p, ServidorConfig.epMovimiento());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a WSMovimiento: "
                    + e.getMessage(), e);
        }
    }
}
