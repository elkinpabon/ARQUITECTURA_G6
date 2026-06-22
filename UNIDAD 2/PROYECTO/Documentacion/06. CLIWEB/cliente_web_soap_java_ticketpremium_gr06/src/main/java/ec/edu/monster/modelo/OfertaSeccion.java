package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Fila "comprable" que combina una categoria (Cat 1-4) con una de sus secciones
 * fisicas, para poblar el selector de compra del cliente web.
 */
public class OfertaSeccion implements Serializable {

    private int codigoPartido;
    private int idSeccion;
    private String categoria;
    private String codigoSeccion;
    private BigDecimal precio;
    private int disponibilidad;
    private int numFilas;
    private int asientosPorFila;

    public OfertaSeccion() { }

    public OfertaSeccion(int codigoPartido, int idSeccion, String categoria,
                         String codigoSeccion, BigDecimal precio, int disponibilidad,
                         int numFilas, int asientosPorFila) {
        this.codigoPartido = codigoPartido;
        this.idSeccion = idSeccion;
        this.categoria = categoria;
        this.codigoSeccion = codigoSeccion;
        this.precio = precio;
        this.disponibilidad = disponibilidad;
        this.numFilas = numFilas;
        this.asientosPorFila = asientosPorFila;
    }

    public int getNumFilas()           { return numFilas; }
    public void setNumFilas(int v)     { this.numFilas = v; }
    public int getAsientosPorFila()    { return asientosPorFila; }
    public void setAsientosPorFila(int v){ this.asientosPorFila = v; }

    /** Etiqueta para el <option>, ej: "CAT1 · S1 · $185.00". */
    public String getLabel() {
        return categoria + " · " + codigoSeccion + " · $" + (precio == null ? "0" : precio.toPlainString());
    }

    public int getCodigoPartido()        { return codigoPartido; }
    public void setCodigoPartido(int v)  { this.codigoPartido = v; }
    public int getIdSeccion()            { return idSeccion; }
    public void setIdSeccion(int v)      { this.idSeccion = v; }
    public String getCategoria()         { return categoria; }
    public void setCategoria(String v)   { this.categoria = v; }
    public String getCodigoSeccion()     { return codigoSeccion; }
    public void setCodigoSeccion(String v){ this.codigoSeccion = v; }
    public BigDecimal getPrecio()        { return precio; }
    public void setPrecio(BigDecimal v)  { this.precio = v; }
    public int getDisponibilidad()       { return disponibilidad; }
    public void setDisponibilidad(int v) { this.disponibilidad = v; }
}
