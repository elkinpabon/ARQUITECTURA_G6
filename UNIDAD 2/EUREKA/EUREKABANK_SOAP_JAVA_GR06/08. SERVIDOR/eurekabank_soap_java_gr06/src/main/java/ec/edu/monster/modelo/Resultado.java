package ec.edu.monster.modelo;

/**
 * DTO de respuesta para operaciones bancarias. En lugar de devolver solo un
 * boolean, el servicio informa exito/fallo, un mensaje legible y, cuando
 * aplica, el saldo resultante.
 */
public class Resultado {

    private boolean exito;
    private String mensaje;
    private double saldo;

    public Resultado() {
    }

    public Resultado(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public Resultado(boolean exito, String mensaje, double saldo) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.saldo = saldo;
    }

    public static Resultado ok(String mensaje, double saldo) {
        return new Resultado(true, mensaje, saldo);
    }

    public static Resultado error(String mensaje) {
        return new Resultado(false, mensaje);
    }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
