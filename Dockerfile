FROM maven:3

ARG frontendBranch=master
ARG backendBranch=master

RUN curl -sL https://deb.nodesource.com/setup_7.x | bash - && \
	apt-get install -y nodejs

RUN git clone -b $frontendBranch --single-branch https://github.com/bptlab/argos-frontend /argos-frontend && \
	mv /argos-frontend/src/config/argosConfig_template.js /argos-frontend/src/config/argosConfig.js && \
	cd /argos-frontend && npm install && npm run build

RUN git clone -b $backendBranch --single-branch https://github.com/bptlab/argos-backend /argos-backend && \
	mv /argos-backend/src/main/resources/argos-backend_template.properties /argos-backend/src/main/resources/argos-backend.properties

RUN mv /argos-frontend/build/* /argos-backend/src/main/resources/public/

RUN cd /argos-backend && mvn clean install -DskipTests
RUN mv /argos-backend/target /target && \
    rm -rf /argos-frontend && rm -rf /argos-backend

EXPOSE 8989

CMD ["sh", "-c", "java -Dorg.slf4j.simpleLogger.defaultLogLevel=error -DargosBackendHost=\"http://`hostname -i`\"-DdatabaseConnectionHost=\"${MYSQL_PORT_3306_TCP_ADDR}:${MYSQL_PORT_3306_TCP_PORT}\" -DdatabaseConnectionUsername=\"${MYSQL_ENV_MYSQL_USER}\" -DdatabaseConnectionPassword=\"${MYSQL_ENV_MYSQL_PASSWORD}\" -DeventPlatformHost=\"http://${UNICORN_PORT_8080_TCP_ADDR}:${UNICORN_PORT_8080_TCP_PORT}\" -jar /target/argos-backend.jar"]
