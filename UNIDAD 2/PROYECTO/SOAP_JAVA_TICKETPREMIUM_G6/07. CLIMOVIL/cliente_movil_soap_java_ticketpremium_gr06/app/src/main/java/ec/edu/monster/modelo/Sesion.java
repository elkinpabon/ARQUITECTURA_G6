package ec.edu.monster.modelo;

public final class Sesion {

    private static Usuario usuario;

    private Sesion() { }

    public static void abrir(Usuario u) { usuario = u; }
    public static void cerrar()        { usuario = null; }
    public static boolean activa()     { return usuario != null; }
    public static Usuario usuario()    { return usuario; }
    public static int idUsuario()      { return usuario == null ? 0 : usuario.getId(); }
    public static String nombre()      { return usuario == null ? "" : usuario.getNombre(); }
    public static boolean isAdmin()    { return usuario != null && usuario.isAdmin(); }
}
