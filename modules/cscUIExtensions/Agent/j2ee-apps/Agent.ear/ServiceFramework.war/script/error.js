function showStackTrace() {
  var stackTraceObject = dojo.byId("errorDetails");
  var imgArrowClosed = dojo.byId("imgErrorDetailArrowClosed");
  var imgArrowDown = dojo.byId("imgErrorDetailArrowDown");

  if (stackTraceObject != null)
  {
    if (isShowing(stackTraceObject))
    {
      show(imgArrowClosed);
      hide(imgArrowDown);
      hide(stackTraceObject);
    }
    else
    {
      hide(imgArrowClosed);
      show(imgArrowDown);
      show(stackTraceObject);
    }
  }
}
function hideErrorPanel() {
  var errorPanelContent = dojo.byId("errorPanelContent");
  if (errorPanelContent)
    errorPanelContent.innerHTML = "";

  var errorPanel = dojo.byId("errorPanel");
  if (errorPanel)
    hide(errorPanel);
}
function hideErrorDetails(id) {
  var errorPanel = dojo.byId(id);
  hide(errorPanel);
}