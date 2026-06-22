# Cliente Movil TicketPremium GR06 (Android Java + SOAP)

Cliente Android que consume el `WSFederacion` del servidor SOAP del grupo
mediante **ksoap2-android**. Replica las pantallas del cliente escritorio y
del web (login, comprar boletos, mis facturas, reporte de ventas, CRUD
de partidos y localidades) y genera el mismo comprobante PDF.

## Stack

| Tema | Valor |
|---|---|
| Lenguaje | Java 17 |
| Android Gradle Plugin | 8.2.2 |
| Gradle | 8.5 |
| compileSdk / targetSdk | 34 |
| minSdk | 24 (Android 7.0) |
| SOAP | `com.googlecode.ksoap2-android:ksoap2-android:3.6.4` |
| PDF | `android.graphics.pdf.PdfDocument` nativo |
| UI | Material 3, ViewPager2, RecyclerView, SwipeRefresh |

## Estructura

```
app/src/main/java/ec/edu/monster/
├── config/ServidorConfig.java          (URL base persistida en SharedPreferences)
├── modelo/                             (POJOs Serializable: Usuario, Partido, ...)
├── ws/
│   ├── SoapHelper.java                 (SoapObject -> POJO)
│   └── WsFederacionClient.java         (todas las operaciones del WSFederacion)
├── controlador/TicketController.java   (Executor + Handler, expone Callback)
├── servicio/GeneradorComprobantePDF.java (PDF comprobante + reporte ventas)
└── vista/
    ├── LoginActivity, MainActivity
    ├── ComprarFragment, FacturasFragment, ReporteFragment, AdminFragment
    ├── AdminLocalidadesActivity
    ├── LocalidadAdapter (item con seleccion unica)
    └── Ui, Moneda
```

## Como abrir y correr

1. **Importar en Android Studio**
   - File > Open > selecciona la carpeta `cliente_movil_soap_java_ticketpremium_gr06`.
   - Android Studio detecta Gradle 8.5 y bajara el wrapper la primera vez (necesitas internet).
   - Si no tienes `gradle-wrapper.jar` aun, Android Studio te ofrecera generarlo, o desde terminal:
     ```bash
     gradle wrapper
     ```

2. **Levantar el servidor SOAP**
   - Inicia GlassFish/Payara con el WAR del proyecto `08. SERVIDOR/servidor_soap_java_federacion_gr06`.
   - Confirma que el WSDL responde: <http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl>

3. **Configurar el URL del servidor en la app**
   - El default es `http://10.0.2.2:8080/servidor_soap_java_federacion_gr06`
     (10.0.2.2 es el localhost de la maquina vista desde el emulador AVD).
   - Si usas dispositivo fisico en la misma red WiFi, edita el campo
     "Servidor base" en la pantalla de login y pon la IP LAN de tu Mac, p. ej.
     `http://192.168.1.10:8080/servidor_soap_java_federacion_gr06`.

4. **Compilar/ejecutar**
   - Selecciona un AVD (API 24+) o tu telefono.
   - Run 'app'.

## Credenciales de prueba

| Usuario | Clave | Rol |
|---|---|---|
| monster | monster9 | ADMIN |
| josue, mikaela, elkin | admin2002 | CLIENTE |

## Funcionalidades implementadas

| Pestana | Operaciones SOAP |
|---|---|
| Comprar | `listarPartidosDisponibles`, `listarLocalidadesPorPartido`, `registrarVenta` |
| Mis facturas | `misFacturas`, descarga PDF (solo compras de la sesion actual) |
| Reporte (admin) | `resumenVentasPartido`, descarga PDF |
| Admin (admin) | `listarPartidosDisponibles`, `registrarPartido`, `actualizarPartido`, `eliminarPartido` |
| Localidades por partido (admin) | `listarTodasLocalidadesPorPartido`, `registrarLocalidad`, `actualizarLocalidad`, `eliminarLocalidad` |

## Notas

- El cliente acepta `usesCleartextTraffic="true"` para que el emulador pueda
  hablar contra `http://10.0.2.2`. En produccion deberia usarse HTTPS.
- El PDF se guarda en `filesDir/comprobantes/` y se comparte por FileProvider
  (`<applicationId>.fileprovider`); se abre con el visor PDF que tenga el
  dispositivo o se puede compartir por WhatsApp/Drive/etc.
- El comprobante usa el mismo formato (cabecera azul + logo Monster + QR
  falso + datos + pie) que el cliente web y el escritorio.
