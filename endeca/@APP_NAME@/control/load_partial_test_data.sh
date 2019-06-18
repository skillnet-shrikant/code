#!/bin/sh

DATA_RS_NAME=@APP_NAME@-data
DATA_FILE_NAME=@APP_NAME@_partial_data.xml.gz
CAS_ROOT=@CAS_ROOT@
CAS_HOST=@CAS_HOST@
CAS_PORT=@CAS_PORT@
WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"
if [ ! -d "$CAS_ROOT" ] ; then
    echo "No CAS install folder found at $CAS_ROOT. Please install CAS."
    exit 1
fi
echo "Loading ${WORKING_DIR}/../test_data/partial/${DATA_FILE_NAME} into $DATA_RS_NAME"
sh $CAS_ROOT/bin/recordstore-cmd.sh write -a ${DATA_RS_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f ${WORKING_DIR}/../test_data/partial/${DATA_FILE_NAME}
