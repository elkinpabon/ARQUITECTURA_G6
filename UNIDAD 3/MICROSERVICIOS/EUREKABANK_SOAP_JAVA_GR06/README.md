# EurekaBank SOAP Java GR06

Sistema bancario distribuido con cinco microservicios SOAP Jakarta EE 10, cuatro clientes y MySQL.

## Estructura

| Carpeta | Contenido |
|---|---|
| `01. UML` | Diagramas UML |
| `02. MER` | Modelos de datos |
| `03. BDD` | Scripts de estructura y datos MySQL |
| `04. SERVIDOR` | Cinco WAR SOAP y la libreria `ms-common` |
| `04. CLICONSOLA` | Cliente Java de consola |
| `04. CLIESCRITORIO` | Cliente Java Swing |
| `04. CLIWEB` | Cliente web Java |
| `04. CLIMOVIL` | Cliente Android |
| `05. DOCUMENTACION` | Documentacion del proyecto |

## Despliegue local

Use JDK 17, NetBeans y Payara Server 6. Registre un dominio Payara en NetBeans, inicie el servidor y despliegue los cinco proyectos de `04. SERVIDOR`. Un solo dominio puede alojar simultaneamente los cinco WAR en el puerto `8080`, porque cada uno declara un context root unico:

| Microservicio | WSDL |
|---|---|
| Autenticacion | `http://localhost:8080/ms-autenticacion/WSLogin?wsdl` |
| Consulta | `http://localhost:8080/ms-consulta/WSConsulta?wsdl` |
| Movimientos | `http://localhost:8080/ms-consulta/WSMovimiento?wsdl` |
| Deposito | `http://localhost:8080/ms-deposito/WSDeposito?wsdl` |
| Retiro | `http://localhost:8080/ms-retiro/WSRetiro?wsdl` |
| Transferencia | `http://localhost:8080/ms-transferencia/WSTransferencia?wsdl` |

Compruebe cada URL `?wsdl` antes de iniciar un cliente. No cree puertos `8081` a `8085`: solo se necesitan si se crean dominios Payara separados, cada uno con su propia configuracion de puertos.

## Trabajo en tres laptops

Cada persona despliega los servicios asignados en su Payara local, normalmente en `:8080`. Todos deben configurar las seis URLs de `microservices.properties` con la IP LAN de la laptop que aloja cada endpoint; consulta y movimientos pueden compartir IP porque pertenecen al mismo WAR. Por ejemplo:

```properties
ms.autenticacion=http://192.168.1.20:8080/ms-autenticacion/WSLogin
ms.consulta=http://192.168.1.21:8080/ms-consulta/WSConsulta
ms.movimiento=http://192.168.1.21:8080/ms-consulta/WSMovimiento
ms.deposito=http://192.168.1.22:8080/ms-deposito/WSDeposito
ms.retiro=http://192.168.1.22:8080/ms-retiro/WSRetiro
ms.transferencia=http://192.168.1.20:8080/ms-transferencia/WSTransferencia
```

Permita Java/Payara o TCP `8080` en el firewall de cada laptop y pruebe los WSDL desde las otras dos. Si se detiene o se elimina un WAR, solo queda fuera de servicio ese microservicio; no detenga el dominio Payara completo cuando los demas servicios deban seguir disponibles.

## Base de datos

Cree la base `eurekabank` y ejecute en orden los scripts de `03. BDD`. Revise las credenciales JDBC de `ms-common` para que correspondan al MySQL del entorno.
