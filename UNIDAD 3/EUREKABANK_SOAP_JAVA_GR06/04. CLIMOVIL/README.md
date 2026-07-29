# Cliente movil Android

Aplicacion Android Java con llamadas SOAP mediante `ksoap2-android`.

## Configuracion de microservicios

Edite `app/src/main/java/com/example/eurekabank_soap_java/config/ServidorConfig.java`. Hay una IP y un puerto para autenticacion, consulta, deposito, retiro y transferencia; movimientos usa el mismo servidor que consulta. Los endpoints se construyen al realizar cada llamada, por lo que siempre reflejan los valores actuales.

Los valores predeterminados son `10.0.2.2:8080`, que permiten al emulador acceder al Payara del equipo anfitrion. En un dispositivo fisico, reemplace cada `10.0.2.2` por la IP LAN de la laptop que aloja ese microservicio. Configure las seis rutas, aunque consulta y movimientos compartan IP:

```text
http://IP_AUTH:8080/ms-autenticacion/WSLogin
http://IP_CONSULTA:8080/ms-consulta/WSConsulta
http://IP_CONSULTA:8080/ms-consulta/WSMovimiento
http://IP_DEPOSITO:8080/ms-deposito/WSDeposito
http://IP_RETIRO:8080/ms-retiro/WSRetiro
http://IP_TRANSFERENCIA:8080/ms-transferencia/WSTransferencia
```

El dispositivo y las laptops deben estar en la misma red. El manifiesto permite HTTP sin TLS, pero el firewall de cada servidor debe aceptar TCP `8080`. Compruebe cada URL agregando `?wsdl` desde un equipo de la red.

## Ejecucion

Abra `eurekabank_soap_java_climov_gr06` en Android Studio y ejecute `testDebugUnitTest` y `assembleDebug`, o use:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Un unico Payara puede alojar los cinco WAR porque tienen context roots distintos. No use puertos `8081` a `8085` salvo que se hayan creado dominios Payara separados. Si un WAR se apaga solo fallan las operaciones asociadas a ese microservicio; no detenga Payara completo si otros servicios siguen en uso.
