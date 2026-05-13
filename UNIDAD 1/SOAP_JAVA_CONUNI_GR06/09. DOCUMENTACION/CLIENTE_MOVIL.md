# 9. CREACIÓN CLIENTE MÓVIL

## 9.1    Paquete monster

Este paquete raíz agrupa la configuración global del cliente Android. Contiene una única clase utilitaria que centraliza la URL del servidor SOAP y el *namespace* del WSDL, de modo que cambiar de red (laboratorio, hotspot, casa) implique modificar un solo punto del código antes de recompilar el APK.

-    Configuracion

### 9.1.1    Código de Configuracion

Clase utilitaria *final* con constructor privado que expone dos constantes públicas: `URL_SERVIDOR` (endpoint completo del WebService CONUNI) y `ESPACIO_NOMBRES` (*targetNamespace* generado por JAX-WS a partir del paquete del `@WebService`). Todos los servicios proxy del paquete modelo leen esta configuración para construir el sobre SOAP, evitando *hardcodear* la dirección IP en varios sitios.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster;

/**
 * UN SOLO LUGAR para configurar la conexion al servidor SOAP CONUNI.
 *
 * Si cambias de red (laboratorio -&gt; hotspot del celular -&gt; casa), modifica
 * la IP en URL_SERVIDOR y vuelve a compilar/instalar el APK.
 *
 * Notas utiles:
 *  - Emulador de Android Studio en la MISMA maquina del servidor -&gt; usar 10.0.2.2 en vez de localhost
 *  - Telefono fisico en la misma red Wi-Fi/hotspot que el servidor -&gt; usar la IP LAN del servidor
 *  - HTTPS: cambiar el esquema http:// -&gt; https://
 */
public final class Configuracion {

    private Configuracion() {}

    /** URL completa del endpoint del WebService CONUNI. */
    public static final String URL_SERVIDOR =
            "http://192.168.106.129:8080/servidor_soap_java_conuni_gr06/CONUNI";

    /** Namespace del servicio (targetNamespace generado por JAX-WS a partir del paquete del @WebService). */
    public static final String ESPACIO_NOMBRES =
            "http://controlador.monster.edu.ec/";
}
</pre></td></tr></table>

---

## 9.2    Paquete controlador

Este paquete contiene la clase helper que coordina el consumo SOAP desde Android. Su responsabilidad principal es desacoplar las *Activities* (UI thread) de las llamadas de red bloqueantes, porque Android prohíbe operaciones de red en el hilo principal (lanzando `NetworkOnMainThreadException`). Toda interacción con el modelo se canaliza por este controlador.

-    ControladorMovil

### 9.2.1    Código de ControladorMovil

Helper estático que ejecuta una tarea SOAP (cualquier `Callable<T>`) en un `ExecutorService` de hilos en *cached pool* y entrega el resultado al UI thread mediante un `Handler(Looper.getMainLooper())`. Define una interfaz `Callback<T>` con dos ramas (`onExito(T)` / `onError(Exception)`) que las *Activities* implementan para reaccionar al resultado. De esta forma, ninguna actividad invoca al modelo directamente: siempre lo hace pasando por `ControladorMovil.ejecutar(...)`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.controlador;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper que ejecuta una llamada SOAP (bloqueante) en un hilo secundario
 * y entrega el resultado en el hilo principal (UI thread).
 *
 * Android prohibe operaciones de red en el UI thread (NetworkOnMainThreadException),
 * por eso TODAS las Activities consumen el modelo a traves de este controlador.
 */
public final class ControladorMovil {

    private static final ExecutorService EJECUTOR = Executors.newCachedThreadPool();
    private static final Handler HILO_PRINCIPAL = new Handler(Looper.getMainLooper());

    private ControladorMovil() {}

    /** Callback con dos ramas: exito (resultado T) o fallo (excepcion). */
    public interface Callback&lt;T&gt; {
        void onExito(T resultado);
        void onError(Exception ex);
    }

