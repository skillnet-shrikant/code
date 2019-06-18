// -------------------------------------------------------------------
// EA help content definitions for Service
// -------------------------------------------------------------------
atg.ea.registerServiceHelpContent = function () {
  atg.ea.registerHelpContent({ "id":"ea_service_customer_search", "excerpt":getResource("ea.service.helpContent.ea_service_customer_search"), "content":"" });
  atg.ea.registerHelpContent({ "id":"atg_arm_contentBrowserTitle", "content":getResource("ea.service.helpContent.atg_arm_contentBrowserTitle") });
  atg.ea.registerHelpContent({ "id":"atg_arm_linkedDocumentsTitle", "content":getResource("ea.service.helpContent.atg_arm_linkedDocumentsTitle") });
  atg.ea.registerHelpContent({ "id":"atg_arm_addAttachment", "content":getResource("ea.service.helpContent.atg_arm_addAttachment") });
};

//console.debug("EA | atg.ea.registerServiceHelpContent called");

dojo.addOnLoad(atg.ea.registerServiceHelpContent);

