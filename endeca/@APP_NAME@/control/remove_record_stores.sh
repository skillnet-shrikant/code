#!/bin/sh

DATA_RS_NAME=@APP_NAME@-data
DIMVALS_RS_NAME=@APP_NAME@-dimvals
PRICE_RS_NAME=@APP_NAME@-price
PRICE_RS_FILE=@APP_NAME@-price.xml
LAST_MILE_CRAWL_NAME=@APP_NAME@-last-mile-crawl
DVAL_ID_MGR_NAME=@APP_NAME@-dimension-value-id-manager
CAS_ROOT=@CAS_ROOT@
CAS_HOST=@CAS_HOST@
CAS_PORT=@CAS_PORT@
WORKING_DIR=`dirname ${0} 2>/dev/null`
. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ ! -d "$CAS_ROOT" ] ; then
    echo "No CAS install folder found at $CAS_ROOT. Please install CAS."
    exit 1
fi


# Remove existing application state

  echo "Removing existing crawl configuration for crawl $LAST_MILE_CRAWL_NAME (ignore errors if crawl doesn't exist)"
  ${CAS_ROOT}/bin/cas-cmd.sh deleteCrawl -h ${CAS_HOST} -p ${CAS_PORT} -id ${LAST_MILE_CRAWL_NAME}

  echo "Removing Record Store $DATA_RS_NAME (ignore errors if Record Store doesn't exist)"
  ${CAS_ROOT}/bin/component-manager-cmd.sh delete-component -h ${CAS_HOST} -p ${CAS_PORT} -n ${DATA_RS_NAME}

  echo "Removing Record Store $DIMVALS_RS_NAME (ignore errors if Record Store doesn't exist)"
  ${CAS_ROOT}/bin/component-manager-cmd.sh delete-component -h ${CAS_HOST} -p ${CAS_PORT} -n ${DIMVALS_RS_NAME}

  echo "Removing Record Store $PRICE_RS_NAME (ignore errors if Record Store doesn't exist)"
  ${CAS_ROOT}/bin/component-manager-cmd.sh delete-component -h ${CAS_HOST} -p ${CAS_PORT} -n ${PRICE_RS_NAME}

  echo "Removing Dimension Value Id Manager $DVAL_ID_MGR_NAME (ignore errors if Dimension Value Id Manager doesn't exist)"
  ${CAS_ROOT}/bin/cas-cmd.sh deleteDimensionValueIdManager -h ${CAS_HOST} -p ${CAS_PORT} -m ${DVAL_ID_MGR_NAME}

  echo "Removing existing application provisioning..."
  "${WORKING_DIR}/runcommand.sh" --remove-app