    /** Ejecuta {@code tarea} en background y entrega el resultado al UI thread. */
    public static &lt;T&gt; void ejecutar(Callable&lt;T&gt; tarea, Callback&lt;T&gt; callback) {
        EJECUTOR.submit(() -&gt; {
            try {
                final T resultado = tarea.call();
                HILO_PRINCIPAL.post(() -&gt; callback.onExito(resultado));
            } catch (Exception ex) {
                HILO_PRINCIPAL.post(() -&gt; callback.onError(ex));
            }
        });
    }
}
</pre></td></tr></table>

---

## 9.3    Paquete modelo

Este paquete agrupa las clases del modelo del cliente Móvil: encapsulan la lógica de envío y recepción de los datos hacia el servidor SOAP, así como el formateo de los resultados que se muestran al usuario. Está formado por un cliente SOAP genérico (`ClienteSoap`), un DTO para transportar resultados (`Resultado`), una utilidad de formateo (`FormatoConversion`) y cuatro servicios proxy que envuelven cada grupo de operaciones del WSDL: `ServicioAutenticacion`, `ServicioLongitud`, `ServicioMasa` y `ServicioTemperatura`.

-    ClienteSoap
-    Resultado
-    FormatoConversion
-    ServicioAutenticacion
-    ServicioLongitud
-    ServicioMasa
-    ServicioTemperatura

### 9.3.1    Código de ClienteSoap

Cliente SOAP genérico que abstrae las llamadas HTTP al servicio CONUNI. Recibe el nombre de la operación y un `Map` de parámetros, construye dinámicamente el sobre SOAP y lo envía por POST a la `URL_SERVIDOR` definida en `Configuracion`. Configura *timeouts* de 10 s para conexión y 15 s para lectura (necesarios en redes móviles), y extrae el contenido de la etiqueta `<return>` de la respuesta. Los demás servicios proxy del paquete delegan en esta clase para evitar duplicar la lógica de red.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

