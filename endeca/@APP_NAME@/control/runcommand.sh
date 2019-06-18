#!/bin/sh

umask 026
WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ -z "${ENDECA_ROOT}" ] ; then
  echo "ERROR: ENDECA_ROOT is not set."
  exit 1
fi

# *******************************************************
# Determine location of java executable
if [ -x "${ENDECA_ROOT}/j2sdk/bin/java" ] ; then
	JAVA="${ENDECA_ROOT}/j2sdk/bin/java"
else
	echo "WARNING: ${ENDECA_ROOT}j2sdk/bin/java does not exist"
	if [ ! "${JAVA_HOME}" = "" ] ; then
		echo "WARNING: Using java in ${JAVA_HOME}/bin/java"
		JAVA=${JAVA_HOME}/bin/java
	else
		echo "WARNING: Using local setting for java"
		JAVA="java"
	fi
fi

WORKING_DIR=`dirname ${0} 2>/dev/null`

APP_CONFIG_XML=${WORKING_DIR}/../config/script/AppConfig.xml
if [ ! -f "${APP_CONFIG_XML}" ]; then
  echo "ERROR: Cannot find file: ${APP_CONFIG_XML}"
  exit 1
fi

CLASSPATH=
CLASSPATH=${ENDECA_ROOT}/lib/java/eacclient.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/jaxrpc.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/mail.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/saaj.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/orawsdl.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/wsdl4j-to-orawsdl-1.0.0.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/activation.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/axis.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/commons-discovery-0.2.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/commons-logging-1.0.4.jar

# Add the script folder of the application to the classpath
CLASSPATH=${CLASSPATH}:${WORKING_DIR}/../config/script

# Add all zip and jar files that are in the ../config/lib/java directory
for i in ${WORKING_DIR}/../config/lib/java/*.jar; do
   CLASSPATH=${CLASSPATH}:$i
done
for i in ${WORKING_DIR}/../config/lib/java/*.zip; do
   CLASSPATH=${CLASSPATH}:$i
done

JAVA_ARGS="${JAVA_ARGS} -Djava.util.logging.config.file=${WORKING_DIR}/../config/script/logging.properties"

CONTROLLER_ARGS="--app-config AppConfig.xml"

OVERRIDE_PROPERTIES="${WORKING_DIR}/../config/script/environment.properties"
if [ -f "${OVERRIDE_PROPERTIES}" ]; then
  CONTROLLER_ARGS="${CONTROLLER_ARGS} --config-override environment.properties"
fi

"${JAVA}" ${JAVA_ARGS} -cp "${CLASSPATH}" com.endeca.soleng.eac.toolkit.Controller ${CONTROLLER_ARGS} $*
