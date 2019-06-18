dojo.provide( "atg.commerce.csr.order.scheduled" );

  atg.commerce.csr.order.scheduled.populateData = function (pTableData){
  console.debug("atg.commerce.csr.order.scheduled.populateData started");
    var rowData;
    var l;
    var cells;
    var cellData;
    var table=dojo.byId('schedulesTable');
    for (var x=0; x < pTableData.length; x++) 
    {
      //console.debug("pTableData  is " + pTableData);
      //console.debug("pTableData  length is " + pTableData.length);
      rowData=pTableData[x];
      //console.debug("row data is " + rowData);
      //console.debug("rowData  length is " + rowData.length);
      for (var y=0; y<rowData.length; y++) 
      {
        //console.debug("y is " + y);
        //console.debug("x is " + x);
         cellData = rowData[y];
        //console.debug("cellData is " + cellData);
        cells = table.rows[x+1].cells;
        cells[y].innerHTML=cellData;
      }
    }
  };

atg.commerce.csr.order.scheduled.viewScheduleErrors = function (pURL,title) {
  console.debug("viewScheduleErrors.pURL: " + pURL);
  console.debug("viewScheduleErrors.pURL: testing");
  var paneid = "viewScheduleErrors";
      atg.commerce.csr.common.showPopupWithReturn({
        popupPaneId: paneid,
        title: title,
        url: pURL,
        onClose: function( args ) {  }
      });
};
/**
 * Submits the form for creating a new schedule
 *
 * @param theForm the form that gets submitted. Different forms
 *              are submitted, depending on the state of the order.
 */
atg.commerce.csr.order.scheduled.createSchedule = function (pFormId){

  var theForm = dojo.byId(pFormId);
  atg.commerce.csr.order.scheduled.setFormFromScheduleWidget(theForm);
  atgSubmitAction({
    form:theForm,
    panelStack:["globalPanels"]
  });
};

/** 
* Submits the form for updating a schedule
*/
atg.commerce.csr.order.scheduled.updateSchedule = function (pFormId){
   var theForm = dojo.byId(pFormId);
   atg.commerce.csr.order.scheduled.setFormFromScheduleWidget(theForm);
     atgSubmitAction({
       form:theForm,
       panelStack:["globalPanels"]
     });
};

/**
* This function populates the form properties from the values provide by the
* schedule widget
*/
atg.commerce.csr.order.scheduled.setFormFromScheduleWidget = function (pForm)
{
  var scheduleType = dijit.byId("cscDateRange").scheduleType();
  pForm.scheduleType.value = scheduleType;
  console.debug("setFormFromScheduleWidget scheduleType: " + scheduleType);

  var daysOption = dijit.byId("cscDateRange").daysOption();
  pForm.daysOption.value = daysOption;
  console.debug("setFormFromScheduleWidget daysOption: " + daysOption);

  var occurrencesOption = dijit.byId("cscDateRange").weeksOption();
  pForm.occurrencesOption.value =  occurrencesOption;
  console.debug("setFormFromScheduleWidget occurrencesOption: " + occurrencesOption);

  var monthsOption =   dijit.byId("cscDateRange").monthsOption();
  pForm.monthsOption.value = monthsOption;
  console.debug("setFormFromScheduleWidget monthsOption: " + monthsOption);
  
  var intervalOption =   dijit.byId("cscDateRange").intervalOption();
  pForm.intervalOption.value = intervalOption;
  console.debug("setFormFromScheduleWidget intervalOption: " + intervalOption);
  
  var selectedInterval = dijit.byId("cscDateRange").interval();
  pForm.selectedInterval.value = selectedInterval;
  console.debug("setFormFromScheduleWidget selectedInterval: " + selectedInterval);

  var selectedDays = dijit.byId("cscDateRange").days();
  var days = selectedDays.join(',');
  pForm.selectedDays.value = days; 
  console.debug("setFormFromScheduleWidget selectedDays: " + days);

  var selectedMonths = dijit.byId("cscDateRange").months();
  var months = selectedMonths.join(',');
  pForm.selectedMonths.value = months; 
  console.debug("setFormFromScheduleWidget selectedMonths: " + months);

  var selectedOccurrences = dijit.byId("cscDateRange").occurrences();
  var occurrences = selectedOccurrences.join(',');
  pForm.selectedOccurrences.value = occurrences; 
  console.debug("setFormFromScheduleWidget selectedOccurrences: " + occurrences);

  var selectedDates = dijit.byId("cscDateRange").dates();
  var dates = selectedDates.join(',');
  pForm.selectedDates.value = dates; 
  console.debug("setFormFromScheduleWidget selectedDates: " + dates);
};


atg.commerce.csr.order.scheduled.cancelCreate = function (pForm){
  var theForm = dojo.byId(pForm);
  atgSubmitAction({
    form:theForm
  });
};

