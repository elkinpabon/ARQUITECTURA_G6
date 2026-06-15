package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.EstadioDAO;
import ec.edu.monster.persistencia.PartidoDAO;
import ec.edu.monster.persistencia.SeleccionDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.sql.Timestamp;
import java.util.List;

/** Logica de negocio relacionada con partidos. */
public class PartidoService {

    private final PartidoDAO   partidoDAO   = new PartidoDAO();
    private final SeleccionDAO seleccionDAO = new SeleccionDAO();
    private final EstadioDAO   estadioDAO   = new EstadioDAO();
    private final UsuarioDAO   usuarioDAO   = new UsuarioDAO();

    /** Catalogo de venta: todos los partidos del torneo, ordenados por fecha. */
    public List<Partido> listarDisponibles() {
        return partidoDAO.listarDisponibles();
    }

    /** Todos los partidos (admin). */
    public List<Partido> listarTodos() {
        return partidoDAO.listarTodos();
    }

    // ------------------------------------------------------------------ CRUD admin

    public Resultado registrar(int idAdmin, int idLocal, int idVisita, int idEstadio,
                               String fechaIso, String grupo) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        Resultado v = validarComun(idLocal, idVisita, idEstadio, fechaIso, grupo);
        if (v != null) return v;

        int codigo = partidoDAO.insertar(idLocal, idVisita, idEstadio,
                parsearFecha(fechaIso), grupo.trim().toUpperCase());
        return codigo > 0
                ? new Resultado(true, "Partido registrado con codigo #" + codigo)
                : Resultado.error("No se pudo registrar el partido.");
    }

    public Resultado actualizar(int idAdmin, int codigo, int idLocal, int idVisita,
                                int idEstadio, String fechaIso, String grupo) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (partidoDAO.buscarPorCodigo(codigo) == null) {
            return Resultado.error("El partido " + codigo + " no existe.");
        }
        Resultado v = validarComun(idLocal, idVisita, idEstadio, fechaIso, grupo);
        if (v != null) return v;

        return partidoDAO.actualizar(codigo, idLocal, idVisita, idEstadio,
                parsearFecha(fechaIso), grupo.trim().toUpperCase())
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

    // ------------------------------------------------------------------ helpers

    private Resultado validarComun(int idLocal, int idVisita, int idEstadio,
                                   String fechaIso, String grupo) {
        if (idLocal == idVisita) {
            return Resultado.error("La seleccion local y visitante no pueden ser la misma.");
        }
        if (!seleccionDAO.existe(idLocal))  return Resultado.error("La seleccion local " + idLocal + " no existe.");
        if (!seleccionDAO.existe(idVisita)) return Resultado.error("La seleccion visitante " + idVisita + " no existe.");
        if (!estadioDAO.existe(idEstadio))  return Resultado.error("El estadio " + idEstadio + " no existe.");
        if (vacio(grupo)) return Resultado.error("El grupo es obligatorio (A-L).");
        if (parsearFecha(fechaIso) == null) return Resultado.error("Fecha invalida. Formato: yyyy-MM-dd HH:mm:ss");
        return null;
    }

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
            String s = iso.trim();
            if (s.length() == 10) s = s + " 00:00:00";
            else if (s.length() == 16) s = s + ":00";
            return Timestamp.valueOf(s);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
