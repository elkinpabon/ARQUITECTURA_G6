package ec.edu.monster.ws;

import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ResumenLocalidad;
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
        u.setId(integer(o, "id"));
        u.setUsuario(str(o, "usuario"));
        u.setNombre(str(o, "nombre"));
        u.setRol(str(o, "rol"));
        return u;
    }

    static SesionResultado toSesionResultado(SoapObject o) {
        SesionResultado r = new SesionResultado();
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
        p.setLugar(str(o, "lugar"));
        return p;
    }

    static Localidad toLocalidad(SoapObject o) {
        Localidad l = new Localidad();
        l.setId(integer(o, "id"));
        l.setCodigoPartido(integer(o, "codigoPartido"));
        l.setCodigoLocalidad(str(o, "codigoLocalidad"));
        l.setDisponibilidad(integer(o, "disponibilidad"));
        l.setPrecio(dec(o, "precio"));
        return l;
    }

    static Factura toFactura(SoapObject o) {
        Factura f = new Factura();
        f.setIdFactura(integer(o, "idFactura"));
        f.setIdUsuario(integer(o, "idUsuario"));
        f.setUsuarioNombre(str(o, "usuarioNombre"));
        f.setFecha(str(o, "fecha"));
        f.setSubtotal(dec(o, "subtotal"));
        f.setIva(dec(o, "iva"));
        f.setTotal(dec(o, "total"));
        return f;
    }

    static Resultado toResultado(SoapObject o) {
        Resultado r = new Resultado();
        r.setExito(bool(o, "exito"));
        r.setMensaje(str(o, "mensaje"));
        SoapObject f = obj(o, "factura");
        if (f != null) r.setFactura(toFactura(f));
        return r;
    }

    static ResumenLocalidad toResumen(SoapObject o) {
        ResumenLocalidad r = new ResumenLocalidad();
        r.setLocalidad(str(o, "localidad"));
        r.setVendidos(integer(o, "vendidos"));
        r.setTotalRecaudado(dec(o, "totalRecaudado"));
        return r;
    }
}
