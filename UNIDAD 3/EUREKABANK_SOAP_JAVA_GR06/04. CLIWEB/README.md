# CLIWEB — Cliente Web Java (Jakarta EE)

`eurekabank_soap_java_cliweb_gr06`: app web **Java** (Servlets + JSP), WAR sobre
Payara, consume cada microservicio mediante un proxy JAX-WS independiente.

> El antiguo cliente Node.js quedó archivado en `_legacy_nodejs/` (no se usa).

## Arquitectura (MVC)

| Capa | Paquete |
|---|---|
| Controlador | `ec.edu.monster.cliweb.controlador` (LoginServlet, CuentaServlet, MovimientoServlet, MenuServlet, LogoutServlet) |
| Servicio (wrapper SOAP) | `ec.edu.monster.cliweb.servicio` |
| Contratos SOAP | `ec.edu.monster.cliweb.ws` |
| Vistas | `WEB-INF/views/*.jsp` |

## Build y despliegue

El build no descarga WSDL ni requiere que los microservicios esten encendidos. Configure las seis URLs en `eurekabank_soap_java_cliweb_gr06/src/main/resources/microservices.properties`. Para un Payara local todas usan `localhost:8080`. En tres laptops, cada URL debe usar la IP LAN del equipo que aloja el servicio; habilite TCP `8080` en el firewall y compruebe cada direccion con `?wsdl`.

En Payara se pueden sobrescribir valores con propiedades JVM (`-Dms.consulta=...`) o variables como `MS_CONSULTA`.

```bash
mvn clean package        # genera target/eurekabank_soap_java_cliweb_gr06.war
asadmin deploy --contextroot eurekabank_soap_java_cliweb_gr06 \
  --name eurekabank_soap_java_cliweb_gr06 target/eurekabank_soap_java_cliweb_gr06.war
```

App: **http://localhost:8080/eurekabank_soap_java_cliweb_gr06/**

Los cinco WAR servidor pueden compartir un dominio Payara porque tienen context roots unicos. No intente asignarles `8081` a `8085` salvo que use dominios separados. Si necesita retirar un servicio, detenga solo ese WAR y no Payara completo.

NetBeans y Maven usan `src/main/java` y `src/main/webapp`; las copias antiguas bajo `src/java` y `web` no son la aplicación activa.

## Funcionalidades (validadas end-to-end)

- Login real (sesión HTTP; clave en texto plano, el servidor aplica SHA1).
- Consultar saldo, depositar, retirar (con validación de saldo y mensajes).
- Listar movimientos con clasificación correcta INGRESO/EGRESO.
- Guard de sesión: acceso sin login redirige a `/login`.

Credenciales de prueba: `monster` / `monster9` · `internet` / `admin2002`
