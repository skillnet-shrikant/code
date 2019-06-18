dojo.provide("atg.csc.toggleLink");

dojo.declare(
  "atg.csc.toggleLink",
  [dijit._Widget, dijit._Templated, dijit._Container],
  {
    widgetsInTemplate: true,
    ppData: '',
        templatePath: atg.commerce.csr.getContextRoot() + "/script/widget/templates/toggleLink.jsp",
    //templatePath: "../dijit/templates/toggleLink.jsp",        
    parentForm: {},
        
    toggleState:0, // 0 | 1
    value: 0,    
    label: "",
    type: "",
    eventTopic: "",

        postCreate: function(){
          this.linkPoint.innerHTML = this.label;
          this.inherited("postCreate", arguments);  
        },
        show: function(){
            if(this.toggleState == 0){
                this.toggleState = 1;
               // this.linkContainer.style = "background-color:pink;"
                this.linkContainer.className = "atg_commerce_csr_selectedItem";

            }else{
                this.toggleState = 0;
                this.linkContainer.className = "atg_commerce_csr_nonSelectedItem";                
            }
            
        },
        selectLink: function(e){

           this.show();
           dojo.publish("atg/csc/scheduleorder/" + this.eventTopic ,[this]);
           //parent.toggleHandler();
           //console.debug("css:" + this.linkPoint.className  );    
           console.debug("ToggleLink:" + this.label + ":" +  this.value + ":" + this.toggleState);
        },
    // Utility Functions
    /* from the Dojo 1.0 editor, no idea why it's not in core */
    
    isTag: function(/*DomNode*/node, /*Array*/tags){
      if(node && node.tagName){
        var _nlc = node.tagName.toLowerCase();
        for(var i=0; i<tags.length; i++){
          var _tlc = String(tags[i]).toLowerCase();
          if(_nlc == _tlc){
            return _tlc;
          }
        }
      }
      return "";
    },
    
    getParentOfType: function(/*DomNode*/node, /*Array*/tags){
      while(node){
        if(this.isTag(node, tags).length){
          return node;
        }
        node = node.parentNode;
      }
      return null;
    },
    
    
    sanitySaver: ''
});
