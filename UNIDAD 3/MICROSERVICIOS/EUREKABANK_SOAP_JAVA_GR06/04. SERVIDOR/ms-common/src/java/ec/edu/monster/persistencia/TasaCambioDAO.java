package ec.edu.monster.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TasaCambioDAO {

    public double tasa(Connection cn, String origen, String destino) throws SQLException {
        if (origen == null || destino == null || origen.equals(destino)) {
            return 1.0d;
        }
        String sql = "SELECT dec_tasa FROM tasacambio "
                   + "WHERE chr_origen = ? AND chr_destino = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, origen);
            ps.setString(2, destino);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("dec_tasa");
                }
            }
        }
        throw new SQLException("No hay tasa de cambio configurada de "
                + origen + " a " + destino + ".");
    }
}
