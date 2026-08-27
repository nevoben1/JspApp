Selenium tests job
- Type: Freestyle
- Git: https://github.com/nevoben1/JspApp.git, branch Itamar
- Build step (Windows batch):
  selenium-side-runner --base-url http://localhost:8080 -c "browserName=MicrosoftEdge" hit-app-tests.side --output-directory=selenium-results
- Post-build: Archive selenium-results/*.json
- Prereq on the Jenkins machine: npm install -g selenium-side-runner edgedriver


npm install -g selenium-side-runner edgedriver
selenium-side-runner --version