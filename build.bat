@echo off
echo Building Token Analyzer...
mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)
echo Build completed successfully!
pause