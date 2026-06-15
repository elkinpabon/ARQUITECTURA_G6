package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Movimiento bancario en la cuenta del usuario, generado por una compra. */
public class Movimiento implements Serializable {
    private int idMovimiento;
    private int idCuenta;
    private String fecha;
    private String tipo;        // COMPRA_CONTADO | CREDITO
    private BigDecimal monto;
    private String descripcion;
    private int idFactura;

    public Movimiento() { }

    public Movimiento(int idMovimiento, int idCuenta, String fecha, String tipo,
                      BigDecimal monto, String descripcion, int idFactura) {
        this.idMovimiento = idMovimiento;
        this.idCuenta = idCuenta;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.idFactura = idFactura;
    }

    public int getIdMovimiento()       { return idMovimiento; }
    public void setIdMovimiento(int v) { this.idMovimiento = v; }
    public int getIdCuenta()           { return idCuenta; }
    public void setIdCuenta(int v)     { this.idCuenta = v; }
    public String getFecha()           { return fecha; }
    public void setFecha(String v)     { this.fecha = v; }
    public String getTipo()            { return tipo; }
    public void setTipo(String v)      { this.tipo = v; }
    public BigDecimal getMonto()       { return monto; }
    public void setMonto(BigDecimal v) { this.monto = v; }
    public String getDescripcion()     { return descripcion; }
    public void setDescripcion(String v){ this.descripcion = v; }
    public int getIdFactura()          { return idFactura; }
    public void setIdFactura(int v)    { this.idFactura = v; }
}
