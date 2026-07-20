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
            ps.setString(5, usuario);
            ps.setString(6, estado);
            ps.executeUpdate();
        }
    }
}
