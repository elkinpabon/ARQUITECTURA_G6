package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Una fila de la tabla de AMORTIZACION (sistema frances, cuota fija). */
public class Cuota implements Serializable {
    private int numCuota;
    private String fechaVencimiento;  // yyyy-MM-dd
    private BigDecimal saldoInicial;
    private BigDecimal cuota;
    private BigDecimal interes;
    private BigDecimal abonoCapital;
    private BigDecimal saldoFinal;

    public Cuota() { }

    public Cuota(int numCuota, String fechaVencimiento, BigDecimal saldoInicial,
                 BigDecimal cuota, BigDecimal interes, BigDecimal abonoCapital,
                 BigDecimal saldoFinal) {
        this.numCuota = numCuota;
        this.fechaVencimiento = fechaVencimiento;
        this.saldoInicial = saldoInicial;
        this.cuota = cuota;
        this.interes = interes;
        this.abonoCapital = abonoCapital;
        this.saldoFinal = saldoFinal;
    }

    public int getNumCuota()                 { return numCuota; }
    public void setNumCuota(int v)           { this.numCuota = v; }
    public String getFechaVencimiento()      { return fechaVencimiento; }
    public void setFechaVencimiento(String v){ this.fechaVencimiento = v; }
    public BigDecimal getSaldoInicial()      { return saldoInicial; }
    public void setSaldoInicial(BigDecimal v){ this.saldoInicial = v; }
    public BigDecimal getCuota()             { return cuota; }
    public void setCuota(BigDecimal v)       { this.cuota = v; }
    public BigDecimal getInteres()           { return interes; }
    public void setInteres(BigDecimal v)     { this.interes = v; }
    public BigDecimal getAbonoCapital()      { return abonoCapital; }
    public void setAbonoCapital(BigDecimal v){ this.abonoCapital = v; }
    public BigDecimal getSaldoFinal()        { return saldoFinal; }
    public void setSaldoFinal(BigDecimal v)  { this.saldoFinal = v; }
}
