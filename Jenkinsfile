pipeline {
    agent any

    parameters {
        string(
            name: 'TARGET_URL',
            defaultValue: 'http://localhost:8080/JspApp/index.jsp',
            description: 'The target application URL to monitor'
        )
        string(
            name: 'TIMEOUT_SEC',
            defaultValue: '5',
            description: 'Request timeout in seconds'
        )
        string(
            name: 'SLOW_THRESHOLD_MS',
            defaultValue: '1000',
            description: 'Threshold in milliseconds to consider response as SLOW'
        )
    }

    triggers {
        cron('H/5 * * * *')
    }

    stages {
        stage('Health Check') {
            steps {
                sh """
                set +x
                powershell.exe -Command '\$url = "${params.TARGET_URL}"; \$timeout = ${params.TIMEOUT_SEC}; \$threshold = ${params.SLOW_THRESHOLD_MS}; \$s=[Diagnostics.Stopwatch]::StartNew(); try { \$r=Invoke-WebRequest \$url -UseBasicParsing -TimeoutSec \$timeout; \$s.Stop(); \$t=\$s.ElapsedMilliseconds; if (\$t -lt \$threshold) { Write-Host "Status: \$(\$r.StatusCode) \$(\$r.StatusDescription) | Time: \${t}ms | State: UP (Optimal)" -ForegroundColor Green; exit 0 } else { Write-Host "Status: \$(\$r.StatusCode) \$(\$r.StatusDescription) | Time: \${t}ms | State: UP (SLOW)" -ForegroundColor Yellow; exit 0 } } catch { Write-Host "Status: FAILED / DOWN | Error: \$(\$_.Exception.Message)" -ForegroundColor Red; exit 1 }'
                """
            }
        }
    }
}
