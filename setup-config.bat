@echo off
echo Setting up Secure Complaint Box configuration...
echo.

if exist "src\main\resources\application.properties" (
    echo Configuration file already exists!
    echo If you want to reset it, delete src\main\resources\application.properties first.
    pause
    exit /b
)

echo Copying example configuration file...
copy "src\main\resources\application.properties.example" "src\main\resources\application.properties"

if %errorlevel% equ 0 (
    echo.
    echo ✅ Configuration file created successfully!
    echo.
    echo 📝 Please edit src\main\resources\application.properties with your actual values:
    echo    - Database credentials
    echo    - Email settings
    echo    - Encryption secret
    echo.
    echo 🔒 Remember: application.properties is ignored by git for security
    echo.
) else (
    echo ❌ Failed to create configuration file
    echo Please check if the example file exists
)

pause 