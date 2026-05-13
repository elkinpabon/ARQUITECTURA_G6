# 8. CREACIÓN CLIENTE WEB

## 8.1    Paquete controlador

Este paquete agrupa los *Servlets* Jakarta EE que actúan como controladores frontales del cliente Web. Cada uno expone una URL del contexto (`/autenticacion`, `/longitud`, `/masa`, `/temperatura`, `/cerrarSesion`), recibe peticiones HTTP, delega la lógica de negocio al paquete `modelo` y reenvía la respuesta a la vista JSP correspondiente.

-    ServletAutenticacion
-    ServletCerrarSesion
-    ServletLongitud
-    ServletMasa
-    ServletTemperatura

### 8.1.1    Código de ServletAutenticacion

Servlet mapeado a `/autenticacion`. En `doGet` reenvía a la vista `iniciarSesion.jsp`. En `doPost` toma usuario y contraseña del formulario, invoca `servicioAutenticacion.iniciarSesion(...)` y, si el login es exitoso, crea una `HttpSession`, guarda el atributo `usuario` y redirige al menú; si falla, vuelve al formulario pasando `mensajeError` como atributo de request.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ServicioAutenticacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ServletAutenticacion", urlPatterns = {"/autenticacion"})
public class ServletAutenticacion extends HttpServlet {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/vista/iniciarSesion.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String usuario = peticion.getParameter("usuario");
        String contrasena = peticion.getParameter("contrasena");

        try {
            boolean valido = servicioAutenticacion.iniciarSesion(usuario, contrasena);
            if (valido) {
                HttpSession sesion = peticion.getSession(true);
                sesion.setAttribute("usuario", usuario);
                respuesta.sendRedirect(peticion.getContextPath() + "/vista/menu.jsp");
                return;
            }
            peticion.setAttribute("mensajeError", "Usuario o contrasena incorrectos.");
        } catch (Exception excepcion) {
            peticion.setAttribute("mensajeError",
                    "No se pudo conectar con el servidor SOAP: " + excepcion.getMessage());
        }
        peticion.getRequestDispatcher("/vista/iniciarSesion.jsp").forward(peticion, respuesta);
    }
}
</pre></td></tr></table>

### 8.1.2    Código de ServletCerrarSesion

Servlet mapeado a `/cerrarSesion`. Recupera la sesión actual con `getSession(false)` (sin crearla si no existe), la invalida con `sesion.invalidate()` y redirige al formulario de login. Es el contrapunto de `ServletAutenticacion`: termina el ciclo de vida de la sesión iniciada con éxito.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ServletCerrarSesion", urlPatterns = {"/cerrarSesion"})
public class ServletCerrarSesion extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        respuesta.sendRedirect(peticion.getContextPath() + "/vista/iniciarSesion.jsp");
    }
}
</pre></td></tr></table>

### 8.1.3    Código de ServletLongitud

Servlet mapeado a `/longitud`. En `doGet` reenvía a `longitud.jsp`. En `doPost` lee `operacion` y `valor` del formulario, parsea el valor a `double`, despacha la operación correcta mediante un `switch` sobre `ServicioLongitud`, formatea el resultado con `FormatoConversion` y deja el `Resultado` como atributo de request para que la JSP lo muestre. Captura `NumberFormatException` y `Exception` para reportar errores de validación o de conexión SOAP.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioLongitud;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ServletLongitud", urlPatterns = {"/longitud"})
public class ServletLongitud extends HttpServlet {

    private final ServicioLongitud servicioLongitud = new ServicioLongitud();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/vista/longitud.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String operacion = peticion.getParameter("operacion");
        String valorTexto = peticion.getParameter("valor");

        Resultado resultado;
        try {
            double valor = Double.parseDouble(valorTexto);
            double convertido;
            switch (operacion) {
                case "metrosAPies":
                    convertido = servicioLongitud.metrosAPies(valor);
                    break;
                case "kilometrosAMillas":
                    convertido = servicioLongitud.kilometrosAMillas(valor);
                    break;
                case "centimetrosAPulgadas":
                    convertido = servicioLongitud.centimetrosAPulgadas(valor);
                    break;
                case "yardasAMetros":
                    convertido = servicioLongitud.yardasAMetros(valor);
                    break;
                case "milimetrosAPulgadas":
                    convertido = servicioLongitud.milimetrosAPulgadas(valor);
                    break;
                default:
                    throw new IllegalArgumentException("Operacion desconocida: " + operacion);
            }
            String[] u = FormatoConversion.unidades(operacion);
            resultado = Resultado.ok(
                    FormatoConversion.formatear(valor, u[0], convertido, u[1]));
        } catch (NumberFormatException excepcion) {
            resultado = Resultado.error("El valor ingresado no es un numero valido.");
        } catch (Exception excepcion) {
            resultado = Resultado.error("Error al invocar el servicio: " + excepcion.getMessage());
        }

        peticion.setAttribute("resultado", resultado);
        peticion.setAttribute("operacionSeleccionada", operacion);
        peticion.setAttribute("valorIngresado", valorTexto);
        peticion.getRequestDispatcher("/vista/longitud.jsp").forward(peticion, respuesta);
    }
}
</pre></td></tr></table>

### 8.1.4    Código de ServletMasa

Servlet mapeado a `/masa`. Sigue el mismo patrón que `ServletLongitud`: `doGet` reenvía a la vista, `doPost` despacha la operación al `ServicioMasa` correspondiente (`kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas`, `miligramosAGramos`) y devuelve el `Resultado` como atributo de request a `masa.jsp`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioMasa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ServletMasa", urlPatterns = {"/masa"})
public class ServletMasa extends HttpServlet {

    private final ServicioMasa servicioMasa = new ServicioMasa();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/vista/masa.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String operacion = peticion.getParameter("operacion");
        String valorTexto = peticion.getParameter("valor");

        Resultado resultado;
        try {
            double valor = Double.parseDouble(valorTexto);
            double convertido;
            switch (operacion) {
                case "kilogramosALibras":
                    convertido = servicioMasa.kilogramosALibras(valor);
                    break;
                case "gramosAOnzas":
                    convertido = servicioMasa.gramosAOnzas(valor);
                    break;
                case "toneladasAKilogramos":
                    convertido = servicioMasa.toneladasAKilogramos(valor);
                    break;
                case "librasAOnzas":
                    convertido = servicioMasa.librasAOnzas(valor);
                    break;
                case "miligramosAGramos":
                    convertido = servicioMasa.miligramosAGramos(valor);
                    break;
                default:
                    throw new IllegalArgumentException("Operacion desconocida: " + operacion);
            }
            String[] u = FormatoConversion.unidades(operacion);
            resultado = Resultado.ok(
                    FormatoConversion.formatear(valor, u[0], convertido, u[1]));
        } catch (NumberFormatException excepcion) {
            resultado = Resultado.error("El valor ingresado no es un numero valido.");
        } catch (Exception excepcion) {
            resultado = Resultado.error("Error al invocar el servicio: " + excepcion.getMessage());
        }

        peticion.setAttribute("resultado", resultado);
        peticion.setAttribute("operacionSeleccionada", operacion);
        peticion.setAttribute("valorIngresado", valorTexto);
        peticion.getRequestDispatcher("/vista/masa.jsp").forward(peticion, respuesta);
    }
}
</pre></td></tr></table>

