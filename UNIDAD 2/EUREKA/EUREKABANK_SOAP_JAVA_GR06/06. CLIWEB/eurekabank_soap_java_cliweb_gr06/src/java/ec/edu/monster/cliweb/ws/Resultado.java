package ec.edu.monster.cliweb.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultado", propOrder = {"exito", "mensaje", "saldo"})
public class Resultado {

    protected boolean exito;
    protected String mensaje;
    protected double saldo;

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean value) {
        this.exito = value;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String value) {
        this.mensaje = value;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double value) {
        this.saldo = value;
    }
}
