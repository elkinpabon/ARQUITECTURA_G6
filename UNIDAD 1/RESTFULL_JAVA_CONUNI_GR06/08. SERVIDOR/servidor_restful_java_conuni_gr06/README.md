# servidor_restful_java_conuni_gr06

Servidor **REST** que expone el servicio **CONUNI** con 16 operaciones: 1 de autenticacion, 5 de longitud, 5 de masa y 5 de temperatura. Equivalente RESTful del servidor SOAP del repo `SOAP_JAVA_CONUNI_GR06`. Es el backend comun que consumen los cuatro clientes (`cliente_consola`, `cliente_escritorio`, `cliente_web`, `cliente_movil`).

---

## 1. Arquitectura (MVC)

Servicio web REST publicado con **JAX-RS** (Jersey, incluido en Payara) sobre **Jakarta EE 10** y desplegado en **Payara Server 6**. El proyecto sigue el patron Modelo-Vista-Controlador organizado en cuatro paquetes dentro de `ec.edu.monster`:

```
Cliente (consola / escritorio / web / movil)
    |  (HTTP GET / POST con JSON)
    v
[Vista API: clases @Path en controlador/]   <-- recursos JAX-RS
    |
    |  (delegacion a la logica)
    v
[Controlador: clases en servicio/]          <-- ServicioAutenticacion, ServicioLongitud, ServicioMasa, ServicioTemperatura
    |
    v
[Modelo: clases en modelo/]                 <-- Credencial, Resultado, PeticionSesion, RespuestaSesion
                +
       recurso `credenciales.txt`
```

### Responsabilidades por capa

| Capa MVC | Paquete | Responsabilidad |
|----------|---------|-----------------|
| **Vista (HTTP/JSON)** | `ec.edu.monster.controlador` | Clases anotadas con `@Path`. Reciben/envian JSON. No tienen logica de negocio. |
| **Controlador (logica)** | `ec.edu.monster.servicio` | Implementa las conversiones matematicas y la autenticacion. POJOs sin anotaciones. |
| **Modelo (datos)** | `ec.edu.monster.modelo` | DTOs serializables a JSON: `Resultado`, `PeticionSesion`, `RespuestaSesion`, `Credencial`. |
| **Configuracion** | `ec.edu.monster.aplicacion` | `AplicacionRest` con `@ApplicationPath("/api")` registra la API. |
| **Recursos** | `src/java/credenciales.txt` | Archivo plano con la credencial unica valida. |

> **Nota:** "Vista" en una API REST significa la representacion HTTP/JSON que ven los clientes. La UI esta del lado de cada cliente (consola, escritorio, web, movil), que sigue su propio MVC interno.

---

## 2. Stack tecnologico

- **Java** 17
- **Jakarta EE 10 Web Profile** (`j2ee.platform=10-web`)
- **JAX-RS 3.1** (implementacion Jersey incluida en Payara)
- **JSON-B** (incluida en Payara) para serializar POJOs a JSON automaticamente
- **Payara Server 6** (`j2ee.server.type=pfv5ee8`)
- **NetBeans Ant Web Project**
- **JUnit 4** para pruebas unitarias

Sin dependencias externas: el `.war` solo trae las pruebas (junit + hamcrest); el resto lo provee Payara.

---

## 3. Estructura del proyecto

```
servidor_restful_java_conuni_gr06/
├── build.xml
├── nbproject/
│   ├── project.xml
│   ├── project.properties               # JDK 17, j2ee.platform=10-web
│   └── build-impl.xml
├── lib/
│   └── hamcrest-core-1.3.jar
├── src/java/
│   ├── credenciales.txt                 # MONSTER:MONSTER9
│   └── ec/edu/monster/
│       ├── aplicacion/
│       │   └── AplicacionRest.java      # @ApplicationPath("/api")
│       ├── controlador/                 # capa Vista de la API REST
│       │   ├── RecursoSesion.java       # POST  /api/sesion
│       │   ├── RecursoLongitud.java     # GET   /api/longitud/{op}
│       │   ├── RecursoMasa.java         # GET   /api/masa/{op}
│       │   └── RecursoTemperatura.java  # GET   /api/temperatura/{op}
│       ├── servicio/                    # capa Controlador (logica de negocio)
│       │   ├── ServicioAutenticacion.java
│       │   ├── ServicioLongitud.java
│       │   ├── ServicioMasa.java
│       │   └── ServicioTemperatura.java
│       └── modelo/                      # capa Modelo (DTOs)
│           ├── Credencial.java
│           ├── Resultado.java
│           ├── PeticionSesion.java
│           └── RespuestaSesion.java
├── test/ec/edu/monster/prueba/
│   ├── pruebaAutenticacion.java
│   ├── pruebaLongitud.java
│   ├── pruebaMasa.java
│   └── pruebaTemperatura.java
└── web/
    ├── index.html                       # probador interactivo de la API
    └── WEB-INF/
        ├── web.xml                      # vacio: la API se registra via @ApplicationPath
        └── glassfish-web.xml            # context-root = /servidor_restful_java_conuni_gr06
```

---

## 4. Endpoints publicados

Con Payara corriendo y el `.war` desplegado:

| Recurso | URL |
|---------|-----|
| **Probador interactivo** | `http://localhost:8080/servidor_restful_java_conuni_gr06/` |
| **Base de la API** | `http://localhost:8080/servidor_restful_java_conuni_gr06/api` |

### 4.1. Autenticacion

| Metodo | Ruta | Cuerpo JSON | Respuesta |
|--------|------|-------------|-----------|
| `POST` | `/api/sesion` | `{"usuario":"MONSTER","contrasena":"MONSTER9"}` | `200` `{"valido":true,"mensaje":"Credenciales validas"}` o `401` `{"valido":false,"mensaje":"..."}` |

