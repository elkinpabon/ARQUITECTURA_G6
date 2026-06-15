package ec.edu.monster.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Carrito de compras en memoria (un asiento reservado = una linea). */
public final class Carrito {

    private static final List<ItemCarrito> ITEMS = new ArrayList<>();

    private Carrito() { }

    public static synchronized List<ItemCarrito> items() {
        return new ArrayList<>(ITEMS);
    }

    public static synchronized void agregar(ItemCarrito item) {
        if (item != null) ITEMS.add(item);
    }

    public static synchronized void quitar(int idSeccion, String fila, String asiento) {
        for (int i = ITEMS.size() - 1; i >= 0; i--) {
            ItemCarrito it = ITEMS.get(i);
            if (it.getIdSeccion() == idSeccion
                    && igual(it.getFila(), fila)
                    && igual(it.getAsientos(), asiento)) {
                ITEMS.remove(i);
            }
        }
    }

    public static synchronized boolean contiene(int idSeccion, String fila, String asiento) {
        for (ItemCarrito it : ITEMS) {
            if (it.getIdSeccion() == idSeccion
                    && igual(it.getFila(), fila)
                    && igual(it.getAsientos(), asiento)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized int numItems() { return ITEMS.size(); }

    public static synchronized BigDecimal subtotal() {
        BigDecimal s = BigDecimal.ZERO;
        for (ItemCarrito it : ITEMS) s = s.add(it.totalLinea());
        return s;
    }

    public static synchronized void limpiar() { ITEMS.clear(); }

    private static boolean igual(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}
