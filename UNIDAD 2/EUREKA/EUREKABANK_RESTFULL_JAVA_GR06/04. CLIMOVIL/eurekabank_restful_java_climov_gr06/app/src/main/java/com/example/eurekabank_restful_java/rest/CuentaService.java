package com.example.eurekabank_restful_java.rest;

import com.example.eurekabank_restful_java.modelo.ClienteResumen;
import com.example.eurekabank_restful_java.modelo.CuentaResumen;
import com.example.eurekabank_restful_java.modelo.Resultado;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Cuentas vía REST/JSON. */
public class CuentaService {

    private Resultado toResultado(String body) throws JSONException {
        JSONObject o = new JSONObject(body);
        Resultado r = new Resultado();
        r.setExito(o.optBoolean("exito", false));
        r.setMensaje(o.optString("mensaje", ""));
        r.setSaldo(o.optDouble("saldo", 0));
        return r;
    }

    public List<CuentaResumen> listarCuentasPorCliente(String criterio) throws Exception {
        JSONArray a = new JSONArray(
                Http.get("/cuentas?cliente=" + Http.enc(criterio)));
        List<CuentaResumen> l = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            CuentaResumen c = new CuentaResumen();
            c.setCodigoCuenta(o.optString("codigoCuenta", null));
            c.setMoneda(o.optString("moneda", null));
            c.setSaldo(o.optDouble("saldo", 0));
            c.setEstado(o.optString("estado", null));
            c.setCodigoCliente(o.optString("codigoCliente", null));
            c.setNombreCliente(o.optString("nombreCliente", null));
            l.add(c);
        }
        return l;
    }

    public List<ClienteResumen> listarClientes() throws Exception {
        JSONArray a = new JSONArray(Http.get("/clientes"));
        List<ClienteResumen> l = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            ClienteResumen c = new ClienteResumen();
            c.setCodigo(o.optString("codigo", null));
            c.setDni(o.optString("dni", null));
            c.setNombre(o.optString("nombre", null));
            l.add(c);
        }
        return l;
    }

    public Resultado consultarSaldo(String cuenta) throws Exception {
        return toResultado(Http.get("/cuentas/" + Http.enc(cuenta) + "/saldo"));
    }

    private String montoBody(String monto, String moneda) throws JSONException {
        JSONObject b = new JSONObject();
        b.put("monto", monto == null ? "" : monto);
        b.put("moneda", moneda == null ? "" : moneda);
        return b.toString();
    }

    public Resultado depositar(String cuenta, String monto, String moneda) throws Exception {
        return toResultado(Http.post(
                "/cuentas/" + Http.enc(cuenta) + "/deposito",
                montoBody(monto, moneda)));
    }

    public Resultado retirar(String cuenta, String monto, String moneda) throws Exception {
        return toResultado(Http.post(
                "/cuentas/" + Http.enc(cuenta) + "/retiro",
                montoBody(monto, moneda)));
    }

    public Resultado transferir(String origen, String destino,
                                String monto, String moneda) throws Exception {
        JSONObject b = new JSONObject();
        b.put("origen", origen == null ? "" : origen);
        b.put("destino", destino == null ? "" : destino);
        b.put("monto", monto == null ? "" : monto);
        b.put("moneda", moneda == null ? "" : moneda);
        return toResultado(Http.post("/transferencias", b.toString()));
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono,
            String email) throws Exception {
        JSONObject b = new JSONObject();
        b.put("paterno", n(paterno)); b.put("materno", n(materno));
        b.put("nombre", n(nombre));   b.put("dni", n(dni));
        b.put("ciudad", n(ciudad));   b.put("direccion", n(direccion));
        b.put("telefono", n(telefono)); b.put("email", n(email));
        return toResultado(Http.post("/clientes", b.toString()));
    }

    public Resultado registrarCuenta(String cliente, String moneda) throws Exception {
        JSONObject b = new JSONObject();
        b.put("cliente", n(cliente));
        b.put("moneda", n(moneda));
        return toResultado(Http.post("/cuentas", b.toString()));
    }

    public Resultado eliminarCuenta(String cuenta) throws Exception {
        return toResultado(Http.delete("/cuentas/" + Http.enc(cuenta)));
    }

    private static String n(String s) { return s == null ? "" : s; }
}
