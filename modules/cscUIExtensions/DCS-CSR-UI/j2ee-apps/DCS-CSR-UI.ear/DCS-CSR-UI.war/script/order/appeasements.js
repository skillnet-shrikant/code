dojo.provide( "atg.commerce.csr.order.appeasement" );

/**
 * Creates a new, transient appeasement and opens up the Appeasements UI screen
 */
atg.commerce.csr.order.appeasement.initiateAppeasementProcess = function (pParams){
  var form  = document.getElementById("createAppeasement");
  if(pParams.orderId !== undefined && pParams.orderId !== null)
    form.orderId.value=pParams.orderId;
  atgSubmitAction({
    form:form
  });
};


/**
 * Applies the values input, reason codes and additional notes to the 
 * refund types for the appeasement
 */
atg.commerce.csr.order.appeasement.applyAppeasementRefundValues = function () {
  var appeasementTypeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementType");
  var typeSelection = appeasementTypeSelect.options[appeasementTypeSelect.selectedIndex].value;
  var appeasementReasonCodeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
  var codeSelection = appeasementReasonCodeSelect.options[appeasementReasonCodeSelect.selectedIndex].value;

  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementType").value=typeSelection;
  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementAmount").value=dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value;
  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementReasonCode").value=codeSelection;
  dojo.byId("atg_commerce_csr_appeasement_updateAppeasementNotes").value=dojo.byId("atg_commerce_csr_appeasement_appeasementsCommentsId").value;

  var theForm = dojo.byId("atg_commerce_csr_updateAppeasement");
      atgSubmitAction( {
      form:theForm,
      panelStack: ["globalPanels", "cmcAppeasementsPS"]});
};
    

/**
 * Cancels the appeasement in progress and returns Agent to the Existing Orders screen
 */
atg.commerce.csr.order.appeasement.cancelAppeasement = function () {
 var form  = document.getElementById('cancelAppeasement');

 var deferred = atgSubmitAction({form:form});
 deferred.addCallback(function() {atg.progress.update('cmcExistingOrderPS');});
};


/**
 * This function sets up the appeasement UI, hiding the appropriate input area based on 
 * the discount type selected (amount or percent). This function takes the radio button
 * that was selected as the param. 
 */
atg.commerce.csr.order.appeasement.setAppeasementValues = function (el){

   console.log("In setAppeasementValue " + el.value);
   if (el.value == "selectAmountOff") {
     console.log("Hiding percent text box");
     dojo.byId("atg_commerce_csr_appeasement_appeasementAmountInputDiv").style.display = 'inline-block';
     dojo.byId("atg_commerce_csr_appeasement_appeasementPercentInputDiv").style.display = 'none';
   }
   else {
     console.log("Hiding amount text box");
     dojo.byId("atg_commerce_csr_appeasement_appeasementAmountInputDiv").style.display = 'none';
     dojo.byId("atg_commerce_csr_appeasement_appeasementPercentInputDiv").style.display = 'inline-block';
   }
   dojo.byId("atg_commerce_csr_appeasement_amountOrPercentageSelectedValue").value = el.value;
 };
 
 
 /**
  * This function is responsible for displaying the calculated discount amount. 
  * It performs certain sanity checks on the inputted data, including whether all required 
  * fields are populated. If data validation fails it disables the apply button and blanks 
  * the total discount amount. If data validation passes (including calculating the 
  * percentage amount), then the formatted total discount amount is displayed and the 
  * apply button enabled.
  */
