#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ -z "${ENDECA_ROOT}" ] ; then
  echo "ERROR: ENDECA_ROOT is not set."
  exit 1
fi

if [ "$2" == "--force" ]; then
  "${WORKING_DIR}/runcommand.sh" IFCR importXml ${1}
else
  "${WORKING_DIR}/runcommand.sh" IFCR importXmlWithPrompt ${1}
fi

