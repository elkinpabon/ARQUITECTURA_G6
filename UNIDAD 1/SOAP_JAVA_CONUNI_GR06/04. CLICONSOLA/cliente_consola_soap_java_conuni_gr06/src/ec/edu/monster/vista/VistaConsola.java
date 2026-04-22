package ec.edu.monster.vista;

import ec.edu.monster.modelo.Resultado;
import java.io.Console;
import java.util.Scanner;

/**
 * Vista de consola. Concentra toda la interaccion con el usuario
 * (System.out / Scanner / Console) para que el Controlador no dependa de I/O.
 */
public class VistaConsola {

    private final Scanner lector = new Scanner(System.in);

    // ========= Presentacion =========

    public void mostrarEncabezado() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("         CLIENTE CONSOLA CONUNI - SOAP           ");
        System.out.println("==================================================");
    }

    public void mostrarTitulo(String titulo) {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    public void mostrarExito(String mensaje) {
        System.out.println("[OK] " + mensaje);
    }

    public void mostrarResultado(Resultado resultado) {
        if (resultado == null) {
            return;
        }
        if (resultado.isExito()) {
            mostrarExito("Resultado: " + resultado.getValor());
        } else {
            mostrarError(resultado.getMensaje());
        }
    }

    public void mostrarDespedida() {
        System.out.println();
        System.out.println("Hasta pronto. Sesion cerrada.");
    }

    // ========= Menus =========

    public void mostrarMenuPrincipal(String usuario) {
        mostrarTitulo("Menu Principal (usuario: " + usuario + ")");
        System.out.println("1. Conversiones de Longitud");
        System.out.println("2. Conversiones de Masa");
        System.out.println("3. Conversiones de Temperatura");
        System.out.println("0. Cerrar Sesion");
    }

    public void mostrarMenuLongitud() {
        mostrarTitulo("Conversiones de Longitud");
        System.out.println("1. Metros a Pies");
        System.out.println("2. Kilometros a Millas");
        System.out.println("3. Centimetros a Pulgadas");
        System.out.println("4. Yardas a Metros");
        System.out.println("5. Milimetros a Pulgadas");
        System.out.println("0. Volver");
    }

    public void mostrarMenuMasa() {
        mostrarTitulo("Conversiones de Masa");
        System.out.println("1. Kilogramos a Libras");
        System.out.println("2. Gramos a Onzas");
        System.out.println("3. Toneladas a Kilogramos");
        System.out.println("4. Libras a Onzas");
        System.out.println("5. Miligramos a Gramos");
        System.out.println("0. Volver");
    }

    public void mostrarMenuTemperatura() {
        mostrarTitulo("Conversiones de Temperatura");
        System.out.println("1. Celsius a Fahrenheit");
        System.out.println("2. Fahrenheit a Celsius");
        System.out.println("3. Celsius a Kelvin");
        System.out.println("4. Kelvin a Celsius");
        System.out.println("5. Fahrenheit a Kelvin");
        System.out.println("0. Volver");
    }

    // ========= Entrada =========

    public String leerTexto(String etiqueta) {
        System.out.print(etiqueta + ": ");
        return lector.nextLine().trim();
    }

    /**
     * Lee una contrasena. Si se ejecuta en una consola real (no en NetBeans),
     * usa Console.readPassword() para ocultarla. En IDE cae a Scanner (visible)
     * con una advertencia para que el usuario lo sepa.
     */
    public String leerContrasena(String etiqueta) {
        Console consola = System.console();
        if (consola != null) {
            char[] caracteres = consola.readPassword(etiqueta + ": ");
            return caracteres == null ? "" : new String(caracteres);
        }
        System.out.println("(Nota: ejecutando en IDE, la contrasena se mostrara en pantalla)");
        return leerTexto(etiqueta);
    }

    public int leerOpcion(String etiqueta, int minimo, int maximo) {
        while (true) {
            String entrada = leerTexto(etiqueta + " [" + minimo + "-" + maximo + "]");
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                mostrarError("La opcion debe estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException ex) {
                mostrarError("Debes ingresar un numero entero.");
            }
        }
    }

    public double leerDouble(String etiqueta) {
        while (true) {
            String entrada = leerTexto(etiqueta);
            try {
                return Double.parseDouble(entrada.replace(',', '.'));
            } catch (NumberFormatException ex) {
                mostrarError("Debes ingresar un numero valido (ejemplo: 12.5).");
            }
        }
    }
}
