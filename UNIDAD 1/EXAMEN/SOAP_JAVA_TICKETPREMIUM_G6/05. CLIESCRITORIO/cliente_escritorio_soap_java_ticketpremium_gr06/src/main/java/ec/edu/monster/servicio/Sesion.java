package ec.edu.monster.servicio;

import ec.edu.monster.ws.Usuario;

/** Estado de la sesion: usuario autenticado y su rol. */
public class Sesion {

    private Usuario usuario;

    public Usuario getUsuario()           { return usuario; }
    public void setUsuario(Usuario v)     { this.usuario = v; }

    public boolean activa()               { return usuario != null; }
    public boolean isAdmin()              { return usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol()); }
    public int    getIdUsuario()          { return usuario == null ? 0 : usuario.getIdUsuario(); }
    public String getNombre()             { return usuario == null ? "" : usuario.getNombre(); }
}
