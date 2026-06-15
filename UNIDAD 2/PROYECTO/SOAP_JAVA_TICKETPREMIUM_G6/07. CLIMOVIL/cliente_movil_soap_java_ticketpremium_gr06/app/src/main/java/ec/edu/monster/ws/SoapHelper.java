package ec.edu.monster.ws;

import ec.edu.monster.modelo.Asiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Seccion;
import ec.edu.monster.modelo.SesionResultado;
import ec.edu.monster.modelo.Usuario;
import java.math.BigDecimal;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

/** Conversion SoapObject -> POJO para las respuestas del WSFederacion. */
final class SoapHelper {

    private SoapHelper() { }

    // ---------- Primitivas ----------
    static String str(SoapObject o, String prop) {
        try {
            Object v = o.getProperty(prop);
            if (v == null) return "";
            if (v instanceof SoapPrimitive) return v.toString();
            return v.toString();
        } catch (Exception e) {
            return "";
        }
    }

    static int integer(SoapObject o, String prop) {
        try {
            return Integer.parseInt(str(o, prop));
        } catch (Exception e) {
            return 0;
        }
    }

    static boolean bool(SoapObject o, String prop) {
        try {
            return Boolean.parseBoolean(str(o, prop));
        } catch (Exception e) {
            return false;
        }
    }

