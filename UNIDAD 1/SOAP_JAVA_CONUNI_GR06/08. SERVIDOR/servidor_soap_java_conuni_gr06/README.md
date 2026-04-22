# servidor_soap_java_conuni_gr06

Servidor **SOAP** que expone el servicio **CONUNI** con 16 operaciones: autenticacion, 5 conversiones de longitud, 5 de masa y 5 de temperatura. Es el backend comun que consumen los tres clientes del repositorio (`cliente_web`, `cliente_consola`, `cliente_movil`).

---

## 1. Arquitectura

Servicio web SOAP publicado con **JAX-WS** sobre **Jakarta EE 10** y desplegado en **Payara Server 6**. La logica esta organizada en tres capas dentro del paquete `ec.edu.monster`:

```
Cliente (web / consola / movil)
    |  (HTTP POST con sobre SOAP)
    v
[Endpoint JAX-WS: CONUNI.java]         <-- unica clase @WebService
    |
    |  (delegacion Java pura)
    v
[Servicios: ServicioAutenticacion, ServicioLongitud,
            ServicioMasa, ServicioTemperatura]
    |
    v
[Modelo: Credencial]  +  recurso `credenciales.txt`
```

### Responsabilidades por capa

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Endpoint / Fachada | `ec.edu.monster.controlador` | Clase anotada con `@WebService`. Traduce las operaciones SOAP en llamadas a los servicios. No contiene logica de negocio. |
| Servicio (logica) | `ec.edu.monster.servicio` | Implementa las conversiones matematicas y la autenticacion. POJOs sin anotaciones JAX-WS — reutilizables. |
| Modelo | `ec.edu.monster.modelo` | Objetos de dominio. Aqui solo `Credencial` (usuario + contrasena + metodo `coincide`). |
| Recursos | `src/java/credenciales.txt` | Archivo plano con la credencial unica valida. Se carga desde el classpath en tiempo de ejecucion. |

---

## 2. Stack tecnologico

- **Java** 17 (`javac.source=17`, `javac.target=17`)
- **Jakarta EE 10 Web Profile** (`j2ee.platform=10-web`)
- **JAX-WS** (implementacion Metro incluida en Payara)
- **Payara Server 6** (`j2ee.server.type=pfv5ee8`)
- **NetBeans Ant Web Project** (tipo de proyecto)
- **JUnit 4** para las pruebas unitarias

Sin dependencias externas mas alla de lo que trae Payara: el `.war` es liviano.

---

## 3. Estructura del proyecto

```
servidor_soap_java_conuni_gr06/
├── build.xml                              # Entrada Ant
├── nbproject/
│   ├── project.xml                        # Metadata NetBeans
│   ├── project.properties                 # JDK 17, j2ee.platform=10-web
│   ├── jax-ws.xml                         # Descriptor JAX-WS (vacio, NetBeans lo llena)
│   └── build-impl.xml                     # Regenerado automaticamente
├── src/java/
│   ├── credenciales.txt                   # usuario:contrasena (cargado del classpath)
│   └── ec/edu/monster/
│       ├── controlador/
│       │   └── CONUNI.java                # @WebService con los 16 @WebMethod
│       ├── servicio/
│       │   ├── ServicioAutenticacion.java
│       │   ├── ServicioLongitud.java
│       │   ├── ServicioMasa.java
│       │   └── ServicioTemperatura.java
│       └── modelo/
│           └── Credencial.java
├── test/ec/edu/monster/prueba/
│   ├── pruebaAutenticacion.java
│   ├── pruebaLongitud.java
│   ├── pruebaMasa.java
│   └── pruebaTemperatura.java
└── web/WEB-INF/
    ├── web.xml                            # Vacio (JAX-WS no lo necesita)
    └── glassfish-web.xml                  # context-root = /servidor_soap_java_conuni_gr06
```

---

## 4. Clases y responsabilidades

### Endpoint JAX-WS

