package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Seccion;
import ec.edu.monster.persistencia.SeccionDAO;
import java.util.List;

/** Logica de negocio de secciones (StubHub). */
public class SeccionService {

    private final SeccionDAO seccionDAO = new SeccionDAO();

    public List<Seccion> listarPorLocalidad(int idLocalidad) {
        return seccionDAO.listarPorLocalidad(idLocalidad);
    }

    public List<Seccion> listarPorPartido(int codigoPartido) {
        return seccionDAO.listarPorPartido(codigoPartido);
    }
}
