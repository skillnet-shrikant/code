#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

FROM_DIR=$1
TO_DIR=$2

cp -rv ${FROM_DIR} ${TO_DIR}
