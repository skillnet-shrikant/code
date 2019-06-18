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

if [ "$1" = "--force" ]; then
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
fi

"${WORKING_DIR}/runcommand.sh" --skip-definition AssertNotDefined
if [ $? != 0 ]; then
  exit 1
fi



# Create new application

echo "Creating Dimension Value Id Manager $DVAL_ID_MGR_NAME"
${CAS_ROOT}/bin/cas-cmd.sh createDimensionValueIdManager -h ${CAS_HOST} -p ${CAS_PORT} -m ${DVAL_ID_MGR_NAME}
if [ $? != 0 ] ; then
	echo "Failure to create Dimension Value Id Manager."
	exit 1
fi

echo "Creating Record Store $DATA_RS_NAME"
${CAS_ROOT}/bin/component-manager-cmd.sh create-component -h ${CAS_HOST} -p ${CAS_PORT} -t RecordStore -n ${DATA_RS_NAME}
if [ $? != 0 ] ; then
	echo "Failure to create Record Store."
	exit 1
fi

# Set the record store configuration
${CAS_ROOT}/bin/recordstore-cmd.sh set-configuration -h ${CAS_HOST} -p ${CAS_PORT} -a ${DATA_RS_NAME} -f ${WORKING_DIR}/../config/cas/data-recordstore-config.xml
if [ $? != 0 ] ; then
	echo "Failure to set Record Store configuration."
	exit 1
fi

echo "Creating Record Store $DIMVALS_RS_NAME"
${CAS_ROOT}/bin/component-manager-cmd.sh create-component -h ${CAS_HOST} -p ${CAS_PORT} -t RecordStore -n ${DIMVALS_RS_NAME}
if [ $? != 0 ] ; then
	echo "Failure to create Record Store."
	exit 1
fi

echo "Creating Record Store $PRICE_RS_NAME"
${CAS_ROOT}/bin/component-manager-cmd.sh create-component -h ${CAS_HOST} -p ${CAS_PORT} -t RecordStore -n ${PRICE_RS_NAME}
if [ $? != 0 ] ; then
	echo "Failure to create Record Store."
	exit 1
fi

echo "Setting EAC provisioning and performing initial setup..."
"${WORKING_DIR}/runcommand.sh" InitialSetup

if [ $? != 0 ] ; then
	echo "Failure to initialize EAC application."
	exit 1
fi

echo "Finished updating EAC."

# Create the crawl after creating the Dimension Value Id Manager, Record Stores, and IFCR Site
echo "Creating crawl ${LAST_MILE_CRAWL_NAME}"
${CAS_ROOT}/bin/cas-cmd.sh createCrawls -h ${CAS_HOST} -p ${CAS_PORT} -f ${WORKING_DIR}/../config/cas/last-mile-crawl.xml
if [ $? != 0 ] ; then
	echo "Failure to create crawl."
	exit 1
fi

# Remove the following step if not using sample application and data.

echo "Importing content..."

# import content using public format
"${WORKING_DIR}/runcommand.sh" IFCR importApplication "${WORKING_DIR}/../config/import"
if [ $? != 0 ]; then
	echo "Failed to import content in public format."
    exit 1
fi

# import content using legacy format
"${WORKING_DIR}/runcommand.sh" IFCR legacyUpdateContent "/" "${WORKING_DIR}/../config/ifcr"
if [ $? != 0 ] ; then
	echo "Failed to import content in legacy format."
	exit 1
fi

# import CAS index configuration using public format
echo y | "${WORKING_DIR}/index_config_cmd.sh" set-config -f "${WORKING_DIR}/../config/index_config/index-config.json" -o all
if [ $? != 0 ] ; then
	echo "Failed to import CAS index configuration using public format."
	exit 1
fi

echo "Finished importing content"

echo "Populating Price Bucket Record Store"

$CAS_ROOT/bin/recordstore-cmd.sh write -b -a ${PRICE_RS_NAME} -h ${CAS_HOST} -p ${CAS_PORT} -f ${WORKING_DIR}/../test_data/baseline/${PRICE_RS_FILE}

echo "Finished Populating Price Bucket Record Store"
