// Job name in Jenkins must be exactly: Gatling_Stress_Test
// Started automatically when Gatling_Load_Test finishes. Do not add a timer.

pipeline {
    agent any

    environment {
        GATLING_HOME = "${env.GATLING_HOME ?: 'C:\\Users\\Noam\\OneDrive\\Desktop\\Noam\\year_3\\DevOps\\gatling-charts-highcharts-bundle-3.10.4-bundle\\gatling-charts-highcharts-bundle-3.10.4'}"
        APP_BASE_URL = 'http://localhost:8080'
        APP_PATH     = '/eldad-noam-nevo-itamar-amit/index.jsp'
        RESULTS_DIR  = "${env.WORKSPACE}\\gatling-results\\stress"
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
                      echo Tomcat did not answer. Start Tomcat and confirm the group app is deployed.
                      exit /b 1
                    )
                '''
            }
        }

        stage('Run Gatling stress test') {
            steps {
                bat '''
                    @echo off
                    set "GATLING_BAT=%GATLING_HOME%\\bin\\gatling.bat"
                    if not exist "%GATLING_BAT%" (
                      echo GATLING_HOME is wrong: %GATLING_HOME%
                      echo Set a Jenkins env var GATLING_HOME to the Gatling 3.10 bundle folder.
                      exit /b 1
                    )

                    if not exist "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"

                    set "NO_PAUSE=1"

                    rem -rm local skips the Enterprise menu. Do not use -nr — we need the HTML report.
                    call "%GATLING_BAT%" -rm local ^
                      -s StressSimulation ^
                      -sf "%WORKSPACE%\\gatling-stress" ^
                      -rf "%RESULTS_DIR%" ^
                      -rd "step10_stress_3min" ^
                      -erjo "-Xms512M -Xmx2G"
                '''
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'gatling-results/stress/**', allowEmptyArchive: true
        }
        success {
            echo 'Step 10 stress test finished. KO and Connection refused in the report are expected. Print index.html to PDF.'
        }
        failure {
            echo 'Stress pipeline failed (tooling/path), not because the app broke. Check GATLING_HOME and Tomcat.'
        }
    }
}
