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
                sh 'ls -la target/'
                sh 'docker build -t gcr.io/$PROJECT_ID/student-survey-service:latest .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([file(credentialsId: "${CREDENTIALS_ID}", variable: 'GCP_KEY')]) {
                    sh '''
                        gcloud auth activate-service-account --key-file=$GCP_KEY
                        gcloud config set project $PROJECT_ID
                        gcloud auth configure-docker --quiet
                        docker push gcr.io/$PROJECT_ID/student-survey-service:latest
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
                    '''
                }
            }
        }

      stage('Verify Deployment Logs') {
            steps {
                sh 'kubectl get pods'
                // Display logs from the first pod with label app=student-survey
                sh 'kubectl logs $(kubectl get pod -l app=student-survey -o jsonpath="{.items[0].metadata.name}")'
            }
        }

    }
}
