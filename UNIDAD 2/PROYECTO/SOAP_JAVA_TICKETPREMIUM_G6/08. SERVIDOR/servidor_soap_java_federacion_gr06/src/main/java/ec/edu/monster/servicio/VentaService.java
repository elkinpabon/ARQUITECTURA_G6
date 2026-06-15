package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Seccion;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.FacturaDAO;
import ec.edu.monster.persistencia.LocalidadDAO;
import ec.edu.monster.persistencia.PartidoDAO;
import ec.edu.monster.persistencia.SeccionDAO;
import ec.edu.monster.persistencia.UsuarioDAO;
import ec.edu.monster.util.CalculadoraAmortizacion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/** Logica de negocio de la venta de boletos (carrito + pago contado/credito). */
public class VentaService {

    private final PartidoDAO   partidoDAO   = new PartidoDAO();
    private final LocalidadDAO localidadDAO = new LocalidadDAO();
    private final SeccionDAO   seccionDAO   = new SeccionDAO();
    private final FacturaDAO   facturaDAO   = new FacturaDAO();
    private final UsuarioDAO   usuarioDAO   = new UsuarioDAO();

    /**
     * Compra por CARRITO: una factura con N items. Soporta CONTADO o CREDITO
     * (con tabla de amortizacion). Valida usuario, items, y que ningun partido
     * del carrito se haya jugado ya.
     */
    public Resultado registrarCompra(int idUsuario, List<ItemCarrito> items,
                                     String tipoPago, int numCuotas,
                                     BigDecimal tasaInteres, BigDecimal entrada) {

        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) return Resultado.error("Usuario no autenticado o inexistente.");

        if (items == null || items.isEmpty()) {
            return Resultado.error("El carrito esta vacio.");
        }

        // Nota: se permite comprar entradas de cualquier partido del torneo
        // (modelo marketplace tipo StubHub), incluso si la fecha de inicio ya paso.
        // La validez del partido y el stock los valida FacturaDAO.registrarCompra.

        FacturaDAO.ResultadoCompra rc = facturaDAO.registrarCompra(
                idUsuario, items, tipoPago, numCuotas, tasaInteres, entrada);

        if (rc.factura == null) return Resultado.error(rc.error);

        String tipo = rc.factura.getTipoPago();
        String msg = "Compra registrada. Factura #" + rc.factura.getIdFactura()
                + " (" + tipo + ", total $" + rc.factura.getTotal() + ")";
        if ("CREDITO".equalsIgnoreCase(tipo)) {
            msg += " - " + rc.factura.getNumCuotas() + " cuotas, financiado $"
                 + rc.factura.getMontoFinanciado();
        }
        return Resultado.ok(msg, rc.factura);
    }

    /**
     * SIMULA un credito (tabla de amortizacion francesa) SIN persistir nada, usando
     * EXACTAMENTE la misma matematica que la compra real (IVA 15%, total, financiado,
     * {@link CalculadoraAmortizacion}). Devuelve una Factura transitoria con la tabla,
     * para que el cliente pueda revisar las cuotas antes de comprar.
     */
    public Resultado simularCredito(BigDecimal subtotal, BigDecimal entrada,
                                    int numCuotas, BigDecimal tasaMensual) {
        if (subtotal == null || subtotal.signum() <= 0) {
            return Resultado.error("El carrito esta vacio.");
        }
        if (numCuotas <= 0) {
            return Resultado.error("El credito requiere un numero de cuotas mayor a cero.");
        }
        BigDecimal iva   = subtotal.multiply(FacturaDAO.TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);

        BigDecimal ent = (entrada == null) ? BigDecimal.ZERO : entrada.setScale(2, RoundingMode.HALF_UP);
        if (ent.signum() < 0 || ent.compareTo(total) >= 0) {
            return Resultado.error("La entrada debe ser >= 0 y menor al total ($" + total + ").");
        }
        BigDecimal tasa = (tasaMensual == null) ? BigDecimal.ZERO : tasaMensual;
        BigDecimal financiado = total.subtract(ent).setScale(2, RoundingMode.HALF_UP);

        Factura f = new Factura();
        f.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        f.setIva(iva);
        f.setTotal(total);
        f.setMoneda("USD");
        f.setTipoPago("CREDITO");
        f.setEntrada(ent);
        f.setMontoFinanciado(financiado);
        f.setNumCuotas(numCuotas);
        f.setTasaInteres(tasa);
        f.setAmortizacion(CalculadoraAmortizacion.generar(financiado, tasa, numCuotas, LocalDate.now()));
        return Resultado.ok("Simulacion generada.", f);
    }

    /**
     * Venta SIMPLE (rubrica): un solo item, a CONTADO. Recibe la categoria
     * (Cat 1-4) y elige automaticamente la primera seccion disponible de esa
     * categoria para el partido.
     */
    public Resultado registrarVenta(int idUsuario, int codigoPartido,
                                    String categoria, int cantidad) {
        if (cantidad <= 0) return Resultado.error("La cantidad debe ser mayor a cero.");

        Localidad loc = localidadDAO.buscar(codigoPartido, categoria);
        if (loc == null) {
            return Resultado.error("La categoria " + categoria
                    + " no esta definida para el partido " + codigoPartido + ".");
        }
        List<Seccion> secciones = seccionDAO.listarPorLocalidad(loc.getId());
        if (secciones.isEmpty()) {
            return Resultado.error("La categoria " + categoria + " no tiene secciones configuradas.");
        }
        ItemCarrito item = new ItemCarrito(codigoPartido, secciones.get(0).getIdSeccion(),
                cantidad, "", "");
        return registrarCompra(idUsuario, List.of(item), "CONTADO", 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public List<Factura> listarFacturasPorUsuario(int idUsuario) {
        return facturaDAO.listarPorUsuario(idUsuario);
    }

    /** Todas las facturas del sistema (solo ADMIN, p.ej. monster). */
    public List<Factura> listarTodasFacturas(int idAdmin) {
        Usuario u = usuarioDAO.buscarPorId(idAdmin);
        if (u == null || !"ADMIN".equalsIgnoreCase(u.getRol())) {
            return Collections.emptyList();
        }
        return facturaDAO.listarTodas();
    }

    /**
     * Comprobante completo (detalles + amortizacion) de una factura. El
     * solicitante debe ser el dueno de la factura o un ADMIN.
     */
    public Factura verComprobante(int idSolicitante, int idFactura) {
        Usuario u = usuarioDAO.buscarPorId(idSolicitante);
        if (u == null) return null;
        Factura f = facturaDAO.buscarCompleta(idFactura);
        if (f == null) return null;
        boolean admin = "ADMIN".equalsIgnoreCase(u.getRol());
        if (!admin && f.getIdUsuario() != idSolicitante) return null; // no puede ver ajenas
        return f;
    }

    public List<DetalleFactura> detalleFactura(int idFactura) {
        return facturaDAO.listarDetalles(idFactura);
    }

    public List<Cuota> amortizacionDe(int idFactura) {
        return facturaDAO.listarAmortizacion(idFactura);
    }
}
