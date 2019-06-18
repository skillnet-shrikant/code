#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ -z "${ENDECA_ROOT}" ] ; then
  echo "ERROR: ENDECA_ROOT is not set."
  exit 1
fi

# Replace all editors config with files in ../config/import/configuration/tools/xmgr folder.
"${WORKING_DIR}/runcommand.sh" IFCR importContent configuration/tools/xmgr ${WORKING_DIR}/../config/import/configuration/tools/xmgr
if [ $? != 0 ]; then
  exit 1
fi
