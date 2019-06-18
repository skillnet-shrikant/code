dojo.provide("framework.FrameworkLink");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare(
  // widget name and class
  "framework.FrameworkLink",

  // superclass
  [dijit._Widget,dijit._Templated],

  // properties and methods
  {
    // parameters
    panelStack: "",

    // settings
    templateString: "<span><a dojoAttachPoint='containerNode' dojoAttachEvent='onclick: onClick'></a></span>",

    // callbacks
    onClick: function(evt) {
      if (this.panelStack) atgSubmitAction({panelStack:this.panelStack,form:dojo.byId('transformForm')});
    }
  }
);


