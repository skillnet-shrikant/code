dojo.provide( "atg.commerce.csr.cart" );

atg.commerce.csr.cart =
{
  deleteCartItem : function( commerceItemId )
  {
    dojo.byId( "removeCommerceId" ).value = commerceItemId;
    atgSubmitAction({
        form : document.getElementById("deleteItemForm"),
        panelStack : "cmcShoppingCartPS" });
  },

  deleteManualAdjustment : function( adjustmentId, adjustmentReason )
  {
    dojo.byId( "dmaId" ).value = adjustmentId;
    dojo.byId( "dmaReason" ).value = adjustmentReason;
    atgSubmitAction({
        form : document.getElementById("dma"),
        panelStack : "cmcShoppingCartPS" });
  },

  submitNextAction : function ()
  {
    atg.commerce.csr.common.setPropertyOnItems( ['modifyOrderSubmitter'],
      "disabled", true );
    atg.commerce.csr.common.setPropertyOnItems( ['moveToPurchaseInfoSubmitter'],
      "disabled", false );

    atgSubmitAction({
        queryParams : { 'init' : 'true' },
        form : document.getElementById('itemsForm')});
  },



  updatePrice : function ()
  {
    atg.commerce.csr.common.setPropertyOnItems( ['moveToPurchaseInfoSubmitter'],
      "disabled", true );
    atg.commerce.csr.common.setPropertyOnItems( ['modifyOrderSubmitter'],
      "disabled", false );

    atgSubmitAction({
      panelStack : ["cmcShoppingCartPS", "globalPanels"],
      form : document.getElementById('itemsForm'),
      sync: true});
  },


  saveAction : function ()
  {
    atg.commerce.csr.common.setPropertyOnItems( ['moveToPurchaseInfoSubmitter'],
      "disabled", true );
    atg.commerce.csr.common.setPropertyOnItems( ['modifyOrderSubmitter'],
      "disabled", false );

    atgSubmitAction({
      panelStack : ["cmcShoppingCartPS", "globalPanels"],
      form : document.getElementById('itemsForm')});
  },

  // 
  skuChangePopupOkSelected : function( mode, url, childNodeId, radioGroupName, popupId )
  {
    var form = atg.commerce.csr.common.getEnclosingForm( childNodeId );
    
    if ( "apply" == mode ) {
      atg.commerce.csr.common.submitPopup( url, form,
        atg.commerce.csr.common.getEnclosingPopup(childNodeId));
      
    }
    else if ( "return" == mode ) {
      form = dojo.byId('productSkuForm-editSKU-return');
      var checked = atg.commerce.csr.common.getCheckedItem(
        form[radioGroupName]);
      if ( checked != "" )
        checked = checked.value;
      atg.commerce.csr.common.hidePopupWithReturn( popupId,
        { result: 'ok', sku: checked });
    }
  },
  
  adjustmentAmountChanged : function( inputValuePattern ) {
    var amount = dojo.byId("amountTxt").value;
    dojo.byId("adjustmentSubmitButton").disabled = ! amount.match(inputValuePattern);
  },
  
  deleteCartItemByRelationalshipId : function( relationalshipId )
  {
    dojo.byId( "removeRelationshipId" ).value = relationalshipId;
    atgSubmitAction({
        form : document.getElementById("deleteItemByRelationshipIdForm"),
        panelStack : "cmcShoppingCartPS" });
  },
  
  setOrderByRelationshipIdForm : function()
  {
	var finalPrice;
	var finalPriceId;
    var form = document.getElementById("setOrderByRelationshipIdForm");
    var inputs = dojo.query('.ciRelationship');
    var submit = false;
    var setOrderByRelationshipIdFormInnerDiv = document.getElementById("setOrderByRelationshipIdFormInnerDiv");
    if (!setOrderByRelationshipIdFormInnerDiv) {
      var setOrderByRelationshipIdFormInnerDiv = document.createElement("div");
      setOrderByRelationshipIdFormInnerDiv.id = "setOrderByRelationshipIdFormInnerDiv";
      form.appendChild(setOrderByRelationshipIdFormInnerDiv);
    } else {
      setOrderByRelationshipIdFormInnerDiv.innerHTML = '';
    }
    for (i = 0; i < inputs.length; i++) {
      var oldQuantity = dojo.byId(inputs[i].id + "_quantity");
      if (inputs[i].value != 0) {
        if (oldQuantity && oldQuantity.value != inputs[i].value) {
          var node = atg.commerce.csr.catalog.createInputFieldWithoutId(inputs[i].id, "hidden");
          node.value = inputs[i].value;
          submit = true;
          setOrderByRelationshipIdFormInnerDiv.appendChild(node);
        }
      } else {
        atg.commerce.csr.cart.deleteCartItemByRelationalshipId(inputs[i].id);
      }
      finalPriceId = "IPO:" + inputs[i].name ;
      finalPrice = document.getElementById(finalPriceId);
      if(finalPrice)
      	setOrderByRelationshipIdFormInnerDiv.appendChild(finalPrice);
    }
    atgSubmitAction({
        form : form,
        panelStack : "cmcShoppingCartPS" });
  }
};
