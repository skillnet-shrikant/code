
function showPopup(url, width, height) {
  var dummyDiv = document.createElement("div");
  dummyDiv.style.position = "absolute";
  document.body.appendChild(dummyDiv);
  dummyDiv.style.width = width;
  dummyDiv.style.height = height;
  dummyDiv.style.top = "100px";
  dummyDiv.style.left = "100px";
  
  var widget = new dojox.Dialog( {href: "/agent/commerceassist/popup.jsp?url=" + encodeURI(url), windowsStates: ["normal"], displayCloseAction: true, title: "Popup", extractContent: true, refreshOnShow: true, duration: 100}, dummyDiv);
  //widget.show();
}
