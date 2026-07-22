# Cliente Java de escritorio

Cliente Swing Maven/JAX-WS.

## Conexion

Configure las seis URLs en `eurekabank_soap_java_cliesc_gr06/src/main/resources/microservices.properties`. El entorno local usa `localhost:8080` para todas. Si los servicios se reparten entre tres laptops, sustituya el host de cada URL por la IP LAN de la laptop responsable; consulta y movimientos apuntan al mismo `ms-consulta`.

Cada URL también admite una propiedad JVM como `-Dms.retiro=http://192.168.1.22:8080/ms-retiro/WSRetiro` o la variable de entorno equivalente, por ejemplo `MS_RETIRO`.

Verifique las seis direcciones con `?wsdl` y habilite TCP `8080` en el firewall. Los cinco WAR pueden coexistir en un dominio Payara por sus context roots unicos. No configure `8081` a `8085` salvo que se utilicen dominios Payara separados. Detener un WAR afecta solamente a ese servicio; no apague Payara completo si los demas deben continuar disponibles.

## Ejecucion

```powershell
mvn clean package
mvn exec:java -Dexec.mainClass=ec.edu.monster.vista.EscritorioApp
```

NetBeans y Maven usan el mismo árbol `src/main/java`; no ejecute las copias antiguas que permanecen bajo `src`.
