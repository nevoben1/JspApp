pipeline {
    agent any

    environment {
        // Tomcat webapp context this project deploys into
        DEPLOY_DIR = 'C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\webapps\\eldad-noam-nevo-itamar-amit'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
    }

    triggers {
        // Deploy on push. Preferred: configure a Git/GitHub webhook to this job
        // ("GitHub hook trigger for GITScm polling"). pollSCM is the fallback
        // when no webhook is available - checks the repo once a minute.
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

                    rem Mirror the workspace into the webapp folder.
                    rem /MIR  - mirror (copy new/changed, remove deleted)
                    rem /XD   - skip these directories
                    rem /XF   - skip these files (CI metadata, not part of the app)
                    rem robocopy exit codes 0-7 mean success; 8+ mean failure.
                    robocopy "%WORKSPACE%" "%DEPLOY_DIR%" /MIR /XD ".git" "@tmp" /XF "Jenkinsfile" ".gitignore"
                    if %ERRORLEVEL% GEQ 8 exit /b %ERRORLEVEL%
                    exit /b 0
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployed. Tomcat autoDeploy will reload the eldad-noam-nevo-itamar-amit context.'
        }
        failure {
            echo 'Deployment failed - check the stage log above.'
        }
    }
}
