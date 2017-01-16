# Deployment
![Quality Gate - Dev](https://bpt-lab.org/sonarqube/api/badges/gate?key=de.hpi.bpt:argos-backend:dev "Developer branch")
1. clone the git repo with command `git@gitlab.hpi.de:bptlab/argos-backend.git`
1. in argos-backend execute `mvn clean package`
1. execute `java -jar target\argos-backend-version.jar`, e.g. `java -jar target\argos-backend-0.1.jar`
1. If asked, allow app to pass firewall
