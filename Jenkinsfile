pipeline {
    agent any

    environment {
        PROJECT_ID = 'groupmicroservices'
        CLUSTER_NAME = 'student-survey-cluster'
        CLUSTER_ZONE = 'us-central1-a'
        CREDENTIALS_ID = 'jenkins-gcp-key'
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                git branch: 'main', url: 'https://github.com/charishmasetty/grouprepository'
            }
        }

        stage('Maven Build') {
            steps {
                sh './mvnw clean package -DskipTests'
                sh 'unzip -p target/student-survey-service-0.0.1-SNAPSHOT.jar BOOT-INF/classes/edu/gmu/swe645/student_survey_service/StudentSurvey.class | strings | grep Survey || echo "Not Found"'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    env.TAG = "v${System.currentTimeMillis() / 1000}"
                }
                sh """
                    echo "Building image with tag: $TAG"
                    docker build --no-cache -t gcr.io/$PROJECT_ID/student-survey-service:$TAG .
                    docker tag gcr.io/$PROJECT_ID/student-survey-service:$TAG gcr.io/$PROJECT_ID/student-survey-service:latest
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([file(credentialsId: CREDENTIALS_ID, variable: 'GCP_KEY')]) {
                    sh """
                        gcloud auth activate-service-account --key-file=$GCP_KEY
                        gcloud config set project $PROJECT_ID
                        docker push gcr.io/$PROJECT_ID/student-survey-service:$TAG
                        docker push gcr.io/$PROJECT_ID/student-survey-service:latest
                    """
                }
            }
        }

        stage('Deploy to GKE') {
            steps {
                withCredentials([file(credentialsId: CREDENTIALS_ID, variable: 'GCP_KEY')]) {
                    sh """
                        gcloud auth activate-service-account --key-file=$GCP_KEY
                        gcloud config set project $PROJECT_ID
                        gcloud container clusters get-credentials $CLUSTER_NAME --zone $CLUSTER_ZONE
                        kubectl set image deployment/student-survey-deployment student-survey=gcr.io/$PROJECT_ID/student-survey-service:$TAG
                        kubectl rollout status deployment/student-survey-deployment
                    """
                }
            }
        }
    }
}
