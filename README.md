# CI/CD — deploy, then load, then stress

After `step-9` and `step-10` are merged into `noam-gatling-9-10`, Jenkins should run this on every Git change:

**Checkout → deploy JSP to Tomcat → Gatling load (3 min) → Gatling stress (3 min)**

Create **three** Pipeline jobs. Names must match exactly.

## 1. Deploy job

- Type: Pipeline
- Definition: Pipeline script from SCM
- Git: `https://github.com/nevoben1/JspApp.git`
- Branch: `noam-gatling-9-10`
- Script path: `Jenkinsfile`

This job copies the app to:

`C:\Program Files\Apache Software Foundation\Tomcat 8.5\webapps\eldad-noam-nevo-itamar-amit`

then starts `Gatling_Load_Test`.

If you already have a deploy job, keep it and add this stage after deploy:

```groovy
stage('Start load test job') {
    steps {
        build job: 'Gatling_Load_Test', wait: false, propagate: false
    }
}
```

## 2. Job `Gatling_Load_Test`

- Pipeline script from SCM
- Same repo and branch
- Script path: `jenkins/Gatling_Load_Test.Jenkinsfile`

Runs the 3-minute load test. On success it starts `Gatling_Stress_Test`.

## 3. Job `Gatling_Stress_Test`

- Pipeline script from SCM
- Same repo and branch
- Script path: `jenkins/Gatling_Stress_Test.Jenkinsfile`

Runs the 3-minute stress test.

## Jenkins environment

**Manage Jenkins → System → Global properties → Environment variables**

| Name | Value |
|---|---|
| `GATLING_HOME` | Folder that contains `bin\gatling.bat` |

Optional: `APP_BASE_URL` = `http://localhost:8080`, `APP_PATH` = `/eldad-noam-nevo-itamar-amit/index.jsp`

Tomcat must be running. After stress, restart Tomcat before a live demo.

## Check

Push a small commit to `noam-gatling-9-10`. The deploy job should run by itself, then load, then stress. Do not click Build Now on the Gatling jobs.
