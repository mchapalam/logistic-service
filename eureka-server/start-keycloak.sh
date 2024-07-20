#!/bin/sh
/opt/keycloak/bin/kc.sh import --file /opt/keycloak/data/import/realm-config.json --realm <my-realm>
/opt/keycloak/bin/kc.sh start-dev