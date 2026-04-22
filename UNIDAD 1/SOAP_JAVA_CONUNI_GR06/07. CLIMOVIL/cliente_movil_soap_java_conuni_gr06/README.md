# cliente_movil_soap_java_conuni_gr06

Aplicacion **Android nativa** que consume el servicio SOAP **CONUNI** para conversiones de unidades. Ofrece autenticacion, un menu por categorias y un conversor con 15 operaciones distribuidas en 3 modulos (longitud, masa, temperatura).

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** con una particularidad: la Vista esta en **Kotlin + Jetpack Compose**, mientras que el Controlador y el Modelo estan en **Java**. La comunicacion es unidireccional y via el DTO `Resultado`, que evita que las pantallas toquen excepciones SOAP.

```
Usuario (pantalla tactil)
    |
    v
[Vista: Compose Activities]  <--->  [Controlador: AppControlador]
   (Kotlin)                            (Java, devuelve Resultado)
                                            |
                                            |  (Dispatchers.IO)
                                            v
                                  [Modelo: Servicios ksoap2]
                                            |
                                            v
                                  Servidor CONUNI (Payara / JAX-WS)
```

### Responsabilidades por capa

| Capa | Paquete | Lenguaje | Responsabilidad |
|------|---------|----------|-----------------|
| Vista | `ec.edu.monster.conuni` | Kotlin + Compose | Pantallas, estado UI, manejo de corutinas (`Dispatchers.IO`), toggle ojo, validacion local de entradas. |
| Controlador | `ec.edu.monster.controlador` | Java | Llama a los servicios, captura excepciones y devuelve `Resultado` a la Vista. |
| Modelo | `ec.edu.monster.servicio` + `modelo` | Java | Consume SOAP con **ksoap2-android**. |
| Util | `ec.edu.monster.util` | Java | `SoapConstants` con URL, namespace y nombres de operacion. |

---

## 2. Stack tecnologico

- **Kotlin** 1.9 + **Jetpack Compose** (BOM 2024.04.01, Material3)
- **Java** 1.8 (nivel de bytecode, target JVM de Kotlin tambien 1.8)
- **Android Gradle Plugin** 8.5.1
- **Gradle** 8.7 (wrapper incluido)
- **compileSdk / targetSdk** 34, **minSdk** 21
- **ksoap2-android** 3.6.4 (libreria SOAP para Android)
- **material-icons-extended** (para `Icons.Filled.Visibility`, `Thermostat`, etc.)

---

## 3. Estructura del proyecto

```
cliente_movil_soap_java_conuni_gr06/
├── build.gradle.kts                     # Raiz (apply false)
├── settings.gradle.kts                  # rootProject.name + repositorios
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml               # Version catalog
│   └── wrapper/                         # gradle-wrapper.jar + props
├── gradlew / gradlew.bat
└── app/
    ├── build.gradle.kts                 # namespace + AGP + deps (ksoap2, compose)
    ├── proguard-rules.pro                # Mantiene ksoap2 en release
    └── src/main/
        ├── AndroidManifest.xml          # Permiso INTERNET, 3 activities
        ├── java/ec/edu/monster/
        │   ├── conuni/                  # VISTA (Kotlin + Compose)
        │   │   ├── MainActivity.kt
        │   │   ├── MenuActivity.kt
        │   │   ├── ConversorActivity.kt
        │   │   └── ui/theme/
        │   │       ├── Color.kt
        │   │       ├── Theme.kt         # ClienteMovilTheme
        │   │       └── Type.kt
        │   ├── controlador/             # CONTROLADOR (Java)
        │   │   └── AppControlador.java
        │   ├── servicio/                # MODELO (Java + ksoap2)
        │   │   ├── ServicioAutenticacion.java
        │   │   ├── ServicioLongitud.java
        │   │   ├── ServicioMasa.java
        │   │   └── ServicioTemperatura.java
        │   ├── modelo/
        │   │   └── Resultado.java
        │   └── util/
        │       └── SoapConstants.java
        └── res/
            ├── drawable/                # login.jpg + moster.webp (del web)
            ├── mipmap-*/                # Iconos del launcher
            ├── values/
            │   ├── colors.xml           # Paleta CONUNI
            │   ├── strings.xml
            │   └── themes.xml           # Theme.ClienteMovil
            └── xml/                     # backup_rules / data_extraction
```

---

## 4. Clases y responsabilidades

### Vista (Kotlin + Compose)

| Activity / Composable | Fichero | Que muestra |
|-----------------------|---------|-------------|
| `MainActivity` -> `PantallaLogin` | `MainActivity.kt` | TopAppBar azul + logo redondo + imagen ilustrativa (`login.jpg`) + campos usuario/contrasena. El campo contrasena tiene **IconButton** con `Icons.Filled.Visibility` / `VisibilityOff` que alterna `VisualTransformation.None` ↔ `PasswordVisualTransformation()`. Al pulsar "Ingresar" llama a `controlador.iniciarSesion` en `Dispatchers.IO` y navega a `MenuActivity`. |
| `MenuActivity` -> `PantallaMenu` + `TarjetaCategoria` | `MenuActivity.kt` | 3 `Card` con icono (Straighten, Scale, Thermostat) que al pulsar lanzan `ConversorActivity` con extra `categoria`. Boton cerrar sesion en el TopAppBar. |
| `ConversorActivity` -> `PantallaConversor` | `ConversorActivity.kt` | `ExposedDropdownMenuBox` con las 5 operaciones de la categoria, `OutlinedTextField` numerico, boton **Convertir** que llama al controlador y muestra caja verde/roja segun `Resultado.isExito`. |
| Theme | `ui/theme/Theme.kt` | `ClienteMovilTheme` aplica `lightColorScheme` con los colores de la marca. |
| Colores | `ui/theme/Color.kt` | `ConuniAzul`, `ConuniAzulClaro`, `ConuniAmarillo`, `ConuniGrisFondo`. |

