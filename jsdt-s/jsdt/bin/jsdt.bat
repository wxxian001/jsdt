@echo off

set JSDT_HOME=%~dp0
set JSDT_VERSION=0.5
set JAVA=%JAVA_HOME%\bin\java

if not "%JAVA_HOME%" == "" goto SET_CLASSPATH

set JAVA=java


echo JAVA_HOME is not set, unexpected results may occur.
echo Set JAVA_HOME to the directory of your local JDK to avoid this message.

:SET_CLASSPATH

rem init classpath
set JSDT_HOME=%JSDT_HOME%..\
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\chardet.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\commons-io-1.3.2.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\js.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\json-1.0.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\rsyntaxtextarea.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\jsdt-engine%JSDT_VERSION%.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\jsdt-core%JSDT_VERSION%.jar
set CLASSPATH=%CLASSPATH%;%JSDT_HOME%lib\jsdt-ui%JSDT_VERSION%.jar

rem JVM parameters, modify as appropriate

set JAVA_OPTS=-Xms128m -Xmx256m "-Djsdt.home=%JSDT_HOME%\"


if "%JSDT_HOME%\" == "" goto START

:START

rem ********* run soapui ***********

"%JAVA%" %JAVA_OPTS% -cp "%CLASSPATH%" org.ayound.js.debug.ui.Main %*

