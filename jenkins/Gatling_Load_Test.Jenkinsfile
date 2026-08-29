// Job name in Jenkins must be exactly: Gatling_Load_Test
// Started by the deploy job. When this job finishes, it starts Gatling_Stress_Test.

pipeline {
    agent any

    environment {
        GATLING_HOME = "${env.GATLING_HOME ?: 'C:\\Users\\Noam\\OneDrive\\Desktop\\Noam\\year_3\\DevOps\\gatling-charts-highcharts-bundle-3.10.4-bundle\\gatling-charts-highcharts-bundle-3.10.4'}"
        APP_BASE_URL = "${env.APP_BASE_URL ?: 'http://localhost:8080'}"
        APP_PATH     = "${env.APP_PATH ?: '/eldad-noam-nevo-itamar-amit/index.jsp'}"
        RESULTS_DIR  = "${env.WORKSPACE}\\gatling-results\\load"
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 10, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tomcat') {
            steps {
                bat '''
                    @echo off
                    curl.exe -s -o NUL -w "HTTP %%{http_code}\\n" "%APP_BASE_URL%%APP_PATH%"
                    if errorlevel 1 (
                      echo Tomcat did not answer. Deploy must succeed first.
                      exit /b 1
                    )
                '''
            }
        }

        stage('Gatling load 3 min') {
            steps {
                bat '''
                    @echo off
                    set "GATLING_BAT=%GATLING_HOME%\\bin\\gatling.bat"
                    if not exist "%GATLING_BAT%" (
                      echo Set Jenkins env GATLING_HOME to the folder that contains bin\\gatling.bat
                      exit /b 1
                    )
                    if not exist "%WORKSPACE%\\gatling-load\\LoadSimulation.java" (
                      echo LoadSimulation.java missing. Merge branch noam-gatling-9-10 first.
                      exit /b 1
                    )
                    if not exist "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"
                    set "NO_PAUSE=1"
                    call "%GATLING_BAT%" -rm local ^
                      -s LoadSimulation ^
                      -sf "%WORKSPACE%\\gatling-load" ^
                      -rf "%RESULTS_DIR%" ^
                      -rd "step9_load_3min" ^
                      -erjo "-Xms512M -Xmx2G"
                '''
            }
        }

        stage('Start stress test job') {
            steps {
                build job: 'Gatling_Stress_Test', wait: false, propagate: false
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'gatling-results/load/**', allowEmptyArchive: true
        }
        success {
            echo 'Load finished. Gatling_Stress_Test was started.'
        }
        failure {
            echo 'Load failed. Stress was not started.'
        }
    }
}
