@echo off

set LOG=log.txt

time /t >> %LOG%

rem java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 wdkextnnative.jar >> %LOG%
java -jar wdkextnnative.jar

echo %errorlevel% >> %LOG%


