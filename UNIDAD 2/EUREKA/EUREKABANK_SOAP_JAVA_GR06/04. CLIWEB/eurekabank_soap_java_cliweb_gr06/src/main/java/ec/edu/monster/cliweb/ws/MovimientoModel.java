package ec.edu.monster.cliweb.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "movimientoModel", propOrder = {
    "codigoCuenta",
    "codigoEmpleado",
    "codigoTipoMovimiento",
    "cuentaReferencia",
    "fechaMovimiento",
    "importeMovimiento",
    "importeOrigen",
    "monedaOrigen",
    "numeroMovimiento",
    "tasaAplicada",
    "tipoDescripcion"
})
public class MovimientoModel {

    protected String codigoCuenta;
    protected String codigoEmpleado;
    protected String codigoTipoMovimiento;
    protected String cuentaReferencia;
    protected String fechaMovimiento;
    protected double importeMovimiento;
    protected Double importeOrigen;
    protected String monedaOrigen;
    protected int numeroMovimiento;
    protected Double tasaAplicada;
    protected String tipoDescripcion;

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String value) {
        this.codigoCuenta = value;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String value) {
        this.codigoEmpleado = value;
    }

    public String getCodigoTipoMovimiento() {
        return codigoTipoMovimiento;
    }

    public void setCodigoTipoMovimiento(String value) {
        this.codigoTipoMovimiento = value;
    }

    public String getCuentaReferencia() {
        return cuentaReferencia;
    }

    public void setCuentaReferencia(String value) {
        this.cuentaReferencia = value;
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String value) {
        this.fechaMovimiento = value;
    }

    public double getImporteMovimiento() {
        return importeMovimiento;
    }

    public void setImporteMovimiento(double value) {
        this.importeMovimiento = value;
    }

    public Double getImporteOrigen() {
        return importeOrigen;
    }

    public void setImporteOrigen(Double value) {
        this.importeOrigen = value;
    }

    public String getMonedaOrigen() {
        return monedaOrigen;
    }

    public void setMonedaOrigen(String value) {
        this.monedaOrigen = value;
    }

    public int getNumeroMovimiento() {
        return numeroMovimiento;
    }

    public void setNumeroMovimiento(int value) {
        this.numeroMovimiento = value;
    }

    public Double getTasaAplicada() {
        return tasaAplicada;
    }

    public void setTasaAplicada(Double value) {
        this.tasaAplicada = value;
    }

    public String getTipoDescripcion() {
        return tipoDescripcion;
    }

    public void setTipoDescripcion(String value) {
        this.tipoDescripcion = value;
    }
}
