pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                withMaven(maven: 'mvn') {
                    sh "mvn clean package"
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