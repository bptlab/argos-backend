#!/bin/bash

mvn clean install -DskipTests
java -jar target/argos-backend.jar \
    -DargosBackendExternalHost=${HOST} \
    -DeventProcessingPlatformHost=${HOST} \
    -DdatabaseConnectionHost=database:3306 \
    -DdatabaseConnectionUsername=${MYSQL_USER} \
    -DdatabaseConnectionPassword=${MYSQL_ROOT_PASSWORD}