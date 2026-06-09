package ec.edu.monster.servicio;

import ec.edu.monster.modelo.MovimientoModel;
import ec.edu.monster.persistencia.MovimientoDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Logica de negocio de movimientos. */
public class MovimientoService {

    private static final Logger LOG = Logger.getLogger(MovimientoService.class.getName());
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();

    public List<MovimientoModel> listarMovimientos(String codigoCuenta) {
        if (codigoCuenta == null || codigoCuenta.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return movimientoDAO.listarPorCuenta(codigoCuenta.trim());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando movimientos", e);
            return Collections.emptyList();
        }
    }
}
