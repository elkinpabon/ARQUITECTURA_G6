package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.FacturaDAO;
import ec.edu.monster.persistencia.LocalidadDAO;
import ec.edu.monster.persistencia.PartidoDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/** Logica de negocio para la venta de boletos. */
public class VentaService {

    private final PartidoDAO   partidoDAO   = new PartidoDAO();
    private final LocalidadDAO localidadDAO = new LocalidadDAO();
    private final FacturaDAO   facturaDAO   = new FacturaDAO();
    private final UsuarioDAO   usuarioDAO   = new UsuarioDAO();

    /**
     * Registra una venta para una localidad/partido:
     *  - valida usuario, partido y disponibilidad
     *  - valida que el partido aun no se haya jugado
     *  - calcula subtotal, IVA (15%) y total
     *  - inserta FACTURA + DETALLE_FACTURA (en transaccion)
     *  - decrementa la disponibilidad de la localidad
     */
    public Resultado registrarVenta(int idUsuario, int codigoPartido,
                                    String codigoLocalidad, int cantidad) {

        if (cantidad <= 0) {
            return Resultado.error("La cantidad debe ser mayor a cero.");
        }

        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) {
            return Resultado.error("Usuario no autenticado o inexistente.");
        }

        Partido partido = partidoDAO.buscarPorCodigo(codigoPartido);
        if (partido == null) {
            return Resultado.error("El partido " + codigoPartido + " no existe.");
        }

        // Defensa profunda: no permitir vender para partidos pasados
        Timestamp t = partidoDAO.fechaDe(codigoPartido);
        if (t != null && t.toLocalDateTime().isBefore(LocalDateTime.now())) {
            return Resultado.error("El partido ya se jugo. No se pueden vender mas boletos.");
        }

        Localidad localidad = localidadDAO.buscar(codigoPartido, codigoLocalidad);
        if (localidad == null) {
            return Resultado.error("La localidad " + codigoLocalidad +
                    " no esta definida para el partido " + codigoPartido + ".");
        }
        if (localidad.getDisponibilidad() < cantidad) {
            return Resultado.error("Disponibilidad insuficiente. Quedan " +
                    localidad.getDisponibilidad() + " boletos en " + codigoLocalidad + ".");
        }

        Factura factura = facturaDAO.registrarVentaSimple(
                idUsuario, codigoPartido, codigoLocalidad, cantidad,
                localidad.getPrecio(), localidadDAO);

        if (factura == null) {
            return Resultado.error("No se pudo completar la venta (conflicto de disponibilidad o error de BD).");
        }
        return Resultado.ok("Venta registrada. Factura #" + factura.getIdFactura(), factura);
    }

    /** Historial de facturas de un usuario. */
    public List<Factura> listarFacturasPorUsuario(int idUsuario) {
        return facturaDAO.listarPorUsuario(idUsuario);
    }
}
