# CLIWEB — Cliente Web Java (Jakarta EE)

`eurekabank_soap_java_cliweb_gr06`: app web **Java** (Servlets + JSP), WAR sobre
Payara, consume el servidor SOAP por JAX-WS (stubs `wsimport`).

> El antiguo cliente Node.js quedó archivado en `_legacy_nodejs/` (no se usa).

## Arquitectura (MVC)

| Capa | Paquete |
|---|---|
| Controlador | `ec.edu.monster.cliweb.servlet` (LoginServlet, CuentaServlet, MovimientoServlet, MenuServlet, LogoutServlet) |
| Servicio (wrapper SOAP) | `ec.edu.monster.cliweb.service` |
| Stubs generados | `ec.edu.monster.cliweb.ws` (wsimport) |
| Vistas | `WEB-INF/views/*.jsp` |

## Build y despliegue

El **servidor debe estar arriba** (wsimport lee sus WSDL al compilar):

```bash
mvn clean package        # genera target/eurekabank_soap_java_cliweb_gr06.war
asadmin deploy --contextroot eurekabank_soap_java_cliweb_gr06 \
  --name eurekabank_soap_java_cliweb_gr06 target/eurekabank_soap_java_cliweb_gr06.war
```

App: **http://localhost:8080/eurekabank_soap_java_cliweb_gr06/**

## Funcionalidades (validadas end-to-end)

- Login real (sesión HTTP; clave en texto plano, el servidor aplica SHA1).
- Consultar saldo, depositar, retirar (con validación de saldo y mensajes).
- Listar movimientos con clasificación correcta INGRESO/EGRESO.
- Guard de sesión: acceso sin login redirige a `/login`.

Credenciales de prueba: `monster` / `monster9` · `internet` / `admin2002`
