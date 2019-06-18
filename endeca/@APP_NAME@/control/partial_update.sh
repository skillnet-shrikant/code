#!/bin/sh
#
# Copyright (c) 2006 Endeca Technologies Inc. All rights reserved.
# COMPANY CONFIDENTIAL
#

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"
"${WORKING_DIR}/runcommand.sh" PartialUpdate run 2>&1
