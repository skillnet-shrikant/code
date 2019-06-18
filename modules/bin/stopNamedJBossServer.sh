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
echo .............Killing off $JBOSS_NAMED_SERVER Server

export JBOSS_PIDFILE=$JBOSS_HOME/standalone/tmp/$JBOSS_NAMED_SERVER.pid
echo JBOSS_PIDFILE=$JBOSS_PIDFILE
kill `cat $JBOSS_PIDFILE`
> $JBOSS_PIDFILE
echo "==============================================================================================================================="


# -----------------------------------------------------------------------
# Grep and tail this log.
# -----------------------------------------------------------------------
tail -f $JBOSS_HOME/standalone/log/$JBOSS_NAMED_SERVER.log