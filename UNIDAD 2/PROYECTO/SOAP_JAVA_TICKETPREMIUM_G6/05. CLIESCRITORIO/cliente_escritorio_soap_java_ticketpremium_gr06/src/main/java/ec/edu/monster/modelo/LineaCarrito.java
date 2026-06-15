package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Una linea del carrito local del cliente escritorio.
 * Cada linea representa UN asiento reservado (cantidad = 1).
 */
public class LineaCarrito implements Serializable {

    private final int codigoPartido;
    private final String partidoDesc;
    private final int idSeccion;
    private final String seccionLabel;
    private final String categoria;
    private final String fila;      // formato "F3"
    private final String asiento;   // formato "7"
    private final BigDecimal precio;

    public LineaCarrito(int codigoPartido, String partidoDesc, int idSeccion,
                        String seccionLabel, String categoria, String fila,
                        String asiento, BigDecimal precio) {
        this.codigoPartido = codigoPartido;
        this.partidoDesc = partidoDesc;
        this.idSeccion = idSeccion;
        this.seccionLabel = seccionLabel;
        this.categoria = categoria;
        this.fila = fila;
        this.asiento = asiento;
        this.precio = precio;
    }

    public int getCodigoPartido()    { return codigoPartido; }
    public String getPartidoDesc()   { return partidoDesc; }
    public int getIdSeccion()        { return idSeccion; }
    public String getSeccionLabel()  { return seccionLabel; }
    public String getCategoria()     { return categoria; }
    public String getFila()          { return fila; }
    public String getAsiento()       { return asiento; }
    public BigDecimal getPrecio()    { return precio == null ? BigDecimal.ZERO : precio; }

    /** true si esta linea corresponde al asiento dado. */
    public boolean es(int idSeccion, String fila, String asiento) {
        return this.idSeccion == idSeccion
                && this.fila.equalsIgnoreCase(fila)
                && this.asiento.equalsIgnoreCase(asiento);
    }
}
