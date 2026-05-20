package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.PartidoDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.sql.Timestamp;
import java.util.List;

/** Logica de negocio relacionada con partidos. */
public class PartidoService {

    private final PartidoDAO partidoDAO = new PartidoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Partidos cuya FECHA aun no ha pasado (disponibles para vender boletos). */
    public List<Partido> listarDisponibles() {
        return partidoDAO.listarDisponibles();
    }

    // -------------------------------------------------------------------------
    //  CRUD admin (todas validan que idAdmin tenga rol ADMIN)
    // -------------------------------------------------------------------------

    public Resultado registrar(int idAdmin, String equipoLocal, String equipoVisita,
                               String fechaIso, String lugar) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (vacio(equipoLocal) || vacio(equipoVisita) || vacio(fechaIso) || vacio(lugar)) {
            return Resultado.error("Todos los campos son obligatorios.");
        }
        Timestamp t = parsearFecha(fechaIso);
        if (t == null) return Resultado.error("Fecha invalida. Formato: yyyy-MM-dd HH:mm:ss");

        int codigo = partidoDAO.insertar(equipoLocal.trim(), equipoVisita.trim(), t, lugar.trim());
        return codigo > 0
                ? new Resultado(true, "Partido registrado con codigo #" + codigo)
                : Resultado.error("No se pudo registrar el partido.");
    }

    public Resultado actualizar(int idAdmin, int codigo, String equipoLocal,
                                String equipoVisita, String fechaIso, String lugar) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (partidoDAO.buscarPorCodigo(codigo) == null) {
            return Resultado.error("El partido " + codigo + " no existe.");
        }
        if (vacio(equipoLocal) || vacio(equipoVisita) || vacio(fechaIso) || vacio(lugar)) {
            return Resultado.error("Todos los campos son obligatorios.");
        }
        Timestamp t = parsearFecha(fechaIso);
        if (t == null) return Resultado.error("Fecha invalida. Formato: yyyy-MM-dd HH:mm:ss");

        return partidoDAO.actualizar(codigo, equipoLocal.trim(), equipoVisita.trim(), t, lugar.trim())
                ? new Resultado(true, "Partido #" + codigo + " actualizado.")
                : Resultado.error("No se pudo actualizar el partido.");
    }

    public Resultado eliminar(int idAdmin, int codigo) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (partidoDAO.buscarPorCodigo(codigo) == null) {
            return Resultado.error("El partido " + codigo + " no existe.");
        }
        if (partidoDAO.tieneVentas(codigo)) {
            return Resultado.error("No se puede eliminar: el partido tiene ventas registradas.");
        }
        if (partidoDAO.tieneLocalidades(codigo)) {
            return Resultado.error("No se puede eliminar: el partido aun tiene localidades. Eliminalas primero.");
        }
        return partidoDAO.eliminar(codigo)
                ? new Resultado(true, "Partido #" + codigo + " eliminado.")
                : Resultado.error("No se pudo eliminar el partido.");
    }

    // -------------------------------------------------------------------------

    private Resultado exigirAdmin(int idUsuario) {
        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) return Resultado.error("Usuario no autenticado.");
        if (!"ADMIN".equalsIgnoreCase(u.getRol())) {
            return Resultado.error("Operacion permitida solo para ADMIN.");
        }
        return null;
    }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }

    private static Timestamp parsearFecha(String iso) {
        try {
            // Acepta "yyyy-MM-dd HH:mm:ss" y "yyyy-MM-dd HH:mm" y "yyyy-MM-dd"
            String s = iso.trim();
            if (s.length() == 10) s = s + " 00:00:00";
            else if (s.length() == 16) s = s + ":00";
            return Timestamp.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
