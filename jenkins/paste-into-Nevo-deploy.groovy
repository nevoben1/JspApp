// If Nevo keeps HIS old deploy job, add only this stage at the end
// (after Deploy to Tomcat). Job name must be Gatling_Load_Test.

        stage('Start load test job') {
            steps {
                build job: 'Gatling_Load_Test', wait: false, propagate: false
            }
        }
