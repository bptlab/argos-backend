#!/bin/bash

mvn clean install -DskipTests
java -jar target/argos-backend.jar \
    -DargosBackendExternalHost=${HOST} \
    -DeventProcessingPlatformHost=${HOST} \
    -DdatabaseConnectionHost=database \
    -DdatabaseConnectionUsername=${MYSQL_USER} \
    -DdatabaseConnectionPassword=${MYSQL_ROOT_PASSWORD}