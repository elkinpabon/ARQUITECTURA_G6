# cliente_consola_soap_java_conuni_gr06

Cliente de consola/terminal que consume el mismo servicio SOAP **CONUNI** usado por los clientes web y movil. Ejecuta un menu interactivo en Java puro (Scanner + System.out) con autenticacion y 15 operaciones de conversion de unidades.

---

## 1. Arquitectura (MVC)

Patron **Modelo-Vista-Controlador** estricto. La Vista concentra toda la I/O de consola y el Controlador orquesta el flujo sin tocar `System.in` ni `System.out` directamente.

```
Terminal (teclado/pantalla)
    |
    v
[Vista: VistaConsola]  <--->  [Controlador: ControladorConsola]
                                       |
                                       |  (llamada Java pura)
                                       v
                              [Modelo: Servicios SOAP]
                                       |
                                       v
                              Servidor CONUNI (Payara / JAX-WS)
```

### Responsabilidades por capa

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Vista | `ec.edu.monster.vista` | I/O de consola: menus, lectura de texto/contrasena/numero, impresion de errores y resultados. |
| Controlador | `ec.edu.monster.controlador` | Bucles de menus, limite de intentos de login, dispatch por categoria/operacion. |
| Modelo | `ec.edu.monster.modelo` | Igual que en el cliente web: construye sobres SOAP y envia por HTTP. |

---

## 2. Stack tecnologico

- **Java** 17
- **NetBeans Ant J2SE Application** (tipo de proyecto)
- **SOAP 1.1** sobre HTTP con `HttpURLConnection` (sin librerias externas).
- `java.io.Console` para leer la contrasena sin mostrarla cuando hay TTY real; fallback a `Scanner` dentro del IDE.

Sin dependencias externas: el proyecto compila y corre con solo el JDK.

---

## 3. Estructura del proyecto

```
cliente_consola_soap_java_conuni_gr06/
├── build.xml                 # Entrada Ant
├── manifest.mf
├── nbproject/
│   ├── project.xml
│   └── project.properties    # main.class = ec.edu.monster.Aplicacion, JDK 17
└── src/ec/edu/monster/
    ├── Aplicacion.java                # main, delega al controlador
    ├── controlador/
    │   └── ControladorConsola.java
    ├── modelo/
    │   ├── ClienteSoap.java
    │   ├── Resultado.java
    │   ├── ServicioAutenticacion.java
    │   ├── ServicioLongitud.java
    │   ├── ServicioMasa.java
    │   └── ServicioTemperatura.java
    └── vista/
        └── VistaConsola.java
```

---

## 4. Clases y responsabilidades

### Entrada

| Clase | Responsabilidad |
|-------|-----------------|
| `Aplicacion` | Unico `main`. Crea el `ControladorConsola` y llama `ejecutar()`. Nada mas. |

### Vista

| Clase | Metodos principales | Responsabilidad |
|-------|---------------------|-----------------|
| `VistaConsola` | `mostrarEncabezado`, `mostrarMenuPrincipal`, `mostrarMenuLongitud/Masa/Temperatura`, `mostrarResultado`, `leerTexto`, `leerContrasena`, `leerOpcion(min, max)`, `leerDouble` | Concentra toda la I/O. `leerContrasena` usa `System.console().readPassword()` si hay TTY real (ejecucion desde terminal) y cae a `Scanner` con aviso visible si se ejecuta desde IDE. `leerOpcion` y `leerDouble` validan en bucle hasta obtener una entrada valida. |

### Controlador

| Clase | Responsabilidad |
|-------|-----------------|
| `ControladorConsola` | `ejecutar()` orquesta: encabezado -> autenticacion (max 3 intentos) -> `bucleMenuPrincipal`. Los bucles `bucleMenuLongitud/Masa/Temperatura` despachan a metodos `ejecutarX` que llaman al Modelo y devuelven `Resultado` (exito o mensaje de error). Todas las excepciones de red/SOAP se capturan aqui, la Vista solo recibe `Resultado`. |

### Modelo

Identico al del cliente web. Se reutiliza el mismo patron y se podria extraer a una libreria compartida en el futuro.

| Clase | Responsabilidad |
|-------|-----------------|
| `ClienteSoap` | Cliente SOAP generico (arma sobre, envia por HTTP, extrae `<return>`). |
| `ServicioAutenticacion` | `iniciarSesion(usuario, contrasena)` -> `boolean`. |
| `ServicioLongitud` | 5 operaciones de longitud. |
| `ServicioMasa` | 5 operaciones de masa. |
| `ServicioTemperatura` | 5 operaciones de temperatura. |
| `Resultado` | DTO `{ exito, mensaje, valor }`. |

---

## 5. Flujo de uso

```
==================================================
         CLIENTE CONSOLA CONUNI - SOAP
==================================================

--- Iniciar Sesion (intento 1 de 3) ---
Usuario: MONSTER
Contrasena: ********        <-- oculta en TTY real
[OK] Bienvenido, MONSTER.

--- Menu Principal (usuario: MONSTER) ---
1. Conversiones de Longitud
2. Conversiones de Masa
3. Conversiones de Temperatura
0. Cerrar Sesion
Selecciona una opcion [0-3]: 1

--- Conversiones de Longitud ---
1. Metros a Pies
...
Selecciona una opcion [0-5]: 1
Ingresa el valor a convertir: 10
[OK] Resultado: 32.8084
```

Caracteristicas de UX:

- **Max 3 intentos** de login. Despues termina.
- **Validacion de entradas**: opcion fuera de rango o valor no numerico -> la Vista re-pregunta sin volver al Controlador.
- **Acepta coma o punto decimal** (`12,5` o `12.5`).
- **Cerrar sesion** desde el menu principal termina limpiamente con un mensaje de despedida.

---

## 6. Operaciones SOAP consumidas

Las mismas 16 que el cliente web, apuntando a:
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

## 7. Requisitos previos

- **JDK 17**.
- **Servidor SOAP corriendo** en `http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI`.

---

## 8. Como ejecutar

### Desde NetBeans
1. `File -> Open Project` -> selecciona `cliente_consola_soap_java_conuni_gr06`.
2. `Properties -> Libraries -> Java Platform` -> **JDK 17**.
3. Pulsa **Run** (F6). La salida aparece en la ventana *Output* de NetBeans.
   > Nota: dentro de NetBeans `System.console()` devuelve `null`, asi que la contrasena se muestra en pantalla (el programa te avisa).

### Desde terminal (recomendado para probar contrasena oculta)
```bash
cd "04. CLICONSOLA/cliente_consola_soap_java_conuni_gr06"
ant clean jar
java -jar dist/cliente_consola_soap_java_conuni_gr06.jar
```
O compilando directo con `javac`:
```bash
javac -d build/classes $(find src -name "*.java")
java -cp build/classes ec.edu.monster.Aplicacion
```

---

## 9. Credenciales de prueba

```
usuario:    MONSTER
contrasena: MONSTER9
```

---

## 10. Puntos a documentar mas adelante

- Diagrama de secuencia login + conversion (usuario -> Vista -> Controlador -> Servicio -> ClienteSoap -> Servidor).
- Capturas de la ejecucion en terminal (TTY real) mostrando la contrasena oculta.
- Justificacion: ¿por que JAR autoejecutable en vez de GUI? (ligereza, scripts, CI/CD).
- Comparativa frente al cliente web (misma capa Modelo reutilizada, distinta capa Vista).