### 8.1.5    Código de ServletTemperatura

Servlet mapeado a `/temperatura`. Estructura idéntica a los dos anteriores: despacha la operación al `ServicioTemperatura` correspondiente (`celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius`, `fahrenheitAKelvin`) y reenvía el resultado a `temperatura.jsp`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioTemperatura;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ServletTemperatura", urlPatterns = {"/temperatura"})
public class ServletTemperatura extends HttpServlet {

    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/vista/temperatura.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String operacion = peticion.getParameter("operacion");
        String valorTexto = peticion.getParameter("valor");

        Resultado resultado;
        try {
            double valor = Double.parseDouble(valorTexto);
            double convertido;
            switch (operacion) {
                case "celsiusAFahrenheit":
                    convertido = servicioTemperatura.celsiusAFahrenheit(valor);
                    break;
                case "fahrenheitACelsius":
                    convertido = servicioTemperatura.fahrenheitACelsius(valor);
                    break;
                case "celsiusAKelvin":
                    convertido = servicioTemperatura.celsiusAKelvin(valor);
                    break;
                case "kelvinACelsius":
                    convertido = servicioTemperatura.kelvinACelsius(valor);
                    break;
                case "fahrenheitAKelvin":
                    convertido = servicioTemperatura.fahrenheitAKelvin(valor);
                    break;
                default:
                    throw new IllegalArgumentException("Operacion desconocida: " + operacion);
            }
            String[] u = FormatoConversion.unidades(operacion);
            resultado = Resultado.ok(
                    FormatoConversion.formatear(valor, u[0], convertido, u[1]));
        } catch (NumberFormatException excepcion) {
            resultado = Resultado.error("El valor ingresado no es un numero valido.");
        } catch (Exception excepcion) {
            resultado = Resultado.error("Error al invocar el servicio: " + excepcion.getMessage());
        }

        peticion.setAttribute("resultado", resultado);
        peticion.setAttribute("operacionSeleccionada", operacion);
        peticion.setAttribute("valorIngresado", valorTexto);
        peticion.getRequestDispatcher("/vista/temperatura.jsp").forward(peticion, respuesta);
    }
}
</pre></td></tr></table>

---

## 8.2    Paquete modelo

Este paquete agrupa las clases del modelo del cliente Web: encapsulan la lógica de envío y recepción de los datos hacia el servidor SOAP, así como el formateo de los resultados que se muestran al usuario. Está formado por un cliente SOAP genérico (`ClienteSoap`), un DTO para transportar resultados (`Resultado`), una utilidad de formateo (`FormatoConversion`) y cuatro servicios proxy que envuelven cada grupo de operaciones del WSDL: `ServicioAutenticacion`, `ServicioLongitud`, `ServicioMasa` y `ServicioTemperatura`.

-    ClienteSoap
-    Resultado
-    FormatoConversion
-    ServicioAutenticacion
-    ServicioLongitud
-    ServicioMasa
-    ServicioTemperatura

### 8.2.1    Código de ClienteSoap

Cliente SOAP genérico que abstrae las llamadas HTTP al servicio CONUNI. Recibe el nombre de la operación y un `Map` de parámetros, construye dinámicamente el sobre SOAP, lo envía por POST a `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI` y extrae el contenido de la etiqueta `<return>` de la respuesta. Los demás servicios proxy del paquete delegan en esta clase para evitar duplicar la lógica de red.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Cliente SOAP generico para consumir el servicio CONUNI.
 * Construye el sobre SOAP, lo envia por HTTP y extrae la respuesta.
 */
public class ClienteSoap {

    private static final String URL_SERVICIO =
            "http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI";
    private static final String ESPACIO_NOMBRES =
            "http://controlador.monster.edu.ec/";

    public String invocar(String nombreOperacion, Map&lt;String, String&gt; parametros) throws Exception {
        String sobreSoap = construirSobre(nombreOperacion, parametros);
        String respuesta = enviarPeticion(sobreSoap);
        return extraerValorRetorno(respuesta);
    }

    private String construirSobre(String nombreOperacion, Map&lt;String, String&gt; parametros) {
        StringBuilder cuerpo = new StringBuilder();
        for (Map.Entry&lt;String, String&gt; entrada : parametros.entrySet()) {
            cuerpo.append("&lt;").append(entrada.getKey()).append("&gt;")
                  .append(entrada.getValue())
                  .append("&lt;/").append(entrada.getKey()).append("&gt;");
        }
        return "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;"
             + "&lt;soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
             +   "xmlns:con=\"" + ESPACIO_NOMBRES + "\"&gt;"
             +   "&lt;soapenv:Header/&gt;"
             +   "&lt;soapenv:Body&gt;"
             +     "&lt;con:" + nombreOperacion + "&gt;" + cuerpo + "&lt;/con:" + nombreOperacion + "&gt;"
             +   "&lt;/soapenv:Body&gt;"
             + "&lt;/soapenv:Envelope&gt;";
    }

    private String enviarPeticion(String sobreSoap) throws Exception {
        URL url = new URL(URL_SERVICIO);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conexion.setRequestProperty("SOAPAction", "");
        conexion.setDoOutput(true);

        byte[] datos = sobreSoap.getBytes(StandardCharsets.UTF_8);
        try (OutputStream salida = conexion.getOutputStream()) {
            salida.write(datos);
        }

        int codigo = conexion.getResponseCode();
        InputStream flujo = (codigo &gt;= 200 &amp;&amp; codigo &lt; 300)
                ? conexion.getInputStream()
                : conexion.getErrorStream();

        StringBuilder contenido = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea);
            }
        }
        return contenido.toString();
    }

    private String extraerValorRetorno(String respuestaXml) {
        int inicio = respuestaXml.indexOf("&lt;return&gt;");
        int fin = respuestaXml.indexOf("&lt;/return&gt;");
        if (inicio == -1 || fin == -1) {
            throw new RuntimeException("Respuesta SOAP sin etiqueta &lt;return&gt;: " + respuestaXml);
        }
        return respuestaXml.substring(inicio + "&lt;return&gt;".length(), fin);
    }
}
</pre></td></tr></table>

### 8.2.2    Código de Resultado

DTO simple para transportar entre el controlador (servlet) y la vista (JSP) el desenlace de una operación. Encapsula tres atributos inmutables: un *flag* `exito`, un `mensaje` descriptivo y el `valor` formateado. Expone dos factorías estáticas, `ok(valor)` y `error(mensaje)`, que los servlets de conversión usan para pasar el resultado a la JSP, que lo renderiza con la clase CSS `mensaje-exito` o `mensaje-error` según corresponda.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

/**
 * DTO simple para transportar un resultado de operacion del controlador a la vista.
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

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getValor() {
        return valor;
    }
}
</pre></td></tr></table>

### 8.2.3    Código de FormatoConversion

Utilidad estática que centraliza el formateo de los resultados de conversión que devuelve el servicio SOAP. Expone tres métodos: `fmt(double)` redondea a cuatro decimales y elimina ceros sobrantes, `unidades(operacion)` devuelve un arreglo `{origen, destino}` para cada una de las 16 conversiones del WSDL, y `formatear(...)` ensambla el string final del tipo `"7 m = 22.9659 ft"` que los servlets entregan como `Resultado.ok(...)`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

/**
 * Utilidad para formatear los resultados de conversion.
 * - fmt(double) redondea a 4 decimales y quita ceros sobrantes.
 * - unidades(operacion) devuelve {origen, destino} para cada una de las 16 conversiones.
 * - formatear(...) arma el string final "7 m = 22.9659 ft".
 */
