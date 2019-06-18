
function atgSetupHelp(locationSet)
{
  dojo.forEach(locationSet, function(helpItem) {
   //dojo.removeClass(dojo.byId(helpItem.id), "hideHelp");
	 //dojo.addClass(dojo.byId(helpItem.id), "showHelp");
   if (dojo.hasClass(dojo.byId(helpItem.id), "popupHelp"))
   {
     atgSetupPopup(helpItem);
   }
   else if (dojo.hasClass(dojo.byId(helpItem.id), "inlineHelp")) {
     atgSetupInline(helpItem);
   }
 }, true);
}
function atgSetupPopup(helpItem)
{
  var theHref = helpItem.url;
  var newImage = document.createElement("img");
  newImage.src = "images/icon_help_authoring.gif";
  newImage.id = helpItem.id + "HelpImg";
  dojo.place(newImage, dojo.byId(helpItem.id), "after");
  var widget = new dijit.Tooltip({connectId: helpItem.id + "HelpImg", href: theHref});
}
function atgSetupInline(helpItem)
{
	var sibDiv = dojo.byId(helpItem.id);
  var newImage = document.createElement("img");
  newImage.src = "images/icon_help_agent.gif";
  newImage.id = helpItem.id + "HelpImg";
  dojo.place(newImage, sibDiv, "after");
	var widget = new dojox.layout.ContentPane({href: helpItem.url, isHidden: true, parseOnLoad: true});
	hide(widget.domNode, false);
	dojo.place(widget.domNode, dojo.byId(newImage.id), "after");
	console.debug("setting onclick on " + sibDiv.firstChild);
	dojo.connect(sibDiv.firstChild, "onclick", function() {
	  console.debug("toggling");
	  toggleShowing(widget.domNode);
	});
}