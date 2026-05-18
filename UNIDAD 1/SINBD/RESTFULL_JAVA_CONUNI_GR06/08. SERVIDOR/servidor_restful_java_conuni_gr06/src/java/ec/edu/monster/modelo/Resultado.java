package ec.edu.monster.modelo;

/**
 * DTO de respuesta serializado a JSON por JSON-B.
 * Contiene tres campos: exito (boolean), valor (numero o null) y mensaje (texto).
 */
public class Resultado {

    private boolean exito;
    private Double valor;
    private String mensaje;

    public Resultado() {
    }

    public Resultado(boolean exito, Double valor, String mensaje) {
        this.exito = exito;
        this.valor = valor;
        this.mensaje = mensaje;
    }

    public static Resultado ok(double valor) {
        return new Resultado(true, valor, "OK");
    }

    public static Resultado error(String mensaje) {
        return new Resultado(false, null, mensaje);
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
