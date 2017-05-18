#!/bin/bash

mvn clean install -DskipTests
java -jar target/argos-backend.jar \
    argosBackendExternalHost=${HOST} \
    eventProcessingPlatformHost=${HOST} \
    databaseConnectionHost=database:3306 \
    databaseConnectionUsername=${MYSQL_USER} \
    databaseConnectionPassword=${MYSQL_ROOT_PASSWORD}