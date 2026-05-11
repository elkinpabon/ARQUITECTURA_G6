---

**Tema**

WEB SERVICE SOAP CON ARCHIVO DE CREDENCIALES — CONVERSIÓN DE UNIDADES (CONUNI)

**Estudiantes**

Josue Marin

Mikaela Salcedo

Elkin Pabon

**Grupo**

GR06

**Tutor**

[Nombre del tutor]

**Fecha**

10/05/2026

---

**Tema**

WEB SERVICE SOAP CON ARCHIVO DE CREDENCIALES — CONVERSIÓN DE UNIDADES (CONUNI)

**Estudiantes**

Josue Marin

Mikaela Salcedo

Elkin Pabon

**Grupo**

GR06

**Tutor**

[Nombre del tutor]

**Fecha**

10/05/2026

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# WEB SERVICE SOAP CON ARCHIVO DE CREDENCIALES — CONVERSIÓN DE UNIDADES (CONUNI)

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# MANUAL TÉCNICO

## Web Service SOAP — Proyecto CONUNI (sin base de datos)

| Campo | Valor |
|---|---|
| **Tema** | Conversión de Unidades con Web Service SOAP en Java |
| **Tipo de servicio** | SOAP (Jakarta JAX-WS) |
| **Lenguaje servidor / clientes Java** | Java 17 |
| **Lenguaje cliente móvil** | Kotlin + Jetpack Compose |
| **Persistencia de credenciales** | Archivo plano `credenciales.txt` (sin base de datos) |
| **Formato del TXT** | `usuario:contrasena` (separador `:`) |
| **Servidor de aplicaciones** | GlassFish / Payara 5 (`pfv5ee8`) |
| **IDE servidor y clientes Java** | Apache NetBeans |
| **IDE cliente móvil** | Android Studio |
| **Materia** | Arquitectura de Software · Unidad 1 |
| **Universidad** | ESPE |
| **Grupo** | GR06 — Josue Marin · Mikaela Salcedo · Elkin Pabon |
| **Marca / dominio** | `monster` · `ec.edu.monster` |
| **Repositorio raíz** | `SOAP_JAVA_CONUNI_GR06/` |

> **Cómo usar este manual:** está dividido en capítulos. Cada capítulo de cliente o servidor describe arquitectura, estructura de paquetes, pantallas, código clave y pasos de creación/ejecución. En los lugares donde dice **`📷 [CAPTURA: ruta/al/archivo.png — descripción]`** debes pegar tu propia captura (PNG) en la ruta indicada dentro de `09. DOCUMENTACION/capturas/`. Los wireframes en bloques ` ``` ` son referencias visuales generadas a partir del código fuente.

---

## ÍNDICE

1. [Introducción](#1-introducción)
2. [Objetivos](#2-objetivos)
3. [Marco teórico](#3-marco-teórico)
4. [Arquitectura del sistema](#4-arquitectura-del-sistema)
5. [Requisitos previos del entorno](#5-requisitos-previos-del-entorno)
6. [Capítulo 1 — Servidor SOAP `CONUNI`](#capítulo-1--servidor-soap-conuni)
7. [Capítulo 2 — Persistencia de credenciales (archivo TXT)](#capítulo-2--persistencia-de-credenciales-archivo-txt)
8. [Capítulo 3 — Pruebas JUnit del servidor](#capítulo-3--pruebas-junit-del-servidor)
9. [Capítulo 4 — Cliente Consola](#capítulo-4--cliente-consola)
10. [Capítulo 5 — Cliente Escritorio (Swing)](#capítulo-5--cliente-escritorio-swing)
11. [Capítulo 6 — Cliente Web (JSP + Servlets)](#capítulo-6--cliente-web-jsp--servlets)
12. [Capítulo 7 — Cliente Móvil (Android + Kotlin)](#capítulo-7--cliente-móvil-android--kotlin)
13. [Capítulo 8 — Despliegue integrado y pruebas](#capítulo-8--despliegue-integrado-y-pruebas)
14. [Conclusiones](#conclusiones)
15. [Recomendaciones](#recomendaciones)
16. [Referencias](#referencias)
17. [Anexos](#anexos)

---

## 1. Introducción

En la actualidad las organizaciones requieren integrar sistemas escritos en lenguajes y plataformas distintas. Los **Servicios Web (Web Services)** dan respuesta a esta necesidad ofreciendo una vía de comunicación estándar, basada en protocolos abiertos (HTTP, XML), entre aplicaciones desplegadas sobre infraestructuras heterogéneas.

El presente manual documenta el proyecto **CONUNI** (Conversión de Unidades), implementado como un **Web Service SOAP** sobre **Java** que expone diecisiete operaciones agrupadas en autenticación, conversiones de longitud, conversiones de masa y conversiones de temperatura. El servicio se consume desde **cuatro clientes heterogéneos**:

- **Cliente de consola** (Java SE).
- **Cliente de escritorio** (Java Swing).
- **Cliente web** (JSP + Servlets).
- **Cliente móvil** (Android nativo en Kotlin + Jetpack Compose + ksoap2).

Todos los clientes comparten el mismo **WSDL** publicado por el servidor, demostrando la **interoperabilidad** característica de SOAP. La autenticación se realiza contra un **archivo de texto plano** (`credenciales.txt`) ubicado en el classpath del servidor, eliminando la dependencia de una base de datos.

---

## 2. Objetivos

### 2.1 Objetivo general

Desarrollar un proyecto **Java** con **Web Service SOAP** que exponga operaciones de conversión de unidades y autenticación contra un archivo plano de credenciales, consumido desde cuatro clientes (consola, escritorio, web y móvil).

### 2.2 Objetivos específicos

- Implementar el servicio web con **Jakarta JAX-WS** y desplegarlo sobre **GlassFish / Payara 5**.
- Diseñar un mecanismo de **autenticación basado en archivo TXT** dentro del classpath del servidor.
- Aplicar el patrón **MVC** en el servidor y en cada uno de los cuatro clientes.
- Generar pruebas **JUnit** que validen la lógica de conversión y autenticación.
- Consumir el WSDL desde Android con la librería **ksoap2-android**.
- Validar la **interoperabilidad** del WSDL entre clientes Java y Kotlin.

---

## 3. Marco teórico

### 3.1 Web Service

Un **Web Service** es un método de comunicación entre dos aplicaciones a través de una red. Permite el intercambio de información entre sistemas escritos en distintos lenguajes y desplegados sobre plataformas distintas.

### 3.2 SOAP — Simple Object Access Protocol

**SOAP** es un protocolo basado en XML para el intercambio de información entre aplicaciones. Define el formato de los mensajes (sobre o *envelope*), las reglas de codificación y la convención de llamadas a procedimientos remotos.

Características principales:

- **Extensibilidad** — soporta extensiones como seguridad y enrutamiento.
- **Neutralidad** — funciona sobre HTTP, SMTP, TCP o JMS.
- **Independencia** — admite cualquier modelo de programación.

### 3.3 WSDL — Web Services Description Language

**WSDL** es un lenguaje XML que describe el servicio web: qué operaciones expone, qué parámetros recibe y qué tipo de respuesta devuelve. Es el contrato público entre servidor y cliente.

URL del WSDL de este proyecto (con el servidor en ejecución local):

```
http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl
```

### 3.4 Patrón MVC

Tanto el servidor como los cuatro clientes se organizan bajo el patrón **Modelo–Vista–Controlador**:

| Capa | Servidor | Cliente |
|---|---|---|
| **Modelo** | DTO `Credencial` | DTO `Resultado` |
| **Vista** | (no aplica) | Consola / Swing / JSP / Compose |
| **Controlador** | `CONUNI` (`@WebService`) | `ControladorConsola`, `ControladorEscritorio`, `Servlet*` |
| **Servicio** | `Servicio*` (lógica + lectura TXT) | `Servicio*` (wrappers SOAP) |

### 3.5 Archivo plano como almacén de credenciales

En lugar de una base de datos relacional, este proyecto utiliza un archivo `credenciales.txt` con formato **`usuario:contrasena`**, ubicado dentro del classpath del servidor (`src/java/credenciales.txt`). Esta elección:

- Elimina dependencias externas (sin MySQL, sin JDBC, sin driver).
- Simplifica el despliegue (solo el WAR).
- Es adecuada para un volumen pequeño de credenciales y propósitos académicos.

---

## 4. Arquitectura del sistema

### 4.1 Vista general

```
┌─────────────────┐   ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│  CLI CONSOLA    │   │  CLI ESCRITORIO  │   │   CLI WEB        │   │   CLI MÓVIL      │
│  (Java SE)      │   │  (Java Swing)    │   │  (JSP + Servlet) │   │  (Android +      │
│                 │   │                  │   │                  │   │   Kotlin/Compose)│
└────────┬────────┘   └────────┬─────────┘   └────────┬─────────┘   └────────┬─────────┘
         │                     │                      │                      │
         │           SOAP / HTTP  (WSDL CONUNI)        │                      │
         └─────────────────────┼──────────────────────┼──────────────────────┘
                               │                      │
                               ▼                      ▼
                    ┌─────────────────────────────────────────┐
                    │  SERVIDOR  (servidor_soap_java_conuni)  │
                    │  Jakarta JAX-WS @WebService "CONUNI"    │
                    │  GlassFish/Payara · puerto 8080         │
                    │                                         │
                    │  controlador → servicio → modelo        │
                    └──────────────────┬──────────────────────┘
                                       │  lectura por classpath
                                       ▼
                              ┌──────────────────┐
                              │ credenciales.txt │
                              │ MONSTER:MONSTER9 │
                              └──────────────────┘