public final class FormatoConversion {

    private FormatoConversion() {}

    public static String formatear(double entrada, String origen,
                                   double salida, String destino) {
        return fmt(entrada) + " " + origen + " = " + fmt(salida) + " " + destino;
    }

    public static String fmt(double v) {
        if (v == Math.floor(v) &amp;&amp; !Double.isInfinite(v) &amp;&amp; Math.abs(v) &lt; 1e15) {
            return String.valueOf((long) v);
        }
        String s = String.format(java.util.Locale.US, "%.4f", v);
        return s.contains(".") ? s.replaceAll("0+$", "").replaceAll("\\.$", "") : s;
    }

    /** Devuelve {origen, destino} para una operacion del WSDL. */
    public static String[] unidades(String operacion) {
        switch (operacion) {
            // Longitud
            case "metrosAPies":          return new String[]{"m",  "ft"};
            case "kilometrosAMillas":    return new String[]{"km", "mi"};
            case "centimetrosAPulgadas": return new String[]{"cm", "in"};
            case "yardasAMetros":        return new String[]{"yd", "m" };
            case "milimetrosAPulgadas":  return new String[]{"mm", "in"};
            // Masa
            case "kilogramosALibras":    return new String[]{"kg", "lb"};
            case "gramosAOnzas":         return new String[]{"g",  "oz"};
            case "toneladasAKilogramos": return new String[]{"t",  "kg"};
            case "librasAOnzas":         return new String[]{"lb", "oz"};
            case "miligramosAGramos":    return new String[]{"mg", "g" };
            // Temperatura
            case "celsiusAFahrenheit":   return new String[]{"°C", "°F"};
            case "fahrenheitACelsius":   return new String[]{"°F", "°C"};
            case "celsiusAKelvin":       return new String[]{"°C", "K"};
            case "kelvinACelsius":       return new String[]{"K",       "°C"};
            case "fahrenheitAKelvin":    return new String[]{"°F", "K"};
            default:                     return new String[]{"",        ""};
        }
    }
}
</pre></td></tr></table>

### 8.2.4    Código de ServicioAutenticacion

Servicio proxy de la operación `iniciarSesion` del WSDL. Recibe usuario y contraseña, los empaqueta en un `LinkedHashMap` (para preservar el orden de los parámetros en el sobre SOAP) y delega la invocación en `ClienteSoap`. Convierte la respuesta textual en `boolean` para que el `ServletAutenticacion` pueda decidir si crear o no la `HttpSession`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioAutenticacion {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public boolean iniciarSesion(String usuario, String contrasena) throws Exception {
        Map&lt;String, String&gt; parametros = new LinkedHashMap&lt;&gt;();
        parametros.put("usuario", usuario);
        parametros.put("contrasena", contrasena);
        String respuesta = clienteSoap.invocar("iniciarSesion", parametros);
        return Boolean.parseBoolean(respuesta);
    }
}
</pre></td></tr></table>

### 8.2.5    Código de ServicioLongitud

Servicio proxy de las cinco operaciones de longitud del WSDL: `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros` y `milimetrosAPulgadas`. Cada método público envuelve la llamada con `invocarUnario(...)`, que arma el `Map` de un solo parámetro, delega en `ClienteSoap` y convierte la respuesta textual a `double`. Lo consume `ServletLongitud`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioLongitud {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public double metrosAPies(double metros) throws Exception {
        return invocarUnario("metrosAPies", "metros", metros);
    }

    public double kilometrosAMillas(double kilometros) throws Exception {
        return invocarUnario("kilometrosAMillas", "kilometros", kilometros);
    }

    public double centimetrosAPulgadas(double centimetros) throws Exception {
        return invocarUnario("centimetrosAPulgadas", "centimetros", centimetros);
    }

    public double yardasAMetros(double yardas) throws Exception {
        return invocarUnario("yardasAMetros", "yardas", yardas);
    }

    public double milimetrosAPulgadas(double milimetros) throws Exception {
        return invocarUnario("milimetrosAPulgadas", "milimetros", milimetros);
    }

    private double invocarUnario(String operacion, String nombreParametro, double valor) throws Exception {
        Map&lt;String, String&gt; parametros = new LinkedHashMap&lt;&gt;();
        parametros.put(nombreParametro, String.valueOf(valor));
        String respuesta = clienteSoap.invocar(operacion, parametros);
        return Double.parseDouble(respuesta);
    }
}
</pre></td></tr></table>

### 8.2.6    Código de ServicioMasa

Servicio proxy de las cinco operaciones de masa del WSDL: `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas` y `miligramosAGramos`. Sigue el mismo patrón que `ServicioLongitud`: cada método publica una conversión y delega en el *helper* `invocarUnario(...)` que centraliza la invocación SOAP y el parseo del resultado. Lo consume `ServletMasa`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioMasa {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public double kilogramosALibras(double kilogramos) throws Exception {
        return invocarUnario("kilogramosALibras", "kilogramos", kilogramos);
    }

    public double gramosAOnzas(double gramos) throws Exception {
        return invocarUnario("gramosAOnzas", "gramos", gramos);
    }

    public double toneladasAKilogramos(double toneladas) throws Exception {
        return invocarUnario("toneladasAKilogramos", "toneladas", toneladas);
    }

    public double librasAOnzas(double libras) throws Exception {
        return invocarUnario("librasAOnzas", "libras", libras);
    }

    public double miligramosAGramos(double miligramos) throws Exception {
        return invocarUnario("miligramosAGramos", "miligramos", miligramos);
    }

    private double invocarUnario(String operacion, String nombreParametro, double valor) throws Exception {
        Map&lt;String, String&gt; parametros = new LinkedHashMap&lt;&gt;();
        parametros.put(nombreParametro, String.valueOf(valor));
        String respuesta = clienteSoap.invocar(operacion, parametros);
        return Double.parseDouble(respuesta);
    }
}
</pre></td></tr></table>

### 8.2.7    Código de ServicioTemperatura

Servicio proxy de las operaciones de temperatura del WSDL: `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius` y `fahrenheitAKelvin`. Comparte la estructura de los otros servicios proxy: un método por conversión y un *helper* privado `invocarUnario(...)` que arma el sobre SOAP a través de `ClienteSoap` y devuelve el `double` ya parseado. Lo consume `ServletTemperatura`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioTemperatura {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public double celsiusAFahrenheit(double celsius) throws Exception {
        return invocarUnario("celsiusAFahrenheit", "celsius", celsius);
    }

    public double fahrenheitACelsius(double fahrenheit) throws Exception {
        return invocarUnario("fahrenheitACelsius", "fahrenheit", fahrenheit);
    }

    public double celsiusAKelvin(double celsius) throws Exception {
        return invocarUnario("celsiusAKelvin", "celsius", celsius);
    }

    public double kelvinACelsius(double kelvin) throws Exception {
        return invocarUnario("kelvinACelsius", "kelvin", kelvin);
    }

    public double fahrenheitAKelvin(double fahrenheit) throws Exception {
        return invocarUnario("fahrenheitAKelvin", "fahrenheit", fahrenheit);
    }

    private double invocarUnario(String operacion, String nombreParametro, double valor) throws Exception {
        Map&lt;String, String&gt; parametros = new LinkedHashMap&lt;&gt;();
        parametros.put(nombreParametro, String.valueOf(valor));
        String respuesta = clienteSoap.invocar(operacion, parametros);
        return Double.parseDouble(respuesta);
    }
}
</pre></td></tr></table>

