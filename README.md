# CI/CD — deploy, then load, then stress

Jobs watch **`master`**, not `noam-gatling-9-10`. Merge this branch into `master` before the pipeline will run on a push.

On every Git change to `master`:

**Checkout → deploy JSP to Tomcat → Gatling load (3 min) → Gatling stress (3 min)**

The start-next-job stages are already in the Jenkinsfiles. Create the three jobs below. Names must match exactly.

## Prerequisites

**Jenkins environment** — Manage Jenkins → System → Global properties → Environment variables

| Name | Value |
|---|---|
| `GATLING_HOME` | Folder that contains `bin\gatling.bat` |

Optional: `APP_BASE_URL` = `http://localhost:8080`, `APP_PATH` = `/eldad-noam-nevo-itamar-amit/index.jsp`

**Tomcat**

- The Windows user that runs Jenkins must be able to write to `C:\Program Files\Apache Software Foundation\Tomcat 8.5\webapps\`.
- Tomcat must be running.
- After stress, restart Tomcat before a live demo.

**Trigger on push**

- Preferred: GitHub webhook to `http://<JENKINS-HOST>:<PORT>/github-webhook/`, and on the deploy job check **GitHub hook trigger for GITScm polling**.
- Fallback: the deploy `Jenkinsfile` already uses `pollSCM` once a minute if there is no webhook.

## 1. Deploy job

- Type: Pipeline
- Definition: Pipeline script from SCM
- Git: `https://github.com/nevoben1/JspApp.git`
- Branch: `master`
- Script path: `Jenkinsfile`
- Triggers: GitHub hook (if webhook is set)

This job copies the app to:

`C:\Program Files\Apache Software Foundation\Tomcat 8.5\webapps\eldad-noam-nevo-itamar-amit`

then starts `Gatling_Load_Test` and **waits** for it (`wait: true`). If load fails, the deploy job fails.

If you keep an older deploy script instead of this `Jenkinsfile`, add this stage after deploy (do not add it twice):

```groovy
stage('Start load test job') {
    steps {
        build job: 'Gatling_Load_Test', wait: true
    }
}
```

## 2. Job `Gatling_Load_Test`

- Type: Pipeline
- Definition: Pipeline script from SCM (same repo and branch)
- Script path: `jenkins/Gatling_Load_Test.Jenkinsfile`

Runs the 3-minute load test. On success it starts `Gatling_Stress_Test` and waits (`wait: true`). That stage is already in the file. Do not paste it again.

## 3. Job `Gatling_Stress_Test`

- Type: Pipeline
- Definition: Pipeline script from SCM (same repo and branch)
- Script path: `jenkins/Gatling_Stress_Test.Jenkinsfile`

Runs the 3-minute stress test. High KO is expected.

## Check

Merge this work into `master` first. Then push a small commit to `master`. The deploy job should start, then load, then stress. Do not click **Build Now** on the Gatling jobs.
