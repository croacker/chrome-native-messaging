@echo off

set LOG=log.txt

time /t >> %LOG%

rem Запуск приложения с возможностью подключения в режиме отладки. 
rem ВАЖНО! ответы из приложения в браузер не будут доставлены, т.к. поток вывода направлен в %LOG% файл.
rem java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -cp ./;./*;./lib/*;  ru.croc.chromenative.HostApplication >> %LOG%

rem Рабочий режим запуска.
java -cp ./;./*;./lib/*;  ru.croc.chromenative.HostApplication

echo %errorlevel% >> %LOG%