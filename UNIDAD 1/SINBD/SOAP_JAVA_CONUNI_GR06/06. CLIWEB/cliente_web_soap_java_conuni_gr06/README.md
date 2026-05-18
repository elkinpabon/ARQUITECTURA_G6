# cliente_web_soap_java_conuni_gr06

Cliente web que consume el servicio SOAP **CONUNI** para conversiones de unidades (longitud, masa y temperatura). Presenta al usuario una interfaz de navegador con autenticacion por sesion y formularios de conversion.

---

## 1. Arquitectura (MVC)

El proyecto sigue el patron **Modelo-Vista-Controlador** estricto. La capa Vista nunca invoca SOAP directamente, la capa Controlador no conoce HTML, y el Modelo no conoce HTTP.

```
Navegador
    |  (HTTP form submit)
    v
[Controlador: Servlets]  <---  [Filtro de Sesion]
    |
    |  (llamada Java pura)
    v
[Modelo: Servicios SOAP]
    |
    |  (POST HTTP con sobre XML)
    v
Servidor CONUNI (Payara / JAX-WS)
```

### Responsabilidades por capa

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Vista | `web/vista/*.jsp` + `web/css/estilo.css` + `web/img/` | HTML, CSS, toggle ojo contrasena (JS inline puramente visual). |
| Controlador | `ec.edu.monster.controlador` | Recibe requests HTTP, valida parametros, delega al Modelo, elige que JSP renderizar. |
| Modelo | `ec.edu.monster.modelo` | Construye sobres SOAP, los envia via HTTP y devuelve resultados como objetos Java. |
| Util | `ec.edu.monster.util` | Filtro que bloquea accesos no autenticados. |

---

## 2. Stack tecnologico

- **Java** 17
- **Jakarta EE 10** (Servlets + JSP)
- **Payara Server 6** (contenedor de despliegue)
- **NetBeans Ant** (tipo de proyecto)
- **SOAP 1.1** sobre HTTP, construido a mano con `HttpURLConnection` y `StringBuilder` (sin ksoap / sin JAX-WS stubs)

Razon de no usar stubs JAX-WS: mantener el cliente ligero y explicito, para que los alumnos vean el sobre SOAP real que viaja por la red.

---

## 3. Estructura del proyecto

```
cliente_web_soap_java_conuni_gr06/
├── build.xml                         # Entrada Ant (re-generable)
├── nbproject/
│   ├── project.xml                   # Metadata NetBeans
│   ├── project.properties            # Classpath, nombre del war, JDK
│   └── build-impl.xml                # Generado por NetBeans, no editar
├── src/java/ec/edu/monster/
│   ├── controlador/                  # Servlets
│   │   ├── ServletAutenticacion.java
│   │   ├── ServletLongitud.java
│   │   ├── ServletMasa.java
│   │   ├── ServletTemperatura.java
│   │   └── ServletCerrarSesion.java
│   ├── modelo/                       # Servicios SOAP y DTO
│   │   ├── ClienteSoap.java
│   │   ├── ServicioAutenticacion.java
│   │   ├── ServicioLongitud.java
│   │   ├── ServicioMasa.java
│   │   ├── ServicioTemperatura.java
│   │   └── Resultado.java
│   └── util/
│       └── FiltroSesion.java
└── web/
    ├── WEB-INF/
    │   ├── web.xml                   # Mapeo servlets y filtros
    │   └── glassfish-web.xml         # context-root = /cliente_web_soap_java_conuni_gr06
    ├── css/estilo.css                # Paleta CONUNI (azul + amarillo)
    ├── img/                          # login.jpg y moster.webp
    ├── index.jsp                     # redirige a /vista/iniciarSesion.jsp
    └── vista/
        ├── iniciarSesion.jsp         # Login + toggle ojo contrasena
        ├── menu.jsp                  # Tarjetas: Longitud / Masa / Temperatura
        ├── longitud.jsp
        ├── masa.jsp
        └── temperatura.jsp
```

---

## 4. Clases y responsabilidades

### Controlador (servlets)

| Clase | URL mapeada | Metodo(s) | Que hace |
|-------|-------------|-----------|----------|
| `ServletAutenticacion` | `/autenticacion` | POST | Recibe usuario/contrasena, llama a `ServicioAutenticacion.iniciarSesion`, si OK crea sesion y redirige a `menu.jsp`, si no vuelve al login con error. |
| `ServletLongitud` | `/longitud` | GET, POST | GET renderiza el formulario; POST valida el numero, llama a la operacion elegida de `ServicioLongitud`, envia `Resultado` a la vista. |
| `ServletMasa` | `/masa` | GET, POST | Idem para las 5 operaciones de masa. |
| `ServletTemperatura` | `/temperatura` | GET, POST | Idem para las 5 operaciones de temperatura. |
| `ServletCerrarSesion` | `/cerrarSesion` | GET | Invalida la `HttpSession` y redirige al login. |

