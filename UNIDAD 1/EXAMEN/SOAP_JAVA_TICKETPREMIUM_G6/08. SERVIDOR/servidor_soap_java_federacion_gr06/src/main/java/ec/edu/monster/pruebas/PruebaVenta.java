package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.LocalidadService;
import ec.edu.monster.servicio.VentaService;
import java.math.BigDecimal;
import java.util.List;

/**
 * Prueba la capa de servicio de ventas (VentaService).
 *
 * Flujo end-to-end:
 *   1) Lee la disponibilidad de la localidad GENERAL del partido 1 ANTES.
 *   2) Vende 2 boletos a idUsuario=2 (josue).
 *   3) Lee la disponibilidad DESPUES y verifica el decremento.
 *   4) Verifica los calculos:  subtotal = precio*2, iva = subtotal*0.15.
 *   5) Llama a misFacturas(2) y verifica que la nueva factura aparece.
 *
 *  OJO: cada ejecucion inserta 1 factura mas y descuenta stock. Para limpiar:
 *       mysql -u root -padmin2002 < "02. MER/03. FISICO/script_ticketpremium.sql"
 */
public class PruebaVenta {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" PRUEBA 5/6 - VentaService.registrarVenta + misFacturas");
        System.out.println("================================================================");

        int idUsuario = 2;             // josue (CLIENTE)
        int codigoPartido = 1;
        String localidad = "GENERAL";
        int cantidad = 2;
        int fallos = 0;

        VentaService    ventaService    = new VentaService();
        LocalidadService localidadService = new LocalidadService();

        // 1) Estado ANTES
        int dispAntes = disponibilidadDe(localidadService, codigoPartido, localidad);
        BigDecimal precio = precioDe(localidadService, codigoPartido, localidad);
        int facturasAntes = ventaService.listarFacturasPorUsuario(idUsuario).size();
        System.out.printf("  ANTES  -> disponibilidad=%d  precio=%s  facturas usuario=%d%n",
                dispAntes, precio, facturasAntes);

        // 2) Compra
        Resultado r = ventaService.registrarVenta(idUsuario, codigoPartido, localidad, cantidad);
        System.out.printf("%n  registrarVenta(uid=%d, partido=%d, %s, x%d) -> %s%n",
                idUsuario, codigoPartido, localidad, cantidad, r.getMensaje());

        if (!r.isExito()) {
            System.err.println("  [FAIL] La venta no se concreto.");
            System.exit(1);
        }
        Factura f = r.getFactura();
        System.out.printf("    factura: id=%d  subtotal=%s  iva=%s  total=%s%n",
                f.getIdFactura(), f.getSubtotal(), f.getIva(), f.getTotal());

        // 3) Estado DESPUES
        int dispDespues = disponibilidadDe(localidadService, codigoPartido, localidad);
        int facturasDespues = ventaService.listarFacturasPorUsuario(idUsuario).size();
        System.out.printf("  DESPUES-> disponibilidad=%d  facturas usuario=%d%n",
                dispDespues, facturasDespues);

        // 4) Validaciones
        BigDecimal subEsperado = precio.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal ivaEsperado = subEsperado.multiply(new BigDecimal("0.15"))
                                            .setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal totEsperado = subEsperado.add(ivaEsperado);

        fallos += verificar("Disponibilidad decrementada en " + cantidad,
                dispDespues == dispAntes - cantidad);
        fallos += verificar("Subtotal correcto",
                f.getSubtotal().compareTo(subEsperado) == 0);
        fallos += verificar("IVA correcto (15%)",
                f.getIva().compareTo(ivaEsperado) == 0);
        fallos += verificar("Total correcto",
                f.getTotal().compareTo(totEsperado) == 0);
        fallos += verificar("Factura aparece en historial",
                facturasDespues == facturasAntes + 1);

        // 5) Caso negativo: cantidad invalida
        Resultado neg = ventaService.registrarVenta(idUsuario, codigoPartido, localidad, 0);
        fallos += verificar("Rechaza cantidad <= 0", !neg.isExito());

        System.out.println("================================================================");
        System.out.println(" RESULTADO: " + (fallos == 0 ? "PASS" : "FAIL (" + fallos + " casos)"));
        if (fallos != 0) System.exit(1);
    }

    private static int disponibilidadDe(LocalidadService s, int codigoPartido, String localidad) {
        for (Localidad l : s.listarPorPartido(codigoPartido)) {
            if (l.getCodigoLocalidad().equals(localidad)) return l.getDisponibilidad();
        }
        return -1;
    }

    private static BigDecimal precioDe(LocalidadService s, int codigoPartido, String localidad) {
        for (Localidad l : s.listarPorPartido(codigoPartido)) {
            if (l.getCodigoLocalidad().equals(localidad)) return l.getPrecio();
        }
        return null;
    }

    private static int verificar(String descripcion, boolean ok) {
        System.out.printf("    [%s] %s%n", ok ? "OK  " : "FAIL", descripcion);
        return ok ? 0 : 1;
    }
}
