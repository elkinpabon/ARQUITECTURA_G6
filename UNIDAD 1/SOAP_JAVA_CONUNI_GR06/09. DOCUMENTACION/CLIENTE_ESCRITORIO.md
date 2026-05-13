# 7. CREACIÓN CLIENTE ESCRITORIO

## 7.1    Paquete monster

Este paquete raíz agrupa el punto de entrada de la aplicación de escritorio. Contiene una única clase ejecutable que prepara el *Look & Feel* del sistema operativo y delega el arranque al Controlador dentro del *Event Dispatch Thread* de Swing.

-    Aplicacion

### 7.1.1    Código de Aplicacion

Clase de arranque del cliente de escritorio. Su método `main` aplica `UIManager.getSystemLookAndFeelClassName()` para que la UI use el aspecto nativo de la plataforma, y a continuación invoca `new ControladorEscritorio().iniciar()` dentro de `SwingUtilities.invokeLater(...)`, garantizando que toda creación de componentes Swing ocurra en el EDT.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster;

import ec.edu.monster.controlador.ControladorEscritorio;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicacion de escritorio. Aplica el Look &amp; Feel del
 * sistema y arranca el Controlador dentro del Event Dispatch Thread, como
 * exige Swing.
 */
public class Aplicacion {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -&gt; {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // Si falla, Swing usa el Look &amp; Feel metal por defecto.
            }
            new ControladorEscritorio().iniciar();
        });
    }
}
</pre></td></tr></table>

---

## 7.2    Paquete controlador

Este paquete contiene la clase que coordina la Vista (paneles Swing) con el Modelo (servicios proxy SOAP). Es el único punto donde se cablean los *callbacks* de la UI con las llamadas remotas, y se encarga de que ninguna operación de red bloquee el hilo de eventos mediante el uso de `SwingWorker`.

-    ControladorEscritorio

### 7.2.1    Código de ControladorEscritorio

