#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ -z "${ENDECA_ROOT}" ] ; then
  echo "ERROR: ENDECA_ROOT is not set."
  exit 1
fi

# Replace all media with files in ../config/ifcr/media folder.
if [ "$(ls -A ${WORKING_DIR}/../config/ifcr/media)" ]; then
  "${WORKING_DIR}/runcommand.sh" IFCR importNode ${WORKING_DIR}/../config/ifcr/media media
  if [ $? != 0 ]; then
    exit 1
  fi
fi
