@echo off

call :isAdmin

if %errorlevel% == 0 (
    goto :run
) else (
    echo Error: Run as administrator!
    pause
)

exit /b

:isAdmin
fsutil dirty query %systemdrive% >nul
exit /b

:run

echo Write to Win-Registry manifest folder information[HKEY_CURRENT_USER\Software\Google\Chrome\NativeMessagingHosts\com.croc.external_app]
reg add HKEY_CURRENT_USER\Software\Google\Chrome\NativeMessagingHosts\com.croc.external_app /f /ve /t REG_SZ /d "%~dp0com.croc.external_app-win.json"

echo . . .
echo Make run app bat-file: %~dp0tn-host-app.bat
echo @echo off > "%~dp0tn-host-app.bat"
echo java -cp ./;./*;./lib/*;  ru.croc.chromenative.HostApplication >> "%~dp0tn-host-app.bat"

echo . . .
pause
