# Servidor REST distribuido

El backend REST se construye como proyectos NetBeans Web Project con Ant,
Java 17 y Jakarta EE 10. No depende ni despliega el servidor SOAP.

## Modulos

| Servicio | Context root | Rutas |
|---|---|---|
| `ms-rest-autenticacion` | `/ms-rest-autenticacion` | `POST /api/login`, `GET /api/login/cliente/{usuario}` |
| `ms-rest-consulta` | `/ms-rest-consulta` | clientes, cuentas, saldo, administracion y movimientos |
| `ms-rest-transacciones` | `/ms-rest-transacciones` | depósito, retiro y transferencia |

`ms-rest-common` es una libreria incluida por los WAR; no se despliega.

## Build

1. Abra `ms-rest-autenticacion`, `ms-rest-consulta` y
   `ms-rest-transacciones` como proyectos existentes en NetBeans.
2. Asigne Payara 6 al proyecto si NetBeans no lo detecta automáticamente.
3. Ejecute primero `ant` dentro de `ms-rest-common`; esto genera
   `dist/ms-rest-common.jar`.
4. Copie ese JAR a `web/WEB-INF/lib/ms-rest-common.jar` de cada WAR, o use el
   script de sincronización descrito abajo.
5. Use **Clean and Build** en cada proyecto web. Los WAR se generan bajo
   `ms-rest-*/dist`.

Desde PowerShell, con Ant disponible:

```powershell
ant -f ms-rest-common/build.xml
Copy-Item ms-rest-common/dist/ms-rest-common.jar ms-rest-autenticacion/web/WEB-INF/lib/
Copy-Item ms-rest-common/dist/ms-rest-common.jar ms-rest-consulta/web/WEB-INF/lib/
Copy-Item ms-rest-common/dist/ms-rest-common.jar ms-rest-transacciones/web/WEB-INF/lib/
ant -f ms-rest-autenticacion/build.xml dist
ant -f ms-rest-consulta/build.xml dist
ant -f ms-rest-transacciones/build.xml dist
```

Cada WAR contiene `ms-rest-common.jar`, MySQL Connector/J y Protobuf.

## Verificacion local

```text
POST http://localhost:8080/ms-rest-autenticacion/api/login
GET  http://localhost:8080/ms-rest-consulta/api/clientes
POST http://localhost:8080/ms-rest-transacciones/api/cuentas/00900021/deposito
POST http://localhost:8080/ms-rest-transacciones/api/cuentas/00900021/retiro
POST http://localhost:8080/ms-rest-transacciones/api/transferencias
```

Todos los servicios usan la misma base MySQL para esta practica. Configure la
conectividad de MySQL antes del despliegue y permita TCP `8080` para el uso LAN.
