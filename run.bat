@echo off
echo Starting Token Analyzer...
mvn javafx:run
if %errorlevel% neq 0 (
    echo Failed to start application!
    pause
    exit /b %errorlevel%
)
pause