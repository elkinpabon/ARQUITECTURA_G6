package ec.edu.monster.servicio;

import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.persistencia.ReporteDAO;
import java.util.List;

/** Logica de negocio para reportes. */
public class ReporteService {

    private final ReporteDAO reporteDAO = new ReporteDAO();

    /** Reporte "Resumen de Ventas de un Partido" agrupado por localidad. */
    public List<ResumenLocalidad> resumenVentasPorPartido(int codigoPartido) {
        return reporteDAO.resumenVentasPorPartido(codigoPartido);
    }
}