```

📷 **[CAPTURA: `capturas/07_integracion/01_arquitectura_diagrama.png` — Diagrama de arquitectura general en alta resolución (puedes exportar desde `01. UML/03. DIAGRAMAS UML/05. ARQUITECTURA`).]**

### 4.2 Estructura de carpetas del proyecto

```
SOAP_JAVA_CONUNI_GR06/
├── 01. UML/
│   ├── 01. ERS                       Especificación de Requerimientos
│   ├── 02. ECUD                      Especificación de Casos de Uso
│   └── 03. DIAGRAMAS UML/
│       ├── 01. CASOS_USOS
│       ├── 02. ACTIVIDAD
│       ├── 03. SECUENCIA
│       ├── 04. CLASES
│       └── 05. ARQUITECTURA
├── 04. CLICONSOLA/                   cliente_consola_soap_java_conuni_gr06
├── 05. CLIESCRITORIO/                cliente_escritorio_soap_java_conuni_gr06
├── 06. CLIWEB/                       cliente_web_soap_java_conuni_gr06
├── 07. CLIMÓVIL/                     cliente_movil_soap_java_conuni_gr06
├── 08. SERVIDOR/                     servidor_soap_java_conuni_gr06
└── 09. DOCUMENTACION/                este manual + capturas/
```

📷 **[CAPTURA: `capturas/07_integracion/02_estructura_carpetas.png` — Finder o `tree` mostrando las carpetas raíz del proyecto.]**

---

## 5. Requisitos previos del entorno

| Componente | Versión / Detalle | Verificación |
|---|---|---|
| macOS | Apple Silicon (Darwin 25.x) | `uname -a` |
| Java | 17 (OpenJDK Homebrew) | `java -version` |
| GlassFish / Payara | 5 (`pfv5ee8`) | NetBeans → Services → Servers |
| Apache NetBeans | Última estable | App `/Applications/Apache NetBeans.app` |
| Android Studio | Compatible con SDK 34 | App `Android Studio.app` |
| JUnit | 4.13.2 (incluido en `lib/`) | NetBeans → Test Libraries |
| Librería SOAP móvil | `ksoap2-android:3.6.4` | `build.gradle.kts` del móvil |

📷 **[CAPTURA: `capturas/01_servidor/00_entorno_versions.png` — Salida de `java -version` y NetBeans abierto mostrando GlassFish/Payara configurado.]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 1 — Servidor SOAP `CONUNI`

## 1.1 Descripción

El servidor expone un único `@WebService` llamado **`CONUNI`** que actúa como fachada y delega cada llamada a un servicio interno especializado por categoría. Publica diecisiete operaciones: una de autenticación (contra `credenciales.txt`) y dieciséis de conversión.

## 1.2 Stack tecnológico del servidor

| Componente | Detalle |
|---|---|
| Lenguaje | Java 17 |
| Build | Ant + `nbproject/` (NetBeans Web Application) |
| API SOAP | Jakarta JAX-WS (`jakarta.jws.WebService`) |
| `web.xml` | Jakarta EE |
| `glassfish-web.xml` | Configuración de despliegue |
| Servidor | GlassFish / Payara 5 (target `pfv5ee8`) |
| Persistencia | Archivo plano `credenciales.txt` en classpath |
| Pruebas | JUnit 4.13.2 (`lib/junit-4.13.2.jar`, `lib/hamcrest-core-1.3.jar`) |
| `serviceName` WSDL | `CONUNI` |
| `targetNamespace` (default) | `http://controlador.monster.edu.ec/` |

## 1.3 Estructura real de carpetas y paquetes

```
servidor_soap_java_conuni_gr06/
├── build.xml
├── lib/
│   ├── jakarta.jws-api.jar
│   ├── junit-4.13.2.jar
│   └── hamcrest-core-1.3.jar
├── nbproject/                        (config NetBeans/Ant)
│   ├── build-impl.xml
│   ├── jax-ws.xml
│   ├── project.properties
│   └── project.xml
├── src/
│   ├── conf/
│   │   └── MANIFEST.MF
│   └── java/
│       ├── credenciales.txt          ← archivo de credenciales
│       └── ec/edu/monster/
│           ├── controlador/
│           │   └── CONUNI.java       (@WebService fachada)
│           ├── modelo/
│           │   └── Credencial.java   (DTO usuario+contrasena)
│           └── servicio/
│               ├── ServicioAutenticacion.java
│               ├── ServicioLongitud.java
│               ├── ServicioMasa.java
│               └── ServicioTemperatura.java
├── test/
│   └── ec/edu/monster/prueba/
│       ├── pruebaAutenticacion.java
│       ├── pruebaLongitud.java
│       ├── pruebaMasa.java
│       └── pruebaTemperatura.java
└── web/
    └── WEB-INF/
        ├── glassfish-web.xml
        └── web.xml
```

| Paquete | Clase | Responsabilidad |
|---|---|---|
| `controlador` | `CONUNI` | Fachada `@WebService`, expone los `@WebMethod` |
| `servicio` | `ServicioAutenticacion` | Lee `credenciales.txt` y valida usuario/contraseña |
| `servicio` | `ServicioLongitud` | Lógica pura de conversiones de longitud |
| `servicio` | `ServicioMasa` | Lógica pura de conversiones de masa |
| `servicio` | `ServicioTemperatura` | Lógica pura de conversiones de temperatura |
| `modelo` | `Credencial` | DTO inmutable de credencial (usuario + contraseña) |
| (recurso) | `credenciales.txt` | Lista blanca de credenciales (en classpath) |
| `prueba` (test) | `pruebaAutenticacion`, `pruebaLongitud`, `pruebaMasa`, `pruebaTemperatura` | Pruebas JUnit |

📷 **[CAPTURA: `capturas/01_servidor/01_estructura_paquetes.png` — NetBeans → árbol del proyecto mostrando los paquetes `controlador`, `servicio`, `modelo` y la ruta de `credenciales.txt`.]**
📷 **[CAPTURA: `capturas/01_servidor/02_formulacion_paquetes.png` — Diálogo "New Package" de NetBeans usado para crear cada paquete.]**

## 1.4 Diagrama de clases del servidor

