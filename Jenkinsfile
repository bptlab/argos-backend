node {
	stage('Checkout') {
		checkout scm
	}

	stage('Build') {
		sh """mvn clean install $SONAR_MAVEN_GOAL -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_AUTH_TOKEN -Dsonar.branch=$BRANCH_NAME"""
	}
}