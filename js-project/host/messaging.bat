@echo off

set LOG=log.txt

time /t >> %LOG%

rem java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 messaging.jar >> %LOG%
java -jar d:/projects/java/chrome-native-messaging/js-project/host/messaging.jar
rem java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 messaging.jar
 
rem java -jar "%~dp0/messaging.jar" %*

echo %errorlevel% >> %LOG%v