dojo.provide("atg.progress");
// array of panels to update for progress
// apps should add their own panels to this array
atg.progress.panels = [];
atg.progress.update = function (newState, newProcess, params){
  if (params === undefined || params === null) {
    params = {};
  }
  if (params.queryParams === undefined || params.queryParams === null) {
    params.queryParams = {};
  }

  if (params.form === undefined || params.form === null){
    params.form = dojo.byId("transformForm");
  }
  
  if (newState) { params.queryParams.state = newState; }
  if (newProcess) { params.queryParams.process = newProcess;}
  if (dojo.isArray(atg.progress.panels)) {
	  params.panels = atg.progress.panels;
  }
  params.queryParams = {panel: newState};
  params.showLoadingCurtain = false;
  return atgSubmitAction(params);
};