# EUREKABANK GR06 — Cliente Escritorio (Swing)

Cliente de **escritorio Swing** (Java + JAX-WS), **misma funcionalidad que el
cliente web**, patrón **MVC** y conexión al servidor en **archivo aparte**.
UI escrita a mano (sin `.form`) para que compile siempre.

## Conexión al servidor (archivo aparte)

`src/main/resources/servidor.properties` → `servidor.base=...` (igual que la
consola). La lee `ec.edu.monster.config.ServidorConfig`.

## Estructura MVC

| Capa | Paquete | Contenido |
|---|---|---|
| Config | `config` | `ServidorConfig` |
| Servicio | `servicio` | `WsFactory`, `LoginClient`, `CuentaClient`, `MovimientoClient`, `Sesion` |
| Controlador | `controlador` | `BancoController` (misma lógica que consola/web) |
| Vista | `vista` | `EscritorioApp`, `LoginPanel`, `MainPanel` (Swing) |
| Util | `util` | `Moneda` |

## Funcionalidad (igual que CLIWEB)

Login con rol, tabla de cuentas + saldo total, consultar saldo,
**depósito (solo admin)**, retiro, **transferencia con conversión de moneda**
(Dólares preferente), movimientos en tabla con **columna Conversión**, y para
admin: registrar cliente, registrar cuenta, eliminar cuenta.

## Ejecutar

```
mvn clean package    # requiere el SERVIDOR desplegado (wsimport)
mvn exec:java -Dexec.mainClass=ec.edu.monster.vista.EscritorioApp
```

Usuarios de prueba: `09. DOCUMENTACION/USUARIOS_PRUEBA.md`.
