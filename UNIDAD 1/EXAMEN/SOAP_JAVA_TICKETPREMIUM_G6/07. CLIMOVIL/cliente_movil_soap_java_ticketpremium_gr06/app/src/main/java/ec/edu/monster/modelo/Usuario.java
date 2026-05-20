package ec.edu.monster.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    private int id;
    private String usuario;
    private String nombre;
    private String rol;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(rol); }
}
