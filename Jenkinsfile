pipeline {
    agent any
    stages {
        stage('init') {
            steps {
                script {
                    groupId = sh script: 'grep -oPm1 "(?<=<groupId>)[^<]+" "pom.xml"', returnStdout: true
                    groupId = groupId.substring(0, groupId.length() - 1)
                    groupId = groupId.replaceAll("\\.", "/")
                    artifactId = sh script: 'grep -oPm1 "(?<=<artifactId>)[^<]+" "pom.xml"', returnStdout: true
                    artifactId = artifactId.substring(0, artifactId.length() - 1)
                    artifactVersion = sh script: 'grep -oPm1 "(?<=<version>)[^<]+" "pom.xml"', returnStdout: true
                    artifactVersion = artifactVersion.substring(0, artifactVersion.length() - 1)
                    timestamp = sh(returnStdout: true, script: 'echo $(date +%Y%m%d%H%M%S)').trim()
                    imageName = "${groupId}/${artifactId}"
                    imageTag = "${artifactVersion}.${BUILD_NUMBER}.${timestamp}"
                }
            }
        }
        stage('Build Docker image') {
            steps {
                sh "docker build --no-cache --rm -t '${imageName}':'${imageTag}' ."
            }
        }
        stage('Push Docker image') {
            steps {
                sh 'docker login --username=pgogol26@gmail.com --password=Qzwsdcrf12.'
                sh 'docker push jocker1234/as'
            }
        }
    }
}