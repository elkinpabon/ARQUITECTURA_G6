package ec.edu.monster.modelo;

/**
 * DTO simple para transportar el resultado de una operacion entre el
 * controlador y la vista. Evita que la vista trate con excepciones SOAP.
 */
public class Resultado {

    private final boolean exito;
    private final String mensaje;
    private final String valor;

    public Resultado(boolean exito, String mensaje, String valor) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.valor = valor;
    }

    public static Resultado ok(String valor) {
        return new Resultado(true, "Operacion exitosa", valor);
    }

    public static Resultado error(String mensaje) {
        return new Resultado(false, mensaje, null);
    }

    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
    public String getValor() { return valor; }
}
