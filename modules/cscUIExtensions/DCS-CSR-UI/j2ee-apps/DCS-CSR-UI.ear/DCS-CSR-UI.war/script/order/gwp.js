dojo.provide( "atg.commerce.csr.gwp" );

atg.commerce.csr.gwp.submitGWPGiftSelectionForm = function(params) {
  dojo.debug("GWP | Submitting Gift Selection form");
  var theForm = atg.commerce.csr.gwp.getGWPGiftSelectionForm();
  
  atgSubmitAction({
    form: theForm
  });
  
};

atg.commerce.csr.gwp.getGWPGiftSelectionForm = function() {
  dojo.debug("GWP | Getting Gift Selection form");
  var theForm = dojo.byId("gwpMakeGiftSelection");
  
  return theForm;
};

atg.commerce.csr.gwp.setGWPGiftSelectionFormInputValues = function(formInput) {
  dojo.debug("GWP | Setting Form Input Values");
  var theForm = atg.commerce.csr.gwp.getGWPGiftSelectionForm();
  atgBindFormValues(theForm,formInput)  ;
  return theForm;
};
