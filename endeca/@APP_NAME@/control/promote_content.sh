#!/bin/sh

WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

# "PromoteAuthoringToLive" can be used to promote the application.
# "PromoteAuthoringToLive" exports configuration for dgraphs and for
# assemblers as files.  These files are then applied to the live
# dgraph cluster(s) and assemblers.
"${WORKING_DIR}/runcommand.sh" PromoteAuthoringToLive run 2>&1