```
                          ┌────────────────────────────────┐
                          │       CONUNI (@WebService)     │
                          │  + iniciarSesion()             │
                          │  + metrosAPies() … (16 conv.)  │
                          └────────────────┬───────────────┘
                                           │ usa
        ┌──────────────────────────────────┼───────────────────────────────┐
        ▼                                  ▼                               ▼
 ┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
 │ ServicioAutenticacion│  │  ServicioLongitud    │  │  ServicioTemperatura │
 │ + autenticar()       │  │  + metrosAPies()     │  │  + celsiusAFahr.()   │
 │ - leerCredencial-    │  │  + kilometrosAMillas │  │  + fahrACelsius()    │
 │   Archivo()          │  │  + cmAPulgadas()     │  │  + celsiusAKelvin()  │
 └──────────┬───────────┘  │  + yardasAMetros()   │  │  + kelvinACelsius()  │
            │              │  + mmAPulgadas()     │  │  + fahrAKelvin()     │
            │              └──────────────────────┘  │  + kelvinAFahr.()    │
            ▼                                        └──────────────────────┘
       ┌──────────┐        ┌──────────────────────┐
       │credencial│        │   ServicioMasa       │
       │   .txt   │        │  + kilogramosALibras │
       └──────────┘        │  + gramosAOnzas()    │
                           │  + toneladasAKg()    │
                           │  + librasAOnzas()    │
                           │  + miligramosAGramos │
                           └──────────────────────┘

                          ┌────────────────────────────────┐
                          │       Credencial (DTO)         │
                          │  - usuario, contrasena         │
                          │  + coincide(otra) : boolean    │
                          └────────────────────────────────┘
```

📷 **[CAPTURA: `capturas/01_servidor/03_diagrama_clases.png` — Diagrama de clases (puedes exportar desde `01. UML/03. DIAGRAMAS UML/04. CLASES`).]**

## 1.5 Operaciones publicadas (las 17)

| # | Operación | Categoría | Entrada | Salida |
|---|---|---|---|---|
| 1 | `iniciarSesion` | Sesión | `usuario:String, contrasena:String` | `boolean` |
| 2 | `metrosAPies` | Longitud | `metros:double` | `double` |
| 3 | `kilometrosAMillas` | Longitud | `kilometros:double` | `double` |
| 4 | `centimetrosAPulgadas` | Longitud | `centimetros:double` | `double` |
| 5 | `yardasAMetros` | Longitud | `yardas:double` | `double` |
| 6 | `milimetrosAPulgadas` | Longitud | `milimetros:double` | `double` |
| 7 | `kilogramosALibras` | Masa | `kilogramos:double` | `double` |
| 8 | `gramosAOnzas` | Masa | `gramos:double` | `double` |
| 9 | `toneladasAKilogramos` | Masa | `toneladas:double` | `double` |
| 10 | `librasAOnzas` | Masa | `libras:double` | `double` |
| 11 | `miligramosAGramos` | Masa | `miligramos:double` | `double` |
| 12 | `celsiusAFahrenheit` | Temperatura | `celsius:double` | `double` |
| 13 | `fahrenheitACelsius` | Temperatura | `fahrenheit:double` | `double` |
| 14 | `celsiusAKelvin` | Temperatura | `celsius:double` | `double` |
| 15 | `kelvinACelsius` | Temperatura | `kelvin:double` | `double` |
| 16 | `fahrenheitAKelvin` | Temperatura | `fahrenheit:double` | `double` |
| 17 | `kelvinAFahrenheit` | Temperatura | `kelvin:double` | `double` |

## 1.6 Código real del servidor

### 1.6.1 Fachada `CONUNI`

```java
package ec.edu.monster.controlador;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import ec.edu.monster.servicio.ServicioAutenticacion;
import ec.edu.monster.servicio.ServicioLongitud;
import ec.edu.monster.servicio.ServicioMasa;
import ec.edu.monster.servicio.ServicioTemperatura;

@WebService(serviceName = "CONUNI")
public class CONUNI {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud      servicioLongitud      = new ServicioLongitud();
    private final ServicioMasa          servicioMasa          = new ServicioMasa();
    private final ServicioTemperatura   servicioTemperatura   = new ServicioTemperatura();

    // ===== Autenticacion =====
    @WebMethod
    public boolean iniciarSesion(@WebParam(name = "usuario") String usuario,
                                 @WebParam(name = "contrasena") String contrasena) {
        return servicioAutenticacion.autenticar(usuario, contrasena);
    }

    // ===== Longitud =====
    @WebMethod public double metrosAPies(@WebParam(name = "metros") double m)
        { return servicioLongitud.metrosAPies(m); }
    // ... resto de los 16 @WebMethod siguen el mismo patrón delegando al Servicio<Categoria>
}
```

📷 **[CAPTURA: `capturas/01_servidor/04_codigo_conuni.png` — NetBeans mostrando `CONUNI.java` en el editor.]**

### 1.6.2 `Credencial` (DTO inmutable)

```java
package ec.edu.monster.modelo;

public class Credencial {

    private final String usuario;
    private final String contrasena;

    public Credencial(String usuario, String contrasena) {
        this.usuario    = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario()    { return usuario; }
    public String getContrasena() { return contrasena; }

    public boolean coincide(Credencial otra) {
        if (otra == null) return false;
        return usuario != null && contrasena != null
            && usuario.equals(otra.usuario)
            && contrasena.equals(otra.contrasena);
    }
}
```

### 1.6.3 `ServicioLongitud` (lógica pura)

```java
package ec.edu.monster.servicio;

public class ServicioLongitud {
    public double metrosAPies(double metros)             { return metros * 3.28084; }
    public double kilometrosAMillas(double kilometros)   { return kilometros * 0.621371; }
    public double centimetrosAPulgadas(double cm)        { return cm / 2.54; }
    public double yardasAMetros(double yardas)           { return yardas / 1.09361; }
    public double milimetrosAPulgadas(double milimetros) { return milimetros * 0.0393701; }
}
```

### 1.6.4 `ServicioMasa`

```java
package ec.edu.monster.servicio;

public class ServicioMasa {
    public double kilogramosALibras(double kg)   { return kg * 2.20462; }
    public double gramosAOnzas(double g)         { return g * 0.035274; }
    public double toneladasAKilogramos(double t) { return t * 1000.0; }
    public double librasAOnzas(double libras)    { return libras * 16.0; }
    public double miligramosAGramos(double mg)   { return mg / 1000.0; }
}
```

### 1.6.5 `ServicioTemperatura`

```java
package ec.edu.monster.servicio;

public class ServicioTemperatura {
    public double celsiusAFahrenheit(double c) { return (c * 9.0 / 5.0) + 32.0; }
    public double fahrenheitACelsius(double f) { return (f - 32.0) * 5.0 / 9.0; }
    public double celsiusAKelvin(double c)     { return c + 273.15; }
    public double kelvinACelsius(double k)     { return k - 273.15; }
    public double fahrenheitAKelvin(double f)  { return (f - 32.0) * 5.0 / 9.0 + 273.15; }
    public double kelvinAFahrenheit(double k)  { return (k - 273.15) * 9.0 / 5.0 + 32.0; }
}
```

📷 **[CAPTURA: `capturas/01_servidor/05_codigo_servicios.png` — NetBeans mostrando los 4 servicios en pestañas o el árbol del paquete `servicio`.]**

## 1.7 Pasos para crear / abrir el servidor en NetBeans

1. Abrir NetBeans → **File → Open Project** → seleccionar `08. SERVIDOR/servidor_soap_java_conuni_gr06`.
2. Verificar en **Services → Servers** que esté configurado **GlassFish 5 / Payara 5** (`pfv5ee8`).
3. Verificar que `credenciales.txt` esté en `src/java/` (lo coloca el classpath en `WEB-INF/classes/credenciales.txt`).
4. Click derecho sobre el proyecto → **Build** → luego **Deploy**.
5. Abrir navegador en `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl`.

📷 **[CAPTURA: `capturas/01_servidor/06_netbeans_proyecto_abierto.png` — NetBeans con el proyecto del servidor abierto.]**
📷 **[CAPTURA: `capturas/01_servidor/07_glassfish_servers.png` — Pestaña Services → Servers mostrando GlassFish/Payara.]**
📷 **[CAPTURA: `capturas/01_servidor/08_deploy_exitoso.png` — Output de NetBeans mostrando "BUILD SUCCESSFUL" + deploy a GlassFish.]**
📷 **[CAPTURA: `capturas/01_servidor/09_wsdl_navegador.png` — Navegador mostrando el XML del WSDL en `?wsdl`.]**
📷 **[CAPTURA: `capturas/01_servidor/10_tester_glassfish.png` — Tester de GlassFish probando `iniciarSesion` con `MONSTER / MONSTER9` → `true`.]**

