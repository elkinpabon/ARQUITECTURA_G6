package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.ServicioAutenticacion;
import ec.edu.monster.servicio.ServicioLongitud;
import ec.edu.monster.servicio.ServicioMasa;
import ec.edu.monster.servicio.ServicioTemperatura;

/**
 * Controlador de la aplicacion movil. Orquesta las llamadas a los servicios
 * SOAP y devuelve {@link Resultado} a la vista para que esta no tenga que
 * manejar excepciones de red/SOAP.
 */
public class AppControlador {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    public Resultado iniciarSesion(String usuario, String contrasena) {
        try {
            boolean ok = servicioAutenticacion.iniciarSesion(usuario, contrasena);
            return ok ? Resultado.ok("true") : Resultado.error("Usuario o contrasena invalidos");
        } catch (Exception ex) {
            return Resultado.error("No se pudo conectar con el servidor: " + ex.getMessage());
        }
    }

    public Resultado convertirLongitud(String operacion, double valor) {
        try {
            double r;
            switch (operacion) {
                case "metrosAPies":          r = servicioLongitud.metrosAPies(valor); break;
                case "kilometrosAMillas":    r = servicioLongitud.kilometrosAMillas(valor); break;
                case "centimetrosAPulgadas": r = servicioLongitud.centimetrosAPulgadas(valor); break;
                case "yardasAMetros":        r = servicioLongitud.yardasAMetros(valor); break;
                case "milimetrosAPulgadas":  r = servicioLongitud.milimetrosAPulgadas(valor); break;
                default: return Resultado.error("Operacion invalida: " + operacion);
            }
            return Resultado.ok(String.valueOf(r));
        } catch (Exception ex) {
            return Resultado.error("Error del servicio: " + ex.getMessage());
        }
    }

    public Resultado convertirMasa(String operacion, double valor) {
        try {
            double r;
            switch (operacion) {
                case "kilogramosALibras":    r = servicioMasa.kilogramosALibras(valor); break;
                case "gramosAOnzas":         r = servicioMasa.gramosAOnzas(valor); break;
                case "toneladasAKilogramos": r = servicioMasa.toneladasAKilogramos(valor); break;
                case "librasAOnzas":         r = servicioMasa.librasAOnzas(valor); break;
                case "miligramosAGramos":    r = servicioMasa.miligramosAGramos(valor); break;
                default: return Resultado.error("Operacion invalida: " + operacion);
            }
            return Resultado.ok(String.valueOf(r));
        } catch (Exception ex) {
            return Resultado.error("Error del servicio: " + ex.getMessage());
        }
    }

    public Resultado convertirTemperatura(String operacion, double valor) {
        try {
            double r;
            switch (operacion) {
                case "celsiusAFahrenheit": r = servicioTemperatura.celsiusAFahrenheit(valor); break;
                case "fahrenheitACelsius": r = servicioTemperatura.fahrenheitACelsius(valor); break;
                case "celsiusAKelvin":     r = servicioTemperatura.celsiusAKelvin(valor); break;
                case "kelvinACelsius":     r = servicioTemperatura.kelvinACelsius(valor); break;
                case "fahrenheitAKelvin":  r = servicioTemperatura.fahrenheitAKelvin(valor); break;
                default: return Resultado.error("Operacion invalida: " + operacion);
            }
            return Resultado.ok(String.valueOf(r));
        } catch (Exception ex) {
            return Resultado.error("Error del servicio: " + ex.getMessage());
        }
    }
}
