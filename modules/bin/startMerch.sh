#Merch

export "JAVA_OPTS=-server -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:MaxNewSize=128m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000  -Dorg.jboss.resolver.warning=tOMSDemo -Djboss.service.binding.set=ports-01"
echo JAVA_OPTS=$JAVA_OPTS

./StartNamedJBossServer.sh OMSDemoMerch