---

## 8.3    Paquete util

Este paquete agrupa los componentes transversales del cliente Web. Contiene un único `Filter` que se aplica a las URLs protegidas para impedir que un usuario sin sesión activa acceda a las pantallas de conversión.

-    FiltroSesion

### 8.3.1    Código de FiltroSesion

Filtro Jakarta Servlet declarado con `@WebFilter` para los patrones `/longitud`, `/masa` y `/temperatura`. Recupera la sesión con `getSession(false)` y comprueba si existe el atributo `usuario`. Si está autenticado, deja pasar la petición por la cadena (`cadena.doFilter`); de lo contrario, redirige al formulario de login. Es el guardián que centraliza la protección de las rutas y evita que cada servlet tenga que comprobar la sesión manualmente.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro que bloquea el acceso a las vistas de conversion si el usuario no tiene sesion activa.
 */
@WebFilter(filterName = "FiltroSesion", urlPatterns = {"/longitud", "/masa", "/temperatura"})
public class FiltroSesion implements Filter {

    @Override
    public void doFilter(ServletRequest peticion, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest peticionHttp = (HttpServletRequest) peticion;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;

        HttpSession sesion = peticionHttp.getSession(false);
        boolean autenticado = (sesion != null &amp;&amp; sesion.getAttribute("usuario") != null);

        if (autenticado) {
            cadena.doFilter(peticion, respuesta);
        } else {
            respuestaHttp.sendRedirect(peticionHttp.getContextPath() + "/vista/iniciarSesion.jsp");
        }
    }
}
</pre></td></tr></table>

---

## 8.4    Paquete vista (JSPs)

Este paquete agrupa las vistas JSP del cliente Web, ubicadas en `web/` y `web/vista/`. Son las plantillas HTML dinámicas que los servlets reenvían al usuario. Toda la presentación se apoya en una hoja de estilos compartida (`css/estilo.css`). Está formado por una página raíz (`index.jsp`) que solo redirige, la pantalla de login (`iniciarSesion.jsp`), el menú principal (`menu.jsp`) y tres pantallas de conversión (`longitud.jsp`, `masa.jsp`, `temperatura.jsp`).

-    index.jsp
-    iniciarSesion.jsp
-    menu.jsp
-    longitud.jsp
-    masa.jsp
-    temperatura.jsp

### 8.4.1    Código de index.jsp

Página raíz del contexto. No renderiza nada: hace `response.sendRedirect` a `/vista/iniciarSesion.jsp` para que cuando el usuario abra el contexto base del WAR caiga directo en el login.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
&lt;%@ page contentType="text/html;charset=UTF-8" language="java" %&gt;
&lt;%
    response.sendRedirect(request.getContextPath() + "/vista/iniciarSesion.jsp");
%&gt;
</pre></td></tr></table>

### 8.4.2    Código de iniciarSesion.jsp

Formulario de inicio de sesión. Layout en dos columnas (imagen + formulario), envía `POST` a `/autenticacion` con los campos `usuario` y `contrasena`. Si el servlet expone el atributo `mensajeError`, lo muestra en un `<div class="mensaje-error">`. Incluye un botón Mostrar/Ocultar contraseña basado en JavaScript que alterna el `type` del *input* entre `password` y `text` y conmuta los iconos SVG correspondientes.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
&lt;%@ page contentType="text/html;charset=UTF-8" language="java" %&gt;
&lt;!DOCTYPE html&gt;
&lt;html lang="es"&gt;
&lt;head&gt;
    &lt;meta charset="UTF-8"&gt;
    &lt;title&gt;Iniciar Sesion - CONUNI&lt;/title&gt;
    &lt;link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css"&gt;
&lt;/head&gt;
&lt;body&gt;
    &lt;div class="login-wrapper"&gt;
        &lt;div class="login-imagen" role="img" aria-label="Imagen CONUNI"&gt;&lt;/div&gt;

        &lt;div class="login-formulario"&gt;
            &lt;div class="login-marca"&gt;
                &lt;img src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI"&gt;
                &lt;span&gt;Cliente Web CONUNI&lt;/span&gt;
            &lt;/div&gt;

            &lt;h2&gt;Iniciar Sesion&lt;/h2&gt;

            &lt;% String mensajeError = (String) request.getAttribute("mensajeError"); %&gt;
            &lt;% if (mensajeError != null) { %&gt;
                &lt;div class="mensaje-error"&gt;&lt;%= mensajeError %&gt;&lt;/div&gt;
            &lt;% } %&gt;

            &lt;form action="${pageContext.request.contextPath}/autenticacion" method="post"&gt;
                &lt;label for="usuario"&gt;Usuario:&lt;/label&gt;
                &lt;input type="text" id="usuario" name="usuario" required autofocus&gt;

                &lt;label for="contrasena"&gt;Contrasena:&lt;/label&gt;
                &lt;div class="password-wrapper"&gt;
                    &lt;input type="password" id="contrasena" name="contrasena" required&gt;
                    &lt;button type="button" class="password-toggle"
                            aria-label="Mostrar contrasena"
                            onclick="alternarContrasena(this)"&gt;
                        &lt;svg class="icono-ojo" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"&gt;
                            &lt;path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/&gt;
                            &lt;circle cx="12" cy="12" r="3"/&gt;
                        &lt;/svg&gt;
                        &lt;svg class="icono-ojo-cerrado" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display:none;"&gt;
                            &lt;path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a19.77 19.77 0 0 1 5.06-5.94"/&gt;
                            &lt;path d="M9.9 4.24A10.94 10.94 0 0 1 12 4c7 0 11 8 11 8a19.77 19.77 0 0 1-3.17 4.19"/&gt;
                            &lt;path d="M14.12 14.12A3 3 0 1 1 9.88 9.88"/&gt;
                            &lt;line x1="1" y1="1" x2="23" y2="23"/&gt;
                        &lt;/svg&gt;
                    &lt;/button&gt;
                &lt;/div&gt;

                &lt;button type="submit"&gt;Ingresar&lt;/button&gt;
            &lt;/form&gt;
        &lt;/div&gt;
    &lt;/div&gt;

    &lt;script&gt;
        function alternarContrasena(boton) {
            var input = document.getElementById('contrasena');
            var ojoAbierto = boton.querySelector('.icono-ojo');
            var ojoCerrado = boton.querySelector('.icono-ojo-cerrado');
            if (input.type === 'password') {
                input.type = 'text';
                ojoAbierto.style.display = 'none';
                ojoCerrado.style.display = 'block';
                boton.setAttribute('aria-label', 'Ocultar contrasena');
            } else {
                input.type = 'password';
                ojoAbierto.style.display = 'block';
                ojoCerrado.style.display = 'none';
                boton.setAttribute('aria-label', 'Mostrar contrasena');
            }
        }
    &lt;/script&gt;