## 1.8 Endpoints útiles

| Recurso | URL |
|---|---|
| WSDL | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl` |
| Endpoint SOAP | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI` |
| Tester GlassFish | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?Tester` |

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 2 — Persistencia de credenciales (archivo TXT)

## 2.1 Descripción

En lugar de una base de datos relacional, el servidor valida las credenciales contra un **archivo de texto plano** llamado `credenciales.txt`, ubicado en `src/java/credenciales.txt` y cargado automáticamente por el classpath del servidor cuando se compila el WAR. Cuando un cliente invoca `iniciarSesion`, la clase `ServicioAutenticacion` lee el archivo línea por línea hasta encontrar la primera credencial válida y la compara con la entrante.

## 2.2 Formato del archivo `credenciales.txt`

```txt
MONSTER:MONSTER9
```

| Característica | Valor |
|---|---|
| Codificación | UTF-8 |
| Separador entre usuario y contraseña | `:` (dos puntos) |
| Comentarios | líneas que empiezan con `#` (se ignoran) |
| Líneas vacías | se ignoran |
| Ubicación en código fuente | `src/java/credenciales.txt` |
| Ubicación en runtime | `WEB-INF/classes/credenciales.txt` (la coloca NetBeans/Ant al empaquetar el WAR) |
| Acceso en runtime | `getClass().getClassLoader().getResourceAsStream("credenciales.txt")` |

📷 **[CAPTURA: `capturas/02_txt/01_archivo_credenciales.png` — Archivo `credenciales.txt` abierto en NetBeans mostrando su contenido.]**
📷 **[CAPTURA: `capturas/02_txt/02_ubicacion_classpath.png` — Árbol del proyecto en NetBeans mostrando la ubicación de `credenciales.txt` dentro de `src/java/`.]**

## 2.3 Código real del `ServicioAutenticacion`

```java
package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Credencial;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ServicioAutenticacion {

    private static final String RECURSO_CREDENCIALES = "credenciales.txt";

    public boolean autenticar(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return false;
        }
        Credencial ingresada  = new Credencial(usuario, contrasena);
        Credencial almacenada = leerCredencialArchivo();
        return almacenada != null && almacenada.coincide(ingresada);
    }

    private Credencial leerCredencialArchivo() {
        try (InputStream entrada = getClass().getClassLoader()
                                             .getResourceAsStream(RECURSO_CREDENCIALES)) {
            if (entrada == null) return null;

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(entrada, StandardCharsets.UTF_8))) {

                String linea;
                while ((linea = lector.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty() || linea.startsWith("#")) continue;

                    String[] partes = linea.split(":", 2);
                    if (partes.length == 2) {
                        return new Credencial(partes[0], partes[1]);
                    }
                }
                return null;
            }
        } catch (Exception excepcion) {
            return null;
        }
    }
}
```

📷 **[CAPTURA: `capturas/02_txt/03_codigo_servicio_autenticacion.png` — NetBeans mostrando `ServicioAutenticacion.java`.]**

## 2.4 Flujo de autenticación

```
Cliente.iniciarSesion(usuario, contrasena)
        │
        ▼  SOAP request (HTTP/XML)
CONUNI.iniciarSesion()  ──▶  ServicioAutenticacion.autenticar()
                                     │
                                     ▼
                            leerCredencialArchivo()
                                     │  classpath
                                     ▼
                        credenciales.txt → Credencial
                                     │
                                     ▼
                       Credencial.coincide(ingresada)
                                     │
        ◀────────── SOAP response (boolean) ──────────
```

## 2.5 Verificación rápida

Una vez desplegado el servidor:

1. Abrir el Tester de GlassFish: `?Tester`.
2. Entrar a `iniciarSesion`.
3. Probar con `MONSTER / MONSTER9` → debe devolver `true`.
4. Probar con `monster / monster9` (minúsculas) → debe devolver `false` (sensible a mayúsculas).
5. Probar con `MONSTER / wrong` → debe devolver `false`.

📷 **[CAPTURA: `capturas/02_txt/04_test_login_ok.png` — Tester GlassFish con login válido devolviendo `true`.]**
📷 **[CAPTURA: `capturas/02_txt/05_test_login_fail.png` — Tester GlassFish con login inválido devolviendo `false`.]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 3 — Pruebas JUnit del servidor

## 3.1 Descripción

El proyecto incluye una batería de pruebas **JUnit 4** que verifican tanto la autenticación contra el archivo TXT como cada una de las dieciséis conversiones de unidades. Las pruebas viven en `test/ec/edu/monster/prueba/`.

## 3.2 Estructura de pruebas

| Archivo | Cobertura |
|---|---|
| `pruebaAutenticacion.java` | Login exitoso, usuario incorrecto, contraseña incorrecta, vacíos, nulos, sensibilidad a mayúsculas |
| `pruebaLongitud.java` | 5 conversiones de longitud |
| `pruebaMasa.java` | 5 conversiones de masa |
| `pruebaTemperatura.java` | 6 conversiones de temperatura |

## 3.3 Código de `pruebaAutenticacion`

```java
package ec.edu.monster.prueba;

import ec.edu.monster.servicio.ServicioAutenticacion;
import org.junit.Test;
import static org.junit.Assert.*;

public class pruebaAutenticacion {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Test
    public void pruebaAutenticacionExitosa() {
        assertTrue(servicioAutenticacion.autenticar("MONSTER", "MONSTER9"));
    }

    @Test
    public void pruebaUsuarioIncorrecto() {
        assertFalse(servicioAutenticacion.autenticar("USUARIO", "MONSTER9"));
    }

    @Test
    public void pruebaContrasenaIncorrecta() {
        assertFalse(servicioAutenticacion.autenticar("MONSTER", "INCORRECTA"));
    }

    @Test
    public void pruebaCredencialesVacias() {
        assertFalse(servicioAutenticacion.autenticar("", ""));
    }

    @Test
    public void pruebaCredencialesNulas() {
        assertFalse(servicioAutenticacion.autenticar(null, null));
    }

    @Test
    public void pruebaSensibleAMayusculas() {
        assertFalse(servicioAutenticacion.autenticar("monster", "monster9"));
    }
}
```

📷 **[CAPTURA: `capturas/03_pruebas/01_codigo_prueba_autenticacion.png` — NetBeans mostrando `pruebaAutenticacion.java`.]**

## 3.4 Ejecución de pruebas en NetBeans

1. Click derecho sobre el proyecto → **Test**.
2. Aparece la pestaña **Test Results** con los resultados por clase.
3. Verde = OK, Rojo = falla.

📷 **[CAPTURA: `capturas/03_pruebas/02_test_results.png` — Panel "Test Results" de NetBeans con todas las pruebas en verde.]**
📷 **[CAPTURA: `capturas/03_pruebas/03_test_individual.png` — Resultado individual de una clase de prueba (`pruebaTemperatura`, por ejemplo).]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 4 — Cliente Consola

## 4.1 Descripción

Aplicación **Java SE** que interactúa por terminal. Permite autenticarse y luego ejecutar cualquiera de las dieciséis conversiones del servicio.

## 4.2 Stack

| Componente | Detalle |
|---|---|
| Tipo | Java Application (consola) |
| IDE | Apache NetBeans |
| Consumo SOAP | Cliente HTTP manual (`HttpURLConnection`) construyendo el sobre XML |
| Punto de entrada | `ec.edu.monster.Aplicacion` |
| Entrada/salida | `System.in` / `System.out` (`Scanner`, `Console`) |

## 4.3 Estructura de paquetes

| Paquete | Clase | Rol |
|---|---|---|
| `ec.edu.monster` | `Aplicacion` | `main()` — arranque |
| `controlador` | `ControladorConsola` | Orquesta `vista` ↔ `modelo` |
| `vista` | `VistaConsola` | Menús, lectura de teclado, impresión |
| `modelo` | `ClienteSoap` | Invocador reutilizable del WSDL |
| `modelo` | `ServicioAutenticacion` | Wrapper SOAP de `iniciarSesion` |
| `modelo` | `ServicioLongitud` | Wrapper SOAP de longitud |
| `modelo` | `ServicioMasa` | Wrapper SOAP de masa |
| `modelo` | `ServicioTemperatura` | Wrapper SOAP de temperatura |
| `modelo` | `Resultado` | DTO de respuesta `{exito, valor, mensaje}` |

