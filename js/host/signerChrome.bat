@echo off

set LOG=log.txt

time /t >> %LOG%

java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 signerChrome.jar >> %LOG%

echo %errorlevel% >> %LOG%


