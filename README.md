[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/bptlab/argos-backend/master/LICENSE)
[![GitHub release](https://img.shields.io/badge/release-1.1-blue.svg)](https://github.com/bptlab/argos-backend/releases/latest)

[![Quality Gate - Dev](https://bpt-lab.org/sonarqube/api/badges/gate?key=de.hpi.bpt:argos-backend:dev "Developer branch")](https://bpt-lab.org/sonarqube/overview?id=de.hpi.bpt%3Aargos-backend)
[![Build Status](https://travis-ci.org/bptlab/argos-backend.svg?branch=master)](https://travis-ci.org/bptlab/argos-backend "Default branch")
[![Coverage Status](https://coveralls.io/repos/github/bptlab/argos-backend/badge.svg?branch=master)](https://coveralls.io/github/bptlab/argos-backend?branch=master)

# Argos Backend

## Prerequisites
* MySQL/MariaDB database
* Java 8 (tested with jdk_1.8.0_31 and above)
* Maven 3 (tested with Maven 3.2.5 and above)
* git
* (Docker)

## Local installation
1. Clone the git repo at [https://github.com/bptlab/argos-backend.git](https://github.com/bptlab/argos-backend.git).
1. Copy and rename the template property file in ```src/main/resources``` to ```argos-backend.properties``` and change the values as required in your environment.
1. Execute the command ```mvn install``` (```mvn clean install``` if you want to delete your old jar file, ```mvn 
install -DskipTests``` if you want to skip the tests). This will compile the project into a jar file in the target 
folder.  
1. execute `java -jar target\argos-backend.jar`
1. If asked, allow app to pass firewall

## Recommended development environment
* IntelliJ IDE
    * SonarLint-Plugin (to use the project specific Lint rules)
    * Configured database connection in IntelliJ
* Database and other applications server
    * Tool by Johannes Schneider (Hannes01071995) -- very lightweight, but Windows only
    * Xampp -- for beginners
    * Docker (see below)


## Build Process
We integrated [Travis CI](http://travis-ci.org/bptlab) as a continuous integration tool. It builds the project 
after every commit, sends a sonarLint diagnose to the Sonarqube server at the chair and executes all tests.


## Deployment Process
### Deploy a database.
```
docker run --name argos-database -e MYSQL_ROOT_PASSWORD=[secret] -e MYSQL_USER=[user] -e MYSQL_PASSWORD=[password] -d mysql:latest
```
e.g.
```
docker run --name argos-database -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_USER=user -e MYSQL_PASSWORD=password -d mysql:latest
```

Then grant new user root privileges: 
1. Execute ```mysql -u root -p``` in container. Enter your root password.
1. Execute ```GRANT ALL PRIVILEGES ON *.* TO user@localhost WITH GRANT OPTION;```


### Deploy Unicorn.
```
docker run --name unicorn -p 8080:8080 --link argos-database:mysql -d bptlab/unicorn:latest
```
### Deploy Argos. 
Windows only: please share the drive in your docker settings, that your event types directory is located on.
```
docker run --name argos -p 8989:8989 --link argos-database:mysql --link unicorn:unicorn -d -v [PATH TO DIR WITH EVENT TYPES WITH TRAILING SLASH]:/target/classes/event_types bptlab/argos:latest
```
Note:
- It's important to deploy step by step, because of the --link flags. Link flags only work on currently existing containers.
- The -p flag exposes a port on the Docker host machine. If that port is already in use on your system you should change the second parameter of the -p flag.


## Troubleshooting
1. Can't access the database although MySQL/MariaDB server is running on the correct port and server? 
    * Make sure that your user has all the rights that he needs for the intended operation.

1. Server doesn't start?
	* Make sure the database is running and accessible from your machine.

1. Can't reach your server via HTTP?
	* Make sure the sure is running. 
	* Make sure that the used port is exposed on your firewall.
