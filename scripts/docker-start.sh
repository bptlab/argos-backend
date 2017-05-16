#!/bin/bash

envsubst \
    < src/main/resources/argos-backend.properties \
    > src/main/resources/argos-backend.properties
mvn clean install -DskipTests
java -jar target/argos-backend.jar \
    -DargosBackendExternalHost=${HOST} \
    -DeventProcessingPlatformHost=${HOST} \
    -DdatabaseConnectionHost=${MYSQL_PORT_3306_TCP_ADDR}:${MYSQL_PORT_3306_TCP_PORT} \
    -DdatabaseConnectionUsername=${MYSQL_ENV_MYSQL_USER} \
    -DdatabaseConnectionPassword=${MYSQL_ENV_MYSQL_PASSWORD}