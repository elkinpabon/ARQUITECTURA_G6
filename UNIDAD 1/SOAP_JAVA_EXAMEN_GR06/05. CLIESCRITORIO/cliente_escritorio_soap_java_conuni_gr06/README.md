# cliente_escritorio_soap_java_conuni_gr06

Aplicacion de escritorio en **Java Swing** que consume el mismo servicio SOAP **CONUNI** que los clientes web, consola y movil. Ofrece una interfaz grafica con autenticacion, menu visual por categorias y formulario de conversion.

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** con comunicacion por **callbacks funcionales** (`Consumer`, `BiConsumer`, `Runnable`). La Vista no conoce el Modelo y expone eventos; el Controlador los escucha y orquesta la llamada SOAP en hilo de fondo con `SwingWorker` para no congelar la UI.

```
Usuario (teclado / raton)
    |
    v
[Vista: Swing Panels (CardLayout)]  <---callbacks--->  [Controlador: ControladorEscritorio]
   PanelLogin, PanelMenu,                                      |
   PanelConversion                                             |  SwingWorker (hilo de fondo)
                                                               v
                                                      [Modelo: Servicios SOAP]
                                                               |
                                                               |  (HTTP POST con sobre SOAP)
                                                               v
                                                      Servidor CONUNI (Payara / JAX-WS)
```

### Responsabilidades por capa

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Vista | `ec.edu.monster.vista` | Paneles Swing. Solo UI; exponen `setOnLogin`, `setOnCategoriaSeleccionada`, `setOnConvertir`, `setOnVolver`, `setOnCerrarSesion` para que el Controlador se suscriba. |
| Controlador | `ec.edu.monster.controlador` | Cablea la Vista, maneja estado de sesion, lanza `SwingWorker` para llamadas SOAP, actualiza la Vista con `Resultado`. |
| Modelo | `ec.edu.monster.modelo` | Identico al de los clientes web y consola: construye sobres SOAP y envia por HTTP. |

La pieza clave es `SwingWorker`: el `doInBackground()` ejecuta la llamada SOAP y `done()` actualiza la UI en el Event Dispatch Thread. Esto evita freezes de la ventana durante la peticion.

---

## 2. Stack tecnologico

- **Java** 17
- **Swing** (biblioteca grafica del JDK, sin dependencias externas)
- **NetBeans Ant J2SE Application** (tipo de proyecto)
- **SOAP 1.1** sobre HTTP con `HttpURLConnection` (mismo cliente que el web y consola)
- **System Look & Feel** activado en el `main` para que la app use los controles nativos de macOS/Windows/Linux

---

## 3. Estructura del proyecto

```
cliente_escritorio_soap_java_conuni_gr06/
├── build.xml                               # Entrada Ant
├── manifest.mf
├── nbproject/
│   ├── project.xml
│   └── project.properties                  # main.class = ec.edu.monster.Aplicacion
└── src/
    ├── img/                                # recursos cargados del classpath
    │   ├── login.jpg                       # misma imagen del cliente web
    │   └── moster.png                      # moster.webp convertido a png (Swing no lee webp)
    └── ec/edu/monster/
        ├── Aplicacion.java                 # main: invokeLater + Look&Feel sistema
        ├── controlador/
        │   └── ControladorEscritorio.java
        ├── modelo/                         # identico a cliente_consola
        │   ├── ClienteSoap.java
        │   ├── Resultado.java
        │   ├── ServicioAutenticacion.java
        │   ├── ServicioLongitud.java
        │   ├── ServicioMasa.java
        │   └── ServicioTemperatura.java
        └── vista/
            ├── Paleta.java                 # Colores + fuentes de la marca CONUNI
            ├── VentanaPrincipal.java       # JFrame con CardLayout (3 tarjetas)
            ├── PanelLogin.java
            ├── PanelMenu.java
            └── PanelConversion.java
```

---

## 4. Clases y responsabilidades

### Entrada

| Clase | Responsabilidad |
|-------|-----------------|
| `Aplicacion` | Unico `main`. `SwingUtilities.invokeLater` + `UIManager.setLookAndFeel(System L&F)` + instancia al `ControladorEscritorio` y llama `iniciar()`. |

### Vista (Swing)

