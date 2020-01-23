pipeline {
    agent {
        label "jenkins-maven"
    }
    environment {
        VERSION = "${currentBuild.number}"
    }
    stages {
        stage('Compile') {
            steps {
                container('maven') {
                    sh 'mvn clean compile -U'
                }
            }
        }
        stage('Test') {
            steps {
                container('maven') {
                    sh 'mvn test'
                }
            }
        }
        stage('Javadoc') {
            steps {
                container('maven') {
                    sh 'mvn org.apache.maven.plugins:maven-javadoc-plugin:3.1.0:javadoc'
                }
            }
        }
        stage('Package') {
            steps {
                container('maven') {
                    sh 'mvn package -DskipTests -Pdeployment'
                }
            }
        }
    }
}