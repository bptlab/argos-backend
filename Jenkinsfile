node {
	stage 'Checkout'
		checkout scm

	stage 'Build'
		configFileProvider(
			[configFile(fileId: '17c115ad-59c3-411c-9767-774e192ad35a', variable: 'MAVEN_SETTINGS')]) {
			echo $BRANCH_NAME
			sh """mvn -s $MAVEN_SETTINGS -Premote,chimera113  clean install tomcat7:redeploy  -Ddb.schema=JEngineV2_${env.BRANCH_NAME/-/} $SONAR_MAVEN_GOAL -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_AUTH_TOKEN -Dsonar.branch=${env.BRANCH_NAME/-/}"""
		}
}
