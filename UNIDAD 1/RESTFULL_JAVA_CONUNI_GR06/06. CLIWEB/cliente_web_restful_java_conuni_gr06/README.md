# cliente_web_restful_java_conuni_gr06

Cliente web que consume el servicio **REST CONUNI** desplegado en `servidor_restful_java_conuni_gr06`. Presenta al usuario una interfaz de navegador con autenticacion por sesion y formularios de conversion para longitud, masa y temperatura. Equivalente del cliente web SOAP, pero usando HTTP + JSON contra la API REST.

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** clasico de aplicaciones Servlets + JSP. Los Servlets coordinan, las JSPs renderizan y el modelo encapsula la comunicacion con el servidor REST.

```
ec.edu.monster
├── controlador/                                  # Servlets (controladores Web)
│   ├── ServletAutenticacion.java                 # POST /autenticacion -> ServicioAutenticacion -> POST /api/sesion
│   ├── ServletLongitud.java                      # GET/POST /longitud
│   ├── ServletMasa.java                          # GET/POST /masa
│   ├── ServletTemperatura.java                   # GET/POST /temperatura
│   └── ServletCerrarSesion.java                  # invalida la sesion
├── modelo/                                       # capa de comunicacion con la API
│   ├── ClienteRest.java                          # HTTP GET/POST + parser JSON minimalista
│   ├── Resultado.java                            # DTO local (exito + valor + mensaje)
│   ├── ServicioAutenticacion.java                # POST  /api/sesion
│   ├── ServicioLongitud.java                     # GET   /api/longitud/{op}?valor=X
│   ├── ServicioMasa.java                         # GET   /api/masa/{op}?valor=X
│   └── ServicioTemperatura.java                  # GET   /api/temperatura/{op}?valor=X
└── util/
    └── FiltroSesion.java                         # @WebFilter - bloquea /longitud /masa /temperatura sin sesion

web/
├── index.jsp                                     # redirige a /vista/iniciarSesion.jsp
├── vista/                                        # Vistas (JSP)
│   ├── iniciarSesion.jsp
│   ├── menu.jsp
│   ├── longitud.jsp
│   ├── masa.jsp
│   └── temperatura.jsp
├── css/estilo.css
├── img/moster.webp
└── img/login.jpg
```

| Capa MVC | Componente | Responsabilidad |
|----------|------------|-----------------|
| Vista | `*.jsp` + `css/estilo.css` | HTML + formularios, lectura de atributos del request, sin logica de negocio. |
| Controlador | `Servlet*` | Recibe parametros, llama al servicio, pone atributos en el request, hace forward a la JSP. Tambien gestiona `HttpSession`. |
| Modelo | `ClienteRest` + `Servicio*` + `Resultado` | Cliente REST que invoca al servidor. Sin estado mutable. |
| Util | `FiltroSesion` | `@WebFilter` que protege las rutas privadas. |

> Nota: el `HttpSession` (cookie `JSESSIONID`) **vive solo en este cliente web**. El servidor REST es stateless: cada peticion lleva sus credenciales o no las necesita. Esta es una diferencia importante con un esquema OAuth/JWT donde el token viaja en cada request.

---

## 2. Stack tecnologico

- **Java** 17
- **Jakarta EE 10 Web Profile** (Servlets 6 + JSP)
- **Payara Server 6**
- `java.net.HttpURLConnection` para hablar con la API REST (sin librerias externas)
- Parser JSON propio (busqueda por `String.indexOf`) en `ClienteRest`

---

## 3. Configuracion de la URL del servicio

`src/java/ec/edu/monster/modelo/ClienteRest.java`:

```java
private static final String URL_BASE =
        "http://localhost:8080/servidor_restful_java_conuni_gr06/api";
```

---

## 4. Como ejecutar

1. **Servidor REST** corriendo: `http://localhost:8080/servidor_restful_java_conuni_gr06/`
2. NetBeans -> `File -> Open Project` -> `cliente_web_restful_java_conuni_gr06`
3. **Properties -> Run** -> Server: **Payara Server 6**, Java Platform: **JDK 17**
4. **Clean and Build** -> **Deploy** -> **Run** abrira el navegador en
   `http://localhost:8080/cliente_web_restful_java_conuni_gr06/`

Tambien por terminal:
```bash
ant clean dist
/opt/payara6/bin/asadmin deploy dist/cliente_web_restful_java_conuni_gr06.war
```

---

## 5. Flujo de la aplicacion

1. **`/`** -> redirige a `/vista/iniciarSesion.jsp`
2. **Login**: el formulario hace `POST /autenticacion`. El `ServletAutenticacion` invoca `ServicioAutenticacion.iniciarSesion()`, que envia un `POST /api/sesion` al servidor REST con JSON `{usuario, contrasena}`. Si responde `{"valido":true}`, guarda el usuario en `HttpSession` y redirige al menu.
3. **Menu** (`/vista/menu.jsp`): tres tarjetas (Longitud / Masa / Temperatura) que llevan a `/longitud`, `/masa`, `/temperatura`.
4. **Conversiones**: cada `Servlet*` recibe `operacion` y `valor` del formulario, llama al `Servicio*` correspondiente (que hace `GET /api/{categoria}/{op}?valor=X`), y devuelve la JSP con el `Resultado` para renderizar.
5. **Cerrar sesion**: `/cerrarSesion` invalida el `HttpSession` y vuelve al login.

`FiltroSesion` (`@WebFilter` sobre `/longitud`, `/masa`, `/temperatura`) intercepta cualquier intento de acceder a las rutas privadas sin sesion activa y redirige a login.

---

## 6. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 7. Diferencias clave vs. el cliente web SOAP

| Aspecto | SOAP | REST (este proyecto) |
|---------|------|----------------------|
| Cliente HTTP | `ClienteSoap` (sobre XML manual) | `ClienteRest` (JSON manual) |
| Login | `iniciarSesion(usuario,contrasena)` -> `<return>true` | `POST /api/sesion` con JSON -> `{"valido":true}` |
| Conversion | `metrosAPies(metros=10)` | `GET /api/longitud/metros-a-pies?valor=10` |
| Servlets | Sin cambios estructurales | Sin cambios estructurales |
| JSPs | Sin cambios | Sin cambios |
| FiltroSesion | Sin cambios | Sin cambios |
| Sesion del navegador | `HttpSession` (cookie) | `HttpSession` (cookie) — el servidor REST es stateless, la sesion vive en este cliente |
