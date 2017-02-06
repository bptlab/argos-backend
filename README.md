![Quality Gate - Dev](https://bpt-lab.org/sonarqube/api/badges/gate?key=de.hpi.bpt:argos-backend:dev "Developer branch")

# Deployment
1. clone the git repo with command `git@gitlab.hpi.de:bptlab/argos-backend.git`
1. in argos-backend execute `mvn clean package`
1. execute `java -jar target\argos-backend-version.jar`, e.g. `java -jar target\argos-backend-0.1.jar`
1. If asked, allow app to pass firewall
=======
# Argos Backend

## Prerequisites
* MySQL database running on localhost (port 3306), with user root (no password)
* Java 8 (tested with jdk_1.8.0_111)
* Maven 3 (tested with Maven 3.3.9)
* git

## Installation Guide
1. Check the prerequisites
1. Clone the git repository: [git@gitlab.hpi.de:bptlab/argos-backend.git](https://gitlab.hpi.de/bptlab/argos-backend/). Development branch is developer, issues are branched and merged after merge request by someone else.

## Local Deployment
1. In your local working directory, start your favorite cli. 
1. Execute the command ```mvn install``` (```mvn clean install``` if you want to delete your old jar file, ```mvn install -DskipTests``` if you want to skip the tests).
1. Go into the target directory
1. Execute the command ```java -jar argos-backend.jar```. The backend server will start running on localhost:8989 (default).

## Troubleshooting
1. Can't access the database although MySQL is running on the correct port and server? 
    * Make sure that your user has all the rights that he needs for the intended operation.

1. Server doesn't start?
	* Make sure the database is running and accessible from your machine.

1. Can't reach your server via HTTP?
	* Make sure the sure is running. 
	* Make sure that the used port is exposed on your firewall.