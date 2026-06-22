package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.util.CalculadoraAmortizacion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a FACTURA, DETALLE_FACTURA y AMORTIZACION. */
public class FacturaDAO {

    private static final Logger LOG = Logger.getLogger(FacturaDAO.class.getName());
    public static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    private final LocalidadDAO localidadDAO = new LocalidadDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final BancoDAO bancoDAO = new BancoDAO();

    /** Resultado interno del registro de la compra. */
    public static final class ResultadoCompra {
        public final Factura factura;     // null si fallo
        public final String error;        // null si exito
        public ResultadoCompra(Factura f, String e) { this.factura = f; this.error = e; }
        public static ResultadoCompra ok(Factura f) { return new ResultadoCompra(f, null); }
        public static ResultadoCompra error(String e) { return new ResultadoCompra(null, e); }
    }

    /**
     * Registra una compra COMPLETA (carrito) en una sola factura, de forma
     * transaccional: por cada item resuelve la categoria via la seccion, valida
     * stock, descuenta disponibilidad e inserta el detalle. Luego calcula
     * SUBTOTAL/IVA/TOTAL. Si el pago es CREDITO, calcula y guarda la amortizacion.
     *
     * @param tipoPago      CONTADO | CREDITO
     * @param numCuotas     nro de cuotas (solo credito)
     * @param tasaInteres   tasa mensual, ej 0.02 (solo credito)
     * @param entrada       abono inicial (solo credito)
     */
    public ResultadoCompra registrarCompra(int idUsuario, List<ItemCarrito> items,
                                           String tipoPago, int numCuotas,
                                           BigDecimal tasaInteres, BigDecimal entrada) {
        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            cn.setAutoCommit(false);

            BigDecimal subtotal = BigDecimal.ZERO;
            List<DetalleFactura> detalles = new ArrayList<>();

            // 1) Procesar cada item del carrito
            for (ItemCarrito it : items) {
                if (it.getCantidad() <= 0) {
                    cn.rollback();
                    return ResultadoCompra.error("La cantidad de cada item debe ser mayor a cero.");
                }
                Localidad loc = localidadDAO.buscarPorSeccion(cn, it.getIdSeccion());
                if (loc == null) {
                    cn.rollback();
                    return ResultadoCompra.error("La seccion " + it.getIdSeccion() + " no existe.");
                }
                if (it.getCodigoPartido() != 0 && it.getCodigoPartido() != loc.getCodigoPartido()) {
                    cn.rollback();
                    return ResultadoCompra.error("La seccion " + it.getIdSeccion()
                            + " no pertenece al partido " + it.getCodigoPartido() + ".");
                }
                boolean ok = localidadDAO.disminuirDisponibilidadPorId(cn, loc.getId(), it.getCantidad());
                if (!ok) {
                    cn.rollback();
                    return ResultadoCompra.error("Disponibilidad insuficiente en " + loc.getCategoria()
                            + " del partido " + loc.getCodigoPartido() + ".");
                }
                BigDecimal lineaTotal = loc.getPrecio().multiply(BigDecimal.valueOf(it.getCantidad()));
                subtotal = subtotal.add(lineaTotal);

                DetalleFactura d = new DetalleFactura();
                d.setCodigoPartido(loc.getCodigoPartido());
                d.setIdSeccion(it.getIdSeccion());
                d.setCategoria(loc.getCategoria());
                d.setFila(it.getFila() == null ? "" : it.getFila());
                d.setAsientos(it.getAsientos() == null ? "" : it.getAsientos());
                d.setCantidad(it.getCantidad());
                d.setPrecioUnitario(loc.getPrecio());
                d.setTotal(lineaTotal);
                detalles.add(d);
            }

            BigDecimal iva   = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);

            // 2) Resolver datos de pago
            boolean credito = "CREDITO".equalsIgnoreCase(tipoPago);
            BigDecimal ent = (entrada == null) ? BigDecimal.ZERO : entrada.setScale(2, RoundingMode.HALF_UP);
            BigDecimal financiado = BigDecimal.ZERO;
            int cuotas = 0;
            BigDecimal tasa = BigDecimal.ZERO;
            List<Cuota> amort = new ArrayList<>();

            if (credito) {
                if (ent.signum() < 0 || ent.compareTo(total) >= 0) {
                    cn.rollback();
                    return ResultadoCompra.error("La entrada debe ser >= 0 y menor al total ($" + total + ").");
                }
                if (numCuotas <= 0) {
                    cn.rollback();
                    return ResultadoCompra.error("El credito requiere un numero de cuotas mayor a cero.");
                }
                cuotas = numCuotas;
                tasa = (tasaInteres == null) ? BigDecimal.ZERO : tasaInteres;
                financiado = total.subtract(ent).setScale(2, RoundingMode.HALF_UP);
                amort = CalculadoraAmortizacion.generar(financiado, tasa, cuotas, LocalDate.now());
            }

