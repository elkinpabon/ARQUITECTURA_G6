# EUREKABANK GR06 — Cliente Móvil (Android)

App Android (Java) con la **misma funcionalidad que el cliente web**, patrón
**MVC** y conexión al servidor en **archivo aparte**. Android **no** ejecuta
JAX-WS, por eso el SOAP se hace con **ksoap2-android 3.6.4** (ya configurado en
`app/build.gradle.kts` y el repo en `settings.gradle.kts`).

> ⚠️ Este cliente **no se compiló/verificó aquí** (requiere Android SDK + Gradle).
> Ábrelo en **Android Studio** y ejecútalo en emulador o dispositivo.

## Conexión al servidor (archivo aparte)

`app/src/main/java/.../config/ServidorConfig.java` → constante **`BASE`**.
Edita solo esa línea:

| Dónde corre la app | `BASE` |
|---|---|
| Emulador Android | `http://10.0.2.2:8080/eurekabank_soap_java_gr06` (10.0.2.2 = localhost del PC) |
| Dispositivo físico | `http://IP_DEL_PC:8080/eurekabank_soap_java_gr06` (misma Wi‑Fi) |

El Manifest ya permite tráfico HTTP (`usesCleartextTraffic` + `network_security_config`).

## Estructura MVC

| Capa | Paquete | Contenido |
|---|---|---|
| Config | `config` | `ServidorConfig` (URL del servidor) |
| Modelo | `modelo` | `Resultado`, `CuentaResumen`, `ClienteResumen`, `Movimiento`, `Sesion` |
| SOAP | `soap` | `SoapBase` (ksoap2), `LoginService`, `CuentaService`, `MovimientoService`, `Async` |
| Controlador | `controlador` | `BancoController` (rol admin/cliente, guard cuenta propia, depósito solo admin, conversión) |
| Vista | `view` | `LoginActivity`, `CuentaActivity`, `MovimientosActivity` |

## Funcionalidad (igual que CLIWEB)

Login con rol (`monster`=ADMIN, resto=cliente con sus cuentas), ver cuentas y
saldo total, consultar saldo, **depósito (solo admin)**, retiro, **transferencia
con conversión de moneda** (Dólares preferente), movimientos con ingresos/egresos
y **ojito de conversión**, y para admin: registrar cliente, registrar cuenta,
eliminar cuenta.

## Interfaz

UI por código con tema oscuro tipo banca (navy `#0F172A`, tarjetas `#1E293B`,
acento `#38BDF8`), logo `logo_login`, tarjetas redondeadas y botones con
`button_background`. El **admin elige el cliente desde un Spinner** poblado con
`listarClientes()` (igual que el combo del cliente web); el cliente normal ve
directo sus cuentas. Movimientos con tarjetas verde/rojo y botón 👁 que
despliega el detalle de conversión.

## Notas técnicas

- Las llamadas SOAP se ejecutan fuera del hilo de UI (`soap.Async`).
- `LoginActivity` usa el layout `activity_login` existente; `CuentaActivity` y
  `MovimientosActivity` construyen la UI por código (no dependen de XML, así
  compilan siempre).
- El SERVIDOR debe estar desplegado y accesible desde el emulador/dispositivo.

Usuarios de prueba: `09. DOCUMENTACION/USUARIOS_PRUEBA.md`.
