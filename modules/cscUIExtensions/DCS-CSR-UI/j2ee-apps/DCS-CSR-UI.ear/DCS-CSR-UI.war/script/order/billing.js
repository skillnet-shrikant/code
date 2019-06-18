dojo.provide( "atg.commerce.csr.order.billing" );

dojo.require("dojox.i18n.currency");
dojo.require("dojox.i18n.number");
dojo.require("dojo.date");
dojo.require("dojox.validate._base");
dojo.require("dojox.validate.creditCard");

atg.commerce.csr.order.billing.container = null;

atg.commerce.csr.order.billing.creditCardTypeDataContainer = null;

atg.commerce.csr.order.billing.applyPaymentGroups = function (pParams){
  var form  = document.getElementById("csrBillingForm");
  
  if (container && container.availablePaymentMethods) {
    for (id in container.availablePaymentMethods) {
      var paymentMethod = container.availablePaymentMethods[id];
      if (paymentMethod.paymentGroupType == 'inStorePayment') {
        var paymentInput = dijit.byId(paymentMethod.paymentGroupId);
        var paymentCheckbox = dojo.byId(paymentMethod.paymentGroupId + "_checkbox");
        var paymentRelationshipType = dojo.byId(paymentMethod.paymentGroupId + "_relationshipType");
        if (paymentInput && paymentCheckbox && !paymentCheckbox.checked) {
          paymentInput.setValue(0);
        }
        if (paymentInput && paymentRelationshipType && paymentCheckbox && !paymentCheckbox.checked) {
          paymentRelationshipType.value = 'ORDERAMOUNT';
        }
      }
    }
  }
  
  atg.commerce.csr.common.enableDisable([{form: "csrBillingForm", name: "csrHandleApplyPaymentGroups"}],
      [{form: "csrBillingForm", name: "csrPaymentGroupsPreserveUserInputOnServerSide"}]);
  atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
  atgSubmitAction({form:form});
};

atg.commerce.csr.order.billing.savePaymentGroups = function (pParams){
  var form  = document.getElementById("csrBillingForm");
  atg.commerce.csr.common.enableDisable([{form: "csrBillingForm", name: "csrHandleApplyPaymentGroups"}],
      [{form: "csrBillingForm", name: "csrPaymentGroupsPreserveUserInputOnServerSide"}]);
  atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
  atgSubmitAction({
    panelStack:["globalPanels"],
    form:form});
};

atg.commerce.csr.order.billing.claimClaimables = function (){
  atgSubmitAction({form:dojo.byId("csrBillingClaimableForm")});
};

atg.commerce.csr.order.billing.editCreditCard = function(pURL){
  atg.commerce.csr.common.submitPopup(pURL, document.getElementById("csrBillingEditCreditCard"), dijit.byId("editPaymentOptionFloatingPane"));
};

atg.commerce.csr.order.billing.addCreditCard = function (){
  atgSubmitAction({form:dojo.byId("csrBillingAddCreditCard"),panelStack: ["globalPanels"]});
};

atg.commerce.csr.order.billing.renderBillingPage = function (){
  atgNavigate({panelStack:'cmcBillingPS'});
};

/**
 * This method initializes the payment container
 *
 */
atg.commerce.csr.order.billing.initializePaymentContainer = function(pOrderTotal, pCurrencyCode, pParams){
  container = new atg.commerce.csr.order.billing.CSRPaymentContainer();
  container.initialize(pOrderTotal, pCurrencyCode, pParams);
};

/**
 * This method adds the payment method to the container.
 *
 * @param paymentGroupId : pPaymentGroupId
 * @param amount : pAmount
 * @param maxAllowedAmount : pMaxAllowedAmount -- optional
 *
 */
atg.commerce.csr.order.billing.addPaymentMethod = function (pParams){
  dojo.debug("addPaymentMethod: " + pParams.paymentGroupId + " amount " + pParams.amount);
  //create javascript object and add to the container
  var methodsInstance = new atg.commerce.csr.order.billing.AvailablePaymentMethod (pParams);
  container.addAvailablePaymentMethod(methodsInstance);
};// end of addPaymentMethod

/**
 * This method initializes the credit card type data container
 *
 */
atg.commerce.csr.order.billing.initializeCreditCardTypeDataContainer = function(){
  creditCardTypeDataContainer = new atg.commerce.csr.order.billing.CSRCreditCardTypeDataContainer();
  creditCardTypeDataContainer.initialize();
};

/**
 * This method adds the credit card type to the container.
 */
atg.commerce.csr.order.billing.addCreditCardTypeData = function (pCardType, pCode){
  //create javascript object and add to the container
  var methodsInstance = new atg.commerce.csr.order.billing.CreditCardTypeData (pCardType,pCode);
  creditCardTypeDataContainer.addCreditCardTypeData(methodsInstance);
};// end of addCreditCardTypeData

/**
 * This method fires the event with balance amount.
 *
 */
atg.commerce.csr.order.billing.firePaymentBalanceDojoEvent = function (){
  container.firePaymentBalanceDojoEvent();
};// end of firePaymentBalanceDojoEvent

/**
 * This method listens for the balance amount event and if the amount is zero,
 * this it disable the next button. Also displays the balance amount.
 */