            // 3) Insertar FACTURA
            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            int idFactura;
            String sqlF =
                "INSERT INTO FACTURA (ID_USUARIO, FECHA, SUBTOTAL, IVA, TOTAL, MONEDA, " +
                "  TIPO_PAGO, ENTRADA, MONTO_FINANCIADO, NUM_CUOTAS, TASA_INTERES) " +
                "VALUES (?, ?, ?, ?, ?, 'USD', ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlF, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUsuario);
                ps.setTimestamp(2, ahora);
                ps.setBigDecimal(3, subtotal);
                ps.setBigDecimal(4, iva);
                ps.setBigDecimal(5, total);
                ps.setString(6, credito ? "CREDITO" : "CONTADO");
                ps.setBigDecimal(7, credito ? ent : BigDecimal.ZERO);
                ps.setBigDecimal(8, financiado);
                ps.setInt(9, cuotas);
                ps.setBigDecimal(10, tasa);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); idFactura = rs.getInt(1); }
            }

            // 4) Insertar DETALLES
            String sqlD =
                "INSERT INTO DETALLE_FACTURA " +
                "(ID_FACTURA, CODIGO_PARTIDO, ID_SECCION, CATEGORIA, FILA, ASIENTOS, CANTIDAD, PRECIO_UNITARIO, TOTAL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlD)) {
                for (DetalleFactura d : detalles) {
                    ps.setInt(1, idFactura);
                    ps.setInt(2, d.getCodigoPartido());
                    ps.setInt(3, d.getIdSeccion());
                    ps.setString(4, d.getCategoria());
                    ps.setString(5, d.getFila());
                    ps.setString(6, d.getAsientos());
                    ps.setInt(7, d.getCantidad());
                    ps.setBigDecimal(8, d.getPrecioUnitario());
                    ps.setBigDecimal(9, d.getTotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 5) Insertar AMORTIZACION (si credito)
            if (credito && !amort.isEmpty()) {
                String sqlA =
                    "INSERT INTO AMORTIZACION " +
                    "(ID_FACTURA, NUM_CUOTA, FECHA_VENCIMIENTO, SALDO_INICIAL, CUOTA, INTERES, ABONO_CAPITAL, SALDO_FINAL) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlA)) {
                    for (Cuota c : amort) {
                        ps.setInt(1, idFactura);
                        ps.setInt(2, c.getNumCuota());
                        ps.setString(3, c.getFechaVencimiento());
                        ps.setBigDecimal(4, c.getSaldoInicial());
                        ps.setBigDecimal(5, c.getCuota());
                        ps.setBigDecimal(6, c.getInteres());
                        ps.setBigDecimal(7, c.getAbonoCapital());
                        ps.setBigDecimal(8, c.getSaldoFinal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // 6) Ocupar (pagado) los asientos que el usuario tenia RESERVADO -> OCUPADO
            reservaDAO.ocuparDeUsuario(cn, idUsuario, idFactura);

            // 7) Movimiento bancario en la cuenta del usuario
            int idCuenta = bancoDAO.idCuentaDe(cn, idUsuario);
            if (idCuenta > 0) {
                if (credito) {
                    bancoDAO.registrarMovimiento(cn, idCuenta, "CREDITO", financiado,
                            "Credito por compra de entradas (factura #" + idFactura + ", " + cuotas + " cuotas)", idFactura);
                    bancoDAO.ajustarSaldo(cn, idCuenta, financiado);   // aumenta la deuda
                } else {
                    bancoDAO.registrarMovimiento(cn, idCuenta, "COMPRA_CONTADO", total,
                            "Compra de entradas al contado (factura #" + idFactura + ")", idFactura);
                }
            }

            cn.commit();

            Factura f = new Factura(idFactura, idUsuario, null, String.valueOf(ahora), subtotal, iva, total);
            f.setTipoPago(credito ? "CREDITO" : "CONTADO");
            f.setEntrada(credito ? ent : BigDecimal.ZERO);
            f.setMontoFinanciado(financiado);
            f.setNumCuotas(cuotas);
            f.setTasaInteres(tasa);
            for (DetalleFactura d : detalles) d.setIdFactura(idFactura);
            f.setDetalles(detalles);
            f.setAmortizacion(amort);
            return ResultadoCompra.ok(f);

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error registrando compra", e);
            if (cn != null) { try { cn.rollback(); } catch (SQLException ex) { } }
            return ResultadoCompra.error("Error de base de datos al registrar la compra.");
        } finally {
            if (cn != null) { try { cn.setAutoCommit(true); } catch (SQLException ex) { } }
            ConexionBD.desconectar(cn);
        }
    }

    // ------------------------------------------------------------------ consultas

    public List<Factura> listarPorUsuario(int idUsuario) {
        return listar("WHERE f.ID_USUARIO = ?", idUsuario);
    }

    public List<Factura> listarTodas() {
        return listar("", null);
    }

    private List<Factura> listar(String whereClause, Integer idUsuario) {
        List<Factura> out = new ArrayList<>();
        String sql =
            "SELECT f.ID_FACTURA, f.ID_USUARIO, u.NOMBRE AS USUARIO_NOMBRE, f.FECHA, " +
            "       f.SUBTOTAL, f.IVA, f.TOTAL, f.MONEDA, f.TIPO_PAGO, f.ENTRADA, " +
            "       f.MONTO_FINANCIADO, f.NUM_CUOTAS, f.TASA_INTERES " +
            "  FROM FACTURA f JOIN USUARIO u ON f.ID_USUARIO = u.ID_USUARIO " +
            (whereClause.isEmpty() ? "" : whereClause + " ") +
            " ORDER BY f.FECHA DESC";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            if (idUsuario != null) ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) out.add(mapFactura(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando facturas", e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    /** Factura completa (cabecera + detalles + amortizacion) para el comprobante. */
    public Factura buscarCompleta(int idFactura) {
        Factura f = null;
        String sql =
            "SELECT f.ID_FACTURA, f.ID_USUARIO, u.NOMBRE AS USUARIO_NOMBRE, f.FECHA, " +
            "       f.SUBTOTAL, f.IVA, f.TOTAL, f.MONEDA, f.TIPO_PAGO, f.ENTRADA, " +
            "       f.MONTO_FINANCIADO, f.NUM_CUOTAS, f.TASA_INTERES " +
            "  FROM FACTURA f JOIN USUARIO u ON f.ID_USUARIO = u.ID_USUARIO " +
            " WHERE f.ID_FACTURA = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idFactura);
            rs = ps.executeQuery();
            if (rs.next()) f = mapFactura(rs);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error buscando factura " + idFactura, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        if (f != null) {
            f.setDetalles(listarDetalles(idFactura));
            f.setAmortizacion(listarAmortizacion(idFactura));
        }
        return f;
    }

    public List<DetalleFactura> listarDetalles(int idFactura) {
        List<DetalleFactura> out = new ArrayList<>();
        String sql =
            "SELECT d.ID_DETALLE, d.ID_FACTURA, d.CODIGO_PARTIDO, d.ID_SECCION, d.CATEGORIA, " +
            "       d.FILA, d.ASIENTOS, d.CANTIDAD, d.PRECIO_UNITARIO, d.TOTAL, " +
            "       CONCAT(sl.NOMBRE, ' vs ', sv.NOMBRE) AS PARTIDO_DESC " +
            "  FROM DETALLE_FACTURA d " +
            "  JOIN PARTIDO_FUTBOL p ON p.CODIGO = d.CODIGO_PARTIDO " +
            "  JOIN SELECCION sl ON sl.ID_SELECCION = p.ID_LOCAL " +
            "  JOIN SELECCION sv ON sv.ID_SELECCION = p.ID_VISITA " +
            " WHERE d.ID_FACTURA = ? ORDER BY d.ID_DETALLE";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idFactura);
            rs = ps.executeQuery();
            while (rs.next()) {
                DetalleFactura d = new DetalleFactura(
                    rs.getInt("ID_DETALLE"), rs.getInt("ID_FACTURA"), rs.getInt("CODIGO_PARTIDO"),
                    rs.getInt("ID_SECCION"), rs.getString("CATEGORIA"), rs.getString("FILA"),
                    rs.getString("ASIENTOS"), rs.getInt("CANTIDAD"),
                    rs.getBigDecimal("PRECIO_UNITARIO"), rs.getBigDecimal("TOTAL"));
                d.setDescripcionPartido(rs.getString("PARTIDO_DESC"));
                out.add(d);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando detalles de factura " + idFactura, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    public List<Cuota> listarAmortizacion(int idFactura) {
        List<Cuota> out = new ArrayList<>();
        String sql =
            "SELECT NUM_CUOTA, FECHA_VENCIMIENTO, SALDO_INICIAL, CUOTA, INTERES, ABONO_CAPITAL, SALDO_FINAL " +
            "  FROM AMORTIZACION WHERE ID_FACTURA = ? ORDER BY NUM_CUOTA";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idFactura);
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new Cuota(
                    rs.getInt("NUM_CUOTA"), String.valueOf(rs.getDate("FECHA_VENCIMIENTO")),
                    rs.getBigDecimal("SALDO_INICIAL"), rs.getBigDecimal("CUOTA"),
                    rs.getBigDecimal("INTERES"), rs.getBigDecimal("ABONO_CAPITAL"),
                    rs.getBigDecimal("SALDO_FINAL")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando amortizacion de factura " + idFactura, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    private Factura mapFactura(ResultSet rs) throws SQLException {
        Factura f = new Factura(
            rs.getInt("ID_FACTURA"), rs.getInt("ID_USUARIO"), rs.getString("USUARIO_NOMBRE"),
            String.valueOf(rs.getTimestamp("FECHA")),
            rs.getBigDecimal("SUBTOTAL"), rs.getBigDecimal("IVA"), rs.getBigDecimal("TOTAL"));
        f.setMoneda(rs.getString("MONEDA"));
        f.setTipoPago(rs.getString("TIPO_PAGO"));
        f.setEntrada(rs.getBigDecimal("ENTRADA"));
        f.setMontoFinanciado(rs.getBigDecimal("MONTO_FINANCIADO"));
        f.setNumCuotas(rs.getInt("NUM_CUOTAS"));
        f.setTasaInteres(rs.getBigDecimal("TASA_INTERES"));
        return f;
    }
}
