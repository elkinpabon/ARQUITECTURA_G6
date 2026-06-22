package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * Item del CARRITO de compra. El cliente arma una lista de estos y la envia en
 * una sola operacion -> se registra UNA factura con N detalles.
 *
 * El precio NO viaja aqui: el servidor lo resuelve (autoritativo) desde la
 * LOCALIDAD a la que pertenece la seccion elegida.
 */
public class ItemCarrito implements Serializable {
    private int codigoPartido;   // partido al que pertenece el item
    private int idSeccion;       // seccion fisica elegida (StubHub)
    private int cantidad;        // numero de entradas
    private String fila;         // opcional, fila elegida ej "F12"
    private String asientos;     // opcional, asientos ej "12,13"

    public ItemCarrito() { }

    public ItemCarrito(int codigoPartido, int idSeccion, int cantidad,
                       String fila, String asientos) {
        this.codigoPartido = codigoPartido;
        this.idSeccion = idSeccion;
        this.cantidad = cantidad;
        this.fila = fila;
        this.asientos = asientos;
    }

    public int getCodigoPartido()       { return codigoPartido; }
    public void setCodigoPartido(int v) { this.codigoPartido = v; }
    public int getIdSeccion()           { return idSeccion; }
    public void setIdSeccion(int v)     { this.idSeccion = v; }
    public int getCantidad()            { return cantidad; }
    public void setCantidad(int v)      { this.cantidad = v; }
    public String getFila()             { return fila; }
    public void setFila(String v)       { this.fila = v; }
    public String getAsientos()         { return asientos; }
    public void setAsientos(String v)   { this.asientos = v; }
}
