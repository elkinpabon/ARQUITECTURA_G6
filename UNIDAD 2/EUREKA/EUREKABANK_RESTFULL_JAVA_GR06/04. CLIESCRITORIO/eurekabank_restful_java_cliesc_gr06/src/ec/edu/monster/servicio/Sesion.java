package ec.edu.monster.servicio;

import ec.edu.monster.ws.CuentaResumen;
import java.util.ArrayList;
import java.util.List;

/** Estado de la sesión (modelo): usuario, rol y cuentas cargadas. */
public class Sesion {

    private String usuario;
    private boolean admin;
    private String clienteAsignado = "";
    private List<CuentaResumen> cuentas = new ArrayList<>();

    public String getUsuario() { return usuario; }
    public void setUsuario(String v) { this.usuario = v; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean v) { this.admin = v; }

    public String getClienteAsignado() { return clienteAsignado; }
    public void setClienteAsignado(String v) { this.clienteAsignado = v == null ? "" : v; }

    public List<CuentaResumen> getCuentas() { return cuentas; }
    public void setCuentas(List<CuentaResumen> v) {
        this.cuentas = v == null ? new ArrayList<>() : v;
    }

    public boolean activa() { return usuario != null; }

    public boolean cuentaPropia(String codigo) {
        if (codigo == null) return false;
        for (CuentaResumen c : cuentas) {
            if (codigo.equals(c.getCodigoCuenta())) return true;
        }
        return false;
    }
}
