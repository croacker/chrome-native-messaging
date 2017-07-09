@echo off

set LOG=log.txt

time /t >> %LOG%

rem Запуск приложения с возможностью подключения в режиме отладки. 
rem ВАЖНО! ответы из приложения в браузер не будут доставлены, т.к. поток вывода направлен в %LOG% файл.
rem java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 tnHostApp.jar >> %LOG%

rem Рабочий режим запуска.
java -jar tnHostApp.jar

echo %errorlevel% >> %LOG%v