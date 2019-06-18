#!/bin/sh
#

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

# IFCR exportContent
# Used to export Workbench content to disk in ECR format as JSON files.  Used to extract all templates

"${WORKING_DIR}/runcommand.sh" IFCR exportContent templates ${WORKING_DIR}/../config/import/templates true
