package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cuota implements Serializable {
    private int numCuota;
    private String fechaVencimiento;   // yyyy-MM-dd
    private BigDecimal saldoInicial;
    private BigDecimal cuota;
    private BigDecimal interes;
    private BigDecimal abonoCapital;
    private BigDecimal saldoFinal;

    public int getNumCuota() { return numCuota; }
    public void setNumCuota(int numCuota) { this.numCuota = numCuota; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
    public BigDecimal getCuota() { return cuota; }
    public void setCuota(BigDecimal cuota) { this.cuota = cuota; }
    public BigDecimal getInteres() { return interes; }
    public void setInteres(BigDecimal interes) { this.interes = interes; }
    public BigDecimal getAbonoCapital() { return abonoCapital; }
    public void setAbonoCapital(BigDecimal abonoCapital) { this.abonoCapital = abonoCapital; }
    public BigDecimal getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(BigDecimal saldoFinal) { this.saldoFinal = saldoFinal; }
}