Caracteristica clave de UX: **toggle ojo** para ver/ocultar la contrasena (equivalente al del cliente web).

### Controlador

| Clase | Metodos publicos | Responsabilidad |
|-------|------------------|-----------------|
| `AppControlador` | `iniciarSesion(usuario, contrasena)`, `convertirLongitud(op, valor)`, `convertirMasa(op, valor)`, `convertirTemperatura(op, valor)` | Todos devuelven `Resultado`. Captura `Exception` y la convierte en `Resultado.error("...")`, asi la Vista no tiene bloques `try/catch` de red. |

### Modelo (Java + ksoap2)

Todos los servicios siguen el mismo patron: construyen `SoapObject` con el namespace, lo envuelven en `SoapSerializationEnvelope(VER11)` y lo envian via `HttpTransportSE.call(SOAP_ACTION, envelope)`.

| Clase | Metodos | Operacion SOAP |
|-------|---------|----------------|
| `ServicioAutenticacion` | `iniciarSesion(usuario, contrasena): boolean` | `iniciarSesion` |
| `ServicioLongitud` | 5 metodos | `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros`, `milimetrosAPulgadas` |
| `ServicioMasa` | 5 metodos | `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas`, `miligramosAGramos` |
| `ServicioTemperatura` | 5 metodos | `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius`, `fahrenheitAKelvin` |
| `Resultado` | DTO | `{ exito, mensaje, valor }` + `ok(valor)` / `error(mensaje)` |

### Util

| Clase | Contenido |
|-------|-----------|
| `SoapConstants` | `NAMESPACE = "http://controlador.monster.edu.ec/"`, `URL = "http://10.0.2.2:8080/servidor_soap_java_conuni_gr06/CONUNI"`, `SOAP_ACTION = ""` y constantes con los 16 nombres de operacion. |

---

## 5. Configuracion de red

| Escenario | Valor de `SoapConstants.URL` |
|-----------|------------------------------|
| **Emulador Android** (mismo Mac) | `http://10.0.2.2:8080/servidor_soap_java_conuni_gr06/CONUNI` (ya configurado). `10.0.2.2` es el alias del emulador al `localhost` del host. |
| **Dispositivo fisico** en el mismo WiFi | `http://<IP_DEL_MAC>:8080/servidor_soap_java_conuni_gr06/CONUNI`. Obtener IP: `ipconfig getifaddr en0`. |
| **Produccion** (HTTPS) | `https://<dominio>/...`. Si se pasa a HTTPS, se puede quitar `android:usesCleartextTraffic="true"` del manifiesto. |

El `AndroidManifest.xml` declara:
- `<uses-permission android:name="android.permission.INTERNET" />` para poder abrir conexiones.
- `android:usesCleartextTraffic="true"` para aceptar HTTP plano (solo para desarrollo).

---

## 6. Flujo de uso

```
Launcher
   v
MainActivity (login + ojo contrasena)
   v  (credenciales validas)
MenuActivity (3 tarjetas: Longitud / Masa / Temperatura)
   v  (pulsa una tarjeta)
ConversorActivity (dropdown de 5 operaciones + input + resultado)
```

Se usan **corutinas de Kotlin** con `Dispatchers.IO` para no bloquear el hilo principal durante las llamadas SOAP. El `CircularProgressIndicator` se muestra mientras la peticion esta en curso.

---

## 7. Requisitos previos

- **Android Studio** (recomendado — NetBeans no tiene soporte para AGP).
  ```bash
  brew install --cask android-studio
  ```
- **Android SDK** 34 + plataformas 21-34. Android Studio ofrece instalarlos al abrir el proyecto.
- **JDK 17 o 21** (Gradle 8.7 no soporta JDK 25).
- **Emulador Android** o dispositivo fisico con depuracion USB activada.
- **Servidor SOAP corriendo** en Payara. Si es desde emulador basta con `localhost:8080`; si es desde celular fisico, el Mac debe ser accesible por WiFi.

---

## 8. Como ejecutar

### Primera vez
1. Abre el proyecto en **Android Studio** (`File -> Open`).
2. Acepta la creacion de `local.properties` con la ruta al Android SDK.
3. Espera a que termine *Gradle sync* (descarga ksoap2, material-icons-extended, Compose BOM).
4. **Tools -> Device Manager -> Create device** -> elige un Pixel + API 34.
5. Pulsa **Run ▶**.

### Ejecuciones posteriores
- Sin cambios en dependencias, `Run ▶` compila e instala el APK en el emulador en pocos segundos.

### Compilar desde linea de comando
```bash
./gradlew assembleDebug
# El APK queda en app/build/outputs/apk/debug/app-debug.apk
```

---

## 9. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 10. Puntos a documentar mas adelante

- Diagrama de componentes resaltando el cambio de lenguaje (Kotlin en Vista, Java en Controlador/Modelo).
- Diagrama de secuencia login + conversion en Android (tocar pantalla -> corutina -> ksoap2 -> red -> vuelta al hilo principal).
- Capturas de pantalla en emulador: login, menu, conversor, resultado.
- Justificacion de ksoap2-android (por que no usar Retrofit, HttpURLConnection manual, etc.).
- Comparativa con los clientes web y consola: **misma logica de negocio**, distinta capa de presentacion y distinta libreria SOAP (plain HTTP vs. ksoap2).
- Consideraciones de despliegue: firma del APK, permisos extra, politica de seguridad de red para produccion (HTTPS, `networkSecurityConfig`).
