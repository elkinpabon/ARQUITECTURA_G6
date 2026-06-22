package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Estadio;
import ec.edu.monster.modelo.Seleccion;
import ec.edu.monster.persistencia.EstadioDAO;
import ec.edu.monster.persistencia.SeleccionDAO;
import java.util.List;

/** Catalogos de apoyo (estadios y selecciones) para clientes y panel admin. */
public class CatalogoService {

    private final EstadioDAO   estadioDAO   = new EstadioDAO();
    private final SeleccionDAO seleccionDAO = new SeleccionDAO();

    public List<Estadio> listarEstadios()       { return estadioDAO.listar(); }
    public List<Seleccion> listarSelecciones()  { return seleccionDAO.listar(); }
}
