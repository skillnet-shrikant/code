#Generic JBoss Server Stop script
#stopNamedJBossServer.sh
if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
		echo Please supply jboss server name 
		exit
fi

export JBOSS_NAMED_SERVER=$1

echo "=================================================================="
echo .............Tailing $JBOSS_NAMED_SERVER Server
echo "==============================================================================================================================="

# -----------------------------------------------------------------------
# Grep and tail this log.
# -----------------------------------------------------------------------
tail -100f $JBOSS_HOME/server/$JBOSS_NAMED_SERVER/log/server.log