&lt;/body&gt;
&lt;/html&gt;
</pre></td></tr></table>

### 8.4.3    Código de menu.jsp

Página del menú principal. Antes de renderizar verifica que exista el atributo `usuario` en sesión; si no, redirige al login (capa de seguridad complementaria al `FiltroSesion`). Muestra una cabecera con el saludo personalizado y el enlace de cerrar sesión, y tres tarjetas con iconos SVG (Longitud, Masa, Temperatura) que apuntan a los servlets de conversión.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
&lt;%@ page contentType="text/html;charset=UTF-8" language="java" %&gt;
&lt;%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/vista/iniciarSesion.jsp");
        return;
    }
%&gt;
&lt;!DOCTYPE html&gt;
&lt;html lang="es"&gt;
&lt;head&gt;
    &lt;meta charset="UTF-8"&gt;
    &lt;title&gt;Menu Principal - CONUNI&lt;/title&gt;
    &lt;link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css"&gt;
&lt;/head&gt;
&lt;body&gt;
    &lt;div class="encabezado"&gt;
        &lt;h1&gt;
            &lt;img class="logo" src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI"&gt;
            Cliente Web CONUNI
        &lt;/h1&gt;
        &lt;div&gt;
            &lt;span&gt;Bienvenido, &lt;%= session.getAttribute("usuario") %&gt;&lt;/span&gt;
            &amp;nbsp;|&amp;nbsp;
            &lt;a href="${pageContext.request.contextPath}/cerrarSesion"&gt;Cerrar Sesion&lt;/a&gt;
        &lt;/div&gt;
    &lt;/div&gt;

    &lt;div class="contenedor"&gt;
        &lt;h2&gt;Menu de Conversiones&lt;/h2&gt;
        &lt;p&gt;Elige una categoria para realizar conversiones de unidades.&lt;/p&gt;

        &lt;div class="menu-opciones"&gt;
            &lt;a href="${pageContext.request.contextPath}/longitud"&gt;
                &lt;svg class="icono" viewBox="0 0 24 24" fill="currentColor"&gt;
                    &lt;path d="M21 6H3c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h18c.55 0 1-.45 1-1V7c0-.55-.45-1-1-1zm-1 10H4V8h2v4h2V8h2v4h2V8h2v4h2V8h2v4h2v4z"/&gt;
                &lt;/svg&gt;
                Longitud
            &lt;/a&gt;
            &lt;a href="${pageContext.request.contextPath}/masa"&gt;
                &lt;svg class="icono" viewBox="0 0 24 24" fill="currentColor"&gt;
                    &lt;path d="M12 3C9.24 3 7 5.24 7 8c0 .85.21 1.65.58 2.35L2 21h20l-5.58-10.65c.37-.7.58-1.5.58-2.35 0-2.76-2.24-5-5-5zm0 2c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3z"/&gt;
                &lt;/svg&gt;
                Masa
            &lt;/a&gt;
            &lt;a href="${pageContext.request.contextPath}/temperatura"&gt;
                &lt;svg class="icono" viewBox="0 0 24 24" fill="currentColor"&gt;
                    &lt;path d="M15 13V5c0-1.66-1.34-3-3-3S9 3.34 9 5v8c-1.21.91-2 2.37-2 4 0 2.76 2.24 5 5 5s5-2.24 5-5c0-1.63-.79-3.09-2-4zm-4-8c0-.55.45-1 1-1s1 .45 1 1h-1v1h1v2h-1v1h1v2h-2V5z"/&gt;
                &lt;/svg&gt;
                Temperatura
            &lt;/a&gt;
        &lt;/div&gt;
    &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</pre></td></tr></table>

### 8.4.4    Código de longitud.jsp

Vista de conversión de longitud. Recibe del servlet tres atributos opcionales: `resultado`, `operacionSeleccionada` y `valorIngresado`. Renderiza un formulario `POST` hacia `/longitud` con un `<select>` de cinco opciones (cada una marcada como `selected` si coincide con el valor recibido) y un `<input type="number">`. Al final pinta el resultado: verde (`mensaje-exito`) si la operación tuvo éxito o rojo (`mensaje-error`) si falló.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
&lt;%@ page contentType="text/html;charset=UTF-8" language="java" %&gt;
&lt;%@ page import="ec.edu.monster.modelo.Resultado" %&gt;
&lt;%
    Resultado resultado = (Resultado) request.getAttribute("resultado");
    String operacionSeleccionada = (String) request.getAttribute("operacionSeleccionada");
    String valorIngresado = (String) request.getAttribute("valorIngresado");
%&gt;
&lt;!DOCTYPE html&gt;
&lt;html lang="es"&gt;
&lt;head&gt;
    &lt;meta charset="UTF-8"&gt;
    &lt;title&gt;Conversiones de Longitud - CONUNI&lt;/title&gt;
    &lt;link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css"&gt;
&lt;/head&gt;
&lt;body&gt;
    &lt;div class="encabezado"&gt;
        &lt;h1&gt;
            &lt;img class="logo" src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI"&gt;
            Cliente Web CONUNI
        &lt;/h1&gt;
        &lt;div&gt;
            &lt;a href="${pageContext.request.contextPath}/vista/menu.jsp"&gt;Menu&lt;/a&gt;
            &amp;nbsp;|&amp;nbsp;
            &lt;a href="${pageContext.request.contextPath}/cerrarSesion"&gt;Cerrar Sesion&lt;/a&gt;
        &lt;/div&gt;
    &lt;/div&gt;

    &lt;div class="contenedor"&gt;
        &lt;div class="conversion-encabezado"&gt;
            &lt;svg viewBox="0 0 24 24" fill="#1f3a5f" width="64" height="64" style="border-radius:12px;border:3px solid #ffd966;padding:6px;background:#fffbe6;"&gt;
                &lt;path d="M21 6H3c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h18c.55 0 1-.45 1-1V7c0-.55-.45-1-1-1zm-1 10H4V8h2v4h2V8h2v4h2V8h2v4h2V8h2v4h2v4z"/&gt;
            &lt;/svg&gt;
            &lt;div&gt;
                &lt;h2&gt;Conversiones de Longitud&lt;/h2&gt;
                &lt;p&gt;Convierte entre metros, pies, kilometros, millas, pulgadas y mas.&lt;/p&gt;
            &lt;/div&gt;
        &lt;/div&gt;

        &lt;form action="${pageContext.request.contextPath}/longitud" method="post"&gt;
            &lt;label for="operacion"&gt;Conversion:&lt;/label&gt;
            &lt;select id="operacion" name="operacion" required&gt;
                &lt;option value="metrosAPies"         &lt;%= "metrosAPies".equals(operacionSeleccionada)         ? "selected" : "" %&gt;&gt;Metros a Pies&lt;/option&gt;
                &lt;option value="kilometrosAMillas"   &lt;%= "kilometrosAMillas".equals(operacionSeleccionada)   ? "selected" : "" %&gt;&gt;Kilometros a Millas&lt;/option&gt;
                &lt;option value="centimetrosAPulgadas"&lt;%= "centimetrosAPulgadas".equals(operacionSeleccionada)? "selected" : "" %&gt;&gt;Centimetros a Pulgadas&lt;/option&gt;
                &lt;option value="yardasAMetros"       &lt;%= "yardasAMetros".equals(operacionSeleccionada)       ? "selected" : "" %&gt;&gt;Yardas a Metros&lt;/option&gt;
                &lt;option value="milimetrosAPulgadas" &lt;%= "milimetrosAPulgadas".equals(operacionSeleccionada) ? "selected" : "" %&gt;&gt;Milimetros a Pulgadas&lt;/option&gt;
            &lt;/select&gt;

            &lt;label for="valor"&gt;Valor:&lt;/label&gt;
            &lt;input type="number" step="any" id="valor" name="valor"
                   value="&lt;%= valorIngresado != null ? valorIngresado : "" %&gt;" required&gt;

            &lt;button type="submit"&gt;Convertir&lt;/button&gt;
        &lt;/form&gt;

        &lt;% if (resultado != null) { %&gt;
            &lt;% if (resultado.isExito()) { %&gt;
                &lt;div class="mensaje-exito"&gt;&lt;%= resultado.getValor() %&gt;&lt;/div&gt;
            &lt;% } else { %&gt;
                &lt;div class="mensaje-error"&gt;&lt;%= resultado.getMensaje() %&gt;&lt;/div&gt;
            &lt;% } %&gt;
        &lt;% } %&gt;
    &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</pre></td></tr></table>

