@echo off
setlocal

:: Backend Start
echo Starting backend...
start cmd /k "cd /d D:\daouoffice && gradlew.bat bootRun"

:: Frontend Start
echo Starting frontend...
start cmd /k "cd /d D:\daouoffice\org-frontend && npx cross-env HOST=0.0.0.0 react-scripts start"

endlocal