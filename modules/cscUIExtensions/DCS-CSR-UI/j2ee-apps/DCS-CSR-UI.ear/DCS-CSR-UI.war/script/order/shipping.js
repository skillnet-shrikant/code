    dojo.provide( "atg.commerce.csr.order.shipping" );

    atg.commerce.csr.order.shipping.applySingleShippingGroup  = function (pParams){
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atgSubmitAction({form:dojo.byId("singleShippingAddressForm")});
    };

    atg.commerce.csr.order.shipping.saveApplySingleShippingGroup  = function (pParams){
      var form  = dojo.byId("singleShippingAddressForm");
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atgSubmitAction({
      panelStack: ["globalPanels"],
      form:dojo.byId("singleShippingAddressForm")
      });
    };
    atg.commerce.csr.order.shipping.applyMultipleShippingGroup = function (pParams){
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atg.commerce.csr.common.enableDisable('csrHandleApplyShippingGroups',
                                            'csrPreserveUserInputOnServerSide');
      atgSubmitAction({form:dojo.byId("csrMultipleShippingAddressForm")});
    };

    atg.commerce.csr.order.shipping.applySelectShippingMethods = function (pParams){
      dojo.debug("entering applySelectShippingMethods()");
      atg.commerce.csr.common.prepareFormToPersistOrder (pParams);
      atgSubmitAction({form:dojo.byId("csrSelectShippingMethods")});
    };

    atg.commerce.csr.order.shipping.addShippingAddress = function (){
      atgSubmitAction({form:dojo.byId("csrAddShippingAddress"), sync: true});
      atgNavigate({panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});
    };

    atg.commerce.csr.order.shipping.addElectronicAddress = function (){
      atgSubmitAction({form:dojo.byId("csrAddElectronicAddress"), sync: true});
      atgNavigate({panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});
    };

    atg.commerce.csr.order.shipping.editShippingAddress = function(pURL){
      atg.commerce.csr.common.submitPopup(pURL, dojo.byId("csrEditShippingAddressForm"), dijit.byId("csrEditAddressFloatingPane"));
    };

    atg.commerce.csr.order.shipping.renderShippingPage = function (pMode){
      atgNavigate({panelStack:'cmcShippingAddressPS', queryParams : { mode: pMode}});
    };

    atg.commerce.csr.order.shipping.splitQtyPrompt = function (pURL, pTitle) {
      var splitQtyWindow = "csrMultipleShippingFloatingPane";
      if (splitQtyWindow) {
          atg.commerce.csr.common.showPopupWithReturn({
                                                        popupPaneId: splitQtyWindow,
                                                        title: pTitle || "",
                                                        url: pURL
                                                      });
        }
    };

    atg.commerce.csr.order.shipping.cancelSplitQtyPrompt = function (pMode) {
      var splitQtyWindow = dijit.byId("csrMultipleShippingFloatingPane");

      if (splitQtyWindow) {
        atg.commerce.csr.common.hidePopup (splitQtyWindow);
        var deferred = atgSubmitAction({
          form:"transformForm",
          panelStack:'cmcShippingAddressPS',
          queryParams : { 'select' : 'multiple' }
        });
        deferred.addCallback(function() {
          //if the agent is working with the split shipping groups, that means 
          //they are working with the multiple shipping. Just in case if the multiple
          //shipping area is not open, this open up the multi shipping area.
          var multishippingdivid = dijit.byId("atg_commerce_csr_shipToMultipleAddresses");
          if (multishippingdivid && !multishippingdivid.open) {
            multishippingdivid.toggle();
          }
          }); 
      }
    };

    atg.commerce.csr.order.shipping.splitShippingGroupQty = function(pURL){
      atg.commerce.csr.common.submitPopup(pURL, dojo.byId("csrSplitShippingGroupQty"), dijit.byId("csrMultipleShippingFloatingPane"));
    };

    atg.commerce.csr.order.shipping.newShippingGroupSelectedRule = function () {
      var radio = dojo.byId('singleShippingAddressForm').singleShippingShipToAddressNickname;
      if(!radio){return false;}

      var checkedValue;
      //find the radio value checked

      //this means there is only one radio button in the
      if (radio.length === undefined ) {
        checkedValue = radio.value;
      } else {
        for(var i = 0; i < radio.length; i++){
          if(radio[i].checked){
            checkedValue = radio[i].value;
            break;
          }
        }// end of for loop
      }

      if (checkedValue == 'atg_nsg_nickname') {
         return true;
      } else {
         return false;
      }
    };

    atg.commerce.csr.order.shipping.notifySingleShippingValidators = function () {
    };

    atg.commerce.csr.order.shipping.notifyEditShippingAddressValidators = function () {
    };

    atg.commerce.csr.order.shipping.notifyAddShippingAddressValidators = function () {
    };

