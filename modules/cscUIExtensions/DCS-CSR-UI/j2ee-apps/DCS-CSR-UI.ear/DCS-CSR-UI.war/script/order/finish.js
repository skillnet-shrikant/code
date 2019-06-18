dojo.provide( "atg.commerce.csr.order.finish" );

/**
 * Submit the order
 *
 * @param theForm the form that gets submitted. Different forms
 *              are submitted, depending on the state of the order.
 */
atg.commerce.csr.order.finish.submitOrder = function (theForm){
  atgSubmitAction({
    form:theForm,
    panelStack: ["globalPanels"]
    });
};

atg.commerce.csr.order.finish.submitAndScehduleOrder = function (theForm){
  atgSubmitAction({
    form:theForm,
    queryParams: { "contentHeader" : true, "cancelScheduleProcess" : "reviewSubmitAndSchedule" },
    panelStack: ["globalPanels"]
    });
};
atg.commerce.csr.order.finish.scheduleOrder = function (theForm){
  atgSubmitAction({
    form:theForm,
    queryParams: { "contentHeader" : true, "cancelPS" : "cmcCompleteOrderPS", "cancelScheduleProcess" : "reviewAndSchedule" },
    panelStack: ["globalPanels"]
    });
};
/**
 * Submit an exchange replacement order.
 *
 */
atg.commerce.csr.order.finish.submitExchange = function (){
  atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_submitExchangeForm"),
    panelStack: ["globalPanels"]
    });
};

/**
 * Cancel the current order.
 */
atg.commerce.csr.order.finish.cancelOrder = function (){

   atg.commerce.csr.common.enableDisable([{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelOrderHandler'}],
                                         [{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelExchangeOrderHandler'}]);

   atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_finishOrderCancelForm"),
    panelStack: ["globalPanels"],
    sync: true
   });
   this.hideCancelOrderPrompt();
};


/**
 * Cancel the exhange order.
 */
atg.commerce.csr.order.finish.cancelExchangeOrder = function (pParams){

   var cancelOrderForm = document.getElementById("atg_commerce_csr_finishOrderCancelForm");
   atg.commerce.csr.common.enableDisable([{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelExchangeOrderHandler'}],
                                         [{form:'atg_commerce_csr_finishOrderCancelForm', name:'csrCancelOrderHandler'}]);
   var deferred = atgSubmitAction({
                  form:cancelOrderForm,
                  panelStack: ["globalPanels"]
                  });
    var checked = atg.commerce.csr.common.getCheckedItem(cancelOrderForm.desiredOption);
    if ( checked !== "" ) {
       checked = checked.value;
    }
    if (checked === 'cancelExchangeOnly') {
      deferred.addCallback(function() {atg.progress.update('cmcRefundTypePS');}); 
    } else {
      deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');}); 
    }
    this.hideCancelOrderPrompt();
};


atg.commerce.csr.order.finish.navActionCancelOrder = function (pPopupURL){
atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'cancelOrderPopup',
                 url: pPopupURL,
                onClose: function( args ) {  } })
};


/**
 * Cancel the exhange order.
 */
atg.commerce.csr.order.finish.resizeCancelOrderWindow = function (){
    var cancelOrderWindow = dijit.byId("cancelOrderPopup");
    if (cancelOrderWindow) {
      cancelOrderWindow.resizeTo('550','250');
    }
};

/**
 * Persist the current order.
 */
atg.commerce.csr.order.finish.saveOrder = function (){
  atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_finishOrderSaveForm"),
    panelStack: ["globalPanels"]
    });
};

/**
 * Adds a new order note. Make this action synchronised so
 * that the order update process is complete before the UI
 * is refreshed. Othwerwise, there is no indication that the
 * new note was actually added i.e. the UI may refresh before
 * the async order update completes.
 */ 
atg.commerce.csr.order.finish.createNewOrderNote = function () {
  var addNewOrderNoteForm = document.getElementById('atg_commerce_csr_order_note_addNewOrderNoteForm');
  if (addNewOrderNoteForm) {    
      atgSubmitAction(
      {
        form: addNewOrderNoteForm,
        sync: true
      });
      atg.commerce.csr.common.hidePopupWithReturn('addOrderNotePopup', {result:'ok'});
    }
};

atg.commerce.csr.order.finish.editExistingOrder = function( formId, orderId )
{
  var theForm = document.getElementById(formId);
  theForm.orderId.value = orderId;
  atgSubmitAction({
    form : theForm,
    queryParams : {init:true}
  });
};

atg.commerce.csr.order.finish.hideCancelOrderPrompt = function ()
{
    var cancelOrderWindow = dijit.byId("cancelOrderPopup");

    if (cancelOrderWindow) {
      atg.commerce.csr.common.hidePopup (cancelOrderWindow);
    }
};
