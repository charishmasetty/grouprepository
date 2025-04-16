pipeline {
    agent any

    environment {
        PROJECT_ID = 'groupmicroservices'         
        CLUSTER_NAME = 'student-survey-cluster'    
        CLUSTER_ZONE = 'us-central1-a'             
        CREDENTIALS_ID = 'jenkins-gcp-key'         
        IMAGE_NAME = 'student-survey-service'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/charishmasetty/grouprepository.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker system prune -a -f'
                sh 'docker rmi charishmasetty/${IMAGE_NAME}:latest || true'
                sh 'docker build -t charishmasetty/${IMAGE_NAME}:latest .'
            }
        }

        stage('Push Docker Image to GCR') {
            steps {
                withCredentials([file(credentialsId: "${CREDENTIALS_ID}", variable: 'GCP_KEY')]) {
                    sh '''
                        gcloud auth activate-service-account --key-file=$GCP_KEY
                        gcloud config set project $PROJECT_ID
                        docker tag charishmasetty/${IMAGE_NAME}:latest gcr.io/$PROJECT_ID/${IMAGE_NAME}:latest
                        docker push gcr.io/$PROJECT_ID/${IMAGE_NAME}:latest
                    '''
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

                        # Force update to make sure the new image is used
                        kubectl set image deployment/student-survey-deployment student-survey=gcr.io/$PROJECT_ID/${IMAGE_NAME}:latest --record=true

                        # Wait for rollout to complete
                        kubectl rollout status deployment/student-survey-deployment
                    '''
                }
            }
        }
    }
}
