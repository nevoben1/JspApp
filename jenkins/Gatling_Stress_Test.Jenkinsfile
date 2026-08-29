// Paste this into Jenkins as "Pipeline script" (not from SCM) until step-10 is on GitHub.
// Job name must be: Gatling_Stress_Test

pipeline {
    agent any

    environment {
        PROJECT_DIR  = 'C:\\Users\\Noam\\OneDrive\\Desktop\\Noam\\year_3\\DevOps\\Final_project\\JspApp'
        GATLING_HOME = "${env.GATLING_HOME ?: 'C:\\Users\\Noam\\OneDrive\\Desktop\\Noam\\year_3\\DevOps\\gatling-charts-highcharts-bundle-3.10.4-bundle\\gatling-charts-highcharts-bundle-3.10.4'}"
        APP_BASE_URL = 'http://localhost:8080'
        APP_PATH     = '/eldad-noam-nevo-itamar-amit/index.jsp'
        RESULTS_DIR  = "${PROJECT_DIR}\\gatling-results\\stress"
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 10, unit: 'MINUTES')
    }

    stages {
        stage('Verify Tomcat') {
            steps {
                bat '''
                    @echo off
                    curl.exe -s -o NUL -w "HTTP %%{http_code}\\n" "%APP_BASE_URL%%APP_PATH%"
                    if errorlevel 1 (
                      echo Tomcat did not answer. Restart Tomcat if stress killed it.
                      exit /b 1
                    )
                '''
            }
        }

        stage('Gatling stress 3 min') {
            steps {
                bat '''
                    @echo off
                    set "GATLING_BAT=%GATLING_HOME%\\bin\\gatling.bat"
                    if not exist "%GATLING_BAT%" (
                      echo GATLING_HOME is wrong: %GATLING_HOME%
                      exit /b 1
                    )
                    if not exist "%PROJECT_DIR%\\gatling-stress\\StressSimulation.java" (
                      echo StressSimulation.java not found. Stay on branch step-10.
                      exit /b 1
                    )
                    if not exist "%RESULTS_DIR%" mkdir "%RESULTS_DIR%"
                    set "NO_PAUSE=1"
                    call "%GATLING_BAT%" -rm local ^
                      -s StressSimulation ^
                      -sf "%PROJECT_DIR%\\gatling-stress" ^
                      -rf "%RESULTS_DIR%" ^
                      -rd "step10_stress_3min" ^
                      -erjo "-Xms512M -Xmx2G"
                '''
            }
        }
    }

    post {
        always {
            echo 'Open the file:/// index.html line in the console, print to PDF for Moshe.'
        }
        success {
            echo 'Stress finished. Screenshot Console Output Global Information. KO is expected.'
        }
        failure {
            echo 'Job failed (Tomcat/Gatling path). Restart Tomcat and check GATLING_HOME.'
        }
    }
}
