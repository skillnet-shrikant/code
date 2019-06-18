#Generic JBoss Server Startup script
#startNamedJBossServer.sh
if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
		echo Please supply jboss server name 
		exit
fi

export JBOSS_NAMED_SERVER=$1

echo "=================================================================="
echo .............Kicking off $JBOSS_NAMED_SERVER Server

export JBOSS_PIDFILE=$JBOSS_HOME/standalone/tmp/$JBOSS_NAMED_SERVER.pid
export LAUNCH_JBOSS_IN_BACKGROUND=1
$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -c $JBOSS_NAMED_SERVER.xml &> /dev/null &
echo $! >$JBOSS_PIDFILE

echo .............$JBOSS_NAMED_SERVER Server has been kicked off
echo "=================================================================="

# -----------------------------------------------------------------------
# Grep and tail this log.
# -----------------------------------------------------------------------
tail -200f $JBOSS_HOME/standalone/log/$JBOSS_NAMED_SERVER.log
