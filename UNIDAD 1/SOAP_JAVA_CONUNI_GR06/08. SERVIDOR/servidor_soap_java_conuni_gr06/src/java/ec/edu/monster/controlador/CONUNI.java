package ec.edu.monster.controlador;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import ec.edu.monster.servicio.ServicioAutenticacion;
import ec.edu.monster.servicio.ServicioLongitud;
import ec.edu.monster.servicio.ServicioMasa;
import ec.edu.monster.servicio.ServicioTemperatura;

@WebService(serviceName = "CONUNI")
public class CONUNI {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    // ===== Autenticacion =====

    @WebMethod(operationName = "iniciarSesion")
    public boolean iniciarSesion(@WebParam(name = "usuario") String usuario,
                                 @WebParam(name = "contrasena") String contrasena) {
        return servicioAutenticacion.autenticar(usuario, contrasena);
    }

    // ===== Longitud (5 ERS) =====

    @WebMethod(operationName = "metrosAPies")
    public double metrosAPies(@WebParam(name = "metros") double metros) {
        return servicioLongitud.metrosAPies(metros);
    }

    @WebMethod(operationName = "kilometrosAMillas")
    public double kilometrosAMillas(@WebParam(name = "kilometros") double kilometros) {
        return servicioLongitud.kilometrosAMillas(kilometros);
    }

    @WebMethod(operationName = "centimetrosAPulgadas")
    public double centimetrosAPulgadas(@WebParam(name = "centimetros") double centimetros) {
        return servicioLongitud.centimetrosAPulgadas(centimetros);
    }

    @WebMethod(operationName = "yardasAMetros")
    public double yardasAMetros(@WebParam(name = "yardas") double yardas) {
        return servicioLongitud.yardasAMetros(yardas);
    }

    @WebMethod(operationName = "milimetrosAPulgadas")
    public double milimetrosAPulgadas(@WebParam(name = "milimetros") double milimetros) {
        return servicioLongitud.milimetrosAPulgadas(milimetros);
    }

    // ===== Masa (5 ERS) =====

    @WebMethod(operationName = "kilogramosALibras")
    public double kilogramosALibras(@WebParam(name = "kilogramos") double kilogramos) {
        return servicioMasa.kilogramosALibras(kilogramos);
    }

    @WebMethod(operationName = "gramosAOnzas")
    public double gramosAOnzas(@WebParam(name = "gramos") double gramos) {
        return servicioMasa.gramosAOnzas(gramos);
    }

    @WebMethod(operationName = "toneladasAKilogramos")
    public double toneladasAKilogramos(@WebParam(name = "toneladas") double toneladas) {
        return servicioMasa.toneladasAKilogramos(toneladas);
    }

    @WebMethod(operationName = "librasAOnzas")
    public double librasAOnzas(@WebParam(name = "libras") double libras) {
        return servicioMasa.librasAOnzas(libras);
    }

    @WebMethod(operationName = "miligramosAGramos")
    public double miligramosAGramos(@WebParam(name = "miligramos") double miligramos) {
        return servicioMasa.miligramosAGramos(miligramos);
    }

    // ===== Temperatura (5 ERS) =====

    @WebMethod(operationName = "celsiusAFahrenheit")
    public double celsiusAFahrenheit(@WebParam(name = "celsius") double celsius) {
        return servicioTemperatura.celsiusAFahrenheit(celsius);
    }

    @WebMethod(operationName = "fahrenheitACelsius")
    public double fahrenheitACelsius(@WebParam(name = "fahrenheit") double fahrenheit) {
        return servicioTemperatura.fahrenheitACelsius(fahrenheit);
    }

    @WebMethod(operationName = "celsiusAKelvin")
    public double celsiusAKelvin(@WebParam(name = "celsius") double celsius) {
        return servicioTemperatura.celsiusAKelvin(celsius);
    }

    @WebMethod(operationName = "kelvinACelsius")
    public double kelvinACelsius(@WebParam(name = "kelvin") double kelvin) {
        return servicioTemperatura.kelvinACelsius(kelvin);
    }

    @WebMethod(operationName = "fahrenheitAKelvin")
    public double fahrenheitAKelvin(@WebParam(name = "fahrenheit") double fahrenheit) {
        return servicioTemperatura.fahrenheitAKelvin(fahrenheit);
    }
}
