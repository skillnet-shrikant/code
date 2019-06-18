/* Copyright (C) 1999-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */

/*************************************************************************
 *
 * deprecatedFunctions.js
 *
 * This file contains any JavaScript functions that have been deprecated
 * and are kept in the build for historical purposes only.
 *
 *************************************************************************/

/**
 * This method removes the currency symbol from the amount
 */
atg.commerce.csr.order.billing.replaceCurrencySymbol = function (pAmount, pCurrencyCode) {
  if (!pAmount)  {
    return;
  }

  if (!pCurrencyCode)  {
    pCurrencyCode ="USD";
  }

  var formatData = dojox.i18n.currency._mapToLocalizedFormatData(dojox.i18n.currency.FORMAT_TABLE, pCurrencyCode, null);
  if (typeof pAmount != 'string') {
    pAmount = pAmount.toString();
  }

  return pAmount.replace(new RegExp("\\" + formatData.symbol), "");
};

/**
 * This method removes the separator from the amount
 */
atg.commerce.csr.order.billing.replaceSeparator = function (pAmount, pCurrencyCode) {
  if (!pAmount)  {
    return;
  }

  if (!pCurrencyCode)  {
    pCurrencyCode ="USD";
  }

  var formatData = dojox.i18n.number._mapToLocalizedFormatData(dojox.i18n.currency.FORMAT_TABLE, pCurrencyCode, null);
  if (typeof pAmount != 'string') {
    pAmount = pAmount.toString();
  }

  return pAmount.replace(new RegExp("\\" + ','), "");
};

/**
 * This method formats the given amount for a text field.
 * We do not want to display the currency symbol or the number separators for the currency.
 * If we have to add the number separator, as soon as the user changes the number, we need to reformat to
 * display the number with correct formatting. While the system is trying to do the formatting, user could not change
 * the value. To begin with we do not want to use the formating and, display the currency symbol and separators for the
 * display only field.
 */
atg.commerce.csr.order.billing.formatTextFieldAmount = function (pAmount, pCurrencyCode, pFlags/*optional*/) {
  return atg.commerce.csr.order.billing.parseAmount (pAmount);
};

/**
 *
 */
atg.commerce.csr.order.billing.formatCurrencyWidgetAmount = function (pParams, pCurrencyCode) {
  dojo.debug("formatCurrencyWidgetAmount: " + pParams.paymentGroupId + " amount " + pParams.amount);

  if (!pParams.id) {
    dojo.debug("Valid id was not passed in");
    return;
  }

  var currencyWidget = dijit.byId(pParams.id);
  if (!currencyWidget){
    dojo.debug("Valid currency widget could not be found.");
    return;
  }

  currencyWidget.setValue(atg.commerce.csr.order.billing.formatTextFieldAmount(pParams.amount));

};


/**
 *
 * This method provides the correct formating for the refund adjustment widgets. This method is called during the
 * page initial rendering.
 *
 */
atg.commerce.csr.order.returns.formatRefundWidgets = function (pParams) {

  var currencyWidgetId = null;
  var currencyWidget = null;
  var amount = 0;

  if (pParams.shippingRefund) {
    currencyWidgetId = pParams.shippingRefund;
    if (currencyWidgetId) {
      currencyWidget = dijit.byId(currencyWidgetId);
      if (currencyWidget) {
        atg.commerce.csr.order.billing.formatCurrencyWidgetAmount({id:currencyWidgetId, amount:currencyWidget.getValue()}, refundContainer.currencyCode);
      }
    }
  }
  if (pParams.taxRefund) {
    currencyWidgetId = pParams.taxRefund;
    if (currencyWidgetId) {
      currencyWidget = dijit.byId(currencyWidgetId);
      if (currencyWidget) {
        atg.commerce.csr.order.billing.formatCurrencyWidgetAmount({id:currencyWidgetId, amount:currencyWidget.getValue()}, refundContainer.currencyCode);
      }
    }
  }

  if (pParams.otherRefund) {
    currencyWidgetId = pParams.otherRefund;
    if (currencyWidgetId) {
      currencyWidget = dijit.byId(currencyWidgetId);
      if (currencyWidget) {
        atg.commerce.csr.order.billing.formatCurrencyWidgetAmount({id:currencyWidgetId, amount:currencyWidget.getValue()}, refundContainer.currencyCode);
      }
    }
  }

  if (pParams.returnFee) {
    currencyWidgetId = pParams.returnFee;
    if (currencyWidgetId) {
      currencyWidget = dijit.byId(currencyWidgetId);
      if (currencyWidget) {
        atg.commerce.csr.order.billing.formatCurrencyWidgetAmount({id:currencyWidgetId, amount:currencyWidget.getValue()}, refundContainer.currencyCode);
      }
    }
  }
};



