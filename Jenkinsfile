pipeline {
    agent any
    environment {
    		DOCKERHUB_CREDENTIALS=credentials('docker')
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
                sh "docker build --no-cache --rm --build-arg VERSION='${artifactVersion}' --build-arg ARTIFACT_ID='${artifactId}' -t '${imageName}':latest' ."
            }
        }
        stage('Push Docker image') {
            steps {
                sh "docker tag ${imageName} jocker1234/${imageName}"
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                sh "docker push jocker1234${imageName}"
            }
        }
    }
    post {
    		always {
    			sh 'docker logout'
    		}
    	}
}