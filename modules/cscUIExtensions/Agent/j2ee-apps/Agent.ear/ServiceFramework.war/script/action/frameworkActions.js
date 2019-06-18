function frameworkCellAction(formId, cellId) {
	var theForm = document.getElementById(formId);
    atgSubmitAction({
    form: theForm,
	 	cell: cellId,
	 	sync: true
  });
}
function frameworkPanelAction(formId, panelId, panelStackId, nextStepsId, nextStepsPanelId) {
	var theForm = document.getElementById(formId);
	if (nextStepsId) {	  
    atgSubmitAction({
      form: theForm,
      nextSteps: nextStepsId,
      panels: [panelId,nextStepsPanelId],
      panelStack: panelStackId
    });
	} else {
    atgSubmitAction({
      form: theForm,
      panels: [panelId],
      panelStack: panelStackId
    });
	}
}
function frameworkMultiPanelAction(formId, panelIdArray) {
	var theForm = document.getElementById(formId);
  atgSubmitAction({
    form: theForm,
    panels: panelIdArray
  });
}
function frameworkPanelStackAction(formId, panelStackId) {
	var theForm = document.getElementById(formId);
  atgSubmitAction({
    form: theForm,
    panelStack: panelStackId
  });
}
function startNewCall(){
  console.debug("publishing startNewCallEvent");
  dojo.publish("startNewCallEvent");
  var theForm = document.getElementById("startCallForm");
  atgSubmitAction({form: theForm});
}
function endCurrentCall(){
  var theForm = document.getElementById("endCallForm");
  atgSubmitAction({
    form: theForm});
}
function endAndStartNewCall(){
  console.debug("publishing endAndStartNewCallEvent");
  dojo.publish("endAndStartNewCallEvent");
  var theForm = document.getElementById("endAndStartCallForm");
  atgSubmitAction({
    form: theForm  });
}