/**
 * This method is called when the user changes the value in the
 * text box.
 */
atg.commerce.csr.order.returns.recalculateTotal = function(pParams){
  var amount = 0.0;
  var currencyWidget = null;
  var validOpResultHolder = null;
  var currencyWidgetId = null;
  var returnItem = null;

  if (pParams.shippingRefund) {
    currencyWidget = pParams.shippingRefund;
  }
  if (pParams.taxRefund) {
    currencyWidget = pParams.taxRefund;
  }

  if (pParams.otherRefund) {
    currencyWidget = pParams.otherRefund;
  }

  if (pParams.returnFee) {
    currencyWidget = pParams.returnFee;
  }

  if (pParams.returnItem) {
    currencyWidget = pParams.returnItem;
  }

  if (!currencyWidget) {
    return;
  }

  amount = currencyWidget.getValue();
  if (isNaN(amount)) {
    var rawTextAmount = currencyWidget.textbox.value;
    if (rawTextAmount) {
      rawTextAmount = atg.commerce.csr.order.billing.replaceCurrencySymbol(rawTextAmount, refundContainer.currencyCode);
      validOpResultHolder = atg.commerce.csr.order.billing.isValidAmount(rawTextAmount, refundContainer.currencyCode);
      if (validOpResultHolder.valid) {
        amount = validOpResultHolder.amount;
      }
    }

    if (isNaN(amount)) {
      currencyWidget.invalidMessage=getResource('csc.billing.invalidAmount');
      atg.commerce.csr.order.returns.disableReturnProcessButtons(true);
      atg.commerce.csr.order.billing.disableCheckoutButtons(true);
      return false;
    }
  }

  atg.commerce.csr.order.returns.disableReturnProcessButtons(false);

  validOpResultHolder = atg.commerce.csr.order.billing.isValidAmount(amount, refundContainer.currencyCode);

  if (validOpResultHolder.valid) {
    if (pParams.shippingRefund) {
      refundContainer.shippingRefund = validOpResultHolder.amount;
      dojo.debug("The shippingRefund is " + validOpResultHolder.amount);
    }
    if (pParams.taxRefund) {
      refundContainer.taxRefund = validOpResultHolder.amount;
      dojo.debug("The taxRefund is " + validOpResultHolder.amount);
    }

    if (pParams.otherRefund) {
      refundContainer.otherRefund = validOpResultHolder.amount;
      dojo.debug("The otherRefund is " + validOpResultHolder.amount);
    }

    if (pParams.returnFee) {
      refundContainer.returnFee = validOpResultHolder.amount;
      dojo.debug("The returnFee is " + validOpResultHolder.amount);
    }

    if (pParams.returnItem) {
      currencyWidgetId = currencyWidget.id;
      returnItem = refundContainer.getReturnItemByKey(currencyWidgetId);
      if (returnItem) {
        returnItem.refundAmount = validOpResultHolder.amount;
      } else {
        return;
      }
      dojo.debug("The return item id ::" + currencyWidgetId);
      dojo.debug("The return item amount is::" + validOpResultHolder.amount);
    }

  } else {
    currencyWidget.invalidMessage=getResource('csc.billing.invalidAmount');
    currencyWidget.validate(false);
    return false;
  }

  atg.commerce.csr.order.returns.computeTotal();
  atg.commerce.csr.order.returns.displayCalculatedTotal();
};

/**
 * This method takes all the data and computes the total.
 */
atg.commerce.csr.order.returns.computeTotal = function() {

  if (!refundContainer) {
    return;
  }

  var itemSubtotal = 0.00;
  for (var i = 0; i < refundContainer.returnItems.length; i++) {
    itemSubtotal = atg.commerce.csr.order.billing.roundAmount( itemSubtotal + (refundContainer.returnItems[i].refundAmount * 1));
    dojo.debug("returnItem id is ::" + refundContainer.returnItems[i].id + " and the amount is ::" + refundContainer.returnItems[i].refundamount);
    dojo.debug("current itemSubtotal is " + itemSubtotal);
  }

  refundContainer.itemSubtotal = itemSubtotal;

  dojo.debug("The itemSubtotal is " + itemSubtotal);

  var refundSubtotal = itemSubtotal;
  if (refundContainer.shippingRefund) {
    refundSubtotal = atg.commerce.csr.order.billing.roundAmount(refundSubtotal + (refundContainer.shippingRefund * 1));
  }

  dojo.debug("The refundSubtotal with shipping is ::" + refundSubtotal);

  if (refundContainer.taxRefund) {
    refundSubtotal = atg.commerce.csr.order.billing.roundAmount( refundSubtotal + (refundContainer.taxRefund * 1));
  }

  dojo.debug("The refundSubtotal with tax is ::" + refundSubtotal);

  if (refundContainer.otherRefund) {
    refundSubtotal = atg.commerce.csr.order.billing.roundAmount(refundSubtotal + (refundContainer.otherRefund * 1));
  }

  dojo.debug("The refundSubtotal with otherRefund is ::" + refundSubtotal);

  refundContainer.refundSubtotal = refundSubtotal;

  if (refundContainer.returnFee) {
    refundSubtotal = atg.commerce.csr.order.billing.roundAmount(refundSubtotal - (refundContainer.returnFee * 1));
  }

  dojo.debug("The refundSubtotal with otherRefund is ::" + refundSubtotal);

  refundContainer.refundTotal = refundSubtotal;
};