atg.commerce.csr.order.appeasement.displayAppeasementAmount = function(){
    var currencyCode = document.getElementById("atg_commerce_csr_order_appeasements_activeCurrencyCode").value;
     
    var spanTotal = dojo.byId("atg_commerce_csr_appeasement_total");
    var balance;
    var result = 0;
    var disable = true;
    var validAmount = false;

    // Set which balance we are using. Shipping or Items.
    if (dojo.byId("atg_commerce_csr_appeasement_appeasementType").value === "shipping"){
      balance = dojo.byId("shippingBalance").value;
    }
    else {
      balance = dojo.byId("itemBalance").value;
    }
   
    // If the discount type is percent off then we have some calculations to do. 
    // We calculate the discount amount on the client side based on the percentage of
    // the balance.
    if(dojo.byId("atg_commerce_csr_appeasement_amountOrPercentageSelectedValue").value === "selectPercentageOff"){
      var percentOff = dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue").value;
      console.log("Precent off " + percentOff);
     
      // Validate the percentage
      if(!isNaN(percentOff) && percentOff <= 100 && percentOff > 0){
        result = balance / 100 * percentOff;
      }
     
      // If a valid result was calculated the we need to format that
      // into a Currency value for display.
      if (result > 0) {
        console.log("Calculated percent off " + result);
        validAmount = true;
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = atg.commerce.csr.order.billing.roundAmount(result);
        
        //display appeasement total on screen
        var formattedAmount = atg.commerce.csr.order.billing.formatAmount(result, container.currencyCode);
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = formattedAmount;
        } else {
          spanTotal.innerText = formattedAmount;
        }
           
      } 
      else {
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = 0;
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = "";
        } else {
          spanTotal.innerText = "";
        }
        
      }
    }
    else{
      // If an discount amount was entered then the calculations are easier. 
      // We just need to make sure that our balance does not end up in negative figures.
      // Again any valid result needs to formatted for display as a currency value.
      var amountValue = dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue").value;
      var amountOff = atg.commerce.csr.order.billing.parseAmount (amountValue);
      
      if(!isNaN(amountOff) && (amountOff <= balance) && (amountOff > 0)){
        var formattedAmount = atg.commerce.csr.order.billing.formatAmount(amountOff, container.currencyCode);
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = formattedAmount;
        } else {
          spanTotal.innerText = formattedAmount;
        }
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = atg.commerce.csr.order.billing.roundAmount(amountOff);
        validAmount = true;
        console.log("Valid amountoff  " + amountOff);
      }
      else {
        dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value = 0;
        if (spanTotal.textContent !== undefined) {
          spanTotal.textContent = "";
        } else {
          spanTotal.innerText = "";
        }
      }
    }
   
    // Validate all fields so we can enable/disable the apply button
    var appeasementReasonCodeSelect = document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
    var codeSelection = appeasementReasonCodeSelect.options[appeasementReasonCodeSelect.selectedIndex].value;
   
    console.log("The Reason Code selection is " + codeSelection + " valid amount " + validAmount);
   
    if (validAmount && dojo.string.trim(codeSelection).length > 0) {
      console.log("Enabling the apply appeasements button");
      disable = false;
    }

   dojo.byId("applyAppeasementValuesButton").disabled = disable;
   
 };
 
 /**
  * This function navigates the Agent to the appeasement summary page 
  */
atg.commerce.csr.order.appeasement.openAppeasementSummary = function() {
  console.debug("Calling atg.commerce.csr.appeasements.openAppeasementsPage");
  var form  = document.getElementById('csrApplyAppeasements');
    atgSubmitAction({form:form});
  
};
/**
 * Send an appeasement confirmation message.
 */
atg.commerce.csr.order.appeasement.sendConfirmationMessage = function (){
  atgSubmitAction({
    form:document.getElementById("csrSendAppeasementConfirmationMessage")
  });
};
atg.commerce.csr.order.appeasement.openConfirmAppeasement = function() {
  console.debug("Calling atg.commerce.csr.order.appeasement.openConfirmAppeasement");
  atg.commerce.csr.openPanelStack('cmcConfirmAppeasementPS');
};
    
atg.commerce.csr.order.appeasement.submitAppeasement = function() {
  console.debug("Calling atg.commerce.csr.order.appeasement.submitAppeasement");
  var form  = document.getElementById('csrSubmitAppeasement');
  atgSubmitAction({form:form});
};    

atg.commerce.csr.order.appeasement.selectAppeasementHistory = function (pAppeasementId){
  atgSubmitAction(
      {
        panels:["cmcAppeasementHistoryP"],
        form:dojo.byId('transformForm'),
        queryParams: { "historyAppeasementId" : pAppeasementId }
      }
  );
};