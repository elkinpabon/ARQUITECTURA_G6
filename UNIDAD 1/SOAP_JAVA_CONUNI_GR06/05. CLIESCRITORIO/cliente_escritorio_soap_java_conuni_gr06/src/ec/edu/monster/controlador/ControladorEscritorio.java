package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioAutenticacion;
import ec.edu.monster.modelo.ServicioLongitud;
import ec.edu.monster.modelo.ServicioMasa;
import ec.edu.monster.modelo.ServicioTemperatura;
import ec.edu.monster.vista.PanelConversion;
import ec.edu.monster.vista.VentanaPrincipal;
import javax.swing.SwingWorker;

/**
 * Controlador de la aplicacion de escritorio. Cablea los paneles de la Vista
 * con los servicios del Modelo. Usa {@link SwingWorker} para que las llamadas
 * SOAP no bloqueen el hilo de eventos (Event Dispatch Thread) y la UI se
 * mantenga responsiva.
 */
public class ControladorEscritorio {

    private final VentanaPrincipal ventana;

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    private String usuarioActual;

    public ControladorEscritorio() {
        this.ventana = new VentanaPrincipal();
        cablearVista();
    }

    public void iniciar() {
        ventana.mostrar(VentanaPrincipal.TARJETA_LOGIN);
        ventana.setVisible(true);
    }

    // ========= Cableado =========

    private void cablearVista() {
        ventana.getPanelLogin().setOnLogin(this::manejarLogin);
        ventana.getPanelMenu().setOnCategoriaSeleccionada(this::manejarCategoria);
        ventana.getPanelMenu().setOnCerrarSesion(this::manejarCerrarSesion);
        ventana.getPanelConversion().setOnConvertir(this::manejarConvertir);
        ventana.getPanelConversion().setOnVolver(this::manejarVolverAlMenu);
    }

    // ========= Autenticacion =========

    private void manejarLogin(String usuario, String contrasena) {
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            ventana.getPanelLogin().mostrarError("Completa usuario y contrasena");
            return;
        }
        ventana.getPanelLogin().mostrarError(" ");
        ventana.getPanelLogin().setBotonHabilitado(false);

        new SwingWorker<Boolean, Void>() {
            private String mensajeError;

            @Override
            protected Boolean doInBackground() {
                try {
                    return servicioAutenticacion.iniciarSesion(usuario, contrasena);
                } catch (Exception ex) {
                    mensajeError = "No se pudo conectar: " + ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                ventana.getPanelLogin().setBotonHabilitado(true);
                try {
                    boolean ok = get();
                    if (mensajeError != null) {
                        ventana.getPanelLogin().mostrarError(mensajeError);
                        return;
                    }
                    if (ok) {
                        usuarioActual = usuario;
                        ventana.getPanelMenu().setUsuario(usuario);
                        ventana.getPanelLogin().limpiar();
                        ventana.mostrar(VentanaPrincipal.TARJETA_MENU);
                    } else {
                        ventana.getPanelLogin().mostrarError("Usuario o contrasena invalidos");
                    }
                } catch (Exception ex) {
                    ventana.getPanelLogin().mostrarError("Error inesperado: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void manejarCerrarSesion() {
        usuarioActual = null;
        ventana.mostrar(VentanaPrincipal.TARJETA_LOGIN);
    }

    // ========= Navegacion al conversor =========

    private void manejarCategoria(String categoria) {
        ventana.getPanelConversion().setCategoria(categoria);
        ventana.mostrar(VentanaPrincipal.TARJETA_CONVERSOR);
    }

    private void manejarVolverAlMenu() {
        ventana.mostrar(VentanaPrincipal.TARJETA_MENU);
    }

    // ========= Conversiones =========

    private void manejarConvertir(String operacion, Double valor) {
        PanelConversion panel = ventana.getPanelConversion();
        String categoria = panel.getCategoria();
        panel.setBotonHabilitado(false);

        new SwingWorker<Resultado, Void>() {
            @Override
            protected Resultado doInBackground() {
                return ejecutar(categoria, operacion, valor);
            }

            @Override
            protected void done() {
                panel.setBotonHabilitado(true);
                try {
                    panel.mostrarResultado(get());
                } catch (Exception ex) {
                    panel.mostrarResultado(Resultado.error("Error: " + ex.getMessage()));
                }
            }
        }.execute();
    }

    private Resultado ejecutar(String categoria, String operacion, double valor) {
        try {
            double r;
            switch (categoria) {
                case "longitud":
                    r = llamarLongitud(operacion, valor);
                    break;
                case "masa":
                    r = llamarMasa(operacion, valor);
                    break;
                case "temperatura":
                    r = llamarTemperatura(operacion, valor);
                    break;
                default:
                    return Resultado.error("Categoria desconocida");
            }
            return Resultado.ok(String.valueOf(r));
        } catch (Exception ex) {
            return Resultado.error("Error del servicio: " + ex.getMessage());
        }
    }

    private double llamarLongitud(String op, double v) throws Exception {
        switch (op) {
            case "metrosAPies":          return servicioLongitud.metrosAPies(v);
            case "kilometrosAMillas":    return servicioLongitud.kilometrosAMillas(v);
            case "centimetrosAPulgadas": return servicioLongitud.centimetrosAPulgadas(v);
            case "yardasAMetros":        return servicioLongitud.yardasAMetros(v);
            case "milimetrosAPulgadas":  return servicioLongitud.milimetrosAPulgadas(v);
            default: throw new IllegalArgumentException("Operacion invalida: " + op);
        }
    }

    private double llamarMasa(String op, double v) throws Exception {
        switch (op) {
            case "kilogramosALibras":    return servicioMasa.kilogramosALibras(v);
            case "gramosAOnzas":         return servicioMasa.gramosAOnzas(v);
            case "toneladasAKilogramos": return servicioMasa.toneladasAKilogramos(v);
            case "librasAOnzas":         return servicioMasa.librasAOnzas(v);
            case "miligramosAGramos":    return servicioMasa.miligramosAGramos(v);
            default: throw new IllegalArgumentException("Operacion invalida: " + op);
        }
    }

    private double llamarTemperatura(String op, double v) throws Exception {
        switch (op) {
            case "celsiusAFahrenheit": return servicioTemperatura.celsiusAFahrenheit(v);
            case "fahrenheitACelsius": return servicioTemperatura.fahrenheitACelsius(v);
            case "celsiusAKelvin":     return servicioTemperatura.celsiusAKelvin(v);
            case "kelvinACelsius":     return servicioTemperatura.kelvinACelsius(v);
            case "fahrenheitAKelvin":  return servicioTemperatura.fahrenheitAKelvin(v);
            default: throw new IllegalArgumentException("Operacion invalida: " + op);
        }
    }
}
