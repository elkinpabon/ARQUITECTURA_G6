package ec.edu.monster.servicio;

import ec.edu.monster.rest.Rest;
import ec.edu.monster.ws.ClienteResumen;
import ec.edu.monster.ws.CuentaResumen;
import ec.edu.monster.ws.Resultado;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

/** Cliente REST de cuentas (consume el servidor JAX-RS). */
public class CuentaClient {

    private Resultado toResultado(JsonObject o) {
        Resultado r = new Resultado();
        if (o != null) {
            r.setExito(Rest.bool(o, "exito"));
            r.setMensaje(Rest.str(o, "mensaje"));
            r.setSaldo(Rest.dbl(o, "saldo"));
        }
        return r;
    }

    private CuentaResumen toCuenta(JsonObject o) {
        CuentaResumen c = new CuentaResumen();
        c.setCodigoCuenta(Rest.str(o, "codigoCuenta"));
        c.setMoneda(Rest.str(o, "moneda"));
        c.setSaldo(Rest.dbl(o, "saldo"));
        c.setEstado(Rest.str(o, "estado"));
        c.setCodigoCliente(Rest.str(o, "codigoCliente"));
        c.setNombreCliente(Rest.str(o, "nombreCliente"));
        return c;
    }

    public List<CuentaResumen> listarCuentasPorCliente(String cliente) {
        List<CuentaResumen> l = new ArrayList<>();
        for (JsonValue v : Rest.getArray("/cuentas?cliente=" + Rest.enc(cliente))) {
            l.add(toCuenta(v.asJsonObject()));
        }
        return l;
    }

    public List<ClienteResumen> listarClientes() {
        List<ClienteResumen> l = new ArrayList<>();
        for (JsonValue v : Rest.getArray("/clientes")) {
            JsonObject o = v.asJsonObject();
            ClienteResumen c = new ClienteResumen();
            c.setCodigo(Rest.str(o, "codigo"));
            c.setDni(Rest.str(o, "dni"));
            c.setNombre(Rest.str(o, "nombre"));
            l.add(c);
        }
        return l;
    }

    public Resultado consultarSaldo(String cuenta) {
        return toResultado(Rest.getObject(
                "/cuentas/" + Rest.enc(cuenta) + "/saldo"));
    }

    private String montoBody(String monto, String moneda) {
        return Json.createObjectBuilder()
                .add("monto", monto == null ? "" : monto)
                .add("moneda", moneda == null ? "" : moneda)
                .build().toString();
    }

    public Resultado depositar(String cuenta, String monto, String moneda) {
        return toResultado(Rest.post(
                "/cuentas/" + Rest.enc(cuenta) + "/deposito",
                montoBody(monto, moneda)));
    }

    public Resultado retirar(String cuenta, String monto, String moneda) {
        return toResultado(Rest.post(
                "/cuentas/" + Rest.enc(cuenta) + "/retiro",
                montoBody(monto, moneda)));
    }

    public Resultado transferir(String origen, String destino,
                                String monto, String moneda) {
        String body = Json.createObjectBuilder()
                .add("origen", origen == null ? "" : origen)
                .add("destino", destino == null ? "" : destino)
                .add("monto", monto == null ? "" : monto)
                .add("moneda", moneda == null ? "" : moneda)
                .build().toString();
        return toResultado(Rest.post("/transferencias", body));
    }

    public Resultado registrarCliente(String paterno, String materno, String nombre,
            String dni, String ciudad, String direccion, String telefono, String email) {
        String body = Json.createObjectBuilder()
                .add("paterno", n(paterno)).add("materno", n(materno))
                .add("nombre", n(nombre)).add("dni", n(dni))
                .add("ciudad", n(ciudad)).add("direccion", n(direccion))
                .add("telefono", n(telefono)).add("email", n(email))
                .build().toString();
        return toResultado(Rest.post("/clientes", body));
    }

    public Resultado registrarCuenta(String cliente, String moneda) {
        String body = Json.createObjectBuilder()
                .add("cliente", n(cliente)).add("moneda", n(moneda))
                .build().toString();
        return toResultado(Rest.post("/cuentas", body));
    }

    public Resultado eliminarCuenta(String cuenta) {
        return toResultado(Rest.delete("/cuentas/" + Rest.enc(cuenta)));
    }

    private static String n(String s) { return s == null ? "" : s; }
}
