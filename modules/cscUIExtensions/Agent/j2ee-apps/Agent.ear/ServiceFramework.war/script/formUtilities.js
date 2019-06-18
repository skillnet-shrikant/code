dojo.provide("atg.service.form");

atg.service.form.isFormEmpty = function (theFormId, trimInputs) {
  //console.debug("isFormEmpty called");
  var elements = dojo.query("input", theFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type == "text" || type == "textarea" || type == "password") {
      //console.debug("found a text input value of " + item.value);
      var itemValue = trimInputs ? dojo.string.trim(item.value) : item.value;
      if (itemValue != '') {return false;}
    }
    else if (type == "checkbox" || type == "radio") {
      //console.debug("found a check/radio input value of " + item.checked);
      if (item.checked == true) {return false;}
    }
  };
  var elements = dojo.query("select", theFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type.match("select") == "select") {
      //console.debug("found a select input value of " + item.value);
      if (item.value != '') {return false;}
    }
  };
  var elements = dojo.query("textarea", theFormId);
  for (var i = 0, length = elements.length; i < length; i++) {
    var item = elements[i];
    var type=item.type;
    if (type.match("textarea") == "textarea") {
      //console.debug("found a textarea input value of " + item.value);
      if (item.value != '') {return false;}
    }
  };
  //console.debug("found all empty fields, returning true");
  return true;
};

atg.service.form.watchInputs = function (theFormId, theFunction) {
  var theForm = dojo.byId(theFormId);
  var atgWatchEvents = [];
  var elements = dojo.query("input", theFormId);
  elements.forEach(function (item,index,array) {
    var type=item.type;
    if (type == "text" || type == "textarea" || type == "password") {
      atgWatchEvents.push(dojo.connect(item, "onkeyup", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onchange", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
    else if (type == "checkbox") {
      atgWatchEvents.push(dojo.connect(item, "onclick", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
    else if (type == "radio") {
      atgWatchEvents.push(dojo.connect(item, "onclick", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
  });
  elements = dojo.query("select", theFormId);
  elements.forEach(function (item,index,array) {
    var type=item.type;
    if (type.match("select") == "select") {
      atgWatchEvents.push(dojo.connect(item, "onchange", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
  });
  elements = dojo.query("textarea", theFormId);
  elements.forEach(function (item,index,array) {
    var type=item.type;
    if (type.match("textarea") == "textarea") {
      atgWatchEvents.push(dojo.connect(item, "onkeyup", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onchange", theFunction));
      atgWatchEvents.push(dojo.connect(item, "onblur", theFunction));
    }
  });
  theForm.atgWatchEvents = atgWatchEvents;
};

atg.service.form.unWatchInputs = function (theFormId) {
  var theForm = dojo.byId(theFormId);
  if (theForm) {
    var watchedInputs = theForm.atgWatchEvents;
    if (watchedInputs) {
      console.debug("removing the watching of the form elements")
      dojo.forEach(watchedInputs, dojo.disconnect);
      theForm.atgWatchEvents = null;
    }
  }
};

// checks maxlenght attribute for textarea
atg.service.form.checkMaxLength = function (obj){
  var mlength=obj.getAttribute ? parseInt(obj.getAttribute("maxlength")) : "";
  if (obj.getAttribute && obj.value.length > mlength){
    obj.value=obj.value.substring(0, mlength);
    obj.scrollTop = obj.scrollHeight;
    return false;
  }
  return true;
};