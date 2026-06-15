package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Fila del reporte "Resumen de Ventas de un Partido", agrupado por localidad. */
public class ResumenLocalidad implements Serializable {
    private String localidad;
    private int vendidos;
    private BigDecimal totalRecaudado;

    public ResumenLocalidad() { }

    public ResumenLocalidad(String localidad, int vendidos, BigDecimal totalRecaudado) {
        this.localidad = localidad;
        this.vendidos = vendidos;
        this.totalRecaudado = totalRecaudado;
    }

    public String getLocalidad()               { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public int getVendidos()                   { return vendidos; }
    public void setVendidos(int vendidos)      { this.vendidos = vendidos; }
    public BigDecimal getTotalRecaudado()      { return totalRecaudado; }
    public void setTotalRecaudado(BigDecimal v){ this.totalRecaudado = v; }
}
