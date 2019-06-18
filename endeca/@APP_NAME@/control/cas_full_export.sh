#!/bin/sh

DATA_RS_NAME=@APP_NAME@-data
DATA_FILE_NAME=@APP_NAME@-data.xml.gz
DIMVALS_RS_NAME=@APP_NAME@-dimvals
DIMVALS_FILE_NAME=@APP_NAME@-dimvals.xml.gz
DIMVAL_ID_MANAGER_NAME=@APP_NAME@-dimension-value-id-manager
DIMVAL_ID_MANAGER_FILE_NAME=@APP_NAME@-dimension-value-id-manager.csv.gz
CAS_ROOT=@CAS_ROOT@
CAS_HOST=@CAS_HOST@
CAS_PORT=@CAS_PORT@
WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"
if [ ! -d "$CAS_ROOT" ] ; then
    echo "No CAS install folder found at $CAS_ROOT. Please install CAS."
    exit 1
fi
echo "Exporting ${DATA_RS_NAME} into @ENDECA_APPS_DIR@/@APP_NAME@/test_data/baseline/${DATA_FILE_NAME}"
sh $CAS_ROOT/bin/recordstore-cmd.sh read-baseline -a ${DATA_RS_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f @ENDECA_APPS_DIR@/@APP_NAME@/test_data/baseline/${DATA_FILE_NAME}

echo "Exporting ${DIMVALS_RS_NAME} into @ENDECA_APPS_DIR@/@APP_NAME@/test_data/baseline/${DIMVALS_FILE_NAME}"
sh $CAS_ROOT/bin/recordstore-cmd.sh read-baseline -b -a ${DIMVALS_RS_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f @ENDECA_APPS_DIR@/@APP_NAME@/test_data/baseline/${DIMVALS_FILE_NAME}

echo "Exporting ${DIMVAL_ID_MANAGER_NAME} into @ENDECA_APPS_DIR@/@APP_NAME@/test_data/baseline/${DIMVAL_ID_MANAGER_FILE_NAME}"
sh $CAS_ROOT/bin/cas-cmd.sh exportDimensionValueIdMappings -m ${DIMVAL_ID_MANAGER_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f @ENDECA_APPS_DIR@/@APP_NAME@/test_data/baseline/${DIMVAL_ID_MANAGER_FILE_NAME}