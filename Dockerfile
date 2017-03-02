FROM maven:3

COPY . .

RUN mv src/main/resources/argos-backend_template.properties src/main/resources/argos-backend.properties && mvn clean install -DskipTests

EXPOSE 8989
CMD ["sh", "-c", "java -DdatabaseConnectionHost=\"${MYSQL_PORT_3306_TCP_ADDR}:${MYSQL_PORT_3306_TCP_PORT}\" -DeventPlatformHost=\"http://${UNICORN_PORT_8080_TCP_ADDR}:${UNICORN_PORT_8080_TCP_PORT}\" -jar target/argos-backend.jar"]