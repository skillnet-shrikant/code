#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ -z "${ENDECA_ROOT}" ] ; then
  echo "ERROR: ENDECA_ROOT is not set."
  exit 1
fi
"${WORKING_DIR}/runcommand.sh" IFCR exportXml ${1}
