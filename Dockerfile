FROM maven:3

COPY . .

RUN mv src/main/resources/argos-backend_template.properties src/main/resources/argos-backend.properties
RUN mvn clean install -DskipTests

EXPOSE 8989
CMD ["sh", "-c", "java -DeventPlatformHost=\"http://127.0.0.1:8080\" -DdatabaseConnectionHost=\"${MYSQL_PORT_3306_TCP_ADDR}:${MYSQL_PORT_3306_TCP_PORT}\" -jar target/argos-backend.jar"]