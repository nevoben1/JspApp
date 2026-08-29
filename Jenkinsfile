pipeline {
    agent any

    environment {
        DEPLOY_DIR = 'C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\webapps\\eldad-noam-nevo-itamar-amit'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 25, unit: 'MINUTES')
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                bat '''
                    @echo off
                    echo Deploying "%WORKSPACE%" to "%DEPLOY_DIR%"
                    if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
                    robocopy "%WORKSPACE%" "%DEPLOY_DIR%" /MIR /XD ".git" "@tmp" "gatling-load" "gatling-stress" "gatling-results" "jenkins" /XF "Jenkinsfile" ".gitignore"
                    if %ERRORLEVEL% GEQ 8 exit /b %ERRORLEVEL%
                    exit /b 0
                '''
            }
        }

        stage('Start load test job') {
            steps {
                build job: 'Gatling_Load_Test', wait: true
            }
        }
    }

    post {
        success {
            echo 'Deploy, load, and stress finished.'
        }
        failure {
            echo 'Deploy failed. Load and stress were not started.'
        }
    }
}
