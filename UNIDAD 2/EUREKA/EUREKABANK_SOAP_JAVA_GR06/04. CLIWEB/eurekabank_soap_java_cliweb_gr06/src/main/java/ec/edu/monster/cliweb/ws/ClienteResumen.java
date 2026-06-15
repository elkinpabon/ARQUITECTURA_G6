package ec.edu.monster.cliweb.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clienteResumen", propOrder = {"codigo", "dni", "nombre"})
public class ClienteResumen {

    protected String codigo;
    protected String dni;
    protected String nombre;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String value) {
        this.codigo = value;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String value) {
        this.dni = value;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }
}
