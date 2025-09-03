@echo off
echo Building and Running Token Analyzer...
echo.
echo Step 1: Clean and Compile
mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)
echo Build completed successfully!
echo.
echo Step 2: Starting Application
mvn javafx:run
if %errorlevel% neq 0 (
    echo Failed to start application!
    pause
    exit /b %errorlevel%
)
echo Application closed.
pause