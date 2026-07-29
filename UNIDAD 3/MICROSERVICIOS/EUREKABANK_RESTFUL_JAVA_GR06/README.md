# EurekaBank REST Java GR06

Sistema bancario REST distribuido en tres microservicios Jakarta EE 10/JAX-RS,
cuatro clientes y MySQL. Es independiente de `EUREKABANK_SOAP_JAVA_GR06`.

## Microservicios

| Servicio | Base REST local |
|---|---|
| `ms-rest-autenticacion` | `http://localhost:8080/ms-rest-autenticacion/api` |
| `ms-rest-consulta` | `http://localhost:8080/ms-rest-consulta/api` |
| `ms-rest-transacciones` | `http://localhost:8080/ms-rest-transacciones/api` |

Los tres WAR pueden ejecutarse simultaneamente en un solo Payara porque cada
uno tiene un context root distinto. No se usan puertos `8081` a `8085` cuando
se comparte un dominio Payara.

## Build y despliegue

```powershell
cd "04. SERVIDOR"
mvn clean package
```

Despliegue los tres WAR generados en los directorios de
`ms-rest-autenticacion`, `ms-rest-consulta` y `ms-rest-transacciones`.
Compruebe las rutas REST antes de iniciar los clientes.

## Trabajo distribuido

Cada cliente configura las tres bases `ms.*` con las IP LAN de los equipos que
alojan los servicios:

```properties
ms.autenticacion=http://192.168.1.20:8080/ms-rest-autenticacion/api
ms.consulta=http://192.168.1.21:8080/ms-rest-consulta/api
ms.transacciones=http://192.168.1.22:8080/ms-rest-transacciones/api
```

Permita TCP `8080` en el firewall. Deshabilitar un WAR deja fuera solamente
ese servicio; no detenga Payara completo mientras los otros servicios deban
seguir disponibles.
