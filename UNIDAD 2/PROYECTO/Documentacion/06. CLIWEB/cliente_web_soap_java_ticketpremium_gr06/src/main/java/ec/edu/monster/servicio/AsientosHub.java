package ec.edu.monster.servicio;

import ec.edu.monster.ws.Asiento;
import ec.edu.monster.ws.WSFederacion;
import jakarta.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hub de tiempo real para los estados de asientos.
 *
 * Los navegadores se conectan por WebSocket (/ws-asientos) y se suscriben a UNA seccion.
 * Un unico hilo del backend consulta al servidor SOAP (asientosNoLibres) solo para las
 * secciones que tienen espectadores y, si el estado cambio, EMPUJA el JSON a todos los
 * suscriptores. Ademas, las acciones locales (reservar/liberar/pagar) disparan un push
 * inmediato. Asi N navegadores = 1 sola consulta SOAP por seccion, y todos ven el cambio
 * al instante sin recargar ni hacer polling propio.
 */
public final class AsientosHub {

    private static final Map<Integer, Set<Session>> SUBS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> ULTIMO = new ConcurrentHashMap<>();
    private static volatile WSFederacion port;
    private static ScheduledExecutorService exec;

    private AsientosHub() { }

    public static synchronized void iniciar() {
        if (exec != null) return;
        exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "asientos-hub");
            t.setDaemon(true);
            return t;
        });
        exec.scheduleWithFixedDelay(AsientosHub::barrido, 3, 3, TimeUnit.SECONDS);
    }

    public static synchronized void detener() {
        if (exec != null) { exec.shutdownNow(); exec = null; }
        SUBS.clear();
        ULTIMO.clear();
    }

    private static WSFederacion port() {
        if (port == null) {
            synchronized (AsientosHub.class) {
                if (port == null) port = WsFactory.federacion();
            }
        }
        return port;
    }

    /** Suscribe la conexion a una seccion (reemplaza la suscripcion anterior) y le manda el estado actual. */
    public static void suscribir(Session s, int idSeccion) {
        desuscribir(s);
        s.getUserProperties().put("idSeccion", idSeccion);
        SUBS.computeIfAbsent(idSeccion, k -> ConcurrentHashMap.newKeySet()).add(s);
        ejecutar(() -> {
            try {
                String json = jsonDe(idSeccion);
                ULTIMO.put(idSeccion, json);
                if (s.isOpen()) s.getBasicRemote().sendText(mensaje(idSeccion, json));
            } catch (Exception ignored) { }
        });
    }

    public static void desuscribir(Session s) {
        Object o = s.getUserProperties().remove("idSeccion");
        if (o instanceof Integer id) {
            Set<Session> set = SUBS.get(id);
            if (set != null) set.remove(s);
        }
    }

    /** Push inmediato (lo llaman reservar/liberar/checkout). Asincrono para no demorar la respuesta HTTP. */
    public static void empujar(int idSeccion) {
        if (idSeccion > 0) ejecutar(() -> refrescar(idSeccion, true));
    }

    private static void ejecutar(Runnable r) {
        ScheduledExecutorService e = exec;
        if (e != null) e.execute(r);
    }

    private static void barrido() {
        for (Map.Entry<Integer, Set<Session>> en : SUBS.entrySet()) {
            if (!en.getValue().isEmpty()) refrescar(en.getKey(), false);
        }
    }

    private static void refrescar(int idSeccion, boolean forzar) {
        try {
            String json = jsonDe(idSeccion);
            String previo = ULTIMO.put(idSeccion, json);
            Set<Session> set = SUBS.get(idSeccion);
            if (set == null || set.isEmpty()) return;
            if (!forzar && json.equals(previo)) return;
            String msg = mensaje(idSeccion, json);
            for (Session s : set) {
                try { if (s.isOpen()) s.getBasicRemote().sendText(msg); }
                catch (Exception ex) { set.remove(s); }
            }
        } catch (Exception ignored) { }
    }

    private static String mensaje(int idSeccion, String asientosJson) {
        return "{\"idSeccion\":" + idSeccion + ",\"asientos\":" + asientosJson + "}";
    }

    private static String jsonDe(int idSeccion) {
        List<Asiento> noLibres = port().asientosNoLibres(idSeccion);
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Asiento a : noLibres) {
            if (!first) sb.append(',');
            first = false;
            sb.append("{\"fila\":\"").append(esc(a.getFila()))
              .append("\",\"asiento\":\"").append(esc(a.getAsiento()))
              .append("\",\"estado\":\"").append(esc(a.getEstado())).append("\"}");
        }
        return sb.append(']').toString();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