atg.commerce.csr.order.scheduled.cancelUpdate = function (pForm){
  var theForm = dojo.byId(pForm);
  atgSubmitAction({
    form:theForm
  });
};
atg.commerce.csr.order.scheduled.loadOrderForAddSchedule = function(orderId)
{
  var theForm = dojo.byId("atg_commerce_csr_loadScheduledOrderForScheduleAdd");
  theForm.orderId.value = orderId;
    atgSubmitAction({
      form : theForm,
      queryParams : {cancelScheduleProcess:"createNewSchedule"}
    });
};    
atg.commerce.csr.order.scheduled.loadOrderForChangeSchedule = function(orderId,scheduleId)
{
  var theForm = dojo.byId("atg_commerce_csr_loadScheduledOrderForScheduleChange");
  theForm.orderId.value = orderId;
    atgSubmitAction({
      form : theForm,
      queryParams : {cancelScheduleProcess:"cancelUpdateSchedule",scheduledOrderId:scheduleId}
    });
    
};

//deprecated: no longer used by CSC. Don't removed this function for backward compatibility
atg.commerce.csr.order.scheduled.activateSchedule = function(orderId,scheduleId)
{
  var theForm = dojo.byId("atg_commerce_csr_scheduled_activateSchedule");
  theForm.orderId.value = orderId;
  theForm.scheduledOrderId.value=scheduleId;
    atgSubmitAction({
      form : theForm
    });
    
};

//deprecated: no longer used by CSC. Don't removed this function for backward compatibility
atg.commerce.csr.order.scheduled.deactivateSchedule = function(orderId,scheduleId)
{
  var theForm = dojo.byId("atg_commerce_csr_scheduled_deactivateSchedule");
  theForm.orderId.value = orderId;
  theForm.scheduledOrderId.value=scheduleId;
    atgSubmitAction({
      form : theForm
    });
    
};

atg.commerce.csr.order.scheduled.submitNow = function(pOrderId)
{
  var theForm = dojo.byId("atg_commerce_csr_scheduled_duplicateAndSubmit");
  theForm.orderId.value = pOrderId;
    atgSubmitAction({
      form : theForm
    });
    
};

/**
* executed when the create schedule form is loaded 
*/
atg.commerce.csr.order.scheduled.loadCreateForm = function(pFormId)
{
  console.debug("atg.commerce.csr.order.scheduled.loadCreateForm");
  var widget = new atg.csc.dateRangePicker({}, dojo.byId("cscDateRange"));
  var theForm = dojo.byId(pFormId);
  atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm(theForm);
  widget.render();
  console.debug("atg.commerce.csr.order.scheduled.loadCreateForm DONE");

};


/**
* executed when the update schedule form is loaded 
*/
atg.commerce.csr.order.scheduled.loadUpdateForm = function(pFormId)
{
  var widget = new atg.csc.dateRangePicker({}, dojo.byId("cscDateRange"));
  var theForm = dojo.byId(pFormId);
  atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm(theForm);
  widget.render();

};

/**
* This function sets the schedule widget values from the form properties 
*/
atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm = function(pForm)
{
  var scheduleType = pForm.scheduleType.value;
  console.debug("setScheduleWidgetFromForm scheduleTypeInput: " + scheduleType);
  dijit.byId("cscDateRange").scheduleType(scheduleType);

  var daysOption = pForm.daysOption.value;
  console.debug("setScheduleWidgetFromForm daysOption: " + daysOption);
  dijit.byId("cscDateRange").daysOption(daysOption);

  var occurrencesOption = pForm.occurrencesOption.value;
  console.debug("setScheduleWidgetFromForm occurrencesOption: " + occurrencesOption);
  dijit.byId("cscDateRange").weeksOption(occurrencesOption);

  var monthsOption = pForm.monthsOption.value;
  console.debug("setScheduleWidgetFromForm monthsOption: " + monthsOption);
  dijit.byId("cscDateRange").monthsOption(monthsOption);
  
  var intervalOption = pForm.intervalOption.value;
  console.debug("setScheduleWidgetFromForm intervalOption: " + intervalOption);
  dijit.byId("cscDateRange").intervalOption(intervalOption);
  
  var selectedInterval = pForm.selectedInterval.value;
  console.debug("setScheduleWidgetFromForm selectedInterval: " + selectedInterval);
  dijit.byId("cscDateRange").interval(selectedInterval);
  
  var selectedDays = pForm.selectedDays.value;
  var days = selectedDays.split(",");
  console.debug("setScheduleWidgetFromForm days: " + days);
  dijit.byId("cscDateRange").days(days);
   
  var selectedOccurrences = pForm.selectedOccurrences.value;
  var occurrences = selectedOccurrences.split(",");
  console.debug("setScheduleWidgetFromForm occurrences: " + occurrences);
  dijit.byId("cscDateRange").occurrences(occurrences);
  
  var selectedMonths = pForm.selectedMonths.value;
  var months = selectedMonths.split(",");
  console.debug("setScheduleWidgetFromForm months: " + months);
  dijit.byId("cscDateRange").months(months);

  var selectedDates = pForm.selectedDates.value;
  var dates = selectedDates.split(",");
  console.debug("setScheduleWidgetFromForm dates: " + dates);
  dijit.byId("cscDateRange").dates(dates);

};
