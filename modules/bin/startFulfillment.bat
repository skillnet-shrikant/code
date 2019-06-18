echo off
TITLE Fulfillment Server
echo off
cd %JBOSS_HOME%\bin
set "JAVA_OPTS=-server -Xms1024m -Xmx2048m -XX:MaxPermSize=512m -XX:MaxNewSize=512m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dorg.jboss.resolver.warning=true -Djava.net.preferIPv4Stack=true "
echo JAVA_OPTS=%JAVA_OPTS%
standalone.bat -b 0.0.0.0 --server-config=aerofulfill.xml 
pause
echo on