/**
 *
 * This method displays the calculated amount in the return items adjustment UI.
 *
 */
atg.commerce.csr.order.returns.displayCalculatedTotal = function () {
  var formattedItemSubtotal = 0.0;
  var formattedRefundSubtotal = 0.0;
  var formattedRefundTotal = 0.0;

  formattedItemSubtotal = atg.commerce.csr.order.billing.formatAmount (refundContainer.itemSubtotal, refundContainer.currencyCode);
  formattedRefundSubtotal = atg.commerce.csr.order.billing.formatAmount (refundContainer.refundSubtotal, refundContainer.currencyCode);
  formattedRefundTotal = atg.commerce.csr.order.billing.formatAmount (refundContainer.refundTotal, refundContainer.currencyCode);

  var itemRefundDiv = document.getElementById('csrReturnTotalItemRefund');
  if (itemRefundDiv) {
    itemRefundDiv.innerHTML=formattedItemSubtotal;
  }

  var refundSubtotalDiv = document.getElementById('csrReturnRefundSubtotal');
  if (refundSubtotalDiv) {
    refundSubtotalDiv.innerHTML=formattedRefundSubtotal;
  }

  var refundTotalDiv = document.getElementById('csrReturnRefundTotal');
  if (refundTotalDiv) {
    refundTotalDiv.innerHTML=formattedRefundTotal;
  }

};

/**
 * This variable holds the container instance.
 */
atg.commerce.csr.order.returns.initializeRefunds = function(pParams){
  refundContainer.initializeRefunds(pParams);
  atg.commerce.csr.order.returns.computeTotal();
};

/**
 * The RefundContainer object contains all refund amount details.
 *
 */
atg.commerce.csr.order.returns.RefundContainer = function () {
  this.returnItems = [];
  this.shippingRefund=0.0;
  this.taxRefund=0.0;
  this.otherRefund=0.0;
  this.returnFee =0.0;
  //all the dispplay fields
  this.itemSubtotal=0.0;
  this.refundSubtotal=0.0;
  this.refundTotal=0.0;
  //currency code
  this.currencyCode=null;

  this.initialize = function (pParams) {
    this.returnItems=[];
    this.currencyCode=pParams.currencyCode;
  };// end of initialize

  this.initializeRefunds = function (pParams) {
    this.shippingRefund=pParams.shippingRefund;
    this.taxRefund=pParams.taxRefund;
    this.otherRefund=pParams.otherRefund;
    this.returnFee =pParams.returnFee;
  };// end of initialize

  this.getReturnItemByKey = function(pKey) {
    for (var i = 0; i < this.returnItems.length; i++) {
      if (this.returnItems[i].id == pKey) {
        return this.returnItems[i];
      }
    }//end of for
  };//end of getReturnItemByKey

  this.addReturnItem = function (pReturnItem) {
    //This will add an element to the array
    var length = this.returnItems.length;
    this.returnItems[length] = pReturnItem;
  };//end of addReturnItem
};

/**
 * The ReturnItem object is to hold the user entered actual refund amount for a refund item.
 */
atg.commerce.csr.order.returns.ReturnItem = function (pParams) {
  this.id=pParams.id;
  this.refundAmount=pParams.amount;
};

/**
 * This variable holds the container instance.
 */
atg.commerce.csr.order.returns.refundContainer = null;

/**
 * This variable holds the container instance.
 */
atg.commerce.csr.order.returns.initializeRefundContainer = function(pParams){
  refundContainer = new atg.commerce.csr.order.returns.RefundContainer();
  refundContainer.initialize(pParams);
};

/**
 * This method adds the return item to the container.
 *
 */
atg.commerce.csr.order.returns.addReturnItem = function (pParams){
  //create javascript object and add to the container
  var methodsInstance = new atg.commerce.csr.order.returns.ReturnItem (pParams);
  refundContainer.addReturnItem(methodsInstance);
  atg.commerce.csr.order.billing.formatCurrencyWidgetAmount({id:pParams.id, amount:pParams.amount}, refundContainer.currencyCode);
};// end of addReturnItem
