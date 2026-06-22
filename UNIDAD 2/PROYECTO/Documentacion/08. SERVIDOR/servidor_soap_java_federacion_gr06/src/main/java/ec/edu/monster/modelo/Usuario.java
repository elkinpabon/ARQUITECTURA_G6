package ec.edu.monster.modelo;

import java.io.Serializable;

/** DTO de un usuario autenticado. La contrasena NO viaja en el WSDL. */
public class Usuario implements Serializable {
    private int idUsuario;
    private String usuario;
    private String nombre;
    private String rol;        // ADMIN | CLIENTE

    public Usuario() { }

    public Usuario(int idUsuario, String usuario, String nombre, String rol) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.nombre = nombre;
        this.rol = rol;
    }

    public int getIdUsuario()           { return idUsuario; }
    public void setIdUsuario(int v)     { this.idUsuario = v; }
    public String getUsuario()          { return usuario; }
    public void setUsuario(String v)    { this.usuario = v; }
    public String getNombre()           { return nombre; }
    public void setNombre(String v)     { this.nombre = v; }
    public String getRol()              { return rol; }
    public void setRol(String rol)      { this.rol = rol; }
}
