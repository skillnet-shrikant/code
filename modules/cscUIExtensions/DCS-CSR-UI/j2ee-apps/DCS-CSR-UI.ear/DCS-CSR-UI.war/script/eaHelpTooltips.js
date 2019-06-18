// -------------------------------------------------------------------
// EA tooltips for CSC
// -------------------------------------------------------------------
atg.ea.registerCSCTooltips = function () {
  atg.ea.registerTooltip("orderLink", getResource("ea.csc.tooltip.orderLink"));
  atg.ea.registerTooltip("orderSave", getResource("ea.csc.tooltip.orderSave"));
  atg.ea.registerTooltip("orderCancel", getResource("ea.csc.tooltip.orderCancel"));
  atg.ea.registerTooltip("submitOrderButton", getResource("ea.csc.tooltip.submitOrderButton"));
  atg.ea.registerTooltip("submitCreateScheduleButton", getResource("ea.csc.tooltip.submitCreateScheduleButton"));
  atg.ea.registerTooltip("createScheduleButton", getResource("ea.csc.tooltip.createScheduleButton"));
};

dojo.subscribe("UpdateGlobalContext", null, function() { 
//  console.debug("EA | atg.ea.registerCSCTooltips called");
  atg.ea.registerCSCTooltips();
});
