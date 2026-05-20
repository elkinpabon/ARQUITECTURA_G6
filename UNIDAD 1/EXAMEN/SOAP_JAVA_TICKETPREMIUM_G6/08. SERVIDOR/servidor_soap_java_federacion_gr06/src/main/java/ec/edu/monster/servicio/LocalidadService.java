package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.persistencia.LocalidadDAO;
import java.util.List;

/** Logica de negocio relacionada con localidades. */
public class LocalidadService {

    private final LocalidadDAO localidadDAO = new LocalidadDAO();

    /** Localidades de un partido con disponibilidad > 0. */
    public List<Localidad> listarPorPartido(int codigoPartido) {
        return localidadDAO.listarDisponiblesPorPartido(codigoPartido);
    }
}
