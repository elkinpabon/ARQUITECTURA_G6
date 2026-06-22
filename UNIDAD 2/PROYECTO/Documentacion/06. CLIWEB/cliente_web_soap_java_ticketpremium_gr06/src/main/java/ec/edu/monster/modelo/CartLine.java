package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Una linea del carrito de compra (vive en la sesion del cliente web). */
public class CartLine implements Serializable {

    private int codigoPartido;
    private String partidoDesc;
    private int idSeccion;
    private String seccionLabel;
    private String categoria;
    private BigDecimal precio;
    private int cantidad;
    private String fila;
    private String asientos;

    public CartLine() { }

    public CartLine(int codigoPartido, String partidoDesc, int idSeccion, String seccionLabel,
                    String categoria, BigDecimal precio, int cantidad, String fila, String asientos) {
        this.codigoPartido = codigoPartido;
        this.partidoDesc = partidoDesc;
        this.idSeccion = idSeccion;
        this.seccionLabel = seccionLabel;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.fila = fila;
        this.asientos = asientos;
    }

    /** subtotal de la linea = precio * cantidad. */
    public BigDecimal getTotal() {
        if (precio == null) return BigDecimal.ZERO;
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public int getCodigoPartido()       { return codigoPartido; }
    public void setCodigoPartido(int v) { this.codigoPartido = v; }
    public String getPartidoDesc()      { return partidoDesc; }
    public void setPartidoDesc(String v){ this.partidoDesc = v; }
    public int getIdSeccion()           { return idSeccion; }
    public void setIdSeccion(int v)     { this.idSeccion = v; }
    public String getSeccionLabel()     { return seccionLabel; }
    public void setSeccionLabel(String v){ this.seccionLabel = v; }
    public String getCategoria()        { return categoria; }
    public void setCategoria(String v)  { this.categoria = v; }
    public BigDecimal getPrecio()       { return precio; }
    public void setPrecio(BigDecimal v) { this.precio = v; }
    public int getCantidad()            { return cantidad; }
    public void setCantidad(int v)      { this.cantidad = v; }
    public String getFila()             { return fila; }
    public void setFila(String v)       { this.fila = v; }
    public String getAsientos()         { return asientos; }
    public void setAsientos(String v)   { this.asientos = v; }
}