### 8.4.5    Código de masa.jsp

Vista de conversión de masa. Comparte estructura y comportamiento con `longitud.jsp`: cabecera, formulario `POST` hacia `/masa` con `<select>` de cinco opciones (Kilogramos a Libras, Gramos a Onzas, Toneladas a Kilogramos, Libras a Onzas, Miligramos a Gramos), campo numérico y bloque de resultado con clases CSS `mensaje-exito` / `mensaje-error`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
&lt;%@ page contentType="text/html;charset=UTF-8" language="java" %&gt;
&lt;%@ page import="ec.edu.monster.modelo.Resultado" %&gt;
&lt;%
    Resultado resultado = (Resultado) request.getAttribute("resultado");
    String operacionSeleccionada = (String) request.getAttribute("operacionSeleccionada");
    String valorIngresado = (String) request.getAttribute("valorIngresado");
%&gt;
&lt;!DOCTYPE html&gt;
&lt;html lang="es"&gt;
&lt;head&gt;
    &lt;meta charset="UTF-8"&gt;
    &lt;title&gt;Conversiones de Masa - CONUNI&lt;/title&gt;
    &lt;link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css"&gt;
&lt;/head&gt;
&lt;body&gt;
    &lt;div class="encabezado"&gt;
        &lt;h1&gt;
            &lt;img class="logo" src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI"&gt;
            Cliente Web CONUNI
        &lt;/h1&gt;
        &lt;div&gt;
            &lt;a href="${pageContext.request.contextPath}/vista/menu.jsp"&gt;Menu&lt;/a&gt;
            &amp;nbsp;|&amp;nbsp;
            &lt;a href="${pageContext.request.contextPath}/cerrarSesion"&gt;Cerrar Sesion&lt;/a&gt;
        &lt;/div&gt;
    &lt;/div&gt;

    &lt;div class="contenedor"&gt;
        &lt;div class="conversion-encabezado"&gt;
            &lt;svg viewBox="0 0 24 24" fill="#1f3a5f" width="64" height="64" style="border-radius:12px;border:3px solid #ffd966;padding:6px;background:#fffbe6;"&gt;
                &lt;path d="M12 3C9.24 3 7 5.24 7 8c0 .85.21 1.65.58 2.35L2 21h20l-5.58-10.65c.37-.7.58-1.5.58-2.35 0-2.76-2.24-5-5-5zm0 2c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3z"/&gt;
            &lt;/svg&gt;
            &lt;div&gt;
                &lt;h2&gt;Conversiones de Masa&lt;/h2&gt;
                &lt;p&gt;Convierte entre kilogramos, libras, gramos, onzas, toneladas y mas.&lt;/p&gt;
            &lt;/div&gt;
        &lt;/div&gt;

        &lt;form action="${pageContext.request.contextPath}/masa" method="post"&gt;
            &lt;label for="operacion"&gt;Conversion:&lt;/label&gt;
            &lt;select id="operacion" name="operacion" required&gt;
                &lt;option value="kilogramosALibras"    &lt;%= "kilogramosALibras".equals(operacionSeleccionada)    ? "selected" : "" %&gt;&gt;Kilogramos a Libras&lt;/option&gt;
                &lt;option value="gramosAOnzas"         &lt;%= "gramosAOnzas".equals(operacionSeleccionada)         ? "selected" : "" %&gt;&gt;Gramos a Onzas&lt;/option&gt;
                &lt;option value="toneladasAKilogramos" &lt;%= "toneladasAKilogramos".equals(operacionSeleccionada) ? "selected" : "" %&gt;&gt;Toneladas a Kilogramos&lt;/option&gt;
                &lt;option value="librasAOnzas"         &lt;%= "librasAOnzas".equals(operacionSeleccionada)         ? "selected" : "" %&gt;&gt;Libras a Onzas&lt;/option&gt;
                &lt;option value="miligramosAGramos"    &lt;%= "miligramosAGramos".equals(operacionSeleccionada)    ? "selected" : "" %&gt;&gt;Miligramos a Gramos&lt;/option&gt;
            &lt;/select&gt;

            &lt;label for="valor"&gt;Valor:&lt;/label&gt;
            &lt;input type="number" step="any" id="valor" name="valor"
                   value="&lt;%= valorIngresado != null ? valorIngresado : "" %&gt;" required&gt;

            &lt;button type="submit"&gt;Convertir&lt;/button&gt;
        &lt;/form&gt;

        &lt;% if (resultado != null) { %&gt;
            &lt;% if (resultado.isExito()) { %&gt;
                &lt;div class="mensaje-exito"&gt;&lt;%= resultado.getValor() %&gt;&lt;/div&gt;
            &lt;% } else { %&gt;
                &lt;div class="mensaje-error"&gt;&lt;%= resultado.getMensaje() %&gt;&lt;/div&gt;
            &lt;% } %&gt;
        &lt;% } %&gt;
    &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</pre></td></tr></table>

### 8.4.6    Código de temperatura.jsp

Vista de conversión de temperatura. Réplica de `longitud.jsp` y `masa.jsp` con las cinco operaciones de temperatura del WSDL (Celsius a Fahrenheit, Fahrenheit a Celsius, Celsius a Kelvin, Kelvin a Celsius, Fahrenheit a Kelvin). Las tres páginas comparten el mismo patrón —cabecera + formulario + bloque de resultado— lo que mantiene la experiencia homogénea en toda la app.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
&lt;%@ page contentType="text/html;charset=UTF-8" language="java" %&gt;
&lt;%@ page import="ec.edu.monster.modelo.Resultado" %&gt;
&lt;%
    Resultado resultado = (Resultado) request.getAttribute("resultado");
    String operacionSeleccionada = (String) request.getAttribute("operacionSeleccionada");
    String valorIngresado = (String) request.getAttribute("valorIngresado");
