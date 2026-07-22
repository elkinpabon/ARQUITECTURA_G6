# Cliente Java de consola

Cliente Maven/JAX-WS con interfaz de texto.

## Conexion

Configure las seis URLs en `eurekabank_soap_java_con_gr06/src/main/resources/microservices.properties`. Para pruebas locales todas usan `localhost:8080`; en tres laptops, cada URL debe llevar la IP LAN del equipo que despliega ese servicio. Consulta y movimientos pertenecen al mismo WAR y normalmente comparten IP.

Cada valor también puede sobrescribirse sin recompilar con una propiedad JVM, por ejemplo `-Dms.deposito=http://192.168.1.22:8080/ms-deposito/WSDeposito`, o con variables como `MS_DEPOSITO`.

Antes de compilar, compruebe los seis endpoints agregando `?wsdl`. Permita TCP `8080` en el firewall de los equipos servidor. No use el contexto unificado anterior ni asigne `8081` a `8085`, salvo que existan dominios Payara separados.

## Ejecucion

```powershell
mvn clean package
mvn exec:java -Dexec.mainClass=ec.edu.monster.vista.ConsolaApp
```

Un dominio Payara puede mantener los cinco WAR activos gracias a sus context roots unicos. Si un WAR se detiene, solo deja de responder ese microservicio; no detenga Payara completo mientras otros integrantes lo consumen.

NetBeans y Maven usan el mismo árbol `src/main/java`; no ejecute las copias antiguas que permanecen bajo `src`.
