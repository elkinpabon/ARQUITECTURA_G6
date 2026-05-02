# cliente_movil_restful_java_conuni_gr06

Aplicacion **Android nativa** (Kotlin + Jetpack Compose) que consume el servicio **REST CONUNI** desplegado en `servidor_restful_java_conuni_gr06`. Ofrece autenticacion, un menu por categorias y un conversor con 15 operaciones (longitud, masa, temperatura). Equivalente del cliente movil SOAP, pero usando HTTP + JSON sobre `HttpURLConnection` + `org.json` (sin libreria ksoap2).

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** sobre tres `Activity` Compose. La Vista (Composables) usa coroutines para no bloquear el hilo principal mientras el Controlador llama al servidor REST.

```
ec.edu.monster
├── conuni/                              # Vistas Compose (Activities)
│   ├── MainActivity.kt                   # PantallaLogin
│   ├── MenuActivity.kt                   # PantallaMenu (Longitud/Masa/Temperatura)
│   ├── ConversorActivity.kt              # PantallaConversor
│   └── ui/theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── controlador/
│   └── AppControlador.java              # Orquesta llamadas a Servicio* y devuelve Resultado
├── servicio/                             # capa de comunicacion con la API
│   ├── ServicioAutenticacion.java        # POST  /api/sesion
│   ├── ServicioLongitud.java             # GET   /api/longitud/{op}?valor=X
│   ├── ServicioMasa.java                 # GET   /api/masa/{op}?valor=X
│   └── ServicioTemperatura.java          # GET   /api/temperatura/{op}?valor=X
├── modelo/
│   └── Resultado.java                    # DTO local (exito + valor + mensaje)
└── util/
    └── ClienteRest.java                  # HTTP GET/POST + parser JSON
```

| Capa MVC | Componente | Responsabilidad |
|----------|------------|-----------------|
| Vista | `*Activity.kt` + Compose + `ui/theme/` | UI, manejo de estado local con `remember`, lectura de inputs, lanzamiento de coroutines. |
| Controlador | `AppControlador.java` | Recibe categoria + operacion + valor desde la Vista, llama al servicio correspondiente, devuelve `Resultado`. |
| Modelo | `ClienteRest` + `Servicio*` + `Resultado` | Cliente REST que invoca al servidor. `org.json.JSONObject` para parsear. |

> Las llamadas HTTP se ejecutan en `Dispatchers.IO` desde Compose (`withContext(Dispatchers.IO) { ... }`) para no bloquear el hilo principal.

---

## 2. Stack tecnologico

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Android Gradle Plugin** 8.5.1, **compileSdk 34**, **minSdk 21**
- `java.net.HttpURLConnection` (incluido en Android) para HTTP
- `org.json.JSONObject` (incluido en Android) para parsear/construir JSON
- **Coroutines** (`kotlinx.coroutines`) para llamadas asincronas
- **Sin** dependencia de ksoap2 (a diferencia del cliente movil SOAP)

---

## 3. Configuracion de la URL del servicio

`app/src/main/java/ec/edu/monster/util/ClienteRest.java`:

```java
public static final String URL_BASE =
        "http://10.0.2.2:8080/servidor_restful_java_conuni_gr06/api";
```

> `10.0.2.2` es la IP especial del **emulador de Android** que apunta al `localhost` de la maquina anfitriona donde corre Payara.
> Para un dispositivo fisico en la misma red, cambia por la IP del host (por ejemplo `192.168.1.10`).

`AndroidManifest.xml` ya tiene:
- `<uses-permission android:name="android.permission.INTERNET" />`
- `android:usesCleartextTraffic="true"` (HTTP sin TLS — solo para desarrollo).

---

## 4. Como ejecutar

1. **Servidor REST** corriendo en Payara (`http://localhost:8080/servidor_restful_java_conuni_gr06/`).
2. Abrir el proyecto en **Android Studio** (`File -> Open` -> seleccionar `cliente_movil_restful_java_conuni_gr06`).
3. Esperar a que Gradle sincronice y descargue dependencias.
4. **Run** sobre el emulador. La app abrira en pantalla de login.

---

## 5. Flujo de la aplicacion

1. **`MainActivity` -> `PantallaLogin`**: el usuario tipea credenciales. El composable lanza una coroutine -> `AppControlador.iniciarSesion()` -> `ServicioAutenticacion` -> `POST /api/sesion`. Si OK, navega a `MenuActivity`.
2. **`MenuActivity` -> `PantallaMenu`**: tres tarjetas (Longitud / Masa / Temperatura). Al toque, abre `ConversorActivity` con la categoria como extra del Intent.
3. **`ConversorActivity` -> `PantallaConversor`**: dropdown con las 5 operaciones de la categoria + campo numerico. Al "Convertir" lanza una coroutine -> `AppControlador.convertirX()` -> `Servicio*` -> `GET /api/{categoria}/{op}?valor=X`. El `Resultado` se muestra en una caja verde (exito) o roja (error).
4. **Cerrar sesion** desde el icono en la barra superior del menu vuelve a `MainActivity`.

---

## 6. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 7. Diferencias clave vs. el cliente movil SOAP

| Aspecto | SOAP | REST (este proyecto) |
|---------|------|----------------------|
| Libreria HTTP | `ksoap2-android` (sobre XML manual via `SoapObject` + `HttpTransportSE`) | `HttpURLConnection` (incluido en Android) |
| Constantes | `SoapConstants.java` (NAMESPACE + URL + nombres SOAP) | `ClienteRest.URL_BASE` (basta una constante) |
| Login | `iniciarSesion(usuario,contrasena)` -> `<return>true` | `POST /api/sesion` con JSON -> `{"valido":true}` |
| Conversion | `metrosAPies(metros=10)` | `GET /api/longitud/metros-a-pies?valor=10` |
| Repos Gradle | requiere repo Sonatype para ksoap2 | solo `google()` y `mavenCentral()` |
| Compose UI | Sin cambios estructurales | Sin cambios estructurales |
| `Dispatchers.IO` + coroutines | Para llamadas SOAP | Para llamadas REST |
| Parser de respuesta | `SoapPrimitive.toString()` | `JSONObject.getDouble("valor")` / `optBoolean("valido")` |

El cambio se concentra en `util/` y `servicio/`. Vista (`*Activity.kt`) y Controlador (`AppControlador.java`) sobreviven sin tocar logica de UI.
