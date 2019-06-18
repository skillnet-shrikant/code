rem #-STORE
echo off
TITLE aerostagestore Server
title STORE
echo off
cd %JBOSS_HOME%\bin
set "JAVA_OPTS=-server -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:MaxNewSize=128m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000  -Dorg.jboss.resolver.warning=tOMSDemo" 
echo JAVA_OPTS=%JAVA_OPTS%
run -c aerostagestore -b 0.0.0.0
pause
echo on