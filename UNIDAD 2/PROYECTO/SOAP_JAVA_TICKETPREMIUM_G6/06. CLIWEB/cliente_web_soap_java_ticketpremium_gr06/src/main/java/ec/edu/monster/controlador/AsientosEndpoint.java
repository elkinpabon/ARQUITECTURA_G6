package ec.edu.monster.controlador;

import ec.edu.monster.servicio.AsientosHub;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * WebSocket de asientos en tiempo real.
 * El cliente envia el id de la seccion como texto ("12") para suscribirse;
 * el servidor le empuja {"idSeccion":12,"asientos":[...]} cada vez que cambia.
 */
@ServerEndpoint("/ws-asientos")
public class AsientosEndpoint {

    @OnMessage
    public void onMessage(String mensaje, Session session) {
        try {
            int idSeccion = Integer.parseInt(mensaje.trim());
            if (idSeccion > 0) AsientosHub.suscribir(session, idSeccion);
        } catch (NumberFormatException ignored) { }
    }

    @OnClose
    public void onClose(Session session) {
        AsientosHub.desuscribir(session);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        AsientosHub.desuscribir(session);
    }
}
