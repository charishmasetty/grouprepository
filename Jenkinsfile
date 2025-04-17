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
            def TAG = "v${sh(script: 'date +%s', returnStdout: true).trim()}"
            env.IMAGE_NAME = "gcr.io/groupmicroservices/student-survey-service:${TAG}"
        }

        sh 'docker system prune -a -f || true'
        sh 'docker rmi charishmasetty/student-survey-service:latest || true'
        sh "docker build -t ${env.IMAGE_NAME} ."
    }
}

stage('Push Docker Image') {
    steps {
        withCredentials([file(credentialsId: "${CREDENTIALS_ID}", variable: 'GCP_KEY')]) {
            sh '''
                gcloud auth activate-service-account --key-file=$GCP_KEY
                gcloud config set project $PROJECT_ID
            '''
            sh "docker push ${env.IMAGE_NAME}"
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
            sh "kubectl rollout status deployment/student-survey-deployment"
        }
    }
}


}
}