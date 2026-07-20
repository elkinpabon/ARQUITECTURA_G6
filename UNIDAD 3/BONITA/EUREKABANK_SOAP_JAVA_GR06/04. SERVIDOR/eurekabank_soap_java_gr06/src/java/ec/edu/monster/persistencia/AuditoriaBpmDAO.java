package ec.edu.monster.persistencia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            ps.setString(5, usuario);
            ps.setString(6, estado);
            ps.executeUpdate();
        }
    }

    /** Evita duplicar el registro generado por el servicio y el conector BPM. */
    public void registrarSiNoExisteReciente(String operacion, String cuentaOrigen,
                                             String cuentaDestino, String monto,
                                             String usuario, String estado) throws SQLException {
        String destino = cuentaDestino == null || cuentaDestino.isBlank() ? "N/A" : cuentaDestino;
        BigDecimal importe = monto == null || monto.isBlank() ? BigDecimal.ZERO : new BigDecimal(monto);
        String existeSql = "SELECT 1 FROM auditoria_bpm "
                + "WHERE operacion = ? AND cuenta_origen = ? AND cuenta_destino = ? "
                + "AND monto = ? AND usuario_bpm = ? AND estado = ? "
                + "AND fecha >= DATE_SUB(NOW(), INTERVAL 10 SECOND) LIMIT 1";

        try (Connection cn = ConexionBD.conectar();
             PreparedStatement existe = cn.prepareStatement(existeSql)) {
            existe.setString(1, operacion);
            existe.setString(2, cuentaOrigen);
            existe.setString(3, destino);
            existe.setBigDecimal(4, importe);
            existe.setString(5, usuario);
            existe.setString(6, estado);
            try (ResultSet rs = existe.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }
        registrar(operacion, cuentaOrigen, destino, importe.toPlainString(), usuario, estado);
    }
}
