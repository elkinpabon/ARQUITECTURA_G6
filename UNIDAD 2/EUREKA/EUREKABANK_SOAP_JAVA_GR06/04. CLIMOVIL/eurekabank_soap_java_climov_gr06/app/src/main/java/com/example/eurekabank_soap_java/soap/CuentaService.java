package com.example.eurekabank_soap_java.soap;

import com.example.eurekabank_soap_java.config.ServidorConfig;
import com.example.eurekabank_soap_java.modelo.ClienteResumen;
import com.example.eurekabank_soap_java.modelo.CuentaResumen;
import com.example.eurekabank_soap_java.modelo.Resultado;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/** WSCuenta vía ksoap2 (mismas operaciones que el cliente web). */
public class CuentaService {

    private Resultado parseResultado(Object body) {
        Resultado r = new Resultado();
        for (Object o : SoapBase.returns(body)) {
            if (o instanceof SoapObject) {
                SoapObject s = (SoapObject) o;
                r.setExito("true".equalsIgnoreCase(SoapBase.prop(s, "exito")));
                r.setMensaje(SoapBase.prop(s, "mensaje"));
                r.setSaldo(SoapBase.propD(s, "saldo"));
            }
            break;
        }
        return r;
    }

    public List<CuentaResumen> listarCuentasPorCliente(String criterio) throws Exception {
        Object body = SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("listarCuentasPorCliente", "cliente", criterio));
        List<CuentaResumen> out = new ArrayList<>();
        for (Object o : SoapBase.returns(body)) {
            if (!(o instanceof SoapObject)) continue;
            SoapObject s = (SoapObject) o;
            CuentaResumen c = new CuentaResumen();
            c.setCodigoCuenta(SoapBase.prop(s, "codigoCuenta"));
            c.setMoneda(SoapBase.prop(s, "moneda"));
            c.setSaldo(SoapBase.propD(s, "saldo"));
            c.setEstado(SoapBase.prop(s, "estado"));
            c.setCodigoCliente(SoapBase.prop(s, "codigoCliente"));
            c.setNombreCliente(SoapBase.prop(s, "nombreCliente"));
            out.add(c);
        }
        return out;
    }

    public List<ClienteResumen> listarClientes() throws Exception {
        Object body = SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("listarClientes"));
        List<ClienteResumen> out = new ArrayList<>();
        for (Object o : SoapBase.returns(body)) {
            if (!(o instanceof SoapObject)) continue;
            SoapObject s = (SoapObject) o;
            ClienteResumen c = new ClienteResumen();
            c.setCodigo(SoapBase.prop(s, "codigo"));
            c.setDni(SoapBase.prop(s, "dni"));
            c.setNombre(SoapBase.prop(s, "nombre"));
            out.add(c);
        }
        return out;
    }

    public Resultado consultarSaldo(String cuenta) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("consultarSaldo", "cuenta", cuenta)));
    }

    public Resultado depositar(String cuenta, String monto, String moneda) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("depositar", "cuenta", cuenta, "monto", monto, "moneda", moneda)));
    }

    public Resultado retirar(String cuenta, String monto, String moneda) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("retirar", "cuenta", cuenta, "monto", monto, "moneda", moneda)));
    }

    public Resultado transferir(String origen, String destino, String monto,
                                String moneda) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("transferir", "origen", origen, "destino", destino,
                        "monto", monto, "moneda", moneda)));
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono,
            String email) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("registrarCliente", "paterno", paterno, "materno", materno,
                        "nombre", nombre, "dni", dni, "ciudad", ciudad,
                        "direccion", direccion, "telefono", telefono, "email", email)));
    }

    public Resultado registrarCuenta(String cliente, String moneda) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("registrarCuenta", "cliente", cliente, "moneda", moneda)));
    }

    public Resultado eliminarCuenta(String cuenta) throws Exception {
        return parseResultado(SoapBase.llamar(ServidorConfig.EP_CUENTA,
                SoapBase.req("eliminarCuenta", "cuenta", cuenta)));
    }
}
