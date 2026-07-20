package ec.edu.monster.modelo;

import java.sql.Date;

/** Representa una fila de la tabla `cuenta`. */
public class CuentaModel {

    private String chrCuenCodigo;
    private String chrMoneCodigo;
    private String chrSucucodigo;
    private String chrEmplCreaCuenta;
    private String chrClieCodigo;
    private double decCuenSaldo;
    private Date dttCuenFechaCreacion;
    private String vchCuenEstado;
    private int intCuenContMov;
    private String chrCuenClave;

    public String getChrCuenCodigo() { return chrCuenCodigo; }
    public void setChrCuenCodigo(String v) { this.chrCuenCodigo = v; }

    public String getChrMoneCodigo() { return chrMoneCodigo; }
    public void setChrMoneCodigo(String v) { this.chrMoneCodigo = v; }

    public String getChrSucucodigo() { return chrSucucodigo; }
    public void setChrSucucodigo(String v) { this.chrSucucodigo = v; }

    public String getChrEmplCreaCuenta() { return chrEmplCreaCuenta; }
    public void setChrEmplCreaCuenta(String v) { this.chrEmplCreaCuenta = v; }

    public String getChrClieCodigo() { return chrClieCodigo; }
    public void setChrClieCodigo(String v) { this.chrClieCodigo = v; }

    public double getDecCuenSaldo() { return decCuenSaldo; }
    public void setDecCuenSaldo(double v) { this.decCuenSaldo = v; }

    public Date getDttCuenFechaCreacion() { return dttCuenFechaCreacion; }
    public void setDttCuenFechaCreacion(Date v) { this.dttCuenFechaCreacion = v; }

    public String getVchCuenEstado() { return vchCuenEstado; }
    public void setVchCuenEstado(String v) { this.vchCuenEstado = v; }

    public int getIntCuenContMov() { return intCuenContMov; }
    public void setIntCuenContMov(int v) { this.intCuenContMov = v; }

    public String getChrCuenClave() { return chrCuenClave; }
    public void setChrCuenClave(String v) { this.chrCuenClave = v; }
}
