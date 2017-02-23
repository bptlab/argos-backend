#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
    mvn verify sonar:sonar -DskipTests \
        -Dsonar.host.url=$SONAR_HOST_URL \
        -Dsonar.login=$SONAR_AUTH_TOKEN \
        -Dsonar.branch=$TRAVIS_BRANCH \

else
    echo "Pull request branch"
    mvn verify sonar:sonar -DskipTests \
        -Dsonar.host.url=$SONAR_HOST_URL \
        -Dsonar.login=$SONAR_AUTH_TOKEN \
        -Dsonar.branch=$TRAVIS_BRANCH \
        -Dsonar.analysis.mode=preview \
        -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
        -Dsonar.github.oauth=$GITHUB_ACCESS_TOKEN \

fi
