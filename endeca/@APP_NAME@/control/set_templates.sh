#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`
. "${WORKING_DIR}/../config/script/set_environment.sh"

echo "Setting new cartridge templates for ${ENDECA_PROJECT_NAME}"
"${WORKING_DIR}/runcommand.sh" IFCR importContent templates "${WORKING_DIR}/../config/import/templates"
if [ $? != 0 ]; then
  exit 1
fi
echo "Finished setting templates"