atg.commerce.csr.order.billing.paymentBalanceEventListener  = function (pEvent) {
  if ( !pEvent) {
    return;
  }
  var formattedBalanceAmount;
  var balanceAmount = pEvent.balance;
  if ((balanceAmount * 1) === 0 ) {
    this.disableCheckoutButtons(false);
  } else {
    this.disableCheckoutButtons(true);
  }
  balanceDivtag = document.getElementById('displayCSRCustomerPaymentBalance');
  if (balanceDivtag) {
    formattedBalanceAmount = this.formatAmount(balanceAmount, container.currencyCode);
    if (typeof formattedBalanceAmount !== 'undefined') {
      balanceDivtag.innerHTML=formattedBalanceAmount;
    }
  }
};

/**
 * This method disables the next button in the billing page and returns page.
 */
atg.commerce.csr.order.billing.disableCheckoutButtons = function (pFlag) {
  var nextButton = dijit.byId('checkoutFooterNextButton');
  if (nextButton) {
    pFlag ? nextButton.disableButton() : nextButton.enableButton();
  }
  var saveButton = dijit.byId('checkoutFooterSaveButton');
  if (saveButton) {
    pFlag ? saveButton.disableButton() : saveButton.enableButton();
  }
};

/**
 * This method is called from the credit card, store credit and gift certificate page. When the users changes the
 * amount in any of the above payment types, then the payment balance is recomputed.
 *
 */
atg.commerce.csr.order.billing.recalculatePaymentBalance  = function (pParams) {

  //on key press, get the payment group name and amount value.
  //deduct the differences.
  if (!pParams.pmtWidget) {
    dojo.debug("The payment widget is not available.");
    return;
  }
  paymentGroupId = pParams.pmtWidget.id;

  var paymentMethod = container.getAvailablePaymentMethodByKey(paymentGroupId);
  if(!paymentMethod ){return;}

  var currentCurrencyWidget = pParams.pmtWidget;

  var newAmount = currentCurrencyWidget.getValue()*1;
  var oldAmount = paymentMethod.amount*1;
  var balanceAmount = container.balance*1;
  var zeroAmount = 0.0 *1;

  var validOpResultHolder = null;

  if (isNaN(newAmount)) {
    var errormessage = getResource('csc.billing.invalidAmount');
    currentCurrencyWidget.invalidMessage=errormessage;
    atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
    return false;
  }

  if (this.isMaxAmountDefined(paymentMethod)) {

    //If the new amount is equal to or greater than the max allowed amount, then the
    //amount is considered as maximum amount reached. If the newAmount === maxAllowedAmount,
    //we should not display error message.

    if (this.isMaxAmountReached(paymentMethod, newAmount)) {
      var remainingAmount = this.calculateRemainingAmount (paymentMethod, newAmount);
      if (typeof remainingAmount !== 'undefined' && ((remainingAmount*1)===zeroAmount) ) {
        paymentMethod.amount = newAmount;
        if (newAmount >= oldAmount) {
          container.balance = this.roundAmount(balanceAmount + (newAmount - oldAmount));
        } else {
          container.balance = this.roundAmount(balanceAmount + (oldAmount - newAmount));
        }
        //after this make sure that we have the correct amount, by executing the
        //reconsileBalanceAmount().
      } else {
        currentCurrencyWidget.state = "Error";
        currentCurrencyWidget._setStateClass();
        currentCurrencyWidget.displayMessage(getResource('csc.billing.invalidMaximumLimit'));
        currentCurrencyWidget.setValue(oldAmount);
        currentCurrencyWidget.validate(false);
        //after this make sure that we have the correct amount, by executing the
        //reconsileBalanceAmount().
      }
    } else {
      paymentMethod.amount = newAmount;
      if (newAmount >= oldAmount) {
        container.balance = this.roundAmount(balanceAmount + (newAmount - oldAmount));
      } else {
        container.balance = this.roundAmount(balanceAmount + (oldAmount - newAmount));
      }
    }
  } else {
    //User is replacing the old amount with new amount. Thus add the old amount back to the balance and deduct the new
    //amount.

    //if the amount is zero, it is not going through this loop.
    //thus adding zero check.

    if (oldAmount || (oldAmount === 0)) {
      balanceAmount = this.roundAmount(balanceAmount + oldAmount);
    }

    //if the amount is zero, it is not going through this loop.
    //thus adding zero check.
    if (newAmount || (newAmount === 0)) {
      balanceAmount = this.roundAmount(balanceAmount - newAmount);
      paymentMethod.amount = newAmount;
    }
    container.balance = balanceAmount;
  }
  this.reconsileBalanceAmount();
  this.firePaymentBalanceDojoEvent();
  return true;
};

/**
 * This method is to make sure that the order amount and user entered amount is not out of sync.
 *
 */