import ec.edu.monster.Configuracion;

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
 *
 * La URL del servidor se lee desde {@link Configuracion#URL_SERVIDOR}.
 */
public class ClienteSoap {

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
             +   "xmlns:con=\"" + Configuracion.ESPACIO_NOMBRES + "\"&gt;"
             +   "&lt;soapenv:Header/&gt;"
             +   "&lt;soapenv:Body&gt;"
             +     "&lt;con:" + nombreOperacion + "&gt;" + cuerpo + "&lt;/con:" + nombreOperacion + "&gt;"
             +   "&lt;/soapenv:Body&gt;"
             + "&lt;/soapenv:Envelope&gt;";
    }

    private String enviarPeticion(String sobreSoap) throws Exception {
        URL url = new URL(Configuracion.URL_SERVIDOR);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conexion.setRequestProperty("SOAPAction", "");
        conexion.setConnectTimeout(10000);
        conexion.setReadTimeout(15000);
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

### 9.3.2    Código de Resultado

DTO simple para transportar entre el controlador y la vista el desenlace de una operación. Encapsula tres atributos inmutables: un *flag* `exito`, un `mensaje` descriptivo y el `valor` formateado. Expone dos factorías estáticas, `ok(valor)` y `error(mensaje)`, que las *Activities* usan para mostrar el resultado en el `TextView lblResultado` (éxito) o reportar el problema mediante `Snackbar` (error) sin instanciar manualmente el constructor.

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

### 9.3.3    Código de FormatoConversion

Utilidad estática que centraliza el formateo de los resultados de conversión que devuelve el servicio SOAP. Expone tres métodos: `fmt(double)` redondea a cuatro decimales y elimina ceros sobrantes, `unidades(operacion)` devuelve un arreglo `{origen, destino}` para cada una de las 15 conversiones del WSDL, y `formatear(...)` ensambla el string final del tipo `"7 m = 22.9659 ft"` que se muestra en el `TextView lblResultado` de cada *Activity* de conversión.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.modelo;

/**
 * Utilidad para formatear los resultados de conversion.
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
            case "metrosAPies":          return new String[]{"m",  "ft"};
            case "kilometrosAMillas":    return new String[]{"km", "mi"};
            case "centimetrosAPulgadas": return new String[]{"cm", "in"};
            case "yardasAMetros":        return new String[]{"yd", "m" };
            case "milimetrosAPulgadas":  return new String[]{"mm", "in"};
            case "kilogramosALibras":    return new String[]{"kg", "lb"};
            case "gramosAOnzas":         return new String[]{"g",  "oz"};
            case "toneladasAKilogramos": return new String[]{"t",  "kg"};
            case "librasAOnzas":         return new String[]{"lb", "oz"};
            case "miligramosAGramos":    return new String[]{"mg", "g" };
            case "celsiusAFahrenheit":   return new String[]{"°C", "°F"};
            case "fahrenheitACelsius":   return new String[]{"°F", "°C"};
            case "celsiusAKelvin":       return new String[]{"°C", "K"};
            case "kelvinACelsius":       return new String[]{"K",  "°C"};
            case "fahrenheitAKelvin":    return new String[]{"°F", "K"};
            default:                     return new String[]{"",   ""};
        }
    }
}
</pre></td></tr></table>

### 9.3.4    Código de ServicioAutenticacion

Servicio proxy de la operación `iniciarSesion` del WSDL. Recibe usuario y contraseña, los empaqueta en un `LinkedHashMap` (para preservar el orden de los parámetros en el sobre SOAP) y delega la invocación en `ClienteSoap`. Convierte la respuesta textual en `boolean` para que la `LoginActivity` pueda evaluar directamente si la autenticación fue exitosa antes de navegar al `MenuActivity`.

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

### 9.3.5    Código de ServicioLongitud

Servicio proxy de las cinco operaciones de longitud del WSDL: `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros` y `milimetrosAPulgadas`. Cada método público envuelve la llamada con `invocarUnario(...)`, que arma el `Map` de un solo parámetro, delega en `ClienteSoap` y convierte la respuesta textual a `double`. La `LongitudActivity` lo consume desde un hilo secundario lanzado por `ControladorMovil`.

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

### 9.3.6    Código de ServicioMasa

Servicio proxy de las cinco operaciones de masa del WSDL: `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas` y `miligramosAGramos`. Sigue el mismo patrón que `ServicioLongitud`: cada método publica una conversión y delega en el *helper* `invocarUnario(...)` que centraliza la invocación SOAP y el parseo del resultado. Lo consume la `MasaActivity`.

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

### 9.3.7    Código de ServicioTemperatura

Servicio proxy de las operaciones de temperatura del WSDL: `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius` y `fahrenheitAKelvin`. Comparte la estructura de los otros servicios proxy: un método por conversión y un *helper* privado `invocarUnario(...)` que arma el sobre SOAP a través de `ClienteSoap` y devuelve el `double` ya parseado. Lo consume la `TemperaturaActivity`.

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

## 9.4    Paquete vista

Este paquete agrupa las *Activities* Android que conforman la interfaz gráfica del cliente Móvil. Cada *Activity* se enlaza con un *layout* XML de Material Design ubicado en `app/src/main/res/layout/` y delega cualquier llamada SOAP al `ControladorMovil`. La navegación entre pantallas se hace con `Intent`. Está formado por una pantalla de inicio de sesión (`LoginActivity`), un menú principal con tarjetas (`MenuActivity`) y tres pantallas de conversión homogéneas (`LongitudActivity`, `MasaActivity`, `TemperaturaActivity`).

-    LoginActivity
-    MenuActivity
-    LongitudActivity
-    MasaActivity
-    TemperaturaActivity

### 9.4.1    Código de LoginActivity

Pantalla de inicio de sesión. Habilita/deshabilita dinámicamente el botón Ingresar según el estado de los `TextInputEditText` mediante un `TextWatcher`. Pulsar "Done" en el teclado del IME dispara el login. Cuando se invoca `intentarIngresar()`, ejecuta `servicioAutenticacion.iniciarSesion(...)` a través de `ControladorMovil.ejecutar(...)`: si la respuesta es `true` lanza el `MenuActivity` con un `Intent` que lleva el usuario como extra; en caso contrario, muestra el error con un `Snackbar` tintado del color de error del tema Material.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorMovil;
import ec.edu.monster.modelo.ServicioAutenticacion;

/**
 * Pantalla de inicio de sesion.
 * - Habilita/deshabilita el boton segun los campos.
 * - Muestra errores con Snackbar (no Toast) para mejor UX.
 * - Pulsar "Done" en el teclado dispara el login.
 */
public class LoginActivity extends AppCompatActivity {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    private TextInputEditText txtUsuario;
    private TextInputEditText txtContrasena;
    private MaterialButton btnIngresar;
    private ProgressBar progreso;
    private View raiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        raiz = findViewById(R.id.raiz);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        progreso = findViewById(R.id.progreso);

        btnIngresar.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                btnIngresar.setEnabled(camposCompletos());
            }
        };
        txtUsuario.addTextChangedListener(watcher);
        txtContrasena.addTextChangedListener(watcher);

        txtContrasena.setOnEditorActionListener((v, actionId, event) -&gt; {
            if (actionId == EditorInfo.IME_ACTION_DONE &amp;&amp; camposCompletos()) {
                intentarIngresar();
                return true;
            }
            return false;
        });

        btnIngresar.setOnClickListener(v -&gt; intentarIngresar());
    }

    private boolean camposCompletos() {
        return !TextUtils.isEmpty(textoDe(txtUsuario)) &amp;&amp; !TextUtils.isEmpty(textoDe(txtContrasena));
    }

    private String textoDe(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void intentarIngresar() {
        final String usuario = textoDe(txtUsuario);
        final String contrasena = txtContrasena.getText() == null ? "" : txtContrasena.getText().toString();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(contrasena)) {
            snack(getString(R.string.msg_campos_vacios), true);
            return;
        }

        mostrarProgreso(true);
        ControladorMovil.ejecutar(
                () -&gt; servicioAutenticacion.iniciarSesion(usuario, contrasena),
                new ControladorMovil.Callback&lt;Boolean&gt;() {
                    @Override
                    public void onExito(Boolean ok) {
                        mostrarProgreso(false);
                        if (Boolean.TRUE.equals(ok)) {
                            Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                            i.putExtra(MenuActivity.EXTRA_USUARIO, usuario);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            snack(getString(R.string.msg_credenciales_invalidas), true);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        mostrarProgreso(false);
                        snack(getString(R.string.msg_error_conexion, ex.getMessage()), true);
                    }
                });
    }

    private void mostrarProgreso(boolean visible) {
        progreso.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!visible &amp;&amp; camposCompletos());
    }

    private void snack(String mensaje, boolean esError) {
        Snackbar sb = Snackbar.make(raiz, mensaje, Snackbar.LENGTH_LONG);
        if (esError) {
            sb.setBackgroundTint(getColor(R.color.md_theme_error));
            sb.setTextColor(getColor(R.color.md_theme_onError));
        }
        sb.show();
    }
}
</pre></td></tr></table>

