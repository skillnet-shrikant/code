dojo.getObject("atg.service", true);

atg.service.openWindow = function() {
  window.isLogout=false;
  initTab();
}
//Code added for a fix to the bug 141105
atg.service.logout = function() {
  window.isLogout=true;
  atgSubmitAction({
    form: dojo.byId('logout'),
    queryParams: {sessioninvalid: true, ppr: true}
  });
  dijit.byId("logoutBox").hide();
  return false;
}
//Code added for a fix to the bug 141105
atg.service.showLogout = function() {
  var ifrm = dojo.byId("ifrmSolution");
  if(ifrm) {
    ifrm.style.display = 'none';
  }
  if (window.confirmLogout) {
    dijit.byId("logoutBox").show();
    dojo.byId("logoutYes").focus();
  }
  else {
    atg.service.logout();
  }
}
atg.service.showUtilities = function()
{
  var util = getElt("utilDD");
  if (util.style.display == "none")
  { 
    divSetVisible("utilDD");
  }
  else
  {
    divSetHide("utilDD");
  }
}

atg.service.popup = function (url, name, width, height) {
  atg_popupSettings="toolbar=no,location=no,directories=no,"+
  "status=no,menubar=no,scrollbars=yes,"+
  "resizable=no,width="+width+",height="+height;
  atg_myNewWindow=window.open(url,name,atg_popupSettings);
}

atg.service.reloadResult = function() {
  dijit.byId("logoutBox").hide();
  var ifrm = dojo.byId("ifrmSolution");
  if(ifrm) {
    ifrm.style.display = "block";
  }
  if (dojo.isMozilla && ifrm) {
    ifrm.src = ifrm.src;
  }
}

var tid, time, action;
atg.service.setInactivityTimer = function(time, action) {
  window.time = time;
  window.action = action;
  if (tid) {clearTimeout(tid);}
  if (document.layers) {document.captureEvents(Event.MOUSEMOVE | Event.KEYUP);}
  document.onmousemove = document.onkeyup = function (evt) {
    atg.service.resetInactivityTimer();
    return true;
  };
  action += '; atg.service.clearEvents();';
  tid = setTimeout(action, time);
}
atg.service.resetInactivityTimer = function()
{
  if (tid) {clearTimeout(tid);}
  tid = setTimeout(action, time);
}
atg.service.clearEvents = function() {
  if (document.layers) {document.releaseEvents(Event.MOUSEMOVE | Event.KEYUP);}
  document.onmousemove = document.onkeyup = null;
}
function showDebugConsole() {

}
var atgLoadingDialog;
function atgInitLoadingDialog(e){
  atgLoadingDialog = dijit.byId("atgLoadingDialogWidget");
}