    static BigDecimal dec(SoapObject o, String prop) {
        try {
            String s = str(o, prop);
            return s.isEmpty() ? BigDecimal.ZERO : new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    static SoapObject obj(SoapObject o, String prop) {
        try {
            Object v = o.getProperty(prop);
            return v instanceof SoapObject ? (SoapObject) v : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- Modelos ----------
    static Usuario toUsuario(SoapObject o) {
        if (o == null) return null;
        Usuario u = new Usuario();
        u.setId(integer(o, "idUsuario"));
        u.setUsuario(str(o, "usuario"));
        u.setNombre(str(o, "nombre"));
        u.setRol(str(o, "rol"));
        return u;
    }

    static SesionResultado toSesionResultado(SoapObject o) {
        SesionResultado r = new SesionResultado();
        if (o == null) return r;
        r.setExito(bool(o, "exito"));
        r.setMensaje(str(o, "mensaje"));
        r.setUsuario(toUsuario(obj(o, "usuario")));
        return r;
    }

    static Partido toPartido(SoapObject o) {
        Partido p = new Partido();
        p.setCodigo(integer(o, "codigo"));
        p.setEquipoLocal(str(o, "equipoLocal"));
        p.setEquipoVisita(str(o, "equipoVisita"));
        p.setFecha(str(o, "fecha"));
        p.setGrupo(str(o, "grupo"));
        p.setEstadio(str(o, "estadio"));
        p.setCiudad(str(o, "ciudad"));
        p.setPais(str(o, "pais"));
        p.setLugar(str(o, "lugar"));
        return p;
    }

    static Localidad toLocalidad(SoapObject o) {
        Localidad l = new Localidad();
        l.setId(integer(o, "id"));
        l.setCodigoPartido(integer(o, "codigoPartido"));
        l.setCategoria(str(o, "categoria"));
        l.setDisponibilidad(integer(o, "disponibilidad"));
        l.setPrecio(dec(o, "precio"));
        return l;
    }

    static Seccion toSeccion(SoapObject o) {
        Seccion s = new Seccion();
        s.setIdSeccion(integer(o, "idSeccion"));
        s.setIdLocalidad(integer(o, "idLocalidad"));
        s.setCodigoSeccion(str(o, "codigoSeccion"));
        s.setNumFilas(integer(o, "numFilas"));
        s.setAsientosPorFila(integer(o, "asientosPorFila"));
        return s;
    }

    static Asiento toAsiento(SoapObject o) {
        Asiento a = new Asiento();
        a.setIdSeccion(integer(o, "idSeccion"));
        a.setFila(str(o, "fila"));
        a.setAsiento(str(o, "asiento"));
        a.setEstado(str(o, "estado"));
        return a;
    }

    static Cuota toCuota(SoapObject o) {
        Cuota c = new Cuota();
        c.setNumCuota(integer(o, "numCuota"));
        c.setFechaVencimiento(str(o, "fechaVencimiento"));
        c.setSaldoInicial(dec(o, "saldoInicial"));
        c.setCuota(dec(o, "cuota"));
        c.setInteres(dec(o, "interes"));
        c.setAbonoCapital(dec(o, "abonoCapital"));
        c.setSaldoFinal(dec(o, "saldoFinal"));
        return c;
    }

    static DetalleFactura toDetalle(SoapObject o) {
        DetalleFactura d = new DetalleFactura();
        d.setIdDetalle(integer(o, "idDetalle"));
        d.setIdFactura(integer(o, "idFactura"));
        d.setCodigoPartido(integer(o, "codigoPartido"));
        d.setIdSeccion(integer(o, "idSeccion"));
        d.setCategoria(str(o, "categoria"));
        d.setFila(str(o, "fila"));
        d.setAsientos(str(o, "asientos"));
        d.setCantidad(integer(o, "cantidad"));
        d.setPrecioUnitario(dec(o, "precioUnitario"));
        d.setTotal(dec(o, "total"));
        d.setDescripcionPartido(str(o, "descripcionPartido"));
        return d;
    }

    /**
     * Factura completa. Las listas anidadas llegan como elementos repetidos
     * "detalles" / "amortizacion": se distinguen por sus propiedades
     * (una Cuota siempre trae numCuota; un DetalleFactura trae precioUnitario).
     */
    static Factura toFactura(SoapObject o) {
        Factura f = new Factura();
        if (o == null) return f;
        f.setIdFactura(integer(o, "idFactura"));
        f.setIdUsuario(integer(o, "idUsuario"));
        f.setUsuarioNombre(str(o, "usuarioNombre"));
        f.setFecha(str(o, "fecha"));
        f.setSubtotal(dec(o, "subtotal"));
        f.setIva(dec(o, "iva"));
        f.setTotal(dec(o, "total"));
        f.setMoneda(str(o, "moneda"));
        f.setTipoPago(str(o, "tipoPago"));
        f.setEntrada(dec(o, "entrada"));
        f.setMontoFinanciado(dec(o, "montoFinanciado"));
        f.setNumCuotas(integer(o, "numCuotas"));
        f.setTasaInteres(dec(o, "tasaInteres"));

        for (int i = 0; i < o.getPropertyCount(); i++) {
            Object v;
            try { v = o.getProperty(i); } catch (Exception e) { continue; }
            if (!(v instanceof SoapObject)) continue;
            SoapObject so = (SoapObject) v;
            if (so.hasProperty("numCuota")) {
                f.getAmortizacion().add(toCuota(so));
            } else if (so.hasProperty("precioUnitario") || so.hasProperty("idDetalle")) {
                f.getDetalles().add(toDetalle(so));
            }
        }
        return f;
    }

    static Resultado toResultado(SoapObject o) {
        Resultado r = new Resultado();
        if (o == null) {
            r.setExito(false);
            r.setMensaje("Respuesta vacia del servidor");
            return r;
        }
        r.setExito(bool(o, "exito"));
        r.setMensaje(str(o, "mensaje"));
        SoapObject f = obj(o, "factura");
        if (f != null) r.setFactura(toFactura(f));
        return r;
    }

    static Cuenta toCuenta(SoapObject o) {
        Cuenta c = new Cuenta();
        if (o == null) return c;
        c.setIdCuenta(integer(o, "idCuenta"));
        c.setIdUsuario(integer(o, "idUsuario"));
        c.setNumero(str(o, "numero"));
        c.setSaldo(dec(o, "saldo"));
        return c;
    }

    static Movimiento toMovimiento(SoapObject o) {
        Movimiento m = new Movimiento();
        m.setIdMovimiento(integer(o, "idMovimiento"));
        m.setIdCuenta(integer(o, "idCuenta"));
        m.setFecha(str(o, "fecha"));
        m.setTipo(str(o, "tipo"));
        m.setMonto(dec(o, "monto"));
        m.setDescripcion(str(o, "descripcion"));
        m.setIdFactura(integer(o, "idFactura"));
        return m;
    }
}
