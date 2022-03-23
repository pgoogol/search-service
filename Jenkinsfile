pipeline {
    agent any
    environment {
    		DOCKERHUB_CREDENTIALS=credentials('docker')
    		NEXUS_CREDENTIALS=credentials('nexus')
    	}
    stages {
        stage('init') {
            steps {
                script {
                    artifactId = sh script: 'grep -oPm1 "(?<=<artifactId>)[^<]+" "pom.xml"', returnStdout: true
                    artifactId = artifactId.substring(0, artifactId.length() - 1)
                    artifactVersion = sh script: 'grep -oPm1 "(?<=<version>)[^<]+" "pom.xml"', returnStdout: true
                    artifactVersion = artifactVersion.substring(0, artifactVersion.length() - 1)
                    timestamp = sh(returnStdout: true, script: 'echo $(date +%Y%m%d%H%M%S)').trim()
                    imageName = "${artifactId}"
                    imageTag = "${artifactVersion}.${BUILD_NUMBER}.${timestamp}"
                }
            }
        }
        stage('Build Docker image') {
            steps {
                sh "docker build --no-cache --rm --build-arg VERSION='${artifactVersion}' --build-arg ARTIFACT_ID='${artifactId}' -t '${imageName}':'${imageTag}' ."
            }
        }
        stage('Push Docker image to DockerHub') {
            steps {
                sh "docker tag ${imageName}:'${imageTag}' jocker1234/${imageName}:'${imageTag}'"
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                sh "docker push jocker1234/${imageName}:'${imageTag}'"
    			sh 'docker logout'
            }
        }
        stage('Push Docker image to Nexus') {
            steps {
                sh "docker tag ${imageName}:'${imageTag}' localhost:8082/${imageName}:'${imageTag}'"
                sh 'echo $NEXUS_CREDENTIALS_PSW | docker login http://localhost:8082/repository/docker-hosted/ -u $NEXUS_CREDENTIALS_USR --password-stdin'
                sh "docker push localhost:8082/${imageName}:'${imageTag}'"
    			sh 'docker logout'
            }
        }
    }
}