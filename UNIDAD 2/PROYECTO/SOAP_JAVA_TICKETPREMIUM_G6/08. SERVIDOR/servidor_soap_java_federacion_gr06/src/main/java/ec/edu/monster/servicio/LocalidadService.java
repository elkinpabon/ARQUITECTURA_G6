package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.LocalidadDAO;
import ec.edu.monster.persistencia.PartidoDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.math.BigDecimal;
import java.util.List;

/** Logica de negocio relacionada con localidades (categorias Cat 1-4). */
public class LocalidadService {

    private final LocalidadDAO localidadDAO = new LocalidadDAO();
    private final PartidoDAO   partidoDAO   = new PartidoDAO();
    private final UsuarioDAO   usuarioDAO   = new UsuarioDAO();

    /** Categorias de un partido con disponibilidad > 0 (uso normal del cliente). */
    public List<Localidad> listarPorPartido(int codigoPartido) {
        return localidadDAO.listarDisponiblesPorPartido(codigoPartido);
    }

    /** Todas las categorias de un partido (admin: incluye disponibilidad 0). */
    public List<Localidad> listarTodasPorPartido(int codigoPartido) {
        return localidadDAO.listarTodasPorPartido(codigoPartido);
    }

    // ------------------------------------------------------------------ CRUD admin

    public Resultado registrar(int idAdmin, int codigoPartido, String categoria,
                               int disponibilidad, BigDecimal precio) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (partidoDAO.buscarPorCodigo(codigoPartido) == null) {
            return Resultado.error("El partido " + codigoPartido + " no existe.");
        }
        if (vacio(categoria)) return Resultado.error("La categoria es obligatoria.");
        if (disponibilidad < 0) return Resultado.error("La disponibilidad no puede ser negativa.");
        if (precio == null || precio.signum() < 0) return Resultado.error("El precio debe ser >= 0.");
        if (localidadDAO.buscar(codigoPartido, categoria.trim().toUpperCase()) != null) {
            return Resultado.error("Ya existe la categoria " + categoria
                    + " para el partido " + codigoPartido + ".");
        }
        int id = localidadDAO.insertar(codigoPartido, categoria.trim().toUpperCase(), disponibilidad, precio);
        return id > 0
                ? new Resultado(true, "Categoria registrada (id=" + id + ").")
                : Resultado.error("No se pudo registrar la categoria.");
    }

    public Resultado actualizar(int idAdmin, int idLocalidad, int disponibilidad, BigDecimal precio) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (disponibilidad < 0) return Resultado.error("La disponibilidad no puede ser negativa.");
        if (precio == null || precio.signum() < 0) return Resultado.error("El precio debe ser >= 0.");
        return localidadDAO.actualizar(idLocalidad, disponibilidad, precio)
                ? new Resultado(true, "Categoria #" + idLocalidad + " actualizada.")
                : Resultado.error("No se pudo actualizar (id no existe).");
    }

    public Resultado eliminar(int idAdmin, int idLocalidad) {
        Resultado guard = exigirAdmin(idAdmin);
        if (guard != null) return guard;

        if (localidadDAO.tieneVentas(idLocalidad)) {
            return Resultado.error("No se puede eliminar: la categoria ya tiene ventas.");
        }
        return localidadDAO.eliminar(idLocalidad)
                ? new Resultado(true, "Categoria #" + idLocalidad + " eliminada.")
                : Resultado.error("No se pudo eliminar (id no existe).");
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
}
