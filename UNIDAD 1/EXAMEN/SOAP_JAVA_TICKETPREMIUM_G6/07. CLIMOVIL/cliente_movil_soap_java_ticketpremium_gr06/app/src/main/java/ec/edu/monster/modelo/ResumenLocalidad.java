package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ResumenLocalidad implements Serializable {
    private String localidad;
    private int vendidos;
    private BigDecimal totalRecaudado;

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public int getVendidos() { return vendidos; }
    public void setVendidos(int vendidos) { this.vendidos = vendidos; }
    public BigDecimal getTotalRecaudado() { return totalRecaudado; }
    public void setTotalRecaudado(BigDecimal totalRecaudado) { this.totalRecaudado = totalRecaudado; }
}
