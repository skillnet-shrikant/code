// -------------------------------------------------------------------
// EA help array structure for Service
// -------------------------------------------------------------------
atg.ea.registerServiceHelpArray = function () {
  atg.ea.registerHelpArray({ id: "ea_service_customer_search", type: "inline", helpId: "ea_service_customer_search" });
  atg.ea.registerHelpArray({ id: "atg_arm_contentBrowserTitle", type: "popup", helpId: "atg_arm_contentBrowserTitle" });
  atg.ea.registerHelpArray({ id: "atg_arm_linkedDocumentsTitle", type: "popup", helpId: "atg_arm_linkedDocumentsTitle" });
  atg.ea.registerHelpArray({ id: "atg_arm_addAttachment", type: "popup", helpId: "atg_arm_addAttachment" });
};

//console.debug("EA | atg.ea.registerServiceHelpArray called");

dojo.addOnLoad(atg.ea.registerServiceHelpArray);
