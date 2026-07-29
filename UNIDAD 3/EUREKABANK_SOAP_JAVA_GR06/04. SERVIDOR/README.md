# Servidor de microservicios SOAP

Backend dividido en cinco aplicaciones web Jakarta EE 10/JAX-WS y una libreria compartida `ms-common`. Payara Server 6 ejecuta todos los WAR en un mismo dominio.

## Proyectos

| Proyecto | Context root | Servicios SOAP |
|---|---|---|
| `ms-autenticacion` | `/ms-autenticacion` | `WSLogin` |
| `ms-consulta` | `/ms-consulta` | `WSConsulta`, `WSMovimiento` |
| `ms-deposito` | `/ms-deposito` | `WSDeposito` |
| `ms-retiro` | `/ms-retiro` | `WSRetiro` |
| `ms-transferencia` | `/ms-transferencia` | `WSTransferencia` |

Cada proyecto declara su context root en `web/WEB-INF/glassfish-web.xml`. Al ser distintos, los cinco WAR pueden permanecer desplegados simultaneamente en el mismo Payara y compartir `http://HOST:8080`.

## NetBeans y Payara 6

1. Instale JDK 17 y Payara Server 6 y registre el servidor en NetBeans.
2. Abra `ms-common` y los cinco proyectos web como proyectos existentes.
3. Asigne el mismo dominio Payara a los cinco proyectos.
4. Inicie Payara una vez y use `Run` o `Deploy` en cada microservicio.
5. Confirme en la consola de administracion que las cinco aplicaciones estan habilitadas.

No configure puertos `8081`, `8082`, `8083`, `8084` o `8085` para separar aplicaciones. Los context roots ya resuelven las rutas. Esos puertos solo tendrian sentido con dominios Payara separados, cada uno configurado expresamente para no colisionar.

Los WAR incluyen actualmente `web/WEB-INF/lib/ms-common.jar`. Los proyectos pueden empaquetarse desde NetBeans; no edite artefactos generados en `build` o `target`. El build independiente de `ms-common` requiere restaurar sus metadatos NetBeans antes de poder regenerar ese JAR.

## Verificacion

Abra las seis direcciones y compruebe que cada una entrega XML WSDL:

```text
http://localhost:8080/ms-autenticacion/WSLogin?wsdl
http://localhost:8080/ms-consulta/WSConsulta?wsdl
http://localhost:8080/ms-consulta/WSMovimiento?wsdl
http://localhost:8080/ms-deposito/WSDeposito?wsdl
http://localhost:8080/ms-retiro/WSRetiro?wsdl
http://localhost:8080/ms-transferencia/WSTransferencia?wsdl
```

Si una URL falla, revise primero que ese WAR este desplegado, luego el log de Payara, la conexion MySQL y el firewall. Deshabilitar o retirar un WAR afecta solo a ese servicio. Para mantener disponibles los demas, no detenga Payara completo.

## Distribucion en tres laptops

Cada integrante despliega los microservicios que tenga asignados en su propio Payara `:8080`. En las tres laptops se deben configurar las seis URLs de `microservices.properties`, reemplazando `localhost` por la IP LAN del equipo responsable de cada servicio. `ms.consulta` y `ms.movimiento` apuntan normalmente a la misma IP porque salen de `ms-consulta`.

Las IP deben ser accesibles entre equipos. Habilite conexiones TCP entrantes al puerto `8080` en el firewall y pruebe cada `?wsdl` desde las otras laptops antes de ejecutar los clientes.
