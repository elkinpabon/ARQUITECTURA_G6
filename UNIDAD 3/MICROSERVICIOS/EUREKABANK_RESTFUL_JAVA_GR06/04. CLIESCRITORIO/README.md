# EUREKABANK GR06 — Cliente Escritorio RESTful (Swing)

Misma UI/MVC que el escritorio SOAP; **solo cambia la arquitectura**: la capa
`servicio` consume el servidor REST/JSON. `vista` (Swing), `controlador`,
`util`, `Sesion` idénticos.

- Proyecto: `eurekabank_restful_java_cliesc_gr06` (JAR Swing).
- Título/login: "EUREKABANK GR06 — Banca RESTFULL".
- Conexión: `ec.edu.monster.config.ServidorConfig` carga
  `src/microservices.properties` con las bases de autenticación, consulta y
  transacciones; para cada `ms.*`, preceden la propiedad de
  sistema y la variable de entorno en mayúsculas.
- Capas: `config`, `rest` (java.net.http + jakarta.json/Parsson), `ws` (POJOs),
  `servicio` (Login/Cuenta/Movimiento + Sesion), `controlador`, `vista`, `util`.

```
ant jar
```
Requiere el servidor REST desplegado.
