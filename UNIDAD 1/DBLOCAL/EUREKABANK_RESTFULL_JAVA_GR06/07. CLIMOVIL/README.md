# EUREKABANK GR06 — Cliente Móvil RESTful (Android)

App Android con la misma funcionalidad/MVC que el móvil SOAP; **arquitectura
REST**: HTTP + JSON con `HttpURLConnection` + `org.json` (sin ksoap2).

> ⚠️ No compilado/verificado aquí (requiere Android Studio/SDK).

- Proyecto: `eurekabank_restful_java_climov_gr06`.
- Login muestra "Banca RESTFULL · Cliente Móvil (Java)".
- Conexión en archivo aparte: `config/ServidorConfig.java` → `BASE`
  (Emulador: `http://10.0.2.2:8080/eurekabank_restful_java_gr06/api`;
   dispositivo físico: IP del PC).
- Capas: `config`, `soap` (helper `Http` + `LoginService/CuentaService/
  MovimientoService` REST + `Async`), `modelo`, `controlador`, `view`.
- `app/build.gradle.kts` ya NO depende de ksoap2.

Construir en Android Studio (Run ▶) con el servidor REST accesible.
