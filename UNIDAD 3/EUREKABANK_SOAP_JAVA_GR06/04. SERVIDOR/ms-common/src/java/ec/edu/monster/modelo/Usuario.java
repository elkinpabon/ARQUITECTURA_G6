package ec.edu.monster.modelo;

public class Usuario {

    private String codigoEmpleado;
    private String usuario;
    private String clave;
    private String estado;
    private String clienteCodigo;

    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getClienteCodigo() { return clienteCodigo; }
    public void setClienteCodigo(String clienteCodigo) { this.clienteCodigo = clienteCodigo; }
}
