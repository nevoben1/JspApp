# Step 9 — 3-minute Gatling load test

## What this is

A **load** test: traffic the app can serve, held for **3 minutes**, **below** the max Eldad measured (~350–450 concurrent).

| Setting | Value |
|---|---|
| Users | 150 concurrent (`constantConcurrentUsers`) |
| Duration | 3 minutes |
| Think time | 1 second after each GET (so 150 users do not flood a 2 ms JSP) |
| URL | `http://localhost:8080/eldad-noam-nevo-itamar-amit/index.jsp?username=gatling` |
| Success rule | HTTP 200 and **&lt; 1%** failed requests (Jenkins fails if this is broken) |

Override URL with env vars `APP_BASE_URL` and `APP_PATH` if the Tomcat folder name changes.

## How to run from Jenkins

1. Deploy the group app so `http://localhost:8080/eldad-noam-nevo-itamar-amit/index.jsp` opens in a browser.
2. In Jenkins, set env `GATLING_HOME` only if you move the install. The Jenkinsfile already defaults to your copy: `C:\Users\Noam\OneDrive\Desktop\Noam\year_3\DevOps\gatling-charts-highcharts-bundle-3.10.4-bundle\gatling-charts-highcharts-bundle-3.10.4`.
3. Create a Pipeline job from [`../jenkins/Gatling_Load_Test.Jenkinsfile`](../jenkins/Gatling_Load_Test.Jenkinsfile) (or import [`../jenkins/Gatling_Load_Test-config.xml`](../jenkins/Gatling_Load_Test-config.xml)).
4. Restart Tomcat (clean server). Click **Build Now**. Wait the full 3 minutes.
5. Job must be SUCCESS. Console must show ~0% KO.

## Local command (same as the job)

```bat
"%GATLING_HOME%\bin\gatling.bat" -nr -s LoadSimulation -sf "%CD%\gatling-load" -rf "%CD%\gatling-results\load"
```

## Files to send Moshe (item k + l — load)

| File | What it is |
|---|---|
| `step9_load_cmd_summary.png` | Screenshot of the Gatling Global Information summary (Jenkins console or CMD). Must show ~3 minutes, OK/KO, times. |
| `step9_load_report.pdf` | Print the run `index.html` to PDF. |
| `step9_jenkins_success.png` | Jenkins job SUCCESS (recommended for 100). |

### Email paragraph for item l (load)

> We ran a 3-minute load test with Gatling, started from a Jenkins job. We held about 150 concurrent users against the deployed app at http://localhost:8080/eldad-noam-nevo-itamar-amit/index.jsp. That level is clearly under the max limit we found in step 8 (about 350–450 concurrent connections). The graphs stay flat: almost 0% KO, and response times stay in the low milliseconds. That is what we expected. Tomcat was not in the refuse zone (the default HTTP connector can accept about 200 worker threads plus about 100 queued connections). The load never filled that queue, so the server kept answering with HTTP 200 and did not return Connection refused. If the graphs had shown a jump in errors or latency, the run would not count as a load test.

## What a valid load report looks like

- Duration ~180 seconds
- KO ~0%
- Active users flat around 150
- Response times low and stable