| Clase | Tipo | Responsabilidad |
|-------|------|-----------------|
| `VentanaPrincipal` | `JFrame` | Aloja un `CardLayout` con 3 tarjetas (`LOGIN`, `MENU`, `CONVERSOR`). Expone getters de cada panel y `mostrar(String)` para cambiar de tarjeta. Tamano 820x560. |
| `PanelLogin` | `JPanel` | Layout en 2 columnas: izquierda `login.jpg` escalada, derecha logo + formulario. Contiene el **boton "Mostrar/Ocultar"** que alterna `setEchoChar('•')` ↔ `setEchoChar((char)0)` sobre el `JPasswordField`. Publica `setOnLogin(BiConsumer<String, String>)`. |
| `PanelMenu` | `JPanel` | Barra azul con logo + saludo + boton "Cerrar Sesion". En el centro, 3 tarjetas (`JButton` con HTML) para Longitud, Masa y Temperatura. Publica `setOnCategoriaSeleccionada(Consumer<String>)` y `setOnCerrarSesion(Runnable)`. |
| `PanelConversion` | `JPanel` | Panel parametrizable. `setCategoria("longitud" / "masa" / "temperatura")` cambia el titulo y el contenido del `JComboBox`. Input numerico, `JButton` "Convertir" y `JLabel` de resultado con fondo verde/rojo. Publica `setOnConvertir(BiConsumer<String, Double>)` y `setOnVolver(Runnable)`. |
| `Paleta` | Clase estatica | Constantes `Color` y `Font` de la marca CONUNI (azul #1F3A5F, amarillo #FFD966, grises, verde/rojo de exito/error). |

### Controlador

| Clase | Responsabilidad |
|-------|-----------------|
| `ControladorEscritorio` | Mantiene la `VentanaPrincipal` + 4 servicios como campos finales. `cablearVista()` registra callbacks en los paneles. `manejarLogin`, `manejarConvertir` lanzan **`SwingWorker<Tipo, Void>`** con `doInBackground()` para la llamada SOAP y `done()` para actualizar la UI. Maneja el estado de `usuarioActual` y la navegacion entre tarjetas. |

### Modelo

Identico al del cliente consola (reutilizado sin cambios). Consulta ese README para el detalle.

---

## 5. Experiencia de usuario (flujo)

```
JFrame ventana principal (CardLayout)
    |
    v
Tarjeta LOGIN
   - Imagen grande a la izquierda (login.jpg)
   - Formulario: usuario + contrasena + boton Mostrar/Ocultar
   - Boton "Ingresar" -> SwingWorker -> SOAP
   - Durante la llamada: boton cambia a "Ingresando..." (deshabilitado)
    |
    v  (credenciales validas)
Tarjeta MENU
   - Barra azul: logo + "Bienvenido, <usuario>" + "Cerrar Sesion"
   - 3 tarjetas grandes (Longitud / Masa / Temperatura)
    |
    v  (clic en una tarjeta)
Tarjeta CONVERSOR
   - Titulo contextual
   - Combo con las 5 operaciones de la categoria
   - Input valor (acepta "12,5" o "12.5")
   - Boton "Convertir" -> SwingWorker -> SOAP -> muestra caja verde/roja
   - Boton "Volver al Menu"
```

### Caracteristicas de UX destacadas

- **Toggle ojo contrasena** (JPasswordField con `setEchoChar`).
- **SwingWorker** para todas las llamadas SOAP: la UI nunca se congela.
- **Feedback visual**: boton deshabilitado y texto "Ingresando.../Convirtiendo..." mientras la peticion esta en curso.
- **Validacion local**: entradas no numericas muestran caja roja sin ir al servidor.
- **Enter** en el campo de usuario mueve al de contrasena; Enter en contrasena envia el login.
- **CardLayout**: una sola ventana, cambia de pantalla sin abrir JFrames nuevos.

---

## 6. Operaciones SOAP consumidas

Las mismas 16 que los demas clientes, apuntando a:
```
http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI
```
Namespace: `http://controlador.monster.edu.ec/`

| Categoria | Operaciones |
|-----------|-------------|
| Autenticacion | `iniciarSesion` |
| Longitud      | `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros`, `milimetrosAPulgadas` |
| Masa          | `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas`, `miligramosAGramos` |
| Temperatura   | `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius`, `fahrenheitAKelvin` |

---

## 7. Recursos graficos

| Archivo | Ubicacion | Uso | Nota |
|---------|-----------|-----|------|
| `login.jpg` | `src/img/` | Columna izquierda del panel de login | Se reutiliza tal cual del cliente web. |
| `moster.png` | `src/img/` | Logo en toolbar del menu y logo redondeado en el login | Convertido con `sips -s format png moster.webp --out moster.png` porque **Swing no soporta WEBP de forma nativa** (`ImageIcon` solo lee gif/jpg/png). |

Al empaquetar el `.jar`, la carpeta `src/img/` queda en el classpath y las imagenes se cargan con `getClass().getResource("/img/archivo")`.

---

## 8. Requisitos previos

- **JDK 17**.
- **Servidor SOAP corriendo** en `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI`.
- (Opcional) **NetBeans 21+** para abrir el proyecto como J2SE Application.

---

## 9. Como ejecutar

### Desde NetBeans
1. `File -> Open Project` -> selecciona `cliente_escritorio_soap_java_conuni_gr06`.
2. `Properties -> Libraries -> Java Platform` -> **JDK 17**.
3. Pulsa **Run** (F6). Abre la ventana en el login.

### Desde terminal (compilacion manual)
```bash
cd "05. CLIESCRITORIO/cliente_escritorio_soap_java_conuni_gr06"
mkdir -p build/classes
javac -d build/classes $(find src -name "*.java")
cp -r src/img build/classes/
java -cp build/classes ec.edu.monster.Aplicacion
```

### Desde terminal (Ant)
```bash
ant clean jar
java -jar dist/cliente_escritorio_soap_java_conuni_gr06.jar
```

---

## 10. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 11. Puntos a documentar mas adelante

- Diagrama de componentes resaltando el uso de `CardLayout` (una sola ventana, 3 tarjetas).
- Diagrama de secuencia login: usuario -> `PanelLogin` -> callback -> `ControladorEscritorio` -> `SwingWorker` -> `ServicioAutenticacion` -> `ClienteSoap` -> Servidor -> vuelta al EDT via `done()`.
- Capturas de pantalla de las 3 tarjetas (login con contrasena oculta y visible, menu, conversor con resultado verde y con error rojo).
- Justificacion de la eleccion: por que Swing y no JavaFX (Swing viene con el JDK, zero-deps; JavaFX requeria instalacion/modulos aparte).
- Justificacion del patron de callbacks funcionales vs. el pattern `Observer` tradicional.
- Por que convertir webp a png (Swing / `ImageIO` por defecto no soporta webp sin plugin).
- Comparativa con el cliente consola: misma capa Modelo reutilizada textualmente, distintas capas Vista y Controlador.
