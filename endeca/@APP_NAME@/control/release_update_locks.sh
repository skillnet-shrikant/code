#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"
"${WORKING_DIR}/runcommand.sh" --skip-definition LockManager releaseLock update_lock 2>&1
