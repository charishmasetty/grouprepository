pipeline {
    agent any

    environment {
        PROJECT_ID = 'groupmicroservices'         
        CLUSTER_NAME = 'student-survey-cluster'    // Your GKE cluster name
        CLUSTER_ZONE = 'us-central1-a'             // Your GKE zone
        CREDENTIALS_ID = 'jenkins-gcp-key'         // Jenkins credential ID for the JSON key
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/charishmasetty/grouprepository'
            }
        }

        stage('Maven Build') {
            steps {
                // Run the Maven build to generate the JAR file.
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                // sh 'ls -la target/'
                sh 'docker system prune -a -f'
                sh 'docker rmi charishmasetty/student-survey-service:latest || true'
                sh 'docker build -t charishmasetty/student-survey-service:latest .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([file(credentialsId: "${CREDENTIALS_ID}", variable: 'GCP_KEY')]) {
                    
                    sh 'gcloud auth activate-service-account --key-file=$GCP_KEY'
                    sh 'gcloud config set project $PROJECT_ID'
                    sh 'docker tag charishmasetty/student-survey-service:latest gcr.io/groupmicroservices/student-survey-service:latest'
                    sh 'docker push gcr.io/$PROJECT_ID/student-survey-service:latest'
            }
        }
        }

        stage('Deploy to GKE') {
            steps {
                withCredentials([file(credentialsId: "${CREDENTIALS_ID}", variable: 'GCP_KEY')]) {
                    sh '''
                        gcloud auth activate-service-account --key-file=$GCP_KEY
                        gcloud config set project $PROJECT_ID
                        gcloud container clusters get-credentials $CLUSTER_NAME --zone $CLUSTER_ZONE

                        kubectl apply -f student-survey-deployment.yaml
                        kubectl apply -f student-survey-service.yaml
                        kubectl rollout restart deployment student-survey-deployment
                        kubectl rollout status deployment student-survey-deployment
                    '''
                        sh 'kubectl set image deployment/student-survey-deployment student-survey=charishmasetty/student-survey-service:latest --record=true'
                }
            }
        }
}
}