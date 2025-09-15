FROM quay.io/keycloak/keycloak:26.3.2
COPY realm-export.json /opt/keycloak/data/import/realm-export.json
ENV KC_BOOTSTRAP_ADMIN_USERNAME="admin"
ENV KC_BOOTSTRAP_ADMIN_PASSWORD="admin"
ENV KC_HTTP_PORT=80
CMD ["start-dev", "--import-realm", "--health-enabled=true", "--verbose"]
