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
                sh 'unzip -p target/student-survey-service-0.0.1-SNAPSHOT.jar BOOT-INF/classes/edu/gmu/swe645/student_survey_service/StudentSurvey.class | strings | grep name'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    env.TAG = "v${System.currentTimeMillis() / 1000}"
                }
                sh '''
                    echo "Tag is $TAG"
                    docker build -t gcr.io/groupmicroservices/student-survey-service:$TAG .
                    docker tag gcr.io/groupmicroservices/student-survey-service:$TAG gcr.io/groupmicroservices/student-survey-service:latest
                '''
            }
        }


stage('Push Docker Image') {
    steps {
        withCredentials([file(credentialsId: "${CREDENTIALS_ID}", variable: 'GCP_KEY')]) {
            sh '''
                gcloud auth activate-service-account --key-file=$GCP_KEY
                gcloud config set project $PROJECT_ID
                docker push gcr.io/groupmicroservices/student-survey-service:$TAG
                docker push gcr.io/groupmicroservices/student-survey-service:latest
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
            '''
            sh "kubectl set image deployment/student-survey-deployment student-survey=${env.IMAGE_NAME}"
        }
    }
}


}
}