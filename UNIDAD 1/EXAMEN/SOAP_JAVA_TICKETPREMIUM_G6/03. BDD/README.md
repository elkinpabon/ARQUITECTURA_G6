# 03. BDD — Datos de prueba

Esta carpeta está vacía a propósito. **No hay que ejecutar nada manualmente.**

## ¿Por qué?

El servidor SOAP lleva un componente de bootstrap automático que, al desplegar el WAR en Payara, hace **todo** sin intervención:

1. Crea la base `ticketpremiumDB` si no existe
2. Crea las 5 tablas (`USUARIO`, `PARTIDO_FUTBOL`, `LOCALIDAD_PARTIDO`, `FACTURA`, `DETALLE_FACTURA`)
3. Inserta los 4 usuarios con sus claves (`monster/monster9` ADMIN, `josue/admin2002`, `mikaela/admin2002`, `elkin/admin2002`)
4. Inserta los 5 partidos y 20 localidades
5. Siembra 5 facturas demo (si `FACTURA` está vacía)

El componente vive en:
- [`08. SERVIDOR/.../persistencia/BootstrapBD.java`](../08.%20SERVIDOR/servidor_soap_java_federacion_gr06/src/main/java/ec/edu/monster/persistencia/BootstrapBD.java) — `@WebListener` que Payara invoca al deploy
- [`08. SERVIDOR/.../persistencia/BootstrapEngine.java`](../08.%20SERVIDOR/servidor_soap_java_federacion_gr06/src/main/java/ec/edu/monster/persistencia/BootstrapEngine.java) — lógica
- [`08. SERVIDOR/.../resources/bootstrap.sql`](../08.%20SERVIDOR/servidor_soap_java_federacion_gr06/src/main/resources/bootstrap.sql) — script idempotente

## Flujo en una PC nueva

1. Instalar Java 17, Maven, MySQL, Payara/GlassFish y NetBeans
2. Asegurar que MySQL corra con `root/admin2002` en `localhost:3306`
3. Abrir el proyecto del servidor en NetBeans → **Run** → Payara lo despliega
4. **Listo**. La BD aparece sola con schema + seed + datos demo.

## Si por alguna razón quieres resetear desde cero

```bash
mysql -u root -padmin2002 -e "DROP DATABASE IF EXISTS ticketpremiumDB"
```

Luego redeploy del WAR → el bootstrap vuelve a crear todo.

## El script destructivo de la MER

[`02. MER/03. FISICO/script_ticketpremium.sql`](../02.%20MER/03.%20FISICO/script_ticketpremium.sql) existe únicamente como **entregable académico** (el "MER físico" de la rúbrica). No se usa en runtime.