Controlador MVC de la aplicación. Crea la `VentanaPrincipal` y los cuatro servicios proxy (`ServicioAutenticacion`, `ServicioLongitud`, `ServicioMasa`, `ServicioTemperatura`), y registra los *callbacks* de cada panel: login, selección de categoría, conversión, volver al menú y cerrar sesión. Tanto la autenticación como las conversiones se ejecutan dentro de un `SwingWorker` para no congelar la interfaz mientras viaja la petición SOAP. El despacho de la operación al servicio correcto se hace por `switch` sobre la categoría y el nombre de operación.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.FormatoConversion;
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

        new SwingWorker&lt;Boolean, Void&gt;() {
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

        new SwingWorker&lt;Resultado, Void&gt;() {
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
            String[] u = FormatoConversion.unidades(operacion);
            return Resultado.ok(FormatoConversion.formatear(valor, u[0], r, u[1]));
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
</pre></td></tr></table>

---

## 7.3    Paquete modelo

Este paquete agrupa las clases del modelo del cliente Escritorio: encapsulan la lógica de envío y recepción de los datos hacia el servidor SOAP, así como el formateo de los resultados que se muestran al usuario. Está formado por un cliente SOAP genérico (`ClienteSoap`), un DTO para transportar resultados (`Resultado`), una utilidad de formateo (`FormatoConversion`) y cuatro servicios proxy que envuelven cada grupo de operaciones del WSDL: `ServicioAutenticacion`, `ServicioLongitud`, `ServicioMasa` y `ServicioTemperatura`.

-    ClienteSoap
-    Resultado
-    FormatoConversion
-    ServicioAutenticacion
-    ServicioLongitud
-    ServicioMasa
-    ServicioTemperatura

### 7.3.1    Código de ClienteSoap

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

### 7.3.2    Código de Resultado

DTO simple para transportar entre el controlador y la vista el desenlace de una operación. Encapsula tres atributos inmutables: un *flag* `exito`, un `mensaje` descriptivo y el `valor` formateado. Expone dos factorías estáticas, `ok(valor)` y `error(mensaje)`, que el `ControladorEscritorio` usa para devolver al panel un resultado verde (éxito) o rojo (error) sin instanciar manualmente el constructor.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

/**
 * DTO simple para transportar un resultado de operacion entre el controlador y la vista.
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

### 7.3.3    Código de FormatoConversion

Utilidad estática que centraliza el formateo de los resultados de conversión que devuelve el servicio SOAP. Expone tres métodos: `fmt(double)` redondea a cuatro decimales y elimina ceros sobrantes, `unidades(operacion)` devuelve un arreglo `{origen, destino}` para cada una de las 16 conversiones del WSDL, y `formatear(...)` ensambla el string final del tipo `"7 m = 22.9659 ft"` que se muestra en el `PanelConversion`.

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

### 7.3.4    Código de ServicioAutenticacion

Servicio proxy de la operación `iniciarSesion` del WSDL. Recibe usuario y contraseña, los empaqueta en un `LinkedHashMap` (para preservar el orden de los parámetros en el sobre SOAP) y delega la invocación en `ClienteSoap`. Convierte la respuesta textual en `boolean` para que el controlador pueda evaluar directamente si la autenticación fue exitosa.

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

### 7.3.5    Código de ServicioLongitud

Servicio proxy de las cinco operaciones de longitud del WSDL: `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros` y `milimetrosAPulgadas`. Cada método público envuelve la llamada con `invocarUnario(...)`, que arma el `Map` de un solo parámetro, delega en `ClienteSoap` y convierte la respuesta textual a `double`.

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

### 7.3.6    Código de ServicioMasa

Servicio proxy de las cinco operaciones de masa del WSDL: `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas` y `miligramosAGramos`. Sigue el mismo patrón que `ServicioLongitud`: cada método publica una conversión y delega en el helper `invocarUnario(...)` que centraliza la invocación SOAP y el parseo del resultado.

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

### 7.3.7    Código de ServicioTemperatura

Servicio proxy de las operaciones de temperatura del WSDL: `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius` y `fahrenheitAKelvin`. Comparte la estructura de los otros servicios proxy: un método por conversión y un helper privado `invocarUnario(...)` que arma el sobre SOAP a través de `ClienteSoap` y devuelve el `double` ya parseado.

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

## 7.4    Paquete vista

Este paquete agrupa las clases de la interfaz gráfica Swing del cliente Escritorio. Contiene la ventana raíz (`VentanaPrincipal`) con un `CardLayout` que alterna entre tres paneles Matisse (`PanelLogin`, `PanelMenu`, `PanelConversion`), más una clase utilitaria (`Paleta`) que centraliza los colores y fuentes corporativos de la marca CONUNI. Los paneles solo manejan presentación y eventos de UI: la lógica de negocio se delega al `ControladorEscritorio` mediante *callbacks*.

-    VentanaPrincipal
-    Paleta
-    PanelLogin
-    PanelMenu
-    PanelConversion

### 7.4.1    Código de VentanaPrincipal

Ventana raíz de la aplicación (`JFrame`). Aloja un `CardLayout` con tres tarjetas (`LOGIN`, `MENU`, `CONVERSOR`) y expone métodos *getter* para que el controlador acceda a cada panel sin acoplarse a su instanciación. El método `mostrar(tarjeta)` cambia la tarjeta visible; no contiene lógica de negocio, solo navegación.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Ventana raiz de la aplicacion. Contiene un {@link CardLayout} con los tres
 * paneles (Login, Menu, Conversion) y expone metodos para navegar entre ellos.
 * No contiene logica de negocio — la navega el Controlador.
 */
public class VentanaPrincipal extends JFrame {

    public static final String TARJETA_LOGIN     = "LOGIN";
    public static final String TARJETA_MENU      = "MENU";
    public static final String TARJETA_CONVERSOR = "CONVERSOR";

    private final CardLayout layoutTarjetas = new CardLayout();
    private final JPanel contenedor = new JPanel(layoutTarjetas);

    private final PanelLogin panelLogin = new PanelLogin();
    private final PanelMenu panelMenu = new PanelMenu();
    private final PanelConversion panelConversion = new PanelConversion();

    public VentanaPrincipal() {
        setTitle("Cliente Escritorio CONUNI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);
        setResizable(true);

        contenedor.add(panelLogin, TARJETA_LOGIN);
        contenedor.add(panelMenu, TARJETA_MENU);
        contenedor.add(panelConversion, TARJETA_CONVERSOR);

        getContentPane().add(contenedor);
    }

    public void mostrar(String tarjeta) {
        layoutTarjetas.show(contenedor, tarjeta);
    }

    public PanelLogin getPanelLogin()         { return panelLogin; }
    public PanelMenu getPanelMenu()           { return panelMenu; }
    public PanelConversion getPanelConversion() { return panelConversion; }
}
</pre></td></tr></table>

### 7.4.2    Código de Paleta

Clase utilitaria con la paleta y tipografías de la marca CONUNI. Declara como constantes públicas los colores corporativos (`AZUL`, `AMARILLO`, `GRIS_FONDO`, fondos y textos de éxito/error) y las fuentes (`TITULO`, `SUBTITULO`, `ETIQUETA`, `CAMPO`). Los tres paneles del paquete referencian estas constantes para mantener una identidad visual coherente.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta y fuentes de la marca CONUNI. Centraliza los estilos para mantener
 * coherencia visual entre los paneles de la aplicacion de escritorio.
 */
public final class Paleta {

    public static final Color AZUL            = new Color(0x1F, 0x3A, 0x5F);
    public static final Color AZUL_CLARO      = new Color(0x2D, 0x4F, 0x7A);
    public static final Color AMARILLO        = new Color(0xFF, 0xD9, 0x66);
    public static final Color GRIS_FONDO      = new Color(0xF2, 0xF4, 0xF7);
    public static final Color VERDE_EXITO_BG  = new Color(0xE2, 0xFD, 0xE2);
    public static final Color VERDE_EXITO_FG  = new Color(0x00, 0x6B, 0x00);
    public static final Color ROJO_ERROR_BG   = new Color(0xFD, 0xE2, 0xE2);
    public static final Color ROJO_ERROR_FG   = new Color(0xA1, 0x00, 0x00);
    public static final Color TEXTO_SUAVE     = new Color(0x6B, 0x75, 0x85);

    public static final Font TITULO     = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBTITULO  = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font ETIQUETA   = new Font("SansSerif", Font.BOLD, 13);
    public static final Font CAMPO      = new Font("SansSerif", Font.PLAIN, 14);

    private Paleta() { }
}
</pre></td></tr></table>

### 7.4.3    Código de PanelLogin

Panel Matisse de inicio de sesión (archivo `.form` emparejado, editable en NetBeans → Design view). Carga la imagen y el logo desde `src/img/`, aplica los estilos de la `Paleta`, conecta los eventos de teclado/ratón a `dispararLogin()` y expone una API mínima al controlador: `setOnLogin(BiConsumer)`, `mostrarError(String)`, `setBotonHabilitado(boolean)` y `limpiar()`. El botón "Mostrar/Ocultar" alterna el *echo char* de la contraseña.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.function.BiConsumer;
import javax.swing.ImageIcon;

/**
 * Panel de login en formato Matisse (archivo .form emparejado).
 * Puedes abrirlo en NetBeans con clic derecho -&gt; Open -&gt; pestana "Design"
 * para editarlo visualmente con drag-and-drop.
 *
 * El metodo initComponents() es generado/mantenido por NetBeans, no lo
 * edites a mano. La logica de UI (imagenes, eventos, MVC) vive en
 * configurarVista() y conectarEventos(), fuera del area generada.
 */
public class PanelLogin extends javax.swing.JPanel {

    private BiConsumer&lt;String, String&gt; accionLogin;

    public PanelLogin() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    /** Carga la imagen y el logo fuera del area generada por Matisse. */
    private void configurarVista() {
        setBackground(Color.WHITE);

        URL urlImagen = getClass().getResource("/img/login.jpg");
        if (urlImagen != null) {
            Image img = new ImageIcon(urlImagen).getImage()
                    .getScaledInstance(320, 380, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
            lblImagen.setText("");
        }

        URL urlLogo = getClass().getResource("/img/moster.png");
        if (urlLogo != null) {
            Image img = new ImageIcon(urlLogo).getImage()
                    .getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setText("");
        }

        lblTitulo.setForeground(Paleta.AZUL);
        lblTitulo.setFont(Paleta.TITULO);
        lblSubtitulo.setForeground(Paleta.TEXTO_SUAVE);

        botonIngresar.setBackground(Paleta.AZUL);
        botonIngresar.setForeground(Color.WHITE);
        botonIngresar.setOpaque(true);
        botonIngresar.setBorderPainted(false);

        lblError.setForeground(Paleta.ROJO_ERROR_FG);
    }

    private void conectarEventos() {
        botonMostrar.addActionListener(e -&gt; toggleVisibilidad());
        botonIngresar.addActionListener(e -&gt; dispararLogin());
        campoUsuario.addActionListener(e -&gt; campoContrasena.requestFocusInWindow());
        campoContrasena.addActionListener(e -&gt; dispararLogin());
    }

    private void toggleVisibilidad() {
        if (campoContrasena.getEchoChar() != (char) 0) {
            campoContrasena.setEchoChar((char) 0);
            botonMostrar.setText("Ocultar");
        } else {
            campoContrasena.setEchoChar('•');
            botonMostrar.setText("Mostrar");
        }
    }

    private void dispararLogin() {
        if (accionLogin != null) {
            accionLogin.accept(
                campoUsuario.getText().trim(),
                new String(campoContrasena.getPassword())
            );
        }
    }

    // ========= API publica consumida por ControladorEscritorio =========

    public void setOnLogin(BiConsumer&lt;String, String&gt; accion) {
        this.accionLogin = accion;
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null ? " " : mensaje);
    }

    public void setBotonHabilitado(boolean habilitado) {
        botonIngresar.setEnabled(habilitado);
        botonIngresar.setText(habilitado ? "Ingresar" : "Ingresando...");
    }

    public void limpiar() {
        campoUsuario.setText("");
        campoContrasena.setText("");
        lblError.setText(" ");
    }

    // &lt;editor-fold defaultstate="collapsed" desc="Generated Code"&gt;//GEN-BEGIN:initComponents
    private void initComponents() {

        lblImagen = new javax.swing.JLabel();
        lblLogo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        campoUsuario = new javax.swing.JTextField();
        lblContrasena = new javax.swing.JLabel();
        campoContrasena = new javax.swing.JPasswordField();
        botonMostrar = new javax.swing.JButton();
        botonIngresar = new javax.swing.JButton();
        lblError = new javax.swing.JLabel();

        lblImagen.setBackground(new java.awt.Color(31, 58, 95));
        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setText("Imagen login");
        lblImagen.setOpaque(true);

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("Logo");

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ec/edu/monster/vista/Bundle"); // NOI18N
        lblTitulo.setText(bundle.getString("PanelLogin.lblTitulo.text")); // NOI18N

        lblSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitulo.setText("Ingresa tus credenciales");

        lblUsuario.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblUsuario.setText("Usuario:");

        lblContrasena.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblContrasena.setText("Contrasena:");

        botonMostrar.setText("Mostrar");

        botonIngresar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        botonIngresar.setText("Ingresar");
        botonIngresar.setFocusPainted(false);

        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblSubtitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblUsuario)
                    .addComponent(campoUsuario)
                    .addComponent(lblContrasena)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(campoContrasena, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonIngresar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(115, 115, 115))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblTitulo)
                .addGap(2, 2, 2)
                .addComponent(lblSubtitulo)
                .addGap(16, 16, 16)
                .addComponent(lblUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblContrasena)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(campoContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(botonIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblError)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// &lt;/editor-fold&gt;//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonIngresar;
    private javax.swing.JButton botonMostrar;
    private javax.swing.JPasswordField campoContrasena;
    private javax.swing.JTextField campoUsuario;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    // End of variables declaration//GEN-END:variables
}
</pre></td></tr></table>

### 7.4.4    Código de PanelMenu

Panel Matisse del menú principal (archivo `.form` emparejado). Muestra una cabecera azul con logo, título y saludo personalizado al usuario autenticado, y debajo tres tarjetas (Longitud, Masa, Temperatura) formateadas con HTML. Notifica al controlador la categoría elegida mediante `setOnCategoriaSeleccionada(Consumer<String>)` y el cierre de sesión mediante `setOnCerrarSesion(Runnable)`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.function.Consumer;
import javax.swing.ImageIcon;

/**
 * Panel del menu principal en formato Matisse (archivo .form emparejado).
 * Editable en NetBeans -&gt; Design view.
 *
 * Cabecera arriba (logo + titulo + saludo + cerrar sesion) y 3 tarjetas
 * debajo (Longitud / Masa / Temperatura).
 */
public class PanelMenu extends javax.swing.JPanel {

    private Consumer&lt;String&gt; accionCategoria;
    private Runnable accionCerrarSesion;

    public PanelMenu() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    private void configurarVista() {
        setBackground(Paleta.GRIS_FONDO);

        // Cabecera: fondo azul con textos claros
        panelCabecera.setBackground(Paleta.AZUL);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(Paleta.ETIQUETA);
        lblSaludo.setForeground(Paleta.AMARILLO);
        lblSaludo.setFont(Paleta.SUBTITULO);

        URL urlLogo = getClass().getResource("/img/moster.png");
        if (urlLogo != null) {
            Image img = new ImageIcon(urlLogo).getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setText("");
        }

        // Tarjetas: azul con texto blanco y opaco (para macOS)
        for (javax.swing.JButton tarjeta : new javax.swing.JButton[]{btnLongitud, btnMasa, btnTemperatura}) {
            tarjeta.setBackground(Paleta.AZUL);
            tarjeta.setForeground(Color.WHITE);
            tarjeta.setOpaque(true);
            tarjeta.setBorderPainted(false);
            tarjeta.setContentAreaFilled(true);
            tarjeta.setFocusPainted(false);
        }

        btnLongitud.setText("&lt;html&gt;&lt;div style='text-align:center;'&gt;"
                + "&lt;div style='font-size:18px;font-weight:bold;'&gt;Longitud&lt;/div&gt;"
                + "&lt;div style='font-size:11px;color:#D0DAE8;margin-top:6px;'&gt;"
                + "Metros, pies, millas, pulgadas...&lt;/div&gt;&lt;/div&gt;&lt;/html&gt;");
        btnMasa.setText("&lt;html&gt;&lt;div style='text-align:center;'&gt;"
                + "&lt;div style='font-size:18px;font-weight:bold;'&gt;Masa&lt;/div&gt;"
                + "&lt;div style='font-size:11px;color:#D0DAE8;margin-top:6px;'&gt;"
                + "Kilogramos, libras, onzas...&lt;/div&gt;&lt;/div&gt;&lt;/html&gt;");
        btnTemperatura.setText("&lt;html&gt;&lt;div style='text-align:center;'&gt;"
                + "&lt;div style='font-size:18px;font-weight:bold;'&gt;Temperatura&lt;/div&gt;"
                + "&lt;div style='font-size:11px;color:#D0DAE8;margin-top:6px;'&gt;"
                + "Celsius, Fahrenheit, Kelvin&lt;/div&gt;&lt;/div&gt;&lt;/html&gt;");
    }

    private void conectarEventos() {
        btnLongitud.addActionListener(e    -&gt; notificar("longitud"));
        btnMasa.addActionListener(e        -&gt; notificar("masa"));
        btnTemperatura.addActionListener(e -&gt; notificar("temperatura"));
        btnCerrarSesion.addActionListener(e -&gt; {
            if (accionCerrarSesion != null) accionCerrarSesion.run();
        });
    }

    private void notificar(String categoria) {
        if (accionCategoria != null) accionCategoria.accept(categoria);
    }

    // ========= API publica consumida por ControladorEscritorio =========

    public void setUsuario(String usuario) {
        lblSaludo.setText("Bienvenido, " + usuario);
    }

    public void setOnCategoriaSeleccionada(Consumer&lt;String&gt; accion) {
        this.accionCategoria = accion;
    }

    public void setOnCerrarSesion(Runnable accion) {
        this.accionCerrarSesion = accion;
    }

    // &lt;editor-fold defaultstate="collapsed" desc="Generated Code"&gt;//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelCabecera = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblSaludo = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        btnLongitud = new javax.swing.JButton();
        btnMasa = new javax.swing.JButton();
        btnTemperatura = new javax.swing.JButton();

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("Logo");

        lblTitulo.setText("Cliente Escritorio CONUNI");

        lblSaludo.setText("Bienvenido");

        btnCerrarSesion.setText("Cerrar Sesion");
        btnCerrarSesion.setFocusPainted(false);

        javax.swing.GroupLayout panelCabeceraLayout = new javax.swing.GroupLayout(panelCabecera);
        panelCabecera.setLayout(panelCabeceraLayout);
        panelCabeceraLayout.setHorizontalGroup(
            panelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSaludo)
                .addGap(18, 18, 18)
                .addComponent(btnCerrarSesion)
                .addContainerGap())
        );
        panelCabeceraLayout.setVerticalGroup(
            panelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitulo)
                    .addComponent(lblSaludo)
                    .addComponent(btnCerrarSesion))
                .addContainerGap())
        );

        btnLongitud.setText("Longitud");
        btnLongitud.setFocusPainted(false);

        btnMasa.setText("Masa");
        btnMasa.setFocusPainted(false);

        btnTemperatura.setText("Temperatura");
        btnTemperatura.setFocusPainted(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCabecera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnLongitud, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(btnMasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(btnTemperatura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCabecera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLongitud, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(btnMasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTemperatura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }// &lt;/editor-fold&gt;//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnLongitud;
    private javax.swing.JButton btnMasa;
    private javax.swing.JButton btnTemperatura;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSaludo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelCabecera;
    // End of variables declaration//GEN-END:variables
}
</pre></td></tr></table>

### 7.4.5    Código de PanelConversion

Panel Matisse parametrizable que ofrece el formulario de conversión. La clase interna `Opcion` empareja la clave de operación SOAP con su etiqueta legible; tres listas estáticas (`OPCIONES_LONGITUD`, `OPCIONES_MASA`, `OPCIONES_TEMPERATURA`) definen las 15 conversiones. `setCategoria(String)` cambia título y `JComboBox` según la categoría seleccionada. La conversión se dispara con `setOnConvertir(BiConsumer<String, Double>)` y el resultado se pinta verde/rojo a través de `mostrarResultado(Resultado)` usando la `Paleta`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import ec.edu.monster.modelo.Resultado;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.DefaultComboBoxModel;

/**
 * Panel de conversion en formato Matisse (archivo .form emparejado).
 * Editable en NetBeans -&gt; Design view.
 *
 * Es parametrizable: llamar {@link #setCategoria(String)} con "longitud",
 * "masa" o "temperatura" cambia el titulo y las opciones del combo.
 */
public class PanelConversion extends javax.swing.JPanel {

    public static class Opcion {
        public final String clave;
        public final String etiqueta;
        public Opcion(String clave, String etiqueta) {
            this.clave = clave;
            this.etiqueta = etiqueta;
        }
        @Override public String toString() { return etiqueta; }
    }

    private static final List&lt;Opcion&gt; OPCIONES_LONGITUD = Arrays.asList(
        new Opcion("metrosAPies",          "Metros a Pies"),
        new Opcion("kilometrosAMillas",    "Kilometros a Millas"),
        new Opcion("centimetrosAPulgadas", "Centimetros a Pulgadas"),
        new Opcion("yardasAMetros",        "Yardas a Metros"),
        new Opcion("milimetrosAPulgadas",  "Milimetros a Pulgadas")
    );
    private static final List&lt;Opcion&gt; OPCIONES_MASA = Arrays.asList(
        new Opcion("kilogramosALibras",    "Kilogramos a Libras"),
        new Opcion("gramosAOnzas",         "Gramos a Onzas"),
        new Opcion("toneladasAKilogramos", "Toneladas a Kilogramos"),
        new Opcion("librasAOnzas",         "Libras a Onzas"),
        new Opcion("miligramosAGramos",    "Miligramos a Gramos")
    );
    private static final List&lt;Opcion&gt; OPCIONES_TEMPERATURA = Arrays.asList(
        new Opcion("celsiusAFahrenheit", "Celsius a Fahrenheit"),
        new Opcion("fahrenheitACelsius", "Fahrenheit a Celsius"),
        new Opcion("celsiusAKelvin",     "Celsius a Kelvin"),
        new Opcion("kelvinACelsius",     "Kelvin a Celsius"),
        new Opcion("fahrenheitAKelvin",  "Fahrenheit a Kelvin")
    );

    private String categoriaActual;
    private BiConsumer&lt;String, Double&gt; accionConvertir;
    private Runnable accionVolver;

    public PanelConversion() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    private void configurarVista() {
        setBackground(Paleta.GRIS_FONDO);

        lblEncabezado.setForeground(Paleta.AZUL);
        lblEncabezado.setFont(Paleta.TITULO);

        btnConvertir.setBackground(Paleta.AZUL);
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setOpaque(true);
        btnConvertir.setBorderPainted(false);
        btnConvertir.setContentAreaFilled(true);

        lblResultado.setOpaque(true);
        lblResultado.setBackground(Color.WHITE);
    }

    private void conectarEventos() {
        btnConvertir.addActionListener(e -&gt; dispararConvertir());
        campoValor.addActionListener(e -&gt; dispararConvertir());
        btnVolver.addActionListener(e -&gt; {
            if (accionVolver != null) accionVolver.run();
        });
    }

    private void dispararConvertir() {
        Opcion seleccion = (Opcion) comboOperacion.getSelectedItem();
        if (seleccion == null) return;
        String texto = campoValor.getText().trim().replace(',', '.');
        try {
            double valor = Double.parseDouble(texto);
            if (accionConvertir != null) accionConvertir.accept(seleccion.clave, valor);
        } catch (NumberFormatException ex) {
            mostrarResultado(Resultado.error("Ingresa un numero valido (ej: 12.5)"));
        }
    }

    // ========= API publica consumida por ControladorEscritorio =========

    @SuppressWarnings("unchecked")
    public void setCategoria(String categoria) {
        this.categoriaActual = categoria;
        List&lt;Opcion&gt; opciones;
        String titulo;
        switch (categoria) {
            case "longitud":
                opciones = OPCIONES_LONGITUD;
                titulo = "Conversiones de Longitud";
                break;
            case "masa":
                opciones = OPCIONES_MASA;
                titulo = "Conversiones de Masa";
                break;
            case "temperatura":
                opciones = OPCIONES_TEMPERATURA;
                titulo = "Conversiones de Temperatura";
                break;
            default:
                opciones = List.of();
                titulo = "Conversiones";
        }
        lblEncabezado.setText(titulo);
        comboOperacion.setModel(new DefaultComboBoxModel(opciones.toArray()));
        campoValor.setText("");
        lblResultado.setText(" ");
        lblResultado.setBackground(Color.WHITE);
    }

    public String getCategoria() {
        return categoriaActual;
    }

    public void mostrarResultado(Resultado resultado) {
        if (resultado == null) return;
        if (resultado.isExito()) {
            lblResultado.setText(resultado.getValor());
            lblResultado.setBackground(Paleta.VERDE_EXITO_BG);
            lblResultado.setForeground(Paleta.VERDE_EXITO_FG);
        } else {
            lblResultado.setText(resultado.getMensaje());
            lblResultado.setBackground(Paleta.ROJO_ERROR_BG);
            lblResultado.setForeground(Paleta.ROJO_ERROR_FG);
        }
    }

    public void setBotonHabilitado(boolean habilitado) {
        btnConvertir.setEnabled(habilitado);
        btnConvertir.setText(habilitado ? "Convertir" : "Convirtiendo...");
    }

    public void setOnConvertir(BiConsumer&lt;String, Double&gt; accion) {
        this.accionConvertir = accion;
    }

    public void setOnVolver(Runnable accion) {
        this.accionVolver = accion;
    }

    // &lt;editor-fold defaultstate="collapsed" desc="Generated Code"&gt;//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblEncabezado = new javax.swing.JLabel();
        lblConversion = new javax.swing.JLabel();
        comboOperacion = new javax.swing.JComboBox&lt;&gt;();
        lblValor = new javax.swing.JLabel();
        campoValor = new javax.swing.JTextField();
        btnConvertir = new javax.swing.JButton();
        lblResultado = new javax.swing.JLabel();
        btnVolver = new javax.swing.JButton();

        lblEncabezado.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblEncabezado.setText("Conversiones");

        lblConversion.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblConversion.setText("Conversion:");

        lblValor.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblValor.setText("Valor:");

        btnConvertir.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnConvertir.setText("Convertir");
        btnConvertir.setFocusPainted(false);

        lblResultado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResultado.setText(" ");
        lblResultado.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));

        btnVolver.setText("Volver al Menu");
        btnVolver.setFocusPainted(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEncabezado)
                    .addComponent(lblConversion)
                    .addComponent(comboOperacion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblValor)
                    .addComponent(campoValor)
                    .addComponent(btnConvertir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVolver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblEncabezado)
                .addGap(16, 16, 16)
                .addComponent(lblConversion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblValor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoValor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(btnConvertir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// &lt;/editor-fold&gt;//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConvertir;
    private javax.swing.JButton btnVolver;
    private javax.swing.JTextField campoValor;
    private javax.swing.JComboBox&lt;Object&gt; comboOperacion;
    private javax.swing.JLabel lblConversion;
    private javax.swing.JLabel lblEncabezado;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblValor;
    // End of variables declaration//GEN-END:variables
}
</pre></td></tr></table>

---

## 7.5    Paquete de pruebas

Este paquete agrupa las pruebas unitarias y de integración del cliente Escritorio, implementadas con JUnit 4. Las clases `pruebaResultado` y `pruebaFormatoConversion` validan el modelo sin red; `pruebaConexionServidor` realiza pruebas de integración que se *saltan* automáticamente (`Assume.assumeTrue`) si el servidor SOAP no está disponible en `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl`.

-    pruebaResultado
-    pruebaFormatoConversion
-    pruebaConexionServidor

### 7.5.1    Código de pruebaResultado

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

### 7.5.2    Código de pruebaFormatoConversion

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

### 7.5.3    Código de pruebaConexionServidor

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