### Modelo

| Clase | Responsabilidad |
|-------|-----------------|
| `ClienteSoap` | Cliente SOAP generico. Arma el sobre `soapenv:Envelope`, envia POST a `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI`, extrae el contenido de `<return>` y lo devuelve como String. |
| `ServicioAutenticacion` | Expone `iniciarSesion(usuario, contrasena)` -> `boolean`. |
| `ServicioLongitud` | Expone 5 metodos: `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros`, `milimetrosAPulgadas`. |
| `ServicioMasa` | 5 metodos: `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas`, `miligramosAGramos`. |
| `ServicioTemperatura` | 5 metodos: `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius`, `fahrenheitAKelvin`. |
| `Resultado` | DTO inmutable `{ exito, mensaje, valor }`. Metodos estaticos `ok(valor)` y `error(mensaje)`. Evita que la JSP trate excepciones. |

### Util

| Clase | Responsabilidad |
|-------|-----------------|
| `FiltroSesion` | `Filter` que intercepta toda peticion. Si no hay atributo `usuario` en la sesion y la URL no es publica (login, CSS, img), redirige a `iniciarSesion.jsp`. |

### Vistas JSP

| JSP | Que muestra |
|-----|-------------|
| `iniciarSesion.jsp` | Layout a 2 columnas (imagen + formulario), logo CONUNI, campos usuario/contrasena con **boton ojo SVG** para alternar `type="password"` / `type="text"`. |
| `menu.jsp` | Tarjetas con iconos SVG para las 3 categorias, saludo con el nombre del usuario. |
| `longitud.jsp` / `masa.jsp` / `temperatura.jsp` | Dropdown de operacion + input numerico + caja verde/roja para `Resultado`. Conservan la seleccion y el valor tras enviar el formulario. |

---

## 5. Operaciones SOAP consumidas

Todas apuntan a:
```
http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI
```
Espacio de nombres: `http://controlador.monster.edu.ec/`

| Categoria | Operaciones |
|-----------|-------------|
| Autenticacion | `iniciarSesion(usuario, contrasena): boolean` |
| Longitud      | `metrosAPies`, `kilometrosAMillas`, `centimetrosAPulgadas`, `yardasAMetros`, `milimetrosAPulgadas` |
| Masa          | `kilogramosALibras`, `gramosAOnzas`, `toneladasAKilogramos`, `librasAOnzas`, `miligramosAGramos` |
| Temperatura   | `celsiusAFahrenheit`, `fahrenheitACelsius`, `celsiusAKelvin`, `kelvinACelsius`, `fahrenheitAKelvin` |

---

## 6. Flujo de uso

1. Usuario abre `http://localhost:8080/cliente_web_soap_java_conuni_gr06/` -> `index.jsp` redirige a login.
2. `iniciarSesion.jsp` POSTea a `ServletAutenticacion`.
3. Si las credenciales son validas (`MONSTER / MONSTER9` por defecto), se crea la `HttpSession` con el atributo `usuario` y se navega a `menu.jsp`.
4. Desde el menu el usuario elige una categoria -> llega a la JSP correspondiente.
5. El formulario envia la operacion y el valor al servlet, que invoca SOAP y renderiza `Resultado`.
6. Cerrar sesion invalida la `HttpSession`.

---

## 7. Requisitos previos

- **JDK 17** (probado con OpenJDK 17.0.18 via Homebrew).
- **Payara Server 6** corriendo en `localhost:8080`.
- **Servidor** `servidor_soap_java_conuni_gr06` desplegado y accesible (esta en la carpeta `08. SERVIDOR`).
- **NetBeans 21+** con el plugin de Payara (para desplegar con F6).

---

## 8. Como ejecutar

### Desde NetBeans
1. `File -> Open Project` -> selecciona la carpeta del proyecto.
2. En `Services -> Servers` asegurate de tener Payara 6 configurado con JDK 17.
3. Clic derecho en el proyecto -> **Clean and Build**.
4. Clic derecho -> **Deploy** (o `Run` para abrir tambien el navegador).
5. Se abre `http://localhost:8080/cliente_web_soap_java_conuni_gr06/`.

### Desde terminal (Ant)
```bash
ant clean dist
/opt/payara6/bin/asadmin deploy dist/cliente_web_soap_java_conuni_gr06.war
```

---

## 9. Credenciales de prueba

Definidas en el archivo `credenciales.txt` del servidor:

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 10. Puntos a documentar mas adelante

- Diagrama de componentes (Vista, Controlador, Modelo, Servidor SOAP).
- Diagrama de secuencia del login y de una conversion (ida y vuelta del sobre SOAP).
- Capturas de pantalla de las 5 pantallas (login, menu, longitud, masa, temperatura).
- Justificacion de la seleccion tecnologica (por que Servlets + JSP + SOAP manual).
