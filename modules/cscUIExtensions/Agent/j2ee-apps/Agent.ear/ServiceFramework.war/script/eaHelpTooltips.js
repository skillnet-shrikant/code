// -------------------------------------------------------------------
// EA tooltips for Service
// -------------------------------------------------------------------
atg.ea.registerServiceTooltips = function () {
  atg.ea.registerTooltip("ticketSave", getResource("ea.service.tooltip.ticketSave"));
  atg.ea.registerTooltip("customerLink", getResource("ea.service.tooltip.customerLink"));
};

dojo.subscribe("UpdateGlobalContext", null, function() { 
  //console.debug("EA | atg.ea.registerServiceTooltips called");
  atg.ea.registerServiceTooltips();
});