📷 **[CAPTURA: `capturas/04_consola/01_estructura_paquetes.png` — Árbol del proyecto en NetBeans.]**
📷 **[CAPTURA: `capturas/04_consola/02_formulacion_paquetes.png` — Diálogo "New Package" usado al crear los paquetes MVC.]**

## 4.4 Wireframes ASCII

```
==================================================
         CLIENTE CONSOLA CONUNI - SOAP
==================================================

--- Iniciar Sesion (intento 1 de 3) ---
Usuario: MONSTER
Contrasena: ********
[OK] Bienvenido, MONSTER.

--- Menu Principal (usuario: MONSTER) ---
1. Conversiones de Longitud
2. Conversiones de Masa
3. Conversiones de Temperatura
0. Cerrar Sesion
Selecciona una opcion [0-3]: 1

--- Conversiones de Longitud ---
1. Metros a Pies
2. Kilometros a Millas
3. Centimetros a Pulgadas
4. Yardas a Metros
5. Milimetros a Pulgadas
0. Volver
Selecciona una opcion [0-5]: 1
Ingresa el valor a convertir: 10
[OK] Resultado: 32.808398950131235
```

## 4.5 Código clave

### 4.5.1 `ControladorConsola` (extracto)

```java
public class ControladorConsola {

    private static final int MAX_INTENTOS_LOGIN = 3;

    private final VistaConsola vista = new VistaConsola();
    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioLongitud servicioLongitud = new ServicioLongitud();
    private final ServicioMasa servicioMasa = new ServicioMasa();
    private final ServicioTemperatura servicioTemperatura = new ServicioTemperatura();

    public void ejecutar() {
        vista.mostrarEncabezado();
        if (!autenticar()) {
            vista.mostrarError("Se agotaron los intentos de inicio de sesion.");
            return;
        }
        bucleMenuPrincipal();
        vista.mostrarDespedida();
    }

    private boolean autenticar() {
        for (int intento = 1; intento <= MAX_INTENTOS_LOGIN; intento++) {
            String usuario    = vista.leerTexto("Usuario");
            String contrasena = vista.leerContrasena("Contrasena");
            if (servicioAutenticacion.iniciarSesion(usuario, contrasena)) {
                vista.mostrarExito("Bienvenido, " + usuario + ".");
                return true;
            }
            vista.mostrarError("Credenciales invalidas.");
        }
        return false;
    }
}
```

### 4.5.2 `ClienteSoap` (consumo SOAP manual)

```java
public class ClienteSoap {

    private static final String URL_SERVICIO =
            "http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI";
    private static final String ESPACIO_NOMBRES =
            "http://controlador.monster.edu.ec/";

    public String invocar(String op, Map<String, String> parametros) throws Exception {
        String sobreSoap = construirSobre(op, parametros);
        String respuesta = enviarPeticion(sobreSoap);
        return extraerValorRetorno(respuesta);
    }

    private String construirSobre(String op, Map<String, String> params) {
        StringBuilder cuerpo = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            cuerpo.append("<").append(e.getKey()).append(">")
                  .append(e.getValue())
                  .append("</").append(e.getKey()).append(">");
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
             + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
             +   " xmlns:con=\"" + ESPACIO_NOMBRES + "\">"
             +   "<soapenv:Body><con:" + op + ">" + cuerpo
             +   "</con:" + op + "></soapenv:Body></soapenv:Envelope>";
    }
}
```

📷 **[CAPTURA: `capturas/04_consola/03_codigo_controlador.png` — NetBeans editor con `ControladorConsola.java`.]**
📷 **[CAPTURA: `capturas/04_consola/04_codigo_cliente_soap.png` — NetBeans editor con `ClienteSoap.java`.]**

## 4.6 Pasos para ejecutar

1. Abrir NetBeans → **Open Project** → `04. CLICONSOLA/cliente_consola_soap_java_conuni_gr06`.
2. Verificar que `URL_SERVICIO` en `ClienteSoap.java` apunte al servidor en ejecución.
3. **Run Project** (F6) → clase principal `ec.edu.monster.Aplicacion`.
4. Iniciar sesión con `MONSTER / MONSTER9`.

📷 **[CAPTURA: `capturas/04_consola/05_proyecto_netbeans.png` — Proyecto abierto en NetBeans.]**
📷 **[CAPTURA: `capturas/04_consola/06_login.png` — Pantalla de login en terminal.]**
📷 **[CAPTURA: `capturas/04_consola/07_menu_principal.png` — Menú principal.]**
📷 **[CAPTURA: `capturas/04_consola/08_conversion_longitud.png` — Ejemplo: metros a pies.]**
📷 **[CAPTURA: `capturas/04_consola/09_conversion_masa.png` — Ejemplo: kilogramos a libras.]**
📷 **[CAPTURA: `capturas/04_consola/10_conversion_temperatura.png` — Ejemplo: celsius a fahrenheit.]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 5 — Cliente Escritorio (Swing)

## 5.1 Descripción

Aplicación **Java Swing** con tres pantallas tipo *card*: **Login → Menú → Conversión**. UI editada en NetBeans con archivos Matisse `.form`.

## 5.2 Stack

| Componente | Detalle |
|---|---|
| Tipo | Java Desktop Application |
| Framework UI | Swing (`javax.swing.*`) + Matisse (`.form`) |
| IDE | Apache NetBeans |
| Look & Feel | Paleta personalizada (clase `Paleta`) |
| Consumo SOAP | Cliente HTTP manual (idéntico al de consola) |

## 5.3 Estructura de paquetes

| Paquete | Clase | Rol |
|---|---|---|
| `ec.edu.monster` | `Aplicacion` | `main()` y bootstrapping de la ventana |
| `controlador` | `ControladorEscritorio` | Eventos UI ↔ servicios SOAP |
| `vista` | `VentanaPrincipal` | `JFrame` raíz; orquesta paneles con CardLayout |
| `vista` | `PanelLogin` | Pantalla de inicio de sesión |
| `vista` | `PanelMenu` | Pantalla de menú (3 tarjetas) |
| `vista` | `PanelConversion` | Pantalla genérica de conversión |
| `vista` | `Paleta` | Constantes de color, tipografía, estilos |
| `modelo` | `ClienteSoap` + `Servicio*` + `Resultado` | Idénticos al cliente consola |

📷 **[CAPTURA: `capturas/05_escritorio/01_estructura_paquetes.png` — Árbol del proyecto en NetBeans.]**
📷 **[CAPTURA: `capturas/05_escritorio/02_formulacion_paquetes.png` — Diálogo "New Package".]**

## 5.4 Wireframes ASCII

### `PanelLogin`

```
┌──────────────────────────────────────────────────────────────┐
│  [Imagen lateral]    │   🟦 [Logo monster]                   │
│                      │   Cliente Escritorio CONUNI           │
│                      │                                       │
│                      │   Iniciar Sesion                      │
│                      │   Usuario:    [________________]      │
│                      │   Contrasena: [********][Mostrar]     │
│                      │   [   INGRESAR   ]                    │
└──────────────────────────────────────────────────────────────┘
```

### `PanelMenu`

```
┌──────────────────────────────────────────────────────────────┐
│ 🟦 logo  Cliente Escritorio CONUNI    Bienvenido, MONSTER    │
│                                       [Cerrar Sesión]        │
├──────────────────────────────────────────────────────────────┤
│   ┌──────────┐    ┌──────────┐    ┌──────────────┐           │
│   │ Longitud │    │   Masa   │    │ Temperatura  │           │
│   └──────────┘    └──────────┘    └──────────────┘           │
└──────────────────────────────────────────────────────────────┘
```

### `PanelConversion`

```
┌──────────────────────────────────────────────────────────────┐
│ 🟦 logo   Conversion de <Categoria>      [Volver al menú]    │
├──────────────────────────────────────────────────────────────┤
│   Tipo de conversión: [ Metros a Pies         ▼ ]            │
│   Valor:              [ 10                       ]           │
│              [   CONVERTIR   ]                               │
│   Resultado: 32.8084                                         │
└──────────────────────────────────────────────────────────────┘
```

