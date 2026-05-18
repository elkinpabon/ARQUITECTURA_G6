# cliente_consola_restful_java_conuni_gr06

Cliente de consola/terminal que consume el servicio **REST CONUNI** desplegado en `servidor_restful_java_conuni_gr06`. Ejecuta un menu interactivo en Java puro (Scanner + System.out) con autenticacion y 15 operaciones de conversion. Equivalente del cliente consola SOAP, pero usando HTTP + JSON.

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** estricto. La Vista concentra toda la I/O de consola y el Controlador orquesta el flujo sin tocar `System.in` ni `System.out` directamente.

```
ec.edu.monster
├── Aplicacion.java                     # main() -> ControladorConsola.ejecutar()
├── controlador/
│   └── ControladorConsola.java         # bucles de menu, llama a servicios, pide a la vista
├── vista/
│   └── VistaConsola.java               # Scanner + System.out (unico punto de I/O)
└── modelo/
    ├── Resultado.java                  # DTO local (exito + valor + mensaje)
    ├── ClienteRest.java                # HTTP GET/POST + parser JSON minimalista
    ├── ServicioAutenticacion.java      # POST  /api/sesion
    ├── ServicioLongitud.java           # GET   /api/longitud/{op}?valor=X (5 metodos)
    ├── ServicioMasa.java               # GET   /api/masa/{op}?valor=X (5 metodos)
    └── ServicioTemperatura.java        # GET   /api/temperatura/{op}?valor=X (5 metodos)
```

| Capa MVC | Clase / paquete | Responsabilidad |
|----------|-----------------|-----------------|
| Vista | `VistaConsola` | Encabezado, menus, prompts, lectura de teclado, formato de errores/resultados. Sin logica. |
| Controlador | `ControladorConsola` | Login con reintentos, navegacion entre menus, despacha al servicio segun la opcion del usuario. |
| Modelo | `ClienteRest` + 4 `Servicio*` + `Resultado` | Cada servicio expone metodos por nombre legible (`metrosAPies`) y por dentro arma la peticion HTTP via `ClienteRest`. |

> El servidor REST tambien sigue MVC: alli `controlador/` son los recursos JAX-RS y `servicio/` la logica. Aqui en el cliente, los `Servicio*` actuan como **proxy** del lado cliente — no contienen formulas, solo invocan al servidor.

---

## 2. Stack tecnologico

- **Java** 17 (JDK)
- **NetBeans Ant Java SE Project** (no requiere servidor)
- `java.net.HttpURLConnection` para HTTP (sin librerias externas)
- Parser JSON propio (busqueda por `String.indexOf`) — no usamos Jackson ni Gson para mantener el `.jar` minimal

---

## 3. Configuracion de la URL del servicio

`ClienteRest.java`:

```java
private static final String URL_BASE =
        "http://localhost:8080/servidor_restful_java_conuni_gr06/api";
```

Cambiala si el servidor REST corre en otro host/puerto.

---

## 4. Como ejecutar

### Desde NetBeans
1. `File -> Open Project` -> selecciona `cliente_consola_restful_java_conuni_gr06`.
2. Asegurate de tener el **servidor REST** corriendo (`http://localhost:8080/servidor_restful_java_conuni_gr06/`).
3. **Run** (F6) — se abrira el menu en la pestana de salida.

### Desde terminal
```bash
ant clean dist
java -jar dist/cliente_consola_restful_java_conuni_gr06.jar
```

---

## 5. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

(Lee `src/java/credenciales.txt` del servidor.)

---

## 6. Diferencias clave vs. el cliente SOAP

| Aspecto | SOAP | REST (este proyecto) |
|---------|------|----------------------|
| Cliente HTTP | `ClienteSoap` (sobre XML manual) | `ClienteRest` (JSON manual) |
| Operaciones | `metrosAPies(metros=10)` | `GET /longitud/metros-a-pies?valor=10` |
| Login | `iniciarSesion(usuario,contrasena)` -> `<return>true` | `POST /sesion` con JSON -> `{"valido":true}` |
| Parametro extraido | `<return>` | campo `valor` o `valido` del JSON |

El resto (Vista, Controlador, Aplicacion) es **identico** — el cambio se concentra en la capa de comunicacion.
