package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Linea del carrito. Los 5 primeros campos son los que viajan en el SOAP
 * (registrarCompra); el resto solo se usa para mostrar en pantalla.
 */
public class ItemCarrito implements Serializable {
    // --- enviados al servidor ---
    private int codigoPartido;
    private int idSeccion;
    private int cantidad = 1;
    private String fila;        // ej "F3"
    private String asientos;    // ej "7" o "7,8"

    // --- solo presentacion (cliente) ---
    private String descripcionPartido;  // "Mexico vs Sudafrica"
    private String categoria;           // CAT1..CAT4
    private String codigoSeccion;       // ej "S-101"
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public int getIdSeccion() { return idSeccion; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }
    public String getAsientos() { return asientos; }
    public void setAsientos(String asientos) { this.asientos = asientos; }
    public String getDescripcionPartido() { return descripcionPartido; }
    public void setDescripcionPartido(String descripcionPartido) { this.descripcionPartido = descripcionPartido; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getCodigoSeccion() { return codigoSeccion; }
    public void setCodigoSeccion(String codigoSeccion) { this.codigoSeccion = codigoSeccion; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario == null ? BigDecimal.ZERO : precioUnitario;
    }

    public BigDecimal totalLinea() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