atg.commerce.csr.order.billing.reconsileBalanceAmount = function ()  {

  var allPayments = container.availablePaymentMethods;
  if (!allPayments) {
    container.balance = container.amountDue;
    return;
  }
  var paymentTotal = 0;
  var i = 0;
  for(i = 0; i < allPayments.length; i++){
    dojo.debug("before adding amount for paymentGroup" + allPayments[i].paymentGroupId + " amount :" + paymentTotal);
    paymentTotal = this.roundAmount((paymentTotal*1) + (allPayments[i].amount * 1));
    dojo.debug("after adding amount for paymentGroup" + allPayments[i].paymentGroupId + " amount :" + paymentTotal);
  }

  var actualBalance = this.roundAmount((container.amountDue * 1) - (paymentTotal * 1));
  dojo.debug("Actual balance is ::" + actualBalance);
  dojo.debug("Calculated balance is ::" + container.balance);

  if (actualBalance == container.balance) {
    dojo.debug("There is no problem with the calculation");
  } else {
    dojo.debug("There is a difference with the calculation. Resetting actual amount.");
    container.balance = actualBalance;
  }
};

/**
 * This method is called from the credit card, store credit and gift certificate page. When the users prefers to
 * apply the remaining amount to a payment group, then this method is called and then the payment balance is recomputed.
 *
 */
