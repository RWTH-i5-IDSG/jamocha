echo off

set CMD_LINE_ARGS=%1 %2

rem set jamocha home
set JAMOCHA_HOME=.

rem Sumatra JAR
set JAMOCHA_LIB=jamocha.jar

rem Logging Library
set LOG4J=lib/log4j-1.2.14.jar

rem JLine - line editing
set JLINE=lib/jline-0.9.9.jar

rem Putting the things together ...
set CLASSPATH=%JAMOCHA_HOME%/%LOG4J%;%JAMOCHA_HOME%/%JAMOCHA_LIB%;%JAMOCHA_HOME%/%JLINE%

set JAVA_OPTS=-server

%JAVA_HOME%\bin\java %JAVA_OPTS% -classpath %CLASSPATH% org.jamocha.Jamocha %CMD_LINE_ARGS%