### 9.4.2    Código de MenuActivity

Pantalla del menú principal. Muestra una `MaterialToolbar` con la opción "Cerrar sesión" y tres `MaterialCardView` grandes (Longitud, Masa, Temperatura). Recibe el nombre del usuario por `Intent` extra (`EXTRA_USUARIO`) y lo pinta en el saludo de la cabecera. Cada tarjeta lanza la *Activity* correspondiente con `startActivity(new Intent(this, destino))`; el ítem del menú "Cerrar sesión" regresa a `LoginActivity` limpiando la pila con `FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import ec.edu.monster.R;

/**
 * Menu principal con tarjetas grandes por categoria.
 */
public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_USUARIO = "extra_usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -&gt; {
            if (item.getItemId() == R.id.accionCerrarSesion) {
                cerrarSesion();
                return true;
            }
            return false;
        });

        String usuario = getIntent().getStringExtra(EXTRA_USUARIO);
        if (usuario == null) usuario = "";

        TextView lblUsuario = findViewById(R.id.lblUsuario);
        lblUsuario.setText(getString(R.string.etiqueta_usuario_activo, usuario));

        MaterialCardView cardLongitud = findViewById(R.id.cardLongitud);
        MaterialCardView cardMasa = findViewById(R.id.cardMasa);
        MaterialCardView cardTemperatura = findViewById(R.id.cardTemperatura);

        cardLongitud.setOnClickListener(v -&gt; abrir(LongitudActivity.class));
        cardMasa.setOnClickListener(v -&gt; abrir(MasaActivity.class));
        cardTemperatura.setOnClickListener(v -&gt; abrir(TemperaturaActivity.class));
    }

    private void abrir(Class&lt;?&gt; destino) {
        startActivity(new Intent(this, destino));
    }

    private void cerrarSesion() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
</pre></td></tr></table>

### 9.4.3    Código de LongitudActivity

Pantalla de conversión de longitud. Carga un `Spinner` con cinco etiquetas tomadas de `strings.xml` (`op_metros_pies`, `op_km_millas`, …) y mapea la posición seleccionada a la clave de operación SOAP mediante el arreglo estático `OPERACIONES`. Al pulsar Convertir, valida el valor numérico (aceptando coma o punto decimal), lanza la llamada SOAP con `ControladorMovil.ejecutar(...)` y muestra el resultado formateado en `lblResultado`. Errores de red se reportan con `Snackbar`.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorMovil;
import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioLongitud;

public class LongitudActivity extends AppCompatActivity {

    private static final String[] OPERACIONES = {
            "metrosAPies",
            "kilometrosAMillas",
            "centimetrosAPulgadas",
            "yardasAMetros",
            "milimetrosAPulgadas"
    };

    private final ServicioLongitud servicio = new ServicioLongitud();

    private Spinner spOperacion;
    private TextInputEditText txtValor;
    private MaterialButton btnConvertir;
    private MaterialButton btnLimpiar;
    private TextView lblResultado;
    private ProgressBar progreso;
    private View raiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_longitud);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -&gt; finish());

        raiz = findViewById(R.id.raiz);
        spOperacion = findViewById(R.id.spOperacion);
        txtValor = findViewById(R.id.txtValor);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        lblResultado = findViewById(R.id.lblResultado);
        progreso = findViewById(R.id.progreso);

        String[] etiquetas = {
                getString(R.string.op_metros_pies),
                getString(R.string.op_km_millas),
                getString(R.string.op_cm_pulgadas),
                getString(R.string.op_yardas_metros),
                getString(R.string.op_mm_pulgadas)
        };
        spOperacion.setAdapter(new ArrayAdapter&lt;&gt;(
                this, android.R.layout.simple_spinner_dropdown_item, etiquetas));

        btnConvertir.setOnClickListener(v -&gt; convertir());
        btnLimpiar.setOnClickListener(v -&gt; limpiar());
    }

    private void convertir() {
        final String texto = txtValor.getText() == null ? "" : txtValor.getText().toString().trim();
        final double valor;
        try {
            valor = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            Snackbar.make(raiz, R.string.msg_valor_invalido, Snackbar.LENGTH_SHORT).show();
            return;
        }

        final int op = spOperacion.getSelectedItemPosition();
        final String operacion = OPERACIONES[op];

        mostrarProgreso(true);
        ControladorMovil.ejecutar(
                () -&gt; {
                    double r;
                    switch (op) {
                        case 0: r = servicio.metrosAPies(valor); break;
                        case 1: r = servicio.kilometrosAMillas(valor); break;
                        case 2: r = servicio.centimetrosAPulgadas(valor); break;
                        case 3: r = servicio.yardasAMetros(valor); break;
                        case 4: r = servicio.milimetrosAPulgadas(valor); break;
                        default: throw new IllegalStateException("Operacion no soportada");
                    }
                    String[] u = FormatoConversion.unidades(operacion);
                    return Resultado.ok(FormatoConversion.formatear(valor, u[0], r, u[1]));
                },
                new ControladorMovil.Callback&lt;Resultado&gt;() {
                    @Override
                    public void onExito(Resultado r) {
                        mostrarProgreso(false);
                        lblResultado.setText(r.isExito() ? r.getValor() : r.getMensaje());
                    }

                    @Override
                    public void onError(Exception ex) {
                        mostrarProgreso(false);
                        Snackbar.make(raiz,
                                getString(R.string.msg_error_servicio, ex.getMessage()),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void limpiar() {
        txtValor.setText("");
        lblResultado.setText(R.string.placeholder_resultado);
        spOperacion.setSelection(0);
    }

    private void mostrarProgreso(boolean visible) {
        progreso.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnConvertir.setEnabled(!visible);
        btnLimpiar.setEnabled(!visible);
    }
}
</pre></td></tr></table>

### 9.4.4    Código de MasaActivity

Pantalla de conversión de masa. Comparte la misma estructura que `LongitudActivity`: `Spinner` con cinco operaciones (`kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas`, `miligramosAGramos`), validación numérica del `txtValor`, ejecución asíncrona vía `ControladorMovil` y reporte de errores con `Snackbar`. Mantiene la coherencia visual y de comportamiento entre las tres pantallas de conversión.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorMovil;
import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioMasa;

public class MasaActivity extends AppCompatActivity {

    private static final String[] OPERACIONES = {
            "kilogramosALibras",
            "gramosAOnzas",
            "toneladasAKilogramos",
            "librasAOnzas",
            "miligramosAGramos"
    };

    private final ServicioMasa servicio = new ServicioMasa();

    private Spinner spOperacion;
    private TextInputEditText txtValor;
    private MaterialButton btnConvertir;
    private MaterialButton btnLimpiar;
    private TextView lblResultado;
    private ProgressBar progreso;
    private View raiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -&gt; finish());

        raiz = findViewById(R.id.raiz);
        spOperacion = findViewById(R.id.spOperacion);
        txtValor = findViewById(R.id.txtValor);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        lblResultado = findViewById(R.id.lblResultado);
        progreso = findViewById(R.id.progreso);

        String[] etiquetas = {
                getString(R.string.op_kg_libras),
                getString(R.string.op_g_onzas),
                getString(R.string.op_t_kg),
                getString(R.string.op_lb_oz),
                getString(R.string.op_mg_g)
        };
        spOperacion.setAdapter(new ArrayAdapter&lt;&gt;(
                this, android.R.layout.simple_spinner_dropdown_item, etiquetas));

        btnConvertir.setOnClickListener(v -&gt; convertir());
        btnLimpiar.setOnClickListener(v -&gt; limpiar());
    }

    private void convertir() {
        final String texto = txtValor.getText() == null ? "" : txtValor.getText().toString().trim();
        final double valor;
        try {
            valor = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            Snackbar.make(raiz, R.string.msg_valor_invalido, Snackbar.LENGTH_SHORT).show();
            return;
        }

        final int op = spOperacion.getSelectedItemPosition();
        final String operacion = OPERACIONES[op];

        mostrarProgreso(true);
        ControladorMovil.ejecutar(
                () -&gt; {
                    double r;
                    switch (op) {
                        case 0: r = servicio.kilogramosALibras(valor); break;
                        case 1: r = servicio.gramosAOnzas(valor); break;
                        case 2: r = servicio.toneladasAKilogramos(valor); break;
                        case 3: r = servicio.librasAOnzas(valor); break;
                        case 4: r = servicio.miligramosAGramos(valor); break;
                        default: throw new IllegalStateException("Operacion no soportada");
                    }
                    String[] u = FormatoConversion.unidades(operacion);
                    return Resultado.ok(FormatoConversion.formatear(valor, u[0], r, u[1]));
                },
                new ControladorMovil.Callback&lt;Resultado&gt;() {
                    @Override
                    public void onExito(Resultado r) {
                        mostrarProgreso(false);
                        lblResultado.setText(r.isExito() ? r.getValor() : r.getMensaje());
                    }

                    @Override
                    public void onError(Exception ex) {
                        mostrarProgreso(false);
                        Snackbar.make(raiz,
                                getString(R.string.msg_error_servicio, ex.getMessage()),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void limpiar() {
        txtValor.setText("");
        lblResultado.setText(R.string.placeholder_resultado);
        spOperacion.setSelection(0);
    }

    private void mostrarProgreso(boolean visible) {
        progreso.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnConvertir.setEnabled(!visible);
        btnLimpiar.setEnabled(!visible);
    }
}
</pre></td></tr></table>

### 9.4.5    Código de TemperaturaActivity

Pantalla de conversión de temperatura. Réplica funcional de `LongitudActivity` y `MasaActivity` con las cinco operaciones de temperatura del WSDL (`celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius`, `fahrenheitAKelvin`). Las tres *Activities* comparten el mismo patrón —`Spinner` + `TextInputEditText` + botones Convertir/Limpiar + `lblResultado`— lo que facilita el mantenimiento y deja una experiencia consistente en toda la app.

<table><tr><td><pre style="font-family: 'Courier New', monospace; font-size: 10pt;">
package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorMovil;
import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioTemperatura;

public class TemperaturaActivity extends AppCompatActivity {

    private static final String[] OPERACIONES = {
            "celsiusAFahrenheit",
            "fahrenheitACelsius",
            "celsiusAKelvin",
            "kelvinACelsius",
            "fahrenheitAKelvin"
    };

    private final ServicioTemperatura servicio = new ServicioTemperatura();

    private Spinner spOperacion;
    private TextInputEditText txtValor;
    private MaterialButton btnConvertir;
    private MaterialButton btnLimpiar;
    private TextView lblResultado;
    private ProgressBar progreso;
    private View raiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatura);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -&gt; finish());

        raiz = findViewById(R.id.raiz);
        spOperacion = findViewById(R.id.spOperacion);
        txtValor = findViewById(R.id.txtValor);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        lblResultado = findViewById(R.id.lblResultado);
        progreso = findViewById(R.id.progreso);

        String[] etiquetas = {
                getString(R.string.op_c_f),
                getString(R.string.op_f_c),
                getString(R.string.op_c_k),
                getString(R.string.op_k_c),
                getString(R.string.op_f_k)
        };
        spOperacion.setAdapter(new ArrayAdapter&lt;&gt;(
                this, android.R.layout.simple_spinner_dropdown_item, etiquetas));

        btnConvertir.setOnClickListener(v -&gt; convertir());
        btnLimpiar.setOnClickListener(v -&gt; limpiar());
    }

    private void convertir() {
        final String texto = txtValor.getText() == null ? "" : txtValor.getText().toString().trim();
        final double valor;
        try {
            valor = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            Snackbar.make(raiz, R.string.msg_valor_invalido, Snackbar.LENGTH_SHORT).show();
            return;
        }

        final int op = spOperacion.getSelectedItemPosition();
        final String operacion = OPERACIONES[op];

        mostrarProgreso(true);
        ControladorMovil.ejecutar(
                () -&gt; {
                    double r;
                    switch (op) {
                        case 0: r = servicio.celsiusAFahrenheit(valor); break;
                        case 1: r = servicio.fahrenheitACelsius(valor); break;
                        case 2: r = servicio.celsiusAKelvin(valor); break;
                        case 3: r = servicio.kelvinACelsius(valor); break;
                        case 4: r = servicio.fahrenheitAKelvin(valor); break;
                        default: throw new IllegalStateException("Operacion no soportada");
                    }
                    String[] u = FormatoConversion.unidades(operacion);
                    return Resultado.ok(FormatoConversion.formatear(valor, u[0], r, u[1]));
                },
                new ControladorMovil.Callback&lt;Resultado&gt;() {
                    @Override
                    public void onExito(Resultado r) {
                        mostrarProgreso(false);
                        lblResultado.setText(r.isExito() ? r.getValor() : r.getMensaje());
                    }

                    @Override
                    public void onError(Exception ex) {
                        mostrarProgreso(false);
                        Snackbar.make(raiz,
                                getString(R.string.msg_error_servicio, ex.getMessage()),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void limpiar() {
        txtValor.setText("");
        lblResultado.setText(R.string.placeholder_resultado);
        spOperacion.setSelection(0);
    }

    private void mostrarProgreso(boolean visible) {
        progreso.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnConvertir.setEnabled(!visible);
        btnLimpiar.setEnabled(!visible);
    }
}
</pre></td></tr></table>
