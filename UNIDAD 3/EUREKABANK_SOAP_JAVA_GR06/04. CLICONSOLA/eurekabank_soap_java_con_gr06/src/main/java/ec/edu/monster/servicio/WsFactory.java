package ec.edu.monster.servicio;

import ec.edu.monster.config.MicroserviciosConfig;
import ec.edu.monster.ws.WSConsulta;
import ec.edu.monster.ws.WSDeposito;
import ec.edu.monster.ws.WSLogin;
import ec.edu.monster.ws.WSMovimiento;
import ec.edu.monster.ws.WSRetiro;
import ec.edu.monster.ws.WSTransferencia;

/**
 * Factoria de puertos SOAP para consumir los microservicios en LAN.
 * Enruta dinámicamente cada llamada a la IP/Endpoint de MicroserviciosConfig.
 */
public final class WsFactory {

    private WsFactory() { }

    private static <T> T port(Class<T> type, String address) {
        return SoapClientProxy.create(type, address);
    }

    public static WSLogin login() {
        try {
            return port(WSLogin.class, MicroserviciosConfig.epLogin());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar al ms-autenticacion: " + e.getMessage(), e);
        }
    }

    public static WSConsulta consulta() {
        try {
            return port(WSConsulta.class, MicroserviciosConfig.epConsulta());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar al ms-consulta: " + e.getMessage(), e);
        }
    }

    public static WSDeposito deposito() {
        try {
            return port(WSDeposito.class, MicroserviciosConfig.epDeposito());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar al ms-deposito: " + e.getMessage(), e);
        }
    }

    public static WSRetiro retiro() {
        try {
            return port(WSRetiro.class, MicroserviciosConfig.epRetiro());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar al ms-retiro: " + e.getMessage(), e);
        }
    }

    public static WSTransferencia transferencia() {
        try {
            return port(WSTransferencia.class, MicroserviciosConfig.epTransferencia());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar al ms-transferencia: " + e.getMessage(), e);
        }
    }

    public static WSMovimiento movimiento() {
        try {
            return port(WSMovimiento.class, MicroserviciosConfig.epMovimiento());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar al ms-consulta (movimientos): " + e.getMessage(), e);
        }
    }

}
