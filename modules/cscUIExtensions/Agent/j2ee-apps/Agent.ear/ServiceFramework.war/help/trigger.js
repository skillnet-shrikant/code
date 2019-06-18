// make the function only fire when the main tab and reloaded
_container_.onLoadDeferred.addCallback(function (){
  if(dijit.byId('assistanceBase')){
   console.debug("Building EA");
   dijit.byId('assistanceBase').buildEA();
  }
  else{
   console.debug("Creating new instance of EA");
   new atg.widget.assistance.Base();
  }
});

if(dijit.byId('assistanceBase')){
 console.debug("Building EA");
 dijit.byId('assistanceBase').buildEA();
}
else{
 console.debug("Creating new instance of EA");
 new atg.widget.assistance.Base();
}