%&gt;
&lt;!DOCTYPE html&gt;
&lt;html lang="es"&gt;
&lt;head&gt;
    &lt;meta charset="UTF-8"&gt;
    &lt;title&gt;Conversiones de Temperatura - CONUNI&lt;/title&gt;
    &lt;link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css"&gt;
&lt;/head&gt;
&lt;body&gt;
    &lt;div class="encabezado"&gt;
        &lt;h1&gt;
            &lt;img class="logo" src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI"&gt;
            Cliente Web CONUNI
        &lt;/h1&gt;
        &lt;div&gt;
            &lt;a href="${pageContext.request.contextPath}/vista/menu.jsp"&gt;Menu&lt;/a&gt;
            &amp;nbsp;|&amp;nbsp;
            &lt;a href="${pageContext.request.contextPath}/cerrarSesion"&gt;Cerrar Sesion&lt;/a&gt;
        &lt;/div&gt;
    &lt;/div&gt;

    &lt;div class="contenedor"&gt;
        &lt;div class="conversion-encabezado"&gt;
            &lt;svg viewBox="0 0 24 24" fill="#1f3a5f" width="64" height="64" style="border-radius:12px;border:3px solid #ffd966;padding:6px;background:#fffbe6;"&gt;
                &lt;path d="M15 13V5c0-1.66-1.34-3-3-3S9 3.34 9 5v8c-1.21.91-2 2.37-2 4 0 2.76 2.24 5 5 5s5-2.24 5-5c0-1.63-.79-3.09-2-4zm-4-8c0-.55.45-1 1-1s1 .45 1 1h-1v1h1v2h-1v1h1v2h-2V5z"/&gt;
            &lt;/svg&gt;
            &lt;div&gt;
                &lt;h2&gt;Conversiones de Temperatura&lt;/h2&gt;
                &lt;p&gt;Convierte entre Celsius, Fahrenheit y Kelvin.&lt;/p&gt;
            &lt;/div&gt;
        &lt;/div&gt;

        &lt;form action="${pageContext.request.contextPath}/temperatura" method="post"&gt;
            &lt;label for="operacion"&gt;Conversion:&lt;/label&gt;
            &lt;select id="operacion" name="operacion" required&gt;
                &lt;option value="celsiusAFahrenheit" &lt;%= "celsiusAFahrenheit".equals(operacionSeleccionada) ? "selected" : "" %&gt;&gt;Celsius a Fahrenheit&lt;/option&gt;
                &lt;option value="fahrenheitACelsius" &lt;%= "fahrenheitACelsius".equals(operacionSeleccionada) ? "selected" : "" %&gt;&gt;Fahrenheit a Celsius&lt;/option&gt;
                &lt;option value="celsiusAKelvin"     &lt;%= "celsiusAKelvin".equals(operacionSeleccionada)     ? "selected" : "" %&gt;&gt;Celsius a Kelvin&lt;/option&gt;
                &lt;option value="kelvinACelsius"     &lt;%= "kelvinACelsius".equals(operacionSeleccionada)     ? "selected" : "" %&gt;&gt;Kelvin a Celsius&lt;/option&gt;
                &lt;option value="fahrenheitAKelvin"  &lt;%= "fahrenheitAKelvin".equals(operacionSeleccionada)  ? "selected" : "" %&gt;&gt;Fahrenheit a Kelvin&lt;/option&gt;
            &lt;/select&gt;

            &lt;label for="valor"&gt;Valor:&lt;/label&gt;
            &lt;input type="number" step="any" id="valor" name="valor"
                   value="&lt;%= valorIngresado != null ? valorIngresado : "" %&gt;" required&gt;

            &lt;button type="submit"&gt;Convertir&lt;/button&gt;
        &lt;/form&gt;

        &lt;% if (resultado != null) { %&gt;
            &lt;% if (resultado.isExito()) { %&gt;
                &lt;div class="mensaje-exito"&gt;&lt;%= resultado.getValor() %&gt;&lt;/div&gt;
            &lt;% } else { %&gt;
                &lt;div class="mensaje-error"&gt;&lt;%= resultado.getMensaje() %&gt;&lt;/div&gt;
            &lt;% } %&gt;
        &lt;% } %&gt;
    &lt;/div&gt;
&lt;/body&gt;
&lt;/html&gt;
</pre></td></tr></table>

---

## 8.5    Paquete de pruebas

Este paquete agrupa las pruebas unitarias y de integración del cliente Web, implementadas con JUnit 4. Las clases `pruebaResultado` y `pruebaFormatoConversion` validan el modelo sin red; `pruebaConexionServidor` realiza pruebas de integración que se *saltan* automáticamente (`Assume.assumeTrue`) si el servidor SOAP no está disponible en `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl`.

-    pruebaResultado
-    pruebaFormatoConversion
-    pruebaConexionServidor

### 8.5.1    Código de pruebaResultado

Suite unitaria del DTO `Resultado`. Verifica que la factoría `ok(...)` marca el resultado como exitoso y conserva el valor, que la factoría `error(...)` invierte el *flag* y deja `valor` nulo, y que el mensaje por defecto del modo exitoso es `"Operacion exitosa"`. No requiere conexión al servidor.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.prueba;

import ec.edu.monster.modelo.Resultado;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaResultado {

    @Test
    public void pruebaResultadoOkContieneValor() {
        Resultado r = Resultado.ok("7 m = 22.9659 ft");
        assertTrue(r.isExito());
        assertEquals("7 m = 22.9659 ft", r.getValor());
    }

    @Test
    public void pruebaResultadoOkMensajePorDefecto() {
        Resultado r = Resultado.ok("cualquier cosa");
        assertEquals("Operacion exitosa", r.getMensaje());
    }

    @Test
    public void pruebaResultadoErrorContieneMensaje() {
        Resultado r = Resultado.error("Credenciales invalidas");
        assertFalse(r.isExito());
        assertEquals("Credenciales invalidas", r.getMensaje());
    }

    @Test
    public void pruebaResultadoErrorValorEsNulo() {
        Resultado r = Resultado.error("Algo fallo");
        assertNull(r.getValor());
    }

    @Test
    public void pruebaResultadoOkSiempreEsExitoso() {
        assertTrue(Resultado.ok("").isExito());
    }

    @Test
    public void pruebaResultadoErrorSiempreFalla() {
        assertFalse(Resultado.error("").isExito());
    }
}
</pre></td></tr></table>

### 8.5.2    Código de pruebaFormatoConversion

Suite unitaria de la utilidad `FormatoConversion`. Cubre los tres métodos: `fmt(double)` con enteros, ceros, negativos, decimales y redondeo; `formatear(...)` con casos de longitud, masa y temperatura; y `unidades(operacion)` con todas las claves del WSDL más una operación inexistente. La última prueba recorre las 15 operaciones para garantizar que ninguna devuelva un arreglo con cadenas vacías.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.prueba;

