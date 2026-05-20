package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.LocalidadDAO;
import ec.edu.monster.persistencia.PartidoDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.math.BigDecimal;
import java.util.List;

/** Logica de negocio relacionada con localidades. */
public class LocalidadService {

    private final LocalidadDAO localidadDAO = new LocalidadDAO();
    private final PartidoDAO   partidoDAO   = new PartidoDAO();
    private final UsuarioDAO   usuarioDAO   = new UsuarioDAO();

    /** Localidades de un partido con disponibilidad > 0 (uso normal del cliente). */
    public List<Localidad> listarPorPartido(int codigoPartido) {
        return localidadDAO.listarDisponiblesPorPartido(codigoPartido);
    }

    /** Todas las localidades de un partido (admin: incluye con disponibilidad 0). */
    public List<Localidad> listarTodasPorPartido(int codigoPartido) {
        return localidadDAO.listarTodasPorPartido(codigoPartido);
    }

    // -------------------------------------------------------------------------
    //  CRUD admin
    // -------------------------------------------------------------------------

    public Resultado registrar(int idAdmin, int codigoPartido, String codigoLocalidad,
                               int disponibilidad, BigDecimal precio) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (partidoDAO.buscarPorCodigo(codigoPartido) == null) {
            return Resultado.error("El partido " + codigoPartido + " no existe.");
        }
        if (vacio(codigoLocalidad)) {
            return Resultado.error("El codigo de localidad es obligatorio.");
        }
        if (disponibilidad < 0) {
            return Resultado.error("La disponibilidad no puede ser negativa.");
        }
        if (precio == null || precio.signum() < 0) {
            return Resultado.error("El precio debe ser >= 0.");
        }
        if (localidadDAO.buscar(codigoPartido, codigoLocalidad.trim()) != null) {
            return Resultado.error("Ya existe la localidad " + codigoLocalidad
                    + " para el partido " + codigoPartido + ".");
        }
        int id = localidadDAO.insertar(codigoPartido, codigoLocalidad.trim().toUpperCase(),
                disponibilidad, precio);
        return id > 0
                ? new Resultado(true, "Localidad registrada (id=" + id + ").")
                : Resultado.error("No se pudo registrar la localidad.");
    }

    public Resultado actualizar(int idAdmin, int idLocalidad,
                                int disponibilidad, BigDecimal precio) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (disponibilidad < 0) {
            return Resultado.error("La disponibilidad no puede ser negativa.");
        }
        if (precio == null || precio.signum() < 0) {
            return Resultado.error("El precio debe ser >= 0.");
        }
        return localidadDAO.actualizar(idLocalidad, disponibilidad, precio)
                ? new Resultado(true, "Localidad #" + idLocalidad + " actualizada.")
                : Resultado.error("No se pudo actualizar la localidad (id no existe).");
    }

    public Resultado eliminar(int idAdmin, int idLocalidad) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (localidadDAO.tieneVentas(idLocalidad)) {
            return Resultado.error("No se puede eliminar: la localidad ya tiene ventas.");
        }
        return localidadDAO.eliminar(idLocalidad)
                ? new Resultado(true, "Localidad #" + idLocalidad + " eliminada.")
                : Resultado.error("No se pudo eliminar la localidad (id no existe).");
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
}