atg.commerce.csr.order.billing.applyRemainder  = function (pParams) {

  if (!this.csrBillingFormValidate()) {
    dojo.debug("The payment form contains errors.");
    var errormessage = getResource('csc.billing.form.error');
    atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
    return;
  }
  
  if (!pParams.pmtWidget) {
    dojo.debug("The payment widget is not available.");
    return;
  }
  var paymentGroupId = pParams.pmtWidget.id;

  var paymentMethod = container.getAvailablePaymentMethodByKey(paymentGroupId);
  if(!paymentMethod){
    return;
  }

  var currentCurrencyWidget = pParams.pmtWidget;

  if (this.isZeroBalance()) {
    var errormessage = getResource('csc.billing.zeroBalance');
    currentCurrencyWidget.invalidMessage=errormessage;
    atg.commerce.csr.common.addMessageInMessagebar ("warning", errormessage);
    this.firePaymentBalanceDojoEvent();
    //here we do not want to set the widget is invalid, because this is just an warning.
    return false;
  }

  var balanceAmount = container.balance*1;
  var currentAmount = paymentMethod.amount*1;
  var sumAmount = this.roundAmount(currentAmount + balanceAmount);
  var newAmount = sumAmount*1;
  var zeroAmount = 0.0 *1;
  var possibleMaxAmount = 0.0*1;

  if (this.isMaxAmountDefined(paymentMethod)) {
    if (this.isMaxAmountReached(paymentMethod, currentAmount)) {
      if (balanceAmount >= zeroAmount) {
        currentCurrencyWidget.state = "Error";
        currentCurrencyWidget._setStateClass();
        currentCurrencyWidget.displayMessage(getResource('csc.billing.maxAmountReached'));
        currentCurrencyWidget.setValue(currentAmount);
        currentCurrencyWidget.validate(false);
        //if the maximum amount is already reached, do not assign more.
        return false;
      } else {
        //if the balance is negative, then we need to reduce the balance to the maximum possible
        //amount.
        if ((balanceAmount * -1) >= currentAmount) {
          currentCurrencyWidget.setValue(zeroAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = zeroAmount;
          container.balance = this.roundAmount(balanceAmount + currentAmount);
        } else {
          possibleMaxAmount = this.roundAmount(balanceAmount + currentAmount);
          currentCurrencyWidget.setValue(possibleMaxAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = possibleMaxAmount;
          container.balance = zeroAmount;
        }
      }
    } else {
      var remainingAmount = this.calculateRemainingAmount (paymentMethod, currentAmount);
      if (remainingAmount && !((remainingAmount *1)  === 0)) {
        //remaining amount > balance, then sum the payment method amount and the balance,
        //that will be less than the maximum allowed amount.
        if (remainingAmount*1  >= balanceAmount*1) {
          currentCurrencyWidget.setValue(newAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = newAmount;
          container.balance = 0.00;
        } else {
          possibleMaxAmount = this.roundAmount((currentAmount*1) + (remainingAmount*1));
          currentCurrencyWidget.setValue(possibleMaxAmount);
          currentCurrencyWidget.validate(false);
          paymentMethod.amount = possibleMaxAmount;
          container.balance = this.roundAmount((container.balance*1) - (remainingAmount*1));
        }
      }
    }
  } else {
    if ((((newAmount * 1) === 0) || ((newAmount * 1) > 0))) {
      if (this.isValidAmount(newAmount, container.currencyCode)) {
        currentCurrencyWidget.setValue(newAmount);
        currentCurrencyWidget.validate(false);
        paymentMethod.amount = newAmount;
        container.balance = 0.00;
      }
    } else {
      var errormessage = getResource('csc.billing.negativeAmount');
      currentCurrencyWidget.invalidMessage=errormessage;
      atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
      //here we do not want to set the widget is invalid, because this is just an warning.
    }
  }
  this.reconsileBalanceAmount();
  this.firePaymentBalanceDojoEvent();
};

/**
 * This method is responsible for handling instore pickup shipping group checkbox checking/unckecking
 *
 */
atg.commerce.csr.order.billing.checkInStorePaymentCheckbox = function(checkbox, inputNode, textNode, amount, fullAmount) {
  if (checkbox.checked) {
    document.getElementById(textNode).innerHTML = fullAmount;
    var paymentMethod = container.getAvailablePaymentMethodByKey(inputNode);
    if (parseInt(amount)) {
      document.getElementById(inputNode).value = amount;
    } else {
      document.getElementById(inputNode).value = container.balance;
    }
    if (parseInt(paymentMethod.amount)) {
      paymentMethod.initialAmount = paymentMethod.amount;
    } else {
      if (!parseInt(paymentMethod.initialAmount) && !parseInt(paymentMethod.amount)) {
        paymentMethod.amount = container.balance;
      } else {
        paymentMethod.amount = paymentMethod.initialAmount;
      }
    }
  } else {
    document.getElementById(inputNode).value = 0;
    document.getElementById(textNode).innerHTML = 0;
    var paymentMethod = container.getAvailablePaymentMethodByKey(inputNode);
    paymentMethod.amount = "0.0";
  }
  atg.commerce.csr.order.billing.reconsileBalanceAmount();
  this.firePaymentBalanceDojoEvent();
};

/**
 *
 * This method is to identify whether the maximum amount is defined and this helps to allocate
 * amount properly.
 *
 */
atg.commerce.csr.order.billing.isMaxAmountDefined = function (pPaymentMethod) {

  if (!pPaymentMethod) {
    return false;
  }
  var maxAllowedAmount = pPaymentMethod.maxAllowedAmount;

  //if the maxAllowedAmount is not set, the application does not need to check the limit.
  if (!maxAllowedAmount) {
    return false;
  }

  //if the maxAllowedAmount is Infinity, the application does not need to check the limit.
  if (maxAllowedAmount === 'Infinity') {
    return false;
  }

  if (typeof maxAllowedAmount != 'undefined'  && ((maxAllowedAmount * 1) >= 0*1)) {
    return true;
  }

  return false;

};


/**
 *
 * Each payment method has an maximum amount. If the maximum allowed amount is set and if the computed
 * amount is greater than the allowed amount, this method return true. Otherwise false.
 *
 */
atg.commerce.csr.order.billing.isMaxAmountReached = function (pPaymentMethod, pAmount) {

  if (this.isMaxAmountDefined (pPaymentMethod)) {
    var maxAllowedAmount = pPaymentMethod.maxAllowedAmount;
    if ((maxAllowedAmount * 1) <= (pAmount * 1)) {
      return true;
    }
  }
  return false;
};

/**
 * Each payment method has an maximum amount. If the maximum allowed amount is set and if the computed
 * amount is greater than the allowed amount, this method return true. Otherwise false.
 *
 */
atg.commerce.csr.order.billing.calculateRemainingAmount = function (pPaymentMethod, pAmount) {
  var maxAllowedAmount;
  var zeroAmount = 0.0 * 1;
  //This provides an additional check. We are making sure that we are not working with undefined
  //or with infinity.


  if (!atg.commerce.csr.order.billing.isMaxAmountReached(pPaymentMethod, pAmount)) {
    maxAllowedAmount = pPaymentMethod.maxAllowedAmount;
    if ((maxAllowedAmount*1) === (pAmount * 1)) {
      return zeroAmount;
    } else {
      return this.roundAmount((maxAllowedAmount*1) - (pAmount * 1));
    }
  } else {
    if (atg.commerce.csr.order.billing.isMaxAmountDefined(pPaymentMethod)) {
      maxAllowedAmount = pPaymentMethod.maxAllowedAmount;
      if ((maxAllowedAmount*1) === (pAmount * 1)) {
        return zeroAmount;
      }
    }
  }
};


atg.commerce.csr.order.billing.existingCreditCardAddressSelectedRule = function (arguments1, arguments2, arguments3) {
  return atg.commerce.csr.order.billing.selectionRule (arguments1, arguments2, 'true');
};

atg.commerce.csr.order.billing.newCreditCardAddressSelectedRule = function (arguments1, arguments2) {
  return atg.commerce.csr.order.billing.selectionRule (arguments1, arguments2, 'false');
};

atg.commerce.csr.order.billing.selectionRule = function (arguments1, arguments2, arguments3) {
  var form = document.getElementById(arguments1);
  var radio = form.elements [arguments2];
  if(!radio){return false;}

  var checkedValue;
  //find the radio value checked

  //this means there is only one radio button in the
  if (radio.length === undefined ) {
    checkedValue = radio.value;
  } else {
    for(i = 0; i < radio.length; i++){
      if(radio[i].checked){
        checkedValue = radio[i].value;
        break;
      }
    }// end of for loop
  }

  if (checkedValue === arguments3) {
    return true;
  } else {
    return false;
  }
};

atg.commerce.csr.order.billing.notifyAddNewCreditCardValidators = function () {

};

atg.commerce.csr.order.billing.notifyEditCreditCardValidators = function () {
};

/**
 *  First this method checks to see if the amount is valid. Sometimes, the user may not enter the data with
 *  appropriate comma separator between the number. If the user does not enter the valid separator, this method
 *  formats and parses again. As a result this method call, this method returns the result of the validation operation.
 */
atg.commerce.csr.order.billing.isValidAmount = function (pAmount, pCurrencyCode, pFlags/*optional*/) {

  if (typeof pAmount === 'undefined') {
    return false;
  }
  if (isNaN(pAmount)) {
    result = this.parseAmount(pAmount, pFlags);
    if (isNaN(result)) {
      return false;
    } else {
      return true;
    }
  } else {
    return true;
  }
};

/**
 * This method returns true, if the balance is zero. Otherwise false
 */
atg.commerce.csr.order.billing.isZeroBalance = function () {
  if ((container.balance * 1) === 0 ) {
    return true;
  }
  return false;
};

/**
 * This method parses the amount.
 */
atg.commerce.csr.order.billing.parseAmount = function (pAmount, pFlags/*optional*/) {
  var result;

  if (typeof pAmount === 'undefined') {
    return;
  }

  if (typeof pFlags == 'undefined') {
    pFlags = {
        places : ('places' in container) ? container.places : 2,
        fractional : true,
        locale : container.locale,
        currency : container.currencyCode,
        symbol : container.currencySymbol
    };
  }
  result = dojo.currency.parse (pAmount, pFlags);
  return result;
};

/**
 * This method formats the given amount.
 */
atg.commerce.csr.order.billing.formatAmount = function (pAmount, pCurrencyCode, pRemoveCurrencySymbol, pFlags/*optional*/) {
  if (typeof pFlags == 'undefined') {
    pFlags = {
        places : ('places' in container) ? container.places : 2,
        round : true,
        locale : container.locale,
        currency : container.currencyCode,
        symbol : container.currencySymbol
    };
  }

  if (typeof pAmount === 'undefined') {
    return;
  }

  if (typeof pCurrencyCode === 'undefined' || pCurrencyCode === '') {
    return;
  }

  return dojo.currency.format(pAmount, pFlags);
};

/**
 *
 * There is a possibility that a credit card can be expired. In case if the credit card is expired,
 * the amount and cvv fields should be disabled.
 *
 */
atg.commerce.csr.order.billing.disableExpiredCreditCardControls = function (pParams) {

  var paymentWidgetId = null;
  var cvvWidgetId = null;
  var paymentWidget = null;
  var cvvWidget = null;
  var currentAmount = 0.0;
  var paymentMethod = null;

  if (pParams.paymentWidgetId) {
    paymentWidgetId = pParams.paymentWidgetId;
    paymentWidget = dijit.byId(paymentWidgetId);
    if (paymentWidget) {
      atg.commerce.csr.common.disableTextboxWidget(paymentWidget);
      currentAmount = paymentWidget.getValue();
      if ((currentAmount * 1) > 0) {
        container.balance =  (container.balance *1) + (currentAmount * 1);
        paymentMethod = container.getAvailablePaymentMethodByKey(paymentWidgetId);
        if (paymentMethod) {
          paymentMethod.amount = 0.0;
        }
        this.reconsileBalanceAmount();
        this.firePaymentBalanceDojoEvent();
      }
      paymentWidget.setValue('0.0');
    }
  }

  if (pParams.cvv) {
    cvvWidgetId = pParams.cvv;
    cvvWidget = dijit.byId(cvvWidgetId);
    if (cvvWidget) {
      atg.commerce.csr.common.disableTextboxWidget(cvvWidget);
    }
  }
};

atg.commerce.csr.order.billing.saveUserInput = function (pParams) {
  dojo.debug("entering saveUserInput()");
  var form  = document.getElementById("csrBillingForm");
  atg.commerce.csr.common.enableDisable([{form: "csrBillingForm", name: "csrPaymentGroupsPreserveUserInputOnServerSide"}],
      [{form: "csrBillingForm", name: "csrHandleApplyPaymentGroups"}]);
  var d = atgSubmitAction({form:form, handleAs: 'json'});
  var result = d.addCallback(function(result){ return result; });
  var value;
  if (result)  {
    value = result.error;
    if (!value) {
      dojo.debug("There is no error in saving user data.");
      return true;
    }
  }
  dojo.debug("leaving saveUserInput()");
  return false;
};

atg.commerce.csr.order.billing.isValidCreditCardMonth = function (pMonthWidget, pYearWidget) {

  if (!pMonthWidget) {
    return false;
  }

  var month = pMonthWidget.getValue();
  var monthNumberIns = new Number (month);

  if (month === '' || !((typeof monthNumberIns == 'number') || (monthNumberIns instanceof Number))) {
    dojo.debug("Supplied month is not a number.");
    pMonthWidget.invalidMessage=getResource('csc.billing.invalidMonth');
    return false;
  }
  
  // if the year widget is not valid, then we do not need to test
  // month and year combination. Year is invalid anyway.
  // by returning true, this function says that month widget is not an issue
  if (!this.isValidCreditCardYear(pYearWidget)) {
    return true;
  }
  
  //if the month and year combination is invalid, there is only one possible wrong answer, that is the month
  // is not valid
  if (!this.isValidCreditCardExpDate(pMonthWidget, pYearWidget)) {
    pMonthWidget.invalidMessage=getResource('csc.billing.invalidMonth');
    return false;
  }
  
  return true;
};

atg.commerce.csr.order.billing.isValidCreditCardYear = function (pYearWidget) {

  if (!pYearWidget) {
    return false;
  }

  var year = pYearWidget.getValue();
  var yearNumberIns = new Number (year);

  if (year === '' || !((typeof yearNumberIns == 'number') || (yearNumberIns instanceof Number))) {
    dojo.debug("Supplied year is not a number.");
    pYearWidget.invalidMessage=getResource('csc.billing.invalidYear');
    return false;
  }
  return true;
};

atg.commerce.csr.order.billing.isValidCreditCardExpDate = function (pMonthWidget, pYearWidget) {

  var month = pMonthWidget.getValue();
  var year = pYearWidget.getValue();

  var monthNumberIns = new Number (month);
  var yearNumberIns = new Number (year);

  dojo.debug("month and year is valid numbers.");
  var today = new Date();
  var jsMonth = monthNumberIns - 1;
  var expiryDate = new Date(yearNumberIns, jsMonth);  
  var daysInMonth = dojo.date.getDaysInMonth (expiryDate);
  //month is deducted by 1 to accommodate the difference between the month drop down and
  // to satisfy the javascript condition.
  expiryDate = new Date(yearNumberIns, jsMonth, daysInMonth);
  if (dojo.date.compare (today, expiryDate,"day") > 0) {
    dojo.debug("You can't choose the non-current date.");
    return false;
  } else {
    dojo.debug("User selected a future date.");
    return true;
  }
};

/**
 *
 * This method is used primarily in edit credit card pages. The edit credit card pages usually have a masked
 * original credit card. If the credit card number is changed, then the credit card should be validated to make sure
 * the entered number follows certain guidelines such as mod check.<b>
 * If the masked credit card number is modified, then the validation route performs the normal check and based on the
 * routine it returns true/false.
 *
 * @param creditCardType -- pCardTypeWidget -- The credit card type widget should be passed in -- Required
 * @param creditCardNumber -- pCardNumberWidget -- The credit card number widget should be passed in -- Required
 * @param originalMaskedCreditCardNumber -- pOriginalMaskedCreditCardNumber -- The original masked credit card number should be passed in -- Required
 *
 */
atg.commerce.csr.order.billing.isValidCreditCardNumberInEditContext = function (pParams) {

  if (!pParams.originalMaskedCreditCardNumber) {
    return false;
  }

  if (!pParams.creditCardType) {
    return false;
  }

  if (!pParams.creditCardNumber) {
    return false;
  }

  var currentCCNumber = pParams.creditCardNumber.getValue();
  if (pParams.originalMaskedCreditCardNumber === currentCCNumber) {
    return true;
  } else {
    return this.isValidCreditCardNumber (pParams.creditCardType, pParams.creditCardNumber);
  }
};

/**
 *
 * This method gets current values from the credit card type and credit card number widgets and
 * using dojo.validate.isValidCreditCard() to validate the credit card.
 *
 */
atg.commerce.csr.order.billing.isValidCreditCardNumber = function (pCardTypeWidget, pCardNumberWidget) {
  if (!pCardTypeWidget) {
    return false;
  }

  if (!pCardNumberWidget) {
    return false;
  }
  var ccType = pCardTypeWidget.getValue();
  var ccNumber = pCardNumberWidget.getValue();

  if (ccType === '') {
    dojo.debug("Supplied credit card type is not valid.");
    pCardTypeWidget.promptMessage="Please select a valid credit card type.";
    return false;
  }
  if (ccNumber === '') {
    dojo.debug("Supplied card number is not valid.");
    pCardNumberWidget.invalidMessage=getResource('csc.billing.invalidCreditCardNumber');
    return false;
  }
  var cardTypeData = creditCardTypeDataContainer.getCreditCardTypeDataByKey(ccType);
  if (!cardTypeData) {
    dojo.debug("Supplied credit card type is not valid.");
    pCardTypeWidget.invalidMessage=getResource('csc.billing.invalidCreditCardType');
    return false;
  }

  var code = cardTypeData.code;
  if (!cardTypeData) {
    dojo.debug("Supplied credit card type is not valid.");
    pCardTypeWidget.invalidMessage=getResource('csc.billing.invalidCreditCardType');
    return false;
  }

  if (dojox.validate.isValidCreditCard(ccNumber, code)) {
    dojo.debug("This is a valid credit card number.");
    return true;
  } else {
    dojo.debug("Please provide a valid credit card number.");
    pCardNumberWidget.invalidMessage=getResource('csc.billing.invalidCreditCardNumber');
    return false;
  }
};

/**
 *
 * The AvailablePaymentMethod instance is created for each payment groups  or refund methods.
 * @param paymentGroupId -- required
 * @param amount  -- required
 * @param maxAllowedAmount -- optional
 *
 */
atg.commerce.csr.order.billing.AvailablePaymentMethod = function (pParams) {
  this.paymentGroupId=pParams.paymentGroupId;
  this.paymentGroupType=pParams.paymentGroupType;
  this.amount=pParams.amount;
  this.initialAmount=pParams.initialAmount;
  this.maxAllowedAmount=pParams.maxAllowedAmount;
};

atg.commerce.csr.order.billing.CSRPaymentContainer = function (pBalance,pAmountDue,pCurrencyCode, pParams) {
  this.balance=pBalance;
  this.availablePaymentMethods=[];
  this.amountDue=pAmountDue;
  this.currencyCode=pCurrencyCode;
  if (pParams != null) {
    this.locale=pParams.locale;
    this.currencySymbol=pParams.currencySymbol;
    this.places=pParams.places;
  }

  this.getAvailablePaymentMethodByKey = function(pKey) {
    var i=0;
    if (!this.availablePaymentMethods) {
      return null;
    }
    if (this.availablePaymentMethods.length === undefined ) {
      if (this.availablePaymentMethods.paymentGroupId == pKey) {
        return this.availablePaymentMethods;
      } else {
        return null;
      }
    } else {
      for (i = 0; i < this.availablePaymentMethods.length; i++) {
        if (this.availablePaymentMethods[i].paymentGroupId == pKey) {
          return this.availablePaymentMethods[i];
        }
      }
      return null;
    }
  };//end of getAvailablePaymentMethodByKey

  this.addAvailablePaymentMethod = function (pAvailablePaymentMethod) {
    //This will add an element to the array
    var length = this.availablePaymentMethods.length;
    this.availablePaymentMethods[length] = pAvailablePaymentMethod;

    var currentPaymentMethodAmount = pAvailablePaymentMethod.amount;
    var balanceAmount;
    if (currentPaymentMethodAmount > 0) {
      balanceAmount = this.balance;
      this.balance = atg.commerce.csr.order.billing.roundAmount ((balanceAmount * 1)  - (currentPaymentMethodAmount * 1));
    }
  };//end of addAvailablePaymentMethod

  this.deleteAvailablePaymentMethodByKey = function (pKey) {
    //TODO: We do not need this function for now. Add this later if we require.
    //This will remove an element from the array
  };//end of deleteAvailablePaymentMethodByKey

  this.initialize = function (pOrderTotal, pCurrencyCode, pParams) {
    //This will remove an element from the array
    this.availablePaymentMethods = [];
    this.amountDue=pOrderTotal;
    this.balance=pOrderTotal;
    this.currencyCode=pCurrencyCode;
    this.locale=pParams.locale;
    this.currencySymbol=pParams.currencySymbol;
    this.places=pParams.places;
  };// end of initialize

  this.firePaymentBalanceDojoEvent = function () {
    dojo.publish( "/atg/commerce/csr/order/PaymentBalance",
        [{
          event: "PaymentBalance",
          balance: this.balance
        }]);
  };//end of deleteAvailablePaymentMethodByKey
};

/**
 *
 * This class contains all available credit card type data.
 * The card type data has type and code properties.
 *
 */
atg.commerce.csr.order.billing.CSRCreditCardTypeDataContainer = function () {
  this.creditCardTypeData=[];
  this.initialize = function () {
    this.creditCardTypeData=[];
  };// end of initialize

  this.getCreditCardTypeDataByKey = function(pKey) {
    for (var i = 0; i < this.creditCardTypeData.length; i++) {
      if (this.creditCardTypeData[i].cardType == pKey) {
        return this.creditCardTypeData[i];
      }
    }//end of for
  };//end of getAvailablePaymentMethodByKey

  this.addCreditCardTypeData = function (pCreditCardTypeData) {
    //This will add an element to the array
    var length = this.creditCardTypeData.length;
    this.creditCardTypeData[length] = pCreditCardTypeData;
  };//end of addCreditCardTypeData
};

atg.commerce.csr.order.billing.CreditCardTypeData = function (pCardType, pCode) {
  this.cardType=pCardType;
  this.code=pCode;
};

/**
 *    This object is used to hold the result data for the currency validation operation.
 *  Sometimes, the user may not enter the data with appropriate comma separator between the number.
 *  After the validation operation, this object will contain the result.
 */
atg.commerce.csr.order.billing.CurrencyValidationResultHolder = function (pValid, pFormatted, pAmount) {
  this.valid = pValid;
  this.formatted=pFormatted;
  this.amount=pAmount;
};

/**
 * This method is called during the page initial rendering. If there is a balance and if there are
 * some payment types, the payment is assigned against to the payment types.
 *
 */
atg.commerce.csr.order.billing.assignBalance = function () {

  var allPayments = container.availablePaymentMethods;
  if (!allPayments) {
    dojo.debug(" There is no payment method. Thus leave the method.");
    return;
  }

  if ((container.balance * 1) === 0) {
    dojo.debug(" The balance is zero. No need to assign the balance to the payment types. Thus leave the method.");
    return;
  }
  var i = 0;
  var paymentId;
  var amount;
  var possibleMaximumAmount;
  var maxAllowedAmount;
  var balanceAmount = container.balance;
  var paymentWidget;

  for(i = 0; i < allPayments.length; i++){
    paymentId = allPayments[i].paymentGroupId;
    amount = allPayments[i].amount;
    maxAllowedAmount = allPayments[i].maxAllowedAmount;
    paymentWidget = dijit.byId(paymentId);
    dojo.debug("Widget Id" + paymentId + " amount :" + amount + " maxAllowedAmount :" + maxAllowedAmount);

    dojo.debug("Looping through a paymentGroup with payment group Id ::" + paymentId);
    dojo.debug("Locale value is ::" + container.locale);

    if ((container.balance * 1) === 0) {
      dojo.debug(" The balance is zero. No need to assign the balance to the payment types. Thus leave the method.");
      return;
    }

    if ((container.balance *1) > 0) {
      //if there max amount specified in the payment method, the first condition will be satisfied,
      //otherwise the second condition will be satisfied.
      if (paymentWidget && maxAllowedAmount && maxAllowedAmount !== 'Infinity') {
        possibleMaximumAmount = this.roundAmount ((maxAllowedAmount * 1) - (amount*1));
        if ((possibleMaximumAmount*1) >= (container.balance*1)) {
          allPayments[i].amount = container.balance*1;
          
          container.balance = 0.0;
          dojo.debug("container balance set to 0.0");
        } else {
          allPayments[i].amount = possibleMaximumAmount*1;
          container.balance = this.roundAmount((container.balance*1) - (possibleMaximumAmount * 1));
          dojo.debug("Container balance now reduced by " + allPayments[i].amount + " new value = " + container.balance);
        }
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      } else if (paymentWidget && ((typeof maxAllowedAmount === 'undefined') || (maxAllowedAmount === 'Infinity'))) {
    	
        allPayments[i].amount = this.roundAmount((allPayments[i].amount *1) + container.balance);
        dojo.debug("Amount being set to " + allPayments[i].amount);
        container.balance = 0.0;
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      }
    } else {
    	dojo.debug("Container balance is not greater than zero");
      //amount is always positive. In order to compare against negative number, multiplying by -1.
      if ((container.balance *-1) >= (amount*1)) {
        allPayments[i].amount =  0.0 ;
        container.balance = this.roundAmount((container.balance*1) + allPayments[i].amount);
        dojo.debug("New balance set to " + container.balance);
        
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      } else {
    	 
        allPayments[i].amount = this.roundAmount((allPayments[i].amount * 1) + (container.balance*1));
        dojo.debug("New amount being set to " + allPayments[i].amount);
        container.balance = 0.0;
        paymentWidget.setValue (allPayments[i].amount);
        paymentWidget.validate(false);
      }
    }
  }
};


/**
 *
 * This method does not alter the cvv value, all it does is to verify the entered
 * values are integer values.
 *
 */
atg.commerce.csr.order.billing.isValidCVV = function (pWidget) {

  if (!pWidget) {
    return false;
  }
  var format = "????";
  var flags = {format:format};

  var cvvValue = pWidget.getValue();
  return dojox.validate.isNumberFormat (cvvValue, flags);
};

/**
 *
 * This function is used to validate the CSC billing form or available payment options page.
 *
 */
atg.commerce.csr.order.billing.csrBillingFormValidate = function () {

  var formvalid = true;

  //This checks to make sure all the cvv elements are valid.
  var allPayments = container.availablePaymentMethods;
  if (!allPayments) {
    dojo.debug(" There is no payment method. Thus leave the method.");
    return true;
  }

  var paymentGroupId = null;
  var cvvElementName = null;
  var cvvWidget = null;
  var paymentWidget = null;
  //need to loop through all CVV to mark the invalid characters.

  for(i = 0; i < allPayments.length; i++){
    paymentGroupId = allPayments[i].paymentGroupId;
    paymentGroupType = allPayments[i].paymentGroupType;
    dojo.debug("Looping through a paymentGroup with payment group Id ::" + paymentGroupId);
    cvvElementName = paymentGroupId+"CVV";
    dojo.debug("CVV element name is ::" + cvvElementName);
    cvvWidget = dijit.byId(cvvElementName);
    paymentWidget = dijit.byId(paymentGroupId);

    if (paymentGroupId && cvvWidget ) {
      if (!atg.commerce.csr.order.billing.isValidCVV(cvvWidget)) {
        var errormessage = getResource('csc.billing.invalidCVVNumber');
        cvvWidget.invalidMessage=errormessage;
        atg.commerce.csr.common.addMessageInMessagebar ("error", errormessage);
        formvalid = false;
        break;
      }
    }
    if (paymentGroupId && paymentWidget ) {
      if (!paymentWidget.isValid() && paymentGroupType != "inStorePayment") {
        formvalid = false;
        break;
      }
    }
    if (allPayments.length == 1 && paymentGroupId && paymentGroupType == "inStorePayment" && !dojo.byId(paymentGroupId + "_checkbox").checked) {
      formvalid = false;
      break;
    }
  }

  if (formvalid) {
    if (atg.commerce.csr.order.billing.isZeroBalance()) {
      //the form is valid and buttons needs to be enabled.
      atg.commerce.csr.order.billing.disableCheckoutButtons(false);
    }
  } else {
    //the form is not valid and buttons needs to be disabled.
    atg.commerce.csr.order.billing.disableCheckoutButtons(true);
  }
  
  return formvalid;

};

/**
 * This method rounds a number to the places value defined in the container and if not present defaults to two decimal places.
 */
atg.commerce.csr.order.billing.roundAmount = function (pAmount) {
  dojo.debug("The original amount is ::" + pAmount);
  var result = dojo.number.round(pAmount, ('places' in container) ? container.places : 2);
  dojo.debug("The rounded original amount is ::" + result);
  return result;
};