## 5.5 Código clave

### 5.5.1 `PanelLogin` — conexión del botón con el controlador

```java
public class PanelLogin extends javax.swing.JPanel {

    private BiConsumer<String, String> accionLogin;

    public PanelLogin() {
        initComponents();
        configurarVista();
        conectarEventos();
    }

    private void conectarEventos() {
        botonIngresar.addActionListener(e -> dispararLogin());
        campoContrasena.addActionListener(e -> dispararLogin());
    }

    private void dispararLogin() {
        if (accionLogin != null) {
            accionLogin.accept(
                campoUsuario.getText().trim(),
                new String(campoContrasena.getPassword()));
        }
    }

    public void setAccionLogin(BiConsumer<String, String> accion) {
        this.accionLogin = accion;
    }
}
```

### 5.5.2 `PanelMenu` — tarjetas de categoría

```java
public class PanelMenu extends javax.swing.JPanel {

    private Consumer<String> accionCategoria;
    private Runnable accionCerrarSesion;

    private void conectarEventos() {
        btnLongitud.addActionListener(e     -> notificar("longitud"));
        btnMasa.addActionListener(e         -> notificar("masa"));
        btnTemperatura.addActionListener(e  -> notificar("temperatura"));
        btnCerrarSesion.addActionListener(e -> {
            if (accionCerrarSesion != null) accionCerrarSesion.run();
        });
    }

    private void notificar(String categoria) {
        if (accionCategoria != null) accionCategoria.accept(categoria);
    }
}
```

📷 **[CAPTURA: `capturas/05_escritorio/03_codigo_panel_login.png` — NetBeans con `PanelLogin.java` en vista Source.]**
📷 **[CAPTURA: `capturas/05_escritorio/04_diseno_matisse.png` — NetBeans con `PanelLogin.form` en vista Design (Matisse).]**

## 5.6 Flujo de navegación

```
VentanaPrincipal (CardLayout)
   │
   ├── PanelLogin  ─[login]─▶ ControladorEscritorio.autenticar()
   │                              ▼ CONUNI.iniciarSesion() = true
   │                          PanelMenu
   │
   └── PanelMenu
         ├─ [Longitud]    ─▶ PanelConversion(categoria="longitud")
         ├─ [Masa]        ─▶ PanelConversion(categoria="masa")
         ├─ [Temperatura] ─▶ PanelConversion(categoria="temperatura")
         └─ [Cerrar Sesión] ─▶ PanelLogin
```

## 5.7 Pasos para ejecutar

1. Abrir NetBeans → **Open Project** → `05. CLIESCRITORIO/cliente_escritorio_soap_java_conuni_gr06`.
2. Verificar la URL del servidor en `ClienteSoap.java`.
3. Para editar pantallas: doble click en un panel (`.java`) → pestaña **Design** (Matisse).
4. **Run Project** (F6).
5. Login con `MONSTER / MONSTER9` → seleccionar categoría → convertir.

📷 **[CAPTURA: `capturas/05_escritorio/05_proyecto_netbeans.png` — Proyecto abierto en NetBeans.]**
📷 **[CAPTURA: `capturas/05_escritorio/06_panel_login.png` — Pantalla de login en ejecución.]**
📷 **[CAPTURA: `capturas/05_escritorio/07_panel_menu.png` — Pantalla de menú con las tres tarjetas.]**
📷 **[CAPTURA: `capturas/05_escritorio/08_panel_conversion_longitud.png` — `PanelConversion` con longitud.]**
📷 **[CAPTURA: `capturas/05_escritorio/09_panel_conversion_masa.png` — `PanelConversion` con masa.]**
📷 **[CAPTURA: `capturas/05_escritorio/10_panel_conversion_temperatura.png` — `PanelConversion` con temperatura.]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 6 — Cliente Web (JSP + Servlets)

## 6.1 Descripción

Aplicación web Java desplegada en **GlassFish / Payara**. Usa **JSP** para vistas, **Servlets** como controladores y un **Filter** que protege rutas que requieren sesión iniciada.

## 6.2 Stack

| Componente | Detalle |
|---|---|
| Tipo | Java Web Application |
| Vistas | JSP en `web/vista/` |
| Controladores | Servlets en `controlador/` |
| Sesión | `HttpSession` + `FiltroSesion` |
| Estilos | `web/css/estilo.css` |
| Imágenes | `web/img/` |
| Consumo SOAP | Cliente HTTP manual (idéntico al de consola/escritorio) |

## 6.3 Estructura

### Vistas JSP

| JSP | Función |
|---|---|
| `index.jsp` | Redirige a `iniciarSesion.jsp` |
| `vista/iniciarSesion.jsp` | Formulario de login |
| `vista/menu.jsp` | Menú con 3 enlaces (longitud, masa, temperatura) |
| `vista/longitud.jsp` | Conversiones de longitud |
| `vista/masa.jsp` | Conversiones de masa |
| `vista/temperatura.jsp` | Conversiones de temperatura |

### Servlets

| Servlet | URL pattern | Función |
|---|---|---|
| `ServletAutenticacion` | `/autenticacion` | Login → valida vía SOAP → crea sesión |
| `ServletLongitud` | `/longitud` | Conversiones de longitud |
| `ServletMasa` | `/masa` | Conversiones de masa |
| `ServletTemperatura` | `/temperatura` | Conversiones de temperatura |
| `ServletCerrarSesion` | `/cerrarSesion` | Invalida sesión |

### Filtros y modelo

| Paquete | Clase | Rol |
|---|---|---|
| `util` | `FiltroSesion` | `Filter` que valida `session.getAttribute("usuario")` |
| `modelo` | `ClienteSoap`, `Servicio*`, `Resultado` | Igual estructura que los otros clientes Java |

📷 **[CAPTURA: `capturas/06_web/01_estructura_paquetes.png` — Árbol del proyecto web en NetBeans.]**
📷 **[CAPTURA: `capturas/06_web/02_formulacion_paquetes.png` — Diálogo de creación de paquetes.]**

## 6.4 Wireframes ASCII

### `iniciarSesion.jsp`

```
┌──────────────────────────────────────────────────────────────┐
│ [imagen lateral]    │  🟦 [Logo]  Cliente Web CONUNI         │
│                     │  Iniciar Sesion                        │
│                     │  Usuario:    [_____________________]   │
│                     │  Contrasena: [********] [👁 mostrar]   │
│                     │       [   INGRESAR   ]                 │
└──────────────────────────────────────────────────────────────┘
```

### `menu.jsp`

```
┌──────────────────────────────────────────────────────────────┐
│ 🟦 logo  Cliente Web CONUNI     Bienvenido, MONSTER          │
│                                  | Cerrar Sesion             │
├──────────────────────────────────────────────────────────────┤
│   ┌──────────┐   ┌──────────┐   ┌──────────────┐             │
│   │ 📏       │   │ ⚖        │   │ 🌡            │             │
│   │ Longitud │   │   Masa   │   │ Temperatura  │             │
│   └──────────┘   └──────────┘   └──────────────┘             │
└──────────────────────────────────────────────────────────────┘
```

### `longitud.jsp` (patrón análogo a masa/temperatura)

```
┌──────────────────────────────────────────────────────────────┐
│ 🟦 Conversion de Longitud         [Volver al menu]           │
├──────────────────────────────────────────────────────────────┤
│  Tipo:  [ Metros a Pies ▼ ]                                  │
│  Valor: [ 10              ]                                  │
│         [   Convertir   ]                                    │
│  Resultado: 32.8084                                          │
└──────────────────────────────────────────────────────────────┘
```

## 6.5 Código clave

### 6.5.1 `ServletAutenticacion`

