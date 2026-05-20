package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Partido;
import ec.edu.monster.persistencia.PartidoDAO;
import java.util.List;

/** Logica de negocio relacionada con partidos. */
public class PartidoService {

    private final PartidoDAO partidoDAO = new PartidoDAO();

    /** Partidos cuya FECHA aun no ha pasado (disponibles para vender boletos). */
    public List<Partido> listarDisponibles() {
        return partidoDAO.listarDisponibles();
    }
}
