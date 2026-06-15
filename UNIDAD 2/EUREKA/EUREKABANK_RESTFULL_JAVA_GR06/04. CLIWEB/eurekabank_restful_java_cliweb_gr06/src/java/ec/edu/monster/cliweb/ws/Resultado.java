package ec.edu.monster.cliweb.ws;

/** Resultado de negocio (mapeado desde el JSON del servidor REST). */
public class Resultado {
    private boolean exito;
    private String mensaje;
    private double saldo;

    public boolean isExito() { return exito; }
    public void setExito(boolean v) { this.exito = v; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String v) { this.mensaje = v; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double v) { this.saldo = v; }
}