```java
@WebServlet(name = "ServletAutenticacion", urlPatterns = {"/autenticacion"})
public class ServletAutenticacion extends HttpServlet {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Override
    protected void doPost(HttpServletRequest peticion,
                          HttpServletResponse respuesta)
            throws ServletException, IOException {

        String usuario    = peticion.getParameter("usuario");
        String contrasena = peticion.getParameter("contrasena");

        try {
            if (servicioAutenticacion.iniciarSesion(usuario, contrasena)) {
                HttpSession sesion = peticion.getSession(true);
                sesion.setAttribute("usuario", usuario);
                respuesta.sendRedirect(peticion.getContextPath() + "/vista/menu.jsp");
                return;
            }
            peticion.setAttribute("mensajeError",
                "Usuario o contrasena incorrectos.");
        } catch (Exception ex) {
            peticion.setAttribute("mensajeError",
                "No se pudo conectar con el servidor SOAP: " + ex.getMessage());
        }
        peticion.getRequestDispatcher("/vista/iniciarSesion.jsp")
                .forward(peticion, respuesta);
    }
}
```

### 6.5.2 `iniciarSesion.jsp` (extracto)

```html
<form action="${pageContext.request.contextPath}/autenticacion" method="post">
    <label for="usuario">Usuario:</label>
    <input type="text" id="usuario" name="usuario" required autofocus>

    <label for="contrasena">Contrasena:</label>
    <input type="password" id="contrasena" name="contrasena" required>

    <button type="submit">Ingresar</button>
</form>
```

### 6.5.3 `menu.jsp` (validación de sesión)

```jsp
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath()
                              + "/vista/iniciarSesion.jsp");
        return;
    }
%>
```

📷 **[CAPTURA: `capturas/06_web/03_codigo_servlet_autenticacion.png` — NetBeans editor con `ServletAutenticacion.java`.]**
📷 **[CAPTURA: `capturas/06_web/04_codigo_iniciar_sesion_jsp.png` — NetBeans editor con `iniciarSesion.jsp`.]**

## 6.6 Pasos para desplegar

1. Abrir NetBeans → **Open Project** → `06. CLIWEB/cliente_web_soap_java_conuni_gr06`.
2. Verificar la URL del WSDL en `ClienteSoap.java`.
3. Verificar que el servidor `08. SERVIDOR/...` esté desplegado en GlassFish.
4. Click derecho proyecto → **Run**.
5. URL: `http://localhost:8080/cliente_web_soap_java_conuni_gr06/`.
6. Login con `MONSTER / MONSTER9`.

📷 **[CAPTURA: `capturas/06_web/05_proyecto_netbeans.png` — Proyecto web abierto.]**
📷 **[CAPTURA: `capturas/06_web/06_iniciar_sesion.png` — `iniciarSesion.jsp` en navegador.]**
📷 **[CAPTURA: `capturas/06_web/07_menu.png` — `menu.jsp`.]**
📷 **[CAPTURA: `capturas/06_web/08_longitud.png` — `longitud.jsp` con conversión.]**
📷 **[CAPTURA: `capturas/06_web/09_masa.png` — `masa.jsp` con conversión.]**
📷 **[CAPTURA: `capturas/06_web/10_temperatura.png` — `temperatura.jsp` con conversión.]**
📷 **[CAPTURA: `capturas/06_web/11_filtro_sesion.png` — Acceso sin sesión → redirige a login.]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 7 — Cliente Móvil (Android + Kotlin)

## 7.1 Descripción

Aplicación **Android nativa** escrita en **Kotlin** con **Jetpack Compose** (Material 3). Consume el WSDL del servidor mediante la librería **ksoap2-android 3.6.4**.

## 7.2 Stack

| Componente | Detalle |
|---|---|
| Plataforma | Android (`minSdk 21`, `targetSdk 34`, `compileSdk 34`) |
| Lenguaje | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Build | Gradle Kotlin DSL |
| `applicationId` / `namespace` | `ec.edu.monster.conuni` |
| Cliente SOAP | `ksoap2-android:3.6.4` |
| IDE | Android Studio |

## 7.3 Dependencias clave (`app/build.gradle.kts`)

```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.code.ksoap2-android:ksoap2-android:3.6.4")
}
```

📷 **[CAPTURA: `capturas/07_movil/01_build_gradle.png` — Android Studio con `app/build.gradle.kts`.]**

## 7.4 Permisos y configuración

`AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="true">
    ...
</application>
```

`res/xml/network_security_config.xml`:

```xml
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

## 7.5 Wireframes ASCII

```
LoginScreen          MenuScreen           ConversionScreen
┌────────────────┐  ┌────────────────┐  ┌────────────────┐
│ 🟦 [Logo]      │  │ ☰  CONUNI   👤 │  │ ← Conversion   │
│ Iniciar Sesion │  │                │  │ Tipo:          │
│ ┌────────────┐ │  │ Bienvenido,    │  │ ┌────────────┐ │
│ │ Usuario    │ │  │     MONSTER    │  │ │ m a pies ▼ │ │
│ └────────────┘ │  │                │  │ └────────────┘ │
│ ┌────────────┐ │  │ ┌────────────┐ │  │ Valor:         │
│ │ Pass    👁 │ │  │ │📏 Longitud │ │  │ ┌────────────┐ │
│ └────────────┘ │  │ └────────────┘ │  │ │ 10         │ │
│                │  │ ┌────────────┐ │  │ └────────────┘ │
│ [INGRESAR]     │  │ │⚖ Masa      │ │  │ [CONVERTIR]    │
│                │  │ └────────────┘ │  │                │
│                │  │ ┌────────────┐ │  │ Resultado:     │
│                │  │ │🌡 Temperat.│ │  │   32.8084      │
│                │  │ └────────────┘ │  │                │
└────────────────┘  └────────────────┘  └────────────────┘
```

## 7.6 Patrón de llamada SOAP con ksoap2

```kotlin
private const val NAMESPACE = "http://controlador.monster.edu.ec/"
private const val URL = "http://10.0.2.2:8080/servidor_soap_java_conuni_gr06/CONUNI"

object ClienteSoap {

    suspend fun iniciarSesion(usuario: String, contrasena: String): Boolean =
        withContext(Dispatchers.IO) {
            val request = SoapObject(NAMESPACE, "iniciarSesion").apply {
                addProperty("usuario", usuario)
                addProperty("contrasena", contrasena)
            }
            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                setOutputSoapObject(request)
            }
            HttpTransportSE(URL).call("$NAMESPACE/iniciarSesion", envelope)
            (envelope.response as SoapPrimitive).toString().toBoolean()
        }

    suspend fun metrosAPies(valor: Double): Double = withContext(Dispatchers.IO) {
        val request = SoapObject(NAMESPACE, "metrosAPies").apply {
            addProperty("metros", valor)
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            setOutputSoapObject(request)
        }
        HttpTransportSE(URL).call("$NAMESPACE/metrosAPies", envelope)
        (envelope.response as SoapPrimitive).toString().toDouble()
    }
}
```

📷 **[CAPTURA: `capturas/07_movil/02_codigo_cliente_soap.png` — Android Studio con `ClienteSoap.kt`.]**

> **Hilos**: las llamadas SOAP **no** pueden ejecutarse en el hilo principal. Usar `Dispatchers.IO` o corrutinas.
>
> **Host desde emulador**: `localhost` apunta al emulador; el host de la máquina se expone como `10.0.2.2`.

## 7.7 Pasos para ejecutar

1. Abrir Android Studio → **Open** → `07. CLIMÓVIL/cliente_movil_soap_java_conuni_gr06`.
2. Esperar a que Gradle sincronice.
3. Iniciar un emulador Android (API 24+).
4. Verificar que `URL` en `ClienteSoap.kt` apunte a `http://10.0.2.2:8080/...`.
5. **Run** ▶.
6. Login con `MONSTER / MONSTER9`.

📷 **[CAPTURA: `capturas/07_movil/03_android_studio_proyecto.png` — Proyecto abierto.]**
📷 **[CAPTURA: `capturas/07_movil/04_gradle_sync.png` — Gradle sync completado.]**
📷 **[CAPTURA: `capturas/07_movil/05_emulador_login.png` — Emulador en login.]**
📷 **[CAPTURA: `capturas/07_movil/06_emulador_menu.png` — Emulador en menú.]**
📷 **[CAPTURA: `capturas/07_movil/07_emulador_conversion.png` — Emulador con conversión hecha.]**

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

# Capítulo 8 — Despliegue integrado y pruebas

## 8.1 Orden de arranque

