package ec.edu.monster.persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Persiste la trazabilidad de las operaciones finalizadas por Bonita BPM. */
public class AuditoriaBpmDAO {

    public void registrar(String operacion, String cuentaOrigen, String cuentaDestino,
                          String monto, String usuario, String estado) throws SQLException {
        String sql = "INSERT INTO auditoria_bpm "
                + "(operacion, cuenta_origen, cuenta_destino, monto, usuario_bpm, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, operacion);
            ps.setString(2, cuentaOrigen);
            ps.setString(3, cuentaDestino == null || cuentaDestino.isBlank() ? "N/A" : cuentaDestino);
            ps.setBigDecimal(4, monto == null || monto.isBlank() ? BigDecimal.ZERO : new BigDecimal(monto));
            ps.setString(5, usuario == null || usuario.isBlank() ? "GR06" : usuario);
            ps.setString(6, estado == null || estado.isBlank() ? "FINALIZADO" : estado);
            ps.executeUpdate();
        }
    }

    public void registrarSiNoExisteReciente(String operacion, String cuentaOrigen,
                                             String cuentaDestino, String monto,
                                             String usuario, String estado) throws SQLException {
        String destino = cuentaDestino == null || cuentaDestino.isBlank() ? "N/A" : cuentaDestino;
        BigDecimal importe = monto == null || monto.isBlank() ? BigDecimal.ZERO : new BigDecimal(monto);
        String usuarioBpm = usuario == null || usuario.isBlank() ? "GR06" : usuario;
        String estadoFinal = estado == null || estado.isBlank() ? "FINALIZADO" : estado;
        String sql = "INSERT INTO auditoria_bpm "
                + "(operacion, cuenta_origen, cuenta_destino, monto, usuario_bpm, estado) "
                + "SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS ("
                + "SELECT 1 FROM auditoria_bpm WHERE operacion = ? AND cuenta_origen = ? "
                + "AND cuenta_destino = ? AND monto = ? AND usuario_bpm = ? AND estado = ? "
                + "AND fecha >= DATE_SUB(NOW(), INTERVAL 10 SECOND))";
        try (Connection cn = ConexionBD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, operacion);
            ps.setString(2, cuentaOrigen);
            ps.setString(3, destino);
            ps.setBigDecimal(4, importe);
            ps.setString(5, usuarioBpm);
            ps.setString(6, estadoFinal);
            ps.setString(7, operacion);
            ps.setString(8, cuentaOrigen);
            ps.setString(9, destino);
            ps.setBigDecimal(10, importe);
            ps.setString(11, usuarioBpm);
            ps.setString(12, estadoFinal);
            ps.executeUpdate();
        }
    }
}
