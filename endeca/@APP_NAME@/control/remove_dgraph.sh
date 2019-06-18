#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

IP=$1
PORT=$2
DIRECTORY="${ENDECA_ROOT}/tools/server/webapps/endeca_jspref/"
FILE=DGRAPH_${IP}_${PORT}.running

ssh ${IP} "rm -f ${DIRECTORY}${FILE}"