| Clase | Anotaciones | Responsabilidad |
|-------|-------------|-----------------|
| `CONUNI` | `@WebService(serviceName = "CONUNI")` | **Fachada del servicio**. Cada uno de sus metodos esta anotado con `@WebMethod(operationName = ...)` y `@WebParam(name = ...)`. Mantiene 4 servicios como campos `final` y simplemente delega cada operacion. Sin estado mutable, sin logica. |

Los 16 metodos expuestos:

| Categoria | `operationName` | Parametro | Retorno |
|-----------|-----------------|-----------|---------|
| Autenticacion | `iniciarSesion` | `usuario`, `contrasena` | `boolean` |
| Longitud | `metrosAPies` | `metros` | `double` |
| Longitud | `kilometrosAMillas` | `kilometros` | `double` |
| Longitud | `centimetrosAPulgadas` | `centimetros` | `double` |
| Longitud | `yardasAMetros` | `yardas` | `double` |
| Longitud | `milimetrosAPulgadas` | `milimetros` | `double` |
| Masa | `kilogramosALibras` | `kilogramos` | `double` |
| Masa | `gramosAOnzas` | `gramos` | `double` |
| Masa | `toneladasAKilogramos` | `toneladas` | `double` |
| Masa | `librasAOnzas` | `libras` | `double` |
| Masa | `miligramosAGramos` | `miligramos` | `double` |
| Temperatura | `celsiusAFahrenheit` | `celsius` | `double` |
| Temperatura | `fahrenheitACelsius` | `fahrenheit` | `double` |
| Temperatura | `celsiusAKelvin` | `celsius` | `double` |
| Temperatura | `kelvinACelsius` | `kelvin` | `double` |
| Temperatura | `fahrenheitAKelvin` | `fahrenheit` | `double` |

### Servicios (logica de negocio)

| Clase | Publico | Detalle |
|-------|---------|---------|
| `ServicioAutenticacion` | `boolean autenticar(String usuario, String contrasena)` | Carga `credenciales.txt` del classpath con `ClassLoader.getResourceAsStream`. Construye una `Credencial` con los parametros y la compara contra la almacenada usando `Credencial.coincide`. Rechaza nulos y lineas en blanco/comentario. |
| `ServicioLongitud` | 5 metodos | Formulas: `metros * 3.28084`, `km * 0.621371`, `cm / 2.54`, `yd / 1.09361`, `mm * 0.0393701`. |
| `ServicioMasa` | 5 metodos | Formulas: `kg * 2.20462`, `g * 0.035274`, `t * 1000`, `lb * 16`, `mg / 1000`. |
| `ServicioTemperatura` | 5 metodos | Formulas: C->F `(c*9/5)+32`, F->C `(f-32)*5/9`, C->K `c+273.15`, K->C `k-273.15`, F->K `(f-32)*5/9+273.15`. |

### Modelo

| Clase | Miembros | Metodos |
|-------|----------|---------|
| `Credencial` | `final String usuario`, `final String contrasena` | Getters + `coincide(Credencial otra)` que compara los dos campos con `equals` (rechaza nulos). Inmutable. |

### Recurso

| Archivo | Formato | Uso |
|---------|---------|-----|
| `src/java/credenciales.txt` | `usuario:contrasena` por linea. Permite `#` para comentarios. | Queda en el `.war` como recurso del classpath. `ServicioAutenticacion` lee la **primera linea valida** y la usa como unica credencial aceptada. |

Valor actual del archivo:
```
MONSTER:MONSTER9
```

---

## 5. Endpoint publicado

Con Payara corriendo y el `.war` desplegado:

| Recurso | URL |
|---------|-----|
| **Contexto del servicio** | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI` |
| **WSDL** | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl` |
| **Namespace** | `http://controlador.monster.edu.ec/` (derivado del paquete `ec.edu.monster.controlador` invertido) |

