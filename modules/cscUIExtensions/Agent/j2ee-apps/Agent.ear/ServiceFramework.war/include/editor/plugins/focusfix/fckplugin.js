function FCK_OnFocus( editorInstance )
{
  var oToolbarset = editorInstance.ToolbarSet ;
  var oInstance = editorInstance || FCK ;

  // Unregister the toolbar window from the current instance.
  // We need the try/catch in case the current instance has been removed
  try {
    oToolbarset.CurrentInstance.FocusManager.RemoveWindow( oToolbarset._IFrame.contentWindow ) ;
  } catch (e) {}

  // Set the new current instance.
  oToolbarset.CurrentInstance = oInstance ;

  // Register the toolbar window in the current instance.
  oInstance.FocusManager.AddWindow( oToolbarset._IFrame.contentWindow, true ) ;

  oToolbarset.Enable() ;
}
