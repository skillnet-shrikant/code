function showPreferences(){
var theForm = document.getElementById("transformForm"); 
  atgSubmitAction({
    form: theForm,
    nextSteps: "userPreferencesNextSteps",
    panels: ["nextStepsPanel"],
    panelStack: ["preferencesPanels","helpfulPanels"],
    sync: true
  }).addCallback(currPrefHolder.saveCurrentPreferences);
  //currPrefHolder - object implemented in userPreferencesAction.js
}

function insertHyperlink() {
  dojo.debug("insertHyperlink called");
  var theForm = document.getElementById("insertHyperlinkForm");

  atgSubmitAction({
    formHandler: "/atg/svc/agent/ui/formhandlers/InsertFormHandler",
    form: theForm
  });
}