import ec.edu.monster.modelo.FormatoConversion;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaFormatoConversion {

    // ========== fmt(double) ==========

    @Test
    public void pruebaFmtEntero() {
        assertEquals("7", FormatoConversion.fmt(7.0));
    }

    @Test
    public void pruebaFmtCero() {
        assertEquals("0", FormatoConversion.fmt(0.0));
    }

    @Test
    public void pruebaFmtNegativoEntero() {
        assertEquals("-273", FormatoConversion.fmt(-273.0));
    }

    @Test
    public void pruebaFmtUnDecimal() {
        assertEquals("7.5", FormatoConversion.fmt(7.5));
    }

    @Test
    public void pruebaFmtCuatroDecimales() {
        assertEquals("22.9659", FormatoConversion.fmt(22.9659));
    }

    @Test
    public void pruebaFmtRedondeaACuatroDecimales() {
        assertEquals("32.8084", FormatoConversion.fmt(32.808398950131235));
    }

    @Test
    public void pruebaFmtQuitaCerosSobrantes() {
        assertEquals("273.15", FormatoConversion.fmt(273.15));
    }

    @Test
    public void pruebaFmtNegativoConDecimales() {
        assertEquals("-7.5", FormatoConversion.fmt(-7.5));
    }

    // ========== formatear(entrada, origen, salida, destino) ==========

    @Test
    public void pruebaFormatearLongitud() {
        assertEquals("7 m = 22.9659 ft",
                FormatoConversion.formatear(7.0, "m", 22.96588, "ft"));
    }

    @Test
    public void pruebaFormatearTemperaturaCero() {
        assertEquals("0 °C = 32 °F",
                FormatoConversion.formatear(0.0, "°C", 32.0, "°F"));
    }

    @Test
    public void pruebaFormatearTemperaturaKelvin() {
        assertEquals("0 °C = 273.15 K",
                FormatoConversion.formatear(0.0, "°C", 273.15, "K"));
    }

    @Test
    public void pruebaFormatearMasa() {
        assertEquals("1 kg = 2.2046 lb",
                FormatoConversion.formatear(1.0, "kg", 2.20462, "lb"));
    }

    // ========== unidades(operacion) ==========

    @Test
    public void pruebaUnidadesMetrosAPies() {
        assertArrayEquals(new String[]{"m", "ft"},
                FormatoConversion.unidades("metrosAPies"));
    }

    @Test
    public void pruebaUnidadesKilogramosALibras() {
        assertArrayEquals(new String[]{"kg", "lb"},
                FormatoConversion.unidades("kilogramosALibras"));
    }

    @Test
    public void pruebaUnidadesCelsiusAFahrenheit() {
        assertArrayEquals(new String[]{"°C", "°F"},
                FormatoConversion.unidades("celsiusAFahrenheit"));
    }

    @Test
    public void pruebaUnidadesKelvinACelsius() {
        assertArrayEquals(new String[]{"K", "°C"},
                FormatoConversion.unidades("kelvinACelsius"));
    }

    @Test
    public void pruebaUnidadesOperacionDesconocida() {
        assertArrayEquals(new String[]{"", ""},
                FormatoConversion.unidades("operacionInexistente"));
    }

    @Test
    public void pruebaUnidadesParaLas15Conversiones() {
        String[] todas = {
            "metrosAPies", "kilometrosAMillas", "centimetrosAPulgadas",
            "yardasAMetros", "milimetrosAPulgadas",
            "kilogramosALibras", "gramosAOnzas", "toneladasAKilogramos",
            "librasAOnzas", "miligramosAGramos",
            "celsiusAFahrenheit", "fahrenheitACelsius",
            "celsiusAKelvin", "kelvinACelsius", "fahrenheitAKelvin"
        };
        for (String op : todas) {
            String[] u = FormatoConversion.unidades(op);
            assertNotNull("Sin unidades para " + op, u);
            assertEquals("Origen vacio para " + op, false, u[0].isEmpty());
            assertEquals("Destino vacio para " + op, false, u[1].isEmpty());
        }
    }
}
</pre></td></tr></table>

### 8.5.3    Código de pruebaConexionServidor

Suite de integración que ejerce los servicios proxy contra el servidor SOAP real. Antes de cada prueba, `@Before verificarServidor()` hace una petición `HEAD` al WSDL y, si no responde, ejecuta `Assume.assumeTrue(...)` para saltar la prueba sin marcarla como fallida. Cubre las tres categorías (autenticación con credenciales válidas e inválidas, longitud, masa y temperatura), con un margen de tolerancia de `0.0001` para los comparativos de `double`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.prueba;

import ec.edu.monster.modelo.ServicioAutenticacion;
import ec.edu.monster.modelo.ServicioLongitud;
import ec.edu.monster.modelo.ServicioMasa;
import ec.edu.monster.modelo.ServicioTemperatura;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de INTEGRACION: requieren que el servidor SOAP este desplegado en
 *   http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI
 *
 * Si el servidor no responde, las pruebas se SALTAN (no fallan) gracias a
 * Assume.assumeTrue(servidorDisponible()).
 *
 * Para forzar la ejecucion: arranca primero el proyecto servidor en NetBeans
 * (Deploy a GlassFish/Payara) y luego corre estas pruebas.
 */
public class pruebaConexionServidor {

    private static final String URL_WSDL =
            "http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl";
    private static final double MARGEN = 0.0001;

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    @Before
    public void verificarServidor() {
        Assume.assumeTrue(
            "Servidor SOAP no disponible en " + URL_WSDL + " — prueba saltada.",
            servidorDisponible());
    }

    // ========== Autenticacion ==========

    @Test
    public void pruebaLoginValido() throws Exception {
        assertTrue(servicioAutenticacion.iniciarSesion("MONSTER", "MONSTER9"));
    }

    @Test
    public void pruebaLoginInvalido() throws Exception {
        assertFalse(servicioAutenticacion.iniciarSesion("MONSTER", "incorrecta"));
    }

    @Test
    public void pruebaLoginUsuarioInexistente() throws Exception {
        assertFalse(servicioAutenticacion.iniciarSesion("noexiste", "x"));
    }

    // ========== Longitud ==========

    @Test
    public void pruebaConversionLongitud() throws Exception {
        assertEquals(32.8084, servicioLongitud.metrosAPies(10.0), MARGEN);
    }

    @Test
    public void pruebaKilometrosAMillas() throws Exception {
        assertEquals(6.21371, servicioLongitud.kilometrosAMillas(10.0), MARGEN);
    }

    // ========== Masa ==========

    @Test
    public void pruebaConversionMasa() throws Exception {
        assertEquals(2.20462, servicioMasa.kilogramosALibras(1.0), MARGEN);
    }

    @Test
    public void pruebaToneladasAKilogramos() throws Exception {
        assertEquals(2000.0, servicioMasa.toneladasAKilogramos(2.0), MARGEN);
    }

    // ========== Temperatura ==========

    @Test
    public void pruebaCelsiusAFahrenheit() throws Exception {
        assertEquals(32.0, servicioTemperatura.celsiusAFahrenheit(0.0), MARGEN);
        assertEquals(212.0, servicioTemperatura.celsiusAFahrenheit(100.0), MARGEN);
    }

    @Test
    public void pruebaCelsiusAKelvin() throws Exception {
        assertEquals(273.15, servicioTemperatura.celsiusAKelvin(0.0), MARGEN);
    }

    // ========== Helper ==========

    private static boolean servidorDisponible() {
        try {
            java.net.URL url = new java.net.URL(URL_WSDL);
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.setRequestMethod("HEAD");
            int code = con.getResponseCode();
            con.disconnect();
            return code &gt;= 200 &amp;&amp; code &lt; 400;
        } catch (Exception ex) {
            return false;
        }
    }
}
</pre></td></tr></table>