Ejemplo de sobre SOAP que enviaria un cliente (`metrosAPies(10)`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:con="http://controlador.monster.edu.ec/">
  <soapenv:Header/>
  <soapenv:Body>
    <con:metrosAPies>
      <metros>10.0</metros>
    </con:metrosAPies>
  </soapenv:Body>
</soapenv:Envelope>
```

Respuesta (extracto):

```xml
<ns2:metrosAPiesResponse xmlns:ns2="http://controlador.monster.edu.ec/">
  <return>32.8084</return>
</ns2:metrosAPiesResponse>
```

---

## 6. Pruebas unitarias (JUnit 4)

Ubicadas en `test/ec/edu/monster/prueba/`. Se ejecutan contra los servicios directamente (no contra la capa SOAP), por lo que **no requieren tener Payara arriba**.

| Clase | Casos | Que cubre |
|-------|-------|-----------|
| `pruebaAutenticacion` | 6 | exito con `MONSTER/MONSTER9`, usuario incorrecto, contrasena incorrecta, ambos vacios, ambos nulos, sensibilidad a mayusculas. |
| `pruebaLongitud` | 6 | Una por cada formula + caso valor cero. `assertEquals` con margen `0.0001`. |
| `pruebaMasa` | 5+ | Similar a longitud, valida las 5 formulas. |
| `pruebaTemperatura` | 5+ | Celsius <-> Fahrenheit <-> Kelvin cruzados. |

Ejecutarlas en NetBeans: clic derecho en `test/` -> **Run Tests**. O via Ant: `ant test`.

---

## 7. Requisitos previos

- **JDK 17** (probado con OpenJDK 17.0.18).
- **Payara Server 6** arriba. Asegurate de que el `asenv.conf` tenga `AS_JAVA` apuntando al JDK 17 (ver README del cliente web).
- **NetBeans 21+** con el plugin de Payara (servicio `pfv5ee8` registrado).

---

## 8. Como ejecutar

### Desde NetBeans
1. `File -> Open Project` -> selecciona `servidor_soap_java_conuni_gr06`.
2. `Properties -> Run` -> **Server: Payara Server 6**, **Java Platform: JDK 17**.
3. **Clean and Build**.
4. **Deploy** (o `Run` para abrir el navegador en el contexto raiz).
5. Verifica el WSDL:
   ```bash
   curl -s -o /dev/null -w "%{http_code}\n" \
     http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl
   # Debe devolver 200
   ```

### Desde terminal
```bash
# Compilar y empaquetar
ant clean dist

# Desplegar
/opt/payara6/bin/asadmin deploy dist/servidor_soap_java_conuni_gr06.war

# Verificar
/opt/payara6/bin/asadmin list-applications

# Deshacer el despliegue
/opt/payara6/bin/asadmin undeploy servidor_soap_java_conuni_gr06
```

---

## 9. Credenciales validas

```
usuario:    MONSTER
contrasena: MONSTER9
```
Para cambiar o agregar credenciales, edita `src/java/credenciales.txt` (una por linea, `usuario:contrasena`) y redespliega.

Nota de seguridad: las credenciales se guardan **en texto plano** y el servicio solo reconoce **una**. Es un diseno didactico, no apto para produccion. Puntos a mejorar documentados en la seccion 10.

---

## 10. Puntos a documentar mas adelante

- Diagrama de componentes del servidor (fachada JAX-WS + servicios + modelo + recurso credenciales).
- Diagrama de despliegue (Payara + JVM + puerto 8080 + clientes remotos).
- Diagrama de secuencia completo: `cliente -> HttpTransport -> CONUNI.java -> servicio -> modelo -> credenciales.txt -> respuesta`.
- Listado de cobertura de pruebas JUnit (casos por metodo, porcentaje).
- Captura del WSDL renderizado (`?wsdl` en el navegador) y de un sobre SOAP de ejemplo capturado con un cliente (SoapUI, Postman).
- Justificacion de la decision JAX-WS (vs. Spring-WS, vs. CXF).
- Mejoras de seguridad propuestas: hashing de contrasena (BCrypt), almacenamiento en base de datos, HTTPS/TLS, timeouts, limites de intentos, logging.
