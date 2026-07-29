# EUREKABANK GR06 — Cliente Móvil RESTful (Android)

App Android con la misma funcionalidad/MVC que el móvil SOAP; **arquitectura
REST**: HTTP + JSON con `HttpURLConnection` + `org.json` (sin ksoap2).

- Proyecto: `eurekabank_restful_java_climov_gr06`.
- Login muestra "Banca RESTFULL · Cliente Móvil (Java)".
- Conexión en `config/ServidorConfig.java`: tres bases independientes, todas con
  host por defecto de emulador `http://10.0.2.2:8080`.
- Endpoints: autenticación (`/ms-rest-autenticacion/api`), consulta y movimientos
  (`/ms-rest-consulta/api`), y transacciones (`/ms-rest-transacciones/api`) para
  depósito, retiro y transferencia.
- Para una LAN, cambia de forma independiente el `HOST_*` necesario a
  `http://IP_DE_TU_PC:8080`.
- Capas: `config`, `rest` (helper `Http` + `LoginService/CuentaService/
  MovimientoService` REST + `Async`), `modelo`, `controlador`, `view`.
- `app/build.gradle.kts` ya NO depende de ksoap2.

Construir en Android Studio (Run ▶) con el servidor REST accesible.
