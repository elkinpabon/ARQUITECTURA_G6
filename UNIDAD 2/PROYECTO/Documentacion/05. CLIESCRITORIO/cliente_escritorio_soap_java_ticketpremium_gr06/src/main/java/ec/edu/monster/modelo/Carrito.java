package ec.edu.monster.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Carrito local del cliente escritorio. Mantiene las lineas (asientos
 * reservados) y notifica a las vistas suscritas cuando cambia.
 */
public class Carrito {

    private final List<LineaCarrito> lineas = new ArrayList<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public void onCambio(Runnable l) { listeners.add(l); }

    private void notificar() {
        for (Runnable l : listeners) l.run();
    }

    public void agregar(LineaCarrito l) {
        lineas.add(l);
        notificar();
    }

    public void quitar(int index) {
        if (index >= 0 && index < lineas.size()) {
            lineas.remove(index);
            notificar();
        }
    }

    /** Quita la linea de un asiento concreto (cuando se libera desde el mapa). */
    public void quitarAsiento(int idSeccion, String fila, String asiento) {
        if (lineas.removeIf(l -> l.es(idSeccion, fila, asiento))) {
            notificar();
        }
    }

    public boolean contiene(int idSeccion, String fila, String asiento) {
        return lineas.stream().anyMatch(l -> l.es(idSeccion, fila, asiento));
    }

    public void vaciar() {
        if (!lineas.isEmpty()) {
            lineas.clear();
            notificar();
        }
    }

    public List<LineaCarrito> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public LineaCarrito get(int index) { return lineas.get(index); }

    public int size() { return lineas.size(); }

    public boolean vacio() { return lineas.isEmpty(); }

    public BigDecimal total() {
        BigDecimal t = BigDecimal.ZERO;
        for (LineaCarrito l : lineas) t = t.add(l.getPrecio());
        return t;
    }
}
