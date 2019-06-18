#!/bin/sh
#
# A wrapper for generating usage reports.

EAC_APP_DIR=`dirname ${0} 2>/dev/null`/..

# *******************************************************
# Locating commons-io jar file
for file in ${EAC_APP_DIR}/config/lib/java/commons-io-*.jar
do
    COMMONS_IO_JAR=$file
done

if [ ! -f "${COMMONS_IO_JAR}" ]; then
    echo "ERROR: Could not find commons-io jar file under ${EAC_APP_DIR}/config/lib/java/"
    exit 1
fi


# *******************************************************
# Locating usageCollection jar file
for file in ${EAC_APP_DIR}/config/lib/java/usageCollection-*.jar
do
    USAGE_COLLECTION_JAR=$file
done

if [ ! -f "${USAGE_COLLECTION_JAR}" ]; then
    echo "ERROR: Could not find usageCollection jar file under ${EAC_APP_DIR}/config/lib/java/"
    exit 1
fi

# *******************************************************
# Determine location of java.exe
if [ ! "$ENDECA_ROOT" = "" ] ; then
    if [ -f "${ENDECA_ROOT}/j2sdk/bin/java" ] ; then
        JAVA="${ENDECA_ROOT}/j2sdk/bin/java"
    else
        echo "WARNING: ${ENDECA_ROOT}/j2sdk/bin/java does not exist"
    fi
else
    echo "WARNING: ENDECA_ROOT is not set."
fi

if [ "${JAVA}" = "" ] ; then
    if [ ! "${JAVA_HOME}" = "" ] ; then
        echo 2
        echo "WARNING: Using java in ${JAVA_HOME}/bin/java"
        JAVA=${JAVA_HOME}/bin/java
    else
        echo "WARNING: Using local setting for java"
        JAVA="java"
    fi
fi
# *******************************************************
# Generate usage reports
CLASSPATH="${COMMONS_IO_JAR}:${USAGE_COLLECTION_JAR}"

REPORT_ARGS="--app-dir ${EAC_APP_DIR}"

${JAVA} -cp ${CLASSPATH} com.endeca.usage.reporting.UsageReporter ${REPORT_ARGS} $*

if [ $? -ne 0 ]
then
    exit $?
fi

