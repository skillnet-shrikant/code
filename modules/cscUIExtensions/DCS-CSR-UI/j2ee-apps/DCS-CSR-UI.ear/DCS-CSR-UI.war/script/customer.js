dojo.provide( "atg.commerce.csr.customer" );

atg.commerce.csr.customer =
{
// Adds new credit
  addNewCredit : function () {
    var addNewCreditForm = dojo.byId('addNewCreditForm');
    if (addNewCreditForm) {
      atgSubmitAction(
      {
        form: addNewCreditForm,
        panelStack: ["globalPanels","customerPanels"]
      });
      atg.commerce.csr.common.hidePopupWithReturn('addNewCreditPopup', {result:'ok'});
    }
  },

  isExistingChecked: function () { return dojo.byId('editCreditCardForm')['/atg/commerce/custsvc/repository/CreditCardFormHandler.createNewAddress'][0].checked; },
  isNewChecked: function () { return dojo.byId('editCreditCardForm')['/atg/commerce/custsvc/repository/CreditCardFormHandler.createNewAddress'][1].checked; },

  existingCreditCardAddressChanged : function()
  {
    var id = dojo.byId('editCreditCardForm_existingAddressList').value;

    if ( id !== "" ) {
      dojo.byId('editCreditCardForm').editCreditCardForm_firstName.value = 
        atg.commerce.csr.customer.addrList[id].first;
      dojo.byId('editCreditCardForm').editCreditCardForm_middleName.value = 
        atg.commerce.csr.customer.addrList[id].middle;
      dojo.byId('editCreditCardForm').editCreditCardForm_lastName.value = 
        atg.commerce.csr.customer.addrList[id].last;
      dojo.byId('editCreditCardForm')['/atg/commerce/custsvc/repository/CreditCardFormHandler.value.billingAddress.REPOSITORYID'].value = 
        dojo.byId('editCreditCardForm_existingAddressList').value;
    }
  },
  
  syncToCustomerCatalog : function(pParams) {
    var theForm = document.getElementById("syncCurrentCustomerCatalog");
    atgSubmitAction({
      form : theForm
    });	  
  },

  syncToCustomerPriceLists : function(pParams) {
    var theForm = document.getElementById("syncCurrentCustomerPriceLists");
    atgSubmitAction({
     form : theForm
    });	  
  }
};

atg.commerce.csr.customer.addrList = [];
