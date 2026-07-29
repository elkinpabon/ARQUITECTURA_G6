package ec.edu.monster.cliweb.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cuentaResumen", propOrder = {
    "codigoCliente",
    "codigoCuenta",
    "estado",
    "moneda",
    "nombreCliente",
    "saldo"
})
public class CuentaResumen {

    protected String codigoCliente;
    protected String codigoCuenta;
    protected String estado;
    protected String moneda;
    protected String nombreCliente;
    protected double saldo;

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String value) {
        this.codigoCliente = value;
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String value) {
        this.codigoCuenta = value;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String value) {
        this.estado = value;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String value) {
        this.moneda = value;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String value) {
        this.nombreCliente = value;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double value) {
        this.saldo = value;
    }
}
