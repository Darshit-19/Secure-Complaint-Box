@echo off
echo Building Secure Complaint Box for Railway deployment...
echo.

echo Step 1: Building Tailwind CSS...
npm run build:css:prod
if %errorlevel% neq 0 (
    echo ❌ Tailwind CSS build failed
    pause
    exit /b 1
)

echo Step 2: Building Java application...
echo Please build your Java project in Eclipse/IDE to create the WAR file
echo Make sure the WAR file is in the target/ directory
echo.

echo Step 3: Checking for WAR file...
if not exist "target\SecureComplaintBox.war" (
    echo ❌ WAR file not found in target/SecureComplaintBox.war
    echo Please build your project in Eclipse first
    pause
    exit /b 1
)

echo ✅ Build completed successfully!
echo.
echo Next steps:
echo 1. Commit and push these changes to GitHub
echo 2. Connect your GitHub repository to Railway
echo 3. Set environment variables in Railway
echo 4. Deploy!
echo.
pause 