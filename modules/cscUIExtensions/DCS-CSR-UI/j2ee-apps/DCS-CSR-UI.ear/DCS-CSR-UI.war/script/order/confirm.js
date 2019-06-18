dojo.provide( "atg.commerce.csr.order.confirm" );


/**
 * Send an order confirmation message.
 */
atg.commerce.csr.order.confirm.sendConfirmationMessage = function (){

   atgSubmitAction({
    form:document.getElementById("atg_commerce_csr_sendConfirmationMessageForm")
    });    
};

/**
 * Saves a new customer profile.
 */
atg.commerce.csr.order.confirm.saveCustomerProfile = function (createUserOkFormat, createUserFailureFormat){
  var theForm = dojo.byId("atg_commerce_csr_customerCreateForm");
  var firstName = theForm["atg_commerce_csr_confirm_fName"].value;
  var lastName = theForm["atg_commerce_csr_confirm_lastName"].value;

  theForm["atg.successMessage"].value = dojo.string.substitute(createUserOkFormat, [firstName, lastName]);
  theForm["atg.failureMessage"].value = dojo.string.substitute(createUserFailureFormat, [firstName, lastName]);
  
  atgSubmitAction({
    form:theForm,
    panelStack: ["globalPanels"]
  });       
}

/**
 * Navigate to the Product Catalog panel.
 */
atg.commerce.csr.order.confirm.renderProductCatalogPanel = function (){
   atgNavigate({panelStack:'cmcCatalogPS'});
};
