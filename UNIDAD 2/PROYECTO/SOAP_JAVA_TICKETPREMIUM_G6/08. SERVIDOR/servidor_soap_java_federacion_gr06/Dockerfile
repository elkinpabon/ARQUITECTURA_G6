FROM payara/server-full:6.2024.6-jdk17

# Bootstrap de BD deshabilitado (schema ya existe en Amazon RDS).
ENV TICKETPREMIUM_BOOTSTRAP_DISABLED=true

# WAR al DEPLOY_DIR para que el start.sh lo despliegue
COPY target/servidor_soap_java_federacion_gr06.war $DEPLOY_DIR/

# Script de arranque: inicia Payara, espera admin port, deploy externo
COPY --chmod=755 start.sh /opt/payara/start.sh

EXPOSE 8080

ENTRYPOINT ["/opt/payara/start.sh"]
