FROM maven:3-jdk-8-alpine

WORKDIR /argos-backend

COPY . .

RUN mv src/main/resources/argos-backend_template.properties src/main/resources/argos-backend.properties

EXPOSE 8989

CMD mvn clean install -DskipTests && java -jar target/argos-backend.jar