| # | Acción | Comando / IDE |
|---|---|---|
| 1 | Verificar `credenciales.txt` en el classpath del servidor | NetBeans → Files |
| 2 | Build & Deploy servidor | NetBeans → Deploy `servidor_soap_java_conuni_gr06` |
| 3 | Verificar WSDL | navegador → `?wsdl` |
| 4 | Ejecutar pruebas JUnit | NetBeans → Test |
| 5 | Ejecutar cliente Consola | NetBeans → Run |
| 6 | Desplegar cliente Web | NetBeans → Run |
| 7 | Ejecutar cliente Escritorio | NetBeans → Run |
| 8 | Ejecutar cliente Móvil | Android Studio → Run en emulador |

## 8.2 Plan de pruebas mínimo

| Prueba | Cliente | Entrada | Resultado esperado |
|---|---|---|---|
| Login válido | Todos | `MONSTER / MONSTER9` | Pasa al menú |
| Login inválido | Todos | `MONSTER / wrong` | Mensaje de error |
| Login sensible a mayúsculas | Todos | `monster / monster9` | Falla (la TXT distingue mayúsculas) |
| Metros a pies | Todos | `10` | `32.8084` |
| Kilómetros a millas | Todos | `5` | `≈ 3.1069` |
| Celsius a Fahrenheit | Todos | `0` | `32` |
| Celsius a Kelvin | Todos | `100` | `373.15` |
| Kilogramos a libras | Todos | `1` | `≈ 2.2046` |
| Cerrar sesión | Todos | clic en `Cerrar Sesión` | Vuelve al login |

📷 **[CAPTURA: `capturas/08_integracion/01_servidor_y_cuatro_clientes.png` — Captura compuesta con los 4 clientes ejecutándose simultáneamente.]**
📷 **[CAPTURA: `capturas/08_integracion/02_glassfish_admin.png` — Consola admin de GlassFish (`http://localhost:4848`).]**

## 8.3 Solución de problemas comunes

| Problema | Causa probable | Solución |
|---|---|---|
| Login siempre falla | `credenciales.txt` no está en el classpath | Verificar que esté en `src/java/` y se copie a `WEB-INF/classes/` al compilar |
| `NullPointerException` al leer TXT | Ruta incorrecta o archivo ausente | Confirmar `RECURSO_CREDENCIALES = "credenciales.txt"` |
| Cliente móvil no conecta | Apuntó a `localhost` | Cambiar a `http://10.0.2.2:8080/...` |
| Cliente móvil error HTTP plano | `network-security-config` bloquea HTTP | Habilitar `cleartextTrafficPermitted` para `10.0.2.2` |
| WSDL no responde | Servidor sin desplegar | NetBeans → Deploy |
| Cliente Java falla con `<return>` ausente | El servicio devolvió un fault SOAP | Revisar logs de GlassFish |

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

## Conclusiones

- Se desarrolló un **Web Service SOAP** en Java con Jakarta JAX-WS que expone 17 operaciones agrupadas en autenticación y conversiones, demostrando el principio SOA de **un servicio = múltiples clientes**.
- Los **cuatro clientes** (consola, escritorio, web, móvil) consumen el mismo WSDL desde tecnologías distintas (Java SE, Swing, JSP/Servlets, Android/Kotlin), evidenciando la **interoperabilidad** inherente al protocolo SOAP.
- El uso de un **archivo TXT** como almacén de credenciales eliminó la necesidad de una base de datos, simplificando el despliegue y enfocando el alcance del proyecto en el contrato SOAP.
- La separación en capas **MVC** facilitó la organización del código y permitió aislar la lectura del archivo dentro del `ServicioAutenticacion`, sin afectar a los servicios de conversión (que son funciones puras).
- Las pruebas **JUnit** dieron cobertura tanto a la autenticación (con casos límite: vacíos, nulos, mayúsculas) como a cada conversión, asegurando la calidad del servidor antes de que los clientes lo consuman.
- La integración con **Android** vía `ksoap2-android` mostró que SOAP, aunque verboso, sigue siendo viable en clientes modernos cuando se respetan las restricciones de hilo y seguridad de red.

## Recomendaciones

- En producción, **no almacenar contraseñas en texto plano**; aplicar `BCrypt` o `Argon2` y considerar migrar a una base de datos cifrada cuando crezca el volumen de credenciales.
- Centralizar la **URL del WSDL** en una constante por cliente para facilitar el cambio de entorno (dev/prod).
- Para el cliente móvil, mover las llamadas SOAP a un `ViewModel` con corrutinas y manejar estados con `StateFlow`.
- Ampliar la cobertura de `pruebaAutenticacion` para soportar **múltiples credenciales** en el TXT (cargar todas, no solo la primera).
- Documentar el WSDL exportándolo a `09. DOCUMENTACION/CONUNI.wsdl` para referencia offline.

## Referencias

- Oracle. *Jakarta XML Web Services (JAX-WS) Specification*.
- Eclipse Foundation. *GlassFish / Payara Server Documentation*.
- JUnit. *JUnit 4 User Guide*.
- Google. *Jetpack Compose Documentation* (developer.android.com).
- ksoap2-android. *ksoap2-android GitHub project*.
- OASIS. *SOA Reference Architecture Foundation*.

```{=openxml}
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
```

## Anexos

### Anexo A — Credenciales y URLs

| Recurso | Valor |
|---|---|
| Usuario aplicación | `MONSTER` |
| Contraseña aplicación | `MONSTER9` |
| Archivo de credenciales | `credenciales.txt` (en classpath) |
| WSDL | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl` |
| Endpoint SOAP | `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI` |
| Endpoint móvil (emulador) | `http://10.0.2.2:8080/servidor_soap_java_conuni_gr06/CONUNI` |
| Cliente web | `http://localhost:8080/cliente_web_soap_java_conuni_gr06/` |
| Admin GlassFish | `http://localhost:4848` |

### Anexo B — Comandos rápidos

```bash
# Probar WSDL
curl -s "http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI?wsdl" | head -30

# Probar iniciarSesion con curl
curl -X POST \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: " \
  --data '<?xml version="1.0"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:con="http://controlador.monster.edu.ec/">
      <soapenv:Body>
        <con:iniciarSesion>
          <usuario>MONSTER</usuario>
          <contrasena>MONSTER9</contrasena>
        </con:iniciarSesion>
      </soapenv:Body>
    </soapenv:Envelope>' \
  http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI
```

### Anexo C — Plantilla de `credenciales.txt`

Contenido actual en `src/java/credenciales.txt`:

```txt
MONSTER:MONSTER9
```

Para agregar más usuarios, una línea por credencial:

```txt
MONSTER:MONSTER9
GR06:GR06_2026
ADMIN:admin123
```

> **Nota:** la implementación actual de `ServicioAutenticacion.leerCredencialArchivo()` retorna la **primera** credencial válida que encuentre, no todas. Si necesitas soporte multiusuario completo, modifica el método para devolver una lista y recorrerla en `autenticar()`.

### Anexo D — Mapa de capturas de pantalla a tomar

| Carpeta | Cantidad mínima | Contenido |
|---|---|---|
| `capturas/01_servidor/` | 10 | Entorno, paquetes, formulación, diagrama, código (CONUNI/servicios), NetBeans, GlassFish, deploy, WSDL, Tester |
| `capturas/02_txt/` | 5 | Archivo TXT, ubicación classpath, código `ServicioAutenticacion`, pruebas login OK/fail |
| `capturas/03_pruebas/` | 3 | Código `pruebaAutenticacion`, panel "Test Results", prueba individual |
| `capturas/04_consola/` | 10 | Paquetes, formulación, código, NetBeans, login, menú, 3 conversiones |
| `capturas/05_escritorio/` | 10 | Paquetes, formulación, código + Matisse, NetBeans, login, menú, conversión ×3 |
| `capturas/06_web/` | 11 | Paquetes, formulación, código (Servlet + JSP), NetBeans, 6 pantallas + filtro |
| `capturas/07_movil/` | 7 | Gradle, código ksoap2, Android Studio, sync, 3 pantallas emulador |
| `capturas/08_integracion/` | 2 | 4 clientes ejecutándose, admin GlassFish |
| **Total estimado** | **~58 capturas** | |
