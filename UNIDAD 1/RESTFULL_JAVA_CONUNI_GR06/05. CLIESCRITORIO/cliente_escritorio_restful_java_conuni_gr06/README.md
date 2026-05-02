# cliente_escritorio_restful_java_conuni_gr06

Aplicacion de escritorio en **Java Swing** que consume el servicio **REST CONUNI** desplegado en `servidor_restful_java_conuni_gr06`. Ofrece una interfaz grafica con autenticacion, menu visual por categorias y formulario de conversion. Equivalente del cliente escritorio SOAP, pero usando HTTP + JSON.

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** con tres paneles Swing administrados por un `CardLayout`. El Controlador cablea eventos de la Vista a los servicios del Modelo y usa `SwingWorker` para que las llamadas REST no bloqueen el Event Dispatch Thread.

```
ec.edu.monster
├── Aplicacion.java                   # main() -> ControladorEscritorio.iniciar()
├── controlador/
│   └── ControladorEscritorio.java    # cablea eventos UI -> servicios; usa SwingWorker
├── vista/                            # Vistas Swing
│   ├── VentanaPrincipal.java         # JFrame raiz con CardLayout
│   ├── PanelLogin.java/.form         # tarjeta LOGIN
│   ├── PanelMenu.java/.form          # tarjeta MENU (Longitud / Masa / Temperatura)
│   ├── PanelConversion.java/.form    # tarjeta CONVERSOR
│   ├── Paleta.java                   # colores y fuentes
│   └── Bundle.properties             # textos
└── modelo/
    ├── Resultado.java                # DTO local (exito + valor + mensaje)
    ├── ClienteRest.java              # HTTP GET/POST + parser JSON minimalista
    ├── ServicioAutenticacion.java    # POST  /api/sesion
    ├── ServicioLongitud.java         # GET   /api/longitud/{op}?valor=X
    ├── ServicioMasa.java             # GET   /api/masa/{op}?valor=X
    └── ServicioTemperatura.java      # GET   /api/temperatura/{op}?valor=X
```

| Capa MVC | Componente | Responsabilidad |
|----------|------------|-----------------|
| Vista | `VentanaPrincipal`, `PanelLogin`, `PanelMenu`, `PanelConversion`, `Paleta` | Componentes Swing, sin logica de negocio. Exponen callbacks (`setOnLogin`, `setOnConvertir`, ...). |
| Controlador | `ControladorEscritorio` | Cablea callbacks de la Vista a los servicios. `SwingWorker` para no bloquear el EDT. |
| Modelo | `ClienteRest` + `Servicio*` + `Resultado` | Cliente REST que invoca al servidor. |

---

## 2. Stack tecnologico

- **Java** 17 (JDK)
- **Swing** + NetBeans Form Editor (`.form`)
- **NetBeans Ant Java SE Project** (no requiere servidor para correr)
- `java.net.HttpURLConnection` para HTTP (sin librerias externas)
- Parser JSON propio (`String.indexOf`) en `ClienteRest`

---

## 3. Configuracion de la URL del servicio

`src/ec/edu/monster/modelo/ClienteRest.java`:

```java
private static final String URL_BASE =
        "http://localhost:8080/servidor_restful_java_conuni_gr06/api";
```

---

## 4. Como ejecutar

### Desde NetBeans
1. Asegurate de tener el **servidor REST** corriendo en Payara.
2. `File -> Open Project` -> `cliente_escritorio_restful_java_conuni_gr06`
3. **Run** (F6) — abre la ventana en pantalla de login.

### Desde terminal
```bash
ant clean dist
java -jar dist/cliente_escritorio_restful_java_conuni_gr06.jar
```

---

## 5. Flujo de la aplicacion

1. **VentanaPrincipal** arranca con la tarjeta LOGIN visible.
2. **Login**: el usuario tipea credenciales -> el `PanelLogin` dispara el callback `onLogin` -> `ControladorEscritorio.manejarLogin()` lanza un `SwingWorker` que llama a `ServicioAutenticacion.iniciarSesion()` -> `POST /api/sesion`. Si la respuesta es `{"valido":true}`, se cambia a la tarjeta MENU.
3. **MENU**: tres botones (Longitud / Masa / Temperatura). Al clic se cambia a la tarjeta CONVERSOR con la categoria correspondiente.
4. **CONVERSOR**: combo con las 5 operaciones de la categoria + campo de valor + boton "Convertir". El controlador lanza un `SwingWorker` que invoca el `Servicio*` correspondiente -> `GET /api/{categoria}/{op}?valor=X`. El resultado se renderiza en una caja de exito o de error.
5. **Cerrar sesion**: vuelve a la tarjeta LOGIN.

---

## 6. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 7. Diferencias clave vs. el cliente escritorio SOAP

| Aspecto | SOAP | REST (este proyecto) |
|---------|------|----------------------|
| Cliente HTTP | `ClienteSoap` (sobre XML manual) | `ClienteRest` (JSON manual) |
| Login | `iniciarSesion(usuario,contrasena)` -> `<return>true` | `POST /api/sesion` -> `{"valido":true}` |
| Conversion | `metrosAPies(metros=10)` | `GET /api/longitud/metros-a-pies?valor=10` |
| UI Swing | Sin cambios estructurales | Sin cambios estructurales |
| `SwingWorker` | Para llamadas SOAP | Para llamadas REST |
| Paleta / fuentes | Sin cambios | Sin cambios |
