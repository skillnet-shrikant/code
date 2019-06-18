dojo.provide( "atg.commerce.csr.order.returns" );
atg.commerce.csr.order.returns.selectReturnRequest = function (pReturnRequestId){
  atgSubmitAction(
      {
        panels:["cmcReturnsHistoryP"],
        form:dojo.byId('transformForm'),
        queryParams: { "historyReturnRequestId" : pReturnRequestId }
      }
  );
};

atg.commerce.csr.order.returns.selectOriginatingOrder = function (pOriginatingOrderId){
  atgSubmitAction(
      {
        panels:["cmcReturnsHistoryP"],
        form:dojo.byId('transformForm'),
        queryParams: { "originatingOrderId" : pOriginatingOrderId }
      }
  );
};
atg.commerce.csr.order.returns.resetRefundValues = function (pParams){
  var form  = document.getElementById("resetRefundValues");
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.modifyRefundValues = function (pParams){
  var form  = document.getElementById("modifyRefundValuesForm");
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.initiateReturnProcess = function (pParams){
  var form  = document.getElementById("csrCreateReturnRequest");
  if(pParams.orderId !== undefined && pParams.orderId !== null)
    form.orderId.value=pParams.orderId;
  atgSubmitAction({
    form:form
  });
};

atg.commerce.csr.order.returns.selectReturnItems = function (pParams){
  var form  = document.getElementById("csrSelectReturnItems");
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.startReturnProcess = function (pParams) {
  var form  = document.getElementById('csrSelectReturnItems');
  form.processName.value = 'Return';
  var successurl = form.startReturnSuccessURL.value;
  form.selectItemsSuccessURL.value = successurl;
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.startExchangeProcess = function (pParams) {
  var form  = document.getElementById('csrSelectReturnItems');
  form.processName.value = 'Exchange';
  var successurl = form.startExchangeSuccessURL.value;
  form.selectItemsSuccessURL.value = successurl;
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.cancelReturnRequest = function (pParams) {
  var form  = document.getElementById('cancelReturnRequest');

  var deferred = atgSubmitAction({form:form});
  deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};

atg.commerce.csr.order.returns.applyRefunds = function (pParams) {
  var form  = document.getElementById('csrApplyRefunds');
  atg.commerce.csr.common.enableDisable({form:'csrApplyRefunds', name:'handleApplyRefunds'},{form:'csrApplyRefunds', name:'handleCancelReturnRequest'});
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.submitReturn = function (pParams){
  var form  = document.getElementById('atg_commerce_csr_submitReturnForm');
  atg.commerce.csr.common.enableDisable('handleSubmitReturnRequest','handleCancelReturnRequest');
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.returns.cancelReturnRequestInRefundPage = function (pParams) {
  var form  = document.getElementById('csrApplyRefunds');
  atg.commerce.csr.common.enableDisable({form:'csrApplyRefunds', name:'handleCancelReturnRequest'},{form:'csrApplyRefunds', name:'handleApplyRefunds'});
  var deferred = atgSubmitAction({form:form});
  deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};

atg.commerce.csr.order.returns.cancelReturnRequestInCompletePage = function (pParams) {
  var form  = document.getElementById('atg_commerce_csr_submitReturnForm');
  atg.commerce.csr.common.enableDisable({form:'atg_commerce_csr_submitReturnForm', name:'handleCancelReturnRequest'},{form:'atg_commerce_csr_submitReturnForm', name:'handleSubmitReturnRequest'});
  var deferred = atgSubmitAction({form:form});
  deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};

atg.commerce.csr.order.returns.editCreditCard = function(pURL){
  atg.commerce.csr.common.submitPopup(pURL, document.getElementById("csrEditCreditCard"), dijit.byId("editPaymentOptionFloatingPane"));
};

/**
 * This method enables/disables the start return process and start exchange process
 * buttons.
 * This is used in the return items selection page.
 *
 */
atg.commerce.csr.order.returns.disableReturnProcessButtons = function (pFlag) {
  var startReturn = dijit.byId('StartReturnProcess');
  if (startReturn) {
    pFlag ? startReturn.disableButton() : startReturn.enableButton();
  }
  var exchangeButton = dijit.byId('StartExchangeProcess');
  if (exchangeButton) {
    pFlag ? exchangeButton.disableButton() : exchangeButton.enableButton();
  }
};
