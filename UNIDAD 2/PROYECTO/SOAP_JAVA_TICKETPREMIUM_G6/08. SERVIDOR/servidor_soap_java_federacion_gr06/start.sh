#!/bin/bash
# Script de arranque para Payara Server Full en Cloud Run.
# Inicia el dominio, luego despliega el WAR via asadmin externo.

ASADMIN=/opt/payara/appserver/glassfish/bin/asadmin
WAR=/opt/payara/deployments/servidor_soap_java_federacion_gr06.war
ASADMIN_BASE="$ASADMIN --user admin --passwordfile $PASSWORD_FILE --host 127.0.0.1 --port 4848 --secure=false"

echo "[start.sh] Iniciando Payara domain1..."
$ASADMIN start-domain --verbose=false domain1
echo "[start.sh] start-domain completo. Intentando desplegar WAR..."

# Reintentar deploy hasta que el admin port responda (max 5 minutos)
DEPLOYED=false
for i in $(seq 1 60); do
  echo "[start.sh] Intento $i de deploy..."
  if $ASADMIN_BASE deploy \
      --contextroot=servidor_soap_java_federacion_gr06 \
      --name=servidor_soap_java_federacion_gr06 \
      "$WAR" 2>&1; then
    echo "[start.sh] DEPLOY EXITOSO en intento $i"
    DEPLOYED=true
    break
  fi
  echo "[start.sh] Fallo intento $i, reintentando en 5s..."
  sleep 5
done

if [ "$DEPLOYED" = "false" ]; then
  echo "[start.sh] ERROR: No se pudo desplegar el WAR despues de 60 intentos."
  # No salir - Payara sigue corriendo para diagnostico
fi

echo "[start.sh] Esperando. Redirigiendo server log..."
# Mantener stdout con el server log
SERVER_LOG=/opt/payara/appserver/glassfish/domains/domain1/logs/server.log
if [ -f "$SERVER_LOG" ]; then
  tail -f "$SERVER_LOG" &
fi

# Mantener el contenedor vivo
while true; do
  sleep 60
  $ASADMIN_BASE uptime > /dev/null 2>&1 || { echo "[start.sh] Payara detenido."; exit 1; }
done
