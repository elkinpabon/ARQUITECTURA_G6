package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Asiento;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.ReservaDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.util.List;

/** Logica de negocio de reservas/estado de asientos en tiempo real. */
public class ReservaService {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Asientos NO libres (reservados/ocupados) de una seccion. */
    public List<Asiento> asientosNoLibres(int idSeccion) {
        return reservaDAO.listarNoLibres(idSeccion);
    }

    /** Reserva un asiento (estado RESERVADO) para el usuario. */
    public Resultado reservarAsiento(int idUsuario, int idSeccion, String fila, String asiento) {
        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) return Resultado.error("Usuario no autenticado.");
        if (vacio(fila) || vacio(asiento)) return Resultado.error("Fila y asiento son obligatorios.");
        boolean ok = reservaDAO.reservar(idUsuario, idSeccion, fila.trim(), asiento.trim());
        return ok
                ? new Resultado(true, "Asiento " + fila + "-" + asiento + " reservado.")
                : Resultado.error("El asiento " + fila + "-" + asiento + " ya no esta libre.");
    }

    /** Libera un asiento RESERVADO del propio usuario. */
    public Resultado liberarAsiento(int idUsuario, int idSeccion, String fila, String asiento) {
        boolean ok = reservaDAO.liberar(idUsuario, idSeccion, fila, asiento);
        return ok
                ? new Resultado(true, "Asiento liberado.")
                : Resultado.error("No habia una reserva tuya para ese asiento.");
    }

    /** Libera todas las reservas (no pagadas) del usuario. */
    public Resultado liberarTodas(int idUsuario) {
        int n = reservaDAO.liberarTodasDeUsuario(idUsuario);
        return new Resultado(true, n + " asiento(s) liberado(s).");
    }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
}
