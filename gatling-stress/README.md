# Step 10 — 3-minute Gatling stress test

## What this is

A **stress** test: concurrency **crosses** Eldad’s ceiling (~350–450) over **3 minutes**. The graphs should **not** look healthy.

| Setting | Value |
|---|---|
| Users | Ramp 100 → 800 concurrent |
| Duration | 3 minutes |
| Think time | None (connections stack on a fast JSP) |
| URL | `http://localhost:8080/eldad-noam-nevo-itamar-amit/index.jsp?username=gatling` |
| Jenkins | Job stays SUCCESS even with many KO |

## How to run from Jenkins

1. Same deploy and `GATLING_HOME` as step 9.
2. Pipeline: [`../jenkins/Gatling_Stress_Test.Jenkinsfile`](../jenkins/Gatling_Stress_Test.Jenkinsfile) (or import [`../jenkins/Gatling_Stress_Test-config.xml`](../jenkins/Gatling_Stress_Test-config.xml)).
3. Restart Tomcat. **Build Now**. Wait 3 minutes.

## Local command

```bat
"%GATLING_HOME%\bin\gatling.bat" -nr -s StressSimulation -sf "%CD%\gatling-stress" -rf "%CD%\gatling-results\stress"
```

## Files to send Moshe (item k + l — stress)

| File | What it is |
|---|---|
| `step10_stress_cmd_summary.png` | Gatling summary showing KO / Connection refused. |
| `step10_stress_report.pdf` | Print `index.html` to PDF. |
| `step10_jenkins_success.png` | Jenkins SUCCESS (recommended). |

### Email paragraph for item l (stress)

> We ran a 3-minute stress test with Gatling, started from a Jenkins job. Concurrent users ramped from 100 to 800 against the same Tomcat app. That starts under the step-8 max (about 350–450 concurrent) and then goes past it. The graphs change after we cross that band: failed requests rise, and the typical error is Connection refused. That matches Tomcat’s default HTTP connector (about 200 worker threads plus about 100 in the accept queue). Once that queue is full, Tomcat rejects new TCP connections instead of slowing down smoothly. Latency on the requests that still succeed also goes up because the remaining workers are busy. This is expected for a stress test. A green, 0% KO report would mean we never actually stressed the server.

## What a valid stress report looks like

- Duration ~180 seconds
- Active users climb toward 800
- KO appears after crossing ~350–450 (often `Connection refused`)
- Do not mail a perfectly green stress run
