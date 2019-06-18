#!/bin/sh

DATA_RS_NAME=@APP_NAME@-data
DATA_FILE_NAME=@APP_NAME@-data.xml.gz
DIMVALS_RS_NAME=@APP_NAME@-dimvals
DIMVALS_FILE_NAME=@APP_NAME@-dimvals.xml.gz
CAS_ROOT=@CAS_ROOT@
CAS_HOST=@CAS_HOST@
CAS_PORT=@CAS_PORT@
WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"
if [ ! -d "$CAS_ROOT" ] ; then
    echo "No CAS install folder found at $CAS_ROOT. Please install CAS."
    exit 1
fi
echo "Loading ${WORKING_DIR}/../test_data/baseline/${DATA_FILE_NAME} into $DATA_RS_NAME"
sh $CAS_ROOT/bin/recordstore-cmd.sh write -b -a ${DATA_RS_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f ${WORKING_DIR}/../test_data/baseline/${DATA_FILE_NAME}

echo "Loading ${WORKING_DIR}/../test_data/baseline/${DIMVALS_FILE_NAME} into $DIMVALS_RS_NAME"
sh $CAS_ROOT/bin/recordstore-cmd.sh write -b -a ${DIMVALS_RS_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f ${WORKING_DIR}/../test_data/baseline/${DIMVALS_FILE_NAME}

