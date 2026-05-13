package ec.edu.monster.controlador;

import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioAutenticacion;
import ec.edu.monster.modelo.ServicioLongitud;
import ec.edu.monster.modelo.ServicioMasa;
import ec.edu.monster.modelo.ServicioTemperatura;
import ec.edu.monster.vista.VistaConsola;

/**
 * Controlador de la aplicacion de consola. Orquesta el flujo,
 * invoca al modelo y delega la I/O a la vista.
 */
public class ControladorConsola {

    private static final int MAX_INTENTOS_LOGIN = 3;

    private final VistaConsola vista = new VistaConsola();
    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    private String usuarioActual;

    public void ejecutar() {
        vista.mostrarEncabezado();
        if (!autenticar()) {
            vista.mostrarError("Se agotaron los intentos de inicio de sesión.");
            return;
        }
        bucleMenuPrincipal();
        vista.mostrarDespedida();
    }

    // ========= Autenticacion =========

    private boolean autenticar() {
        for (int intento = 1; intento <= MAX_INTENTOS_LOGIN; intento++) {
            vista.mostrarTitulo("Iniciar Sesión (intento " + intento + " de " + MAX_INTENTOS_LOGIN + ")");
            String usuario = vista.leerTexto("Usuario");
            String contrasena = vista.leerContrasena("Contraseña");
            try {
                boolean ok = servicioAutenticacion.iniciarSesion(usuario, contrasena);
                if (ok) {
                    usuarioActual = usuario;
                    vista.mostrarExito("Bienvenido, " + usuario + ".");
                    return true;
                }
                vista.mostrarError("Credenciales inválidas.");
            } catch (Exception ex) {
                vista.mostrarError("No se pudo conectar con el servidor: " + ex.getMessage());
            }
        }
        return false;
    }

    // ========= Menu principal =========

    private void bucleMenuPrincipal() {
        while (true) {
            vista.mostrarMenuPrincipal(usuarioActual);
            int opcion = vista.leerOpcion("Selecciona una opción", 0, 3);
            switch (opcion) {
                case 1 -> bucleMenuLongitud();
                case 2 -> bucleMenuMasa();
                case 3 -> bucleMenuTemperatura();
                case 0 -> { return; }
            }
        }
    }

    // ========= Longitud =========

    private void bucleMenuLongitud() {
        while (true) {
            vista.mostrarMenuLongitud();
            int opcion = vista.leerOpcion("Selecciona una opción", 0, 5);
            if (opcion == 0) return;
            double valor = vista.leerDouble("Ingresa el valor a convertir");
            Resultado resultado = ejecutarLongitud(opcion, valor);
            vista.mostrarResultado(resultado);
        }
    }

    private Resultado ejecutarLongitud(int opcion, double valor) {
        try {
            double r;
            String orig, dest;
            switch (opcion) {
                case 1 -> { r = servicioLongitud.metrosAPies(valor);          orig = "m";  dest = "ft"; }
                case 2 -> { r = servicioLongitud.kilometrosAMillas(valor);    orig = "km"; dest = "mi"; }
                case 3 -> { r = servicioLongitud.centimetrosAPulgadas(valor); orig = "cm"; dest = "in"; }
                case 4 -> { r = servicioLongitud.yardasAMetros(valor);        orig = "yd"; dest = "m";  }
                case 5 -> { r = servicioLongitud.milimetrosAPulgadas(valor);  orig = "mm"; dest = "in"; }
                default -> { return Resultado.error("Opción inválida"); }
            }
            return Resultado.ok(FormatoConversion.formatear(valor, orig, r, dest));
        } catch (Exception ex) {
            return Resultado.error("Error al invocar el servicio: " + ex.getMessage());
        }
    }

    // ========= Masa =========

    private void bucleMenuMasa() {
        while (true) {
            vista.mostrarMenuMasa();
            int opcion = vista.leerOpcion("Selecciona una opción", 0, 5);
            if (opcion == 0) return;
            double valor = vista.leerDouble("Ingresa el valor a convertir");
            Resultado resultado = ejecutarMasa(opcion, valor);
            vista.mostrarResultado(resultado);
        }
    }

    private Resultado ejecutarMasa(int opcion, double valor) {
        try {
            double r;
            String orig, dest;
            switch (opcion) {
                case 1 -> { r = servicioMasa.kilogramosALibras(valor);    orig = "kg"; dest = "lb"; }
                case 2 -> { r = servicioMasa.gramosAOnzas(valor);         orig = "g";  dest = "oz"; }
                case 3 -> { r = servicioMasa.toneladasAKilogramos(valor); orig = "t";  dest = "kg"; }
                case 4 -> { r = servicioMasa.librasAOnzas(valor);         orig = "lb"; dest = "oz"; }
                case 5 -> { r = servicioMasa.miligramosAGramos(valor);    orig = "mg"; dest = "g";  }
                default -> { return Resultado.error("Opción inválida"); }
            }
            return Resultado.ok(FormatoConversion.formatear(valor, orig, r, dest));
        } catch (Exception ex) {
            return Resultado.error("Error al invocar el servicio: " + ex.getMessage());
        }
    }

    // ========= Temperatura =========

    private void bucleMenuTemperatura() {
        while (true) {
            vista.mostrarMenuTemperatura();
            int opcion = vista.leerOpcion("Selecciona una opción", 0, 5);
            if (opcion == 0) return;
            double valor = vista.leerDouble("Ingresa el valor a convertir");
            Resultado resultado = ejecutarTemperatura(opcion, valor);
            vista.mostrarResultado(resultado);
        }
    }

    private Resultado ejecutarTemperatura(int opcion, double valor) {
        try {
            double r;
            String orig, dest;
            switch (opcion) {
                case 1 -> { r = servicioTemperatura.celsiusAFahrenheit(valor); orig = "°C"; dest = "°F"; }
                case 2 -> { r = servicioTemperatura.fahrenheitACelsius(valor); orig = "°F"; dest = "°C"; }
                case 3 -> { r = servicioTemperatura.celsiusAKelvin(valor);     orig = "°C"; dest = "K";      }
                case 4 -> { r = servicioTemperatura.kelvinACelsius(valor);     orig = "K";       dest = "°C"; }
                case 5 -> { r = servicioTemperatura.fahrenheitAKelvin(valor);  orig = "°F"; dest = "K";      }
                default -> { return Resultado.error("Opción inválida"); }
            }
            return Resultado.ok(FormatoConversion.formatear(valor, orig, r, dest));
        } catch (Exception ex) {
            return Resultado.error("Error al invocar el servicio: " + ex.getMessage());
        }
    }

}