### 4.2. Longitud (`GET /api/longitud/{operacion}?valor=X`)

| Operacion | Ejemplo |
|-----------|---------|
| `metros-a-pies` | `/api/longitud/metros-a-pies?valor=10` |
| `kilometros-a-millas` | `/api/longitud/kilometros-a-millas?valor=10` |
| `centimetros-a-pulgadas` | `/api/longitud/centimetros-a-pulgadas?valor=2.54` |
| `yardas-a-metros` | `/api/longitud/yardas-a-metros?valor=1.09361` |
| `milimetros-a-pulgadas` | `/api/longitud/milimetros-a-pulgadas?valor=10` |

### 4.3. Masa (`GET /api/masa/{operacion}?valor=X`)

| Operacion | Ejemplo |
|-----------|---------|
| `kilogramos-a-libras` | `/api/masa/kilogramos-a-libras?valor=1` |
| `gramos-a-onzas` | `/api/masa/gramos-a-onzas?valor=100` |
| `toneladas-a-kilogramos` | `/api/masa/toneladas-a-kilogramos?valor=2` |
| `libras-a-onzas` | `/api/masa/libras-a-onzas?valor=2` |
| `miligramos-a-gramos` | `/api/masa/miligramos-a-gramos?valor=1000` |

### 4.4. Temperatura (`GET /api/temperatura/{operacion}?valor=X`)

| Operacion | Ejemplo |
|-----------|---------|
| `celsius-a-fahrenheit` | `/api/temperatura/celsius-a-fahrenheit?valor=100` |
| `fahrenheit-a-celsius` | `/api/temperatura/fahrenheit-a-celsius?valor=212` |
| `celsius-a-kelvin` | `/api/temperatura/celsius-a-kelvin?valor=0` |
| `kelvin-a-celsius` | `/api/temperatura/kelvin-a-celsius?valor=273.15` |
| `fahrenheit-a-kelvin` | `/api/temperatura/fahrenheit-a-kelvin?valor=32` |

### 4.5. Formato de respuesta

Todas las conversiones devuelven el mismo JSON:

```json
{ "exito": true, "valor": 32.8084, "mensaje": "OK" }
```

Ejemplo con `curl`:

```bash
# Conversion
curl -s 'http://localhost:8080/servidor_restful_java_conuni_gr06/api/longitud/metros-a-pies?valor=10'
# -> {"exito":true,"valor":32.8084,"mensaje":"OK"}

# Autenticacion
curl -s -X POST -H 'Content-Type: application/json' \
  -d '{"usuario":"MONSTER","contrasena":"MONSTER9"}' \
  http://localhost:8080/servidor_restful_java_conuni_gr06/api/sesion
# -> {"valido":true,"mensaje":"Credenciales validas"}
```

---

## 5. Pruebas unitarias (JUnit 4)

Ubicadas en `test/ec/edu/monster/prueba/`. Se ejecutan contra los servicios directamente (no contra la capa REST), por lo que **no requieren tener Payara arriba**.

| Clase | Que cubre |
|-------|-----------|
| `pruebaAutenticacion` | exito con `MONSTER/MONSTER9`, usuario incorrecto, contrasena incorrecta, vacios, nulos, sensibilidad a mayusculas. |
| `pruebaLongitud` | una por cada formula + caso valor cero. `assertEquals` con margen `0.0001`. |
| `pruebaMasa` | las 5 formulas + caso cero. |
| `pruebaTemperatura` | Celsius/Fahrenheit/Kelvin cruzados. |

Ejecutar en NetBeans: clic derecho en `test/` -> **Run Tests**. O via Ant: `ant test`.

---

## 6. Requisitos previos

- **JDK 17**
- **Payara Server 6** arriba
- **NetBeans 21+** con el plugin de Payara

---

## 7. Como ejecutar

### Desde NetBeans
1. `File -> Open Project` -> selecciona `servidor_restful_java_conuni_gr06`.
2. `Properties -> Run` -> **Server: Payara Server 6**, **Java Platform: JDK 17**.
3. **Clean and Build** -> **Deploy** (o `Run` para abrir el probador en el navegador).
4. Verifica que la API responde:
   ```bash
   curl -s -o /dev/null -w "%{http_code}\n" \
     'http://localhost:8080/servidor_restful_java_conuni_gr06/api/longitud/metros-a-pies?valor=1'
   # Debe devolver 200
   ```

### Desde terminal
```bash
ant clean dist
/opt/payara6/bin/asadmin deploy dist/servidor_restful_java_conuni_gr06.war
/opt/payara6/bin/asadmin list-applications
/opt/payara6/bin/asadmin undeploy servidor_restful_java_conuni_gr06
```

---

## 8. Credenciales validas

```
usuario:    MONSTER
contrasena: MONSTER9
```
Las mismas que el servidor SOAP — definidas en `src/java/credenciales.txt`. Cambialas y redespliega.

---

## 9. Diferencias clave vs. el servidor SOAP

| Aspecto | SOAP (`servidor_soap_*`) | REST (este proyecto) |
|---------|--------------------------|----------------------|
| Protocolo | SOAP 1.1 (sobre XML) | HTTP + JSON |
| Fachada | `CONUNI.java` con `@WebService` y 16 `@WebMethod` | 4 clases `Recurso*` con `@Path` |
| Contrato | WSDL en `?wsdl` | URLs REST + JSON ad-hoc |
| Codigo verboso del cliente | Sobre SOAP construido a mano | `fetch`/`HttpURLConnection` con JSON |
| Logica de negocio | Identica (paquete `servicio/`) | Identica (paquete `servicio/`) |
| Modelo | `Credencial` | `Credencial` + `Resultado` + `PeticionSesion` + `RespuestaSesion` |
