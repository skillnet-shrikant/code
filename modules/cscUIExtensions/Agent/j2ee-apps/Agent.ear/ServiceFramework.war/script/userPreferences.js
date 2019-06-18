function okDiv(divObject_id) {
  hideDiv(divObject_id);
}

function hideDiv(divObject_id) {
  var theDiv = document.getElementById(divObject_id);
  var imgClicked = document.getElementById("img" + divObject_id);
  if (theDiv) {
    theDiv.style.display = "none";
    imgClicked.src = "image/icons/icon_closeNavItem.gif";
  } else {
    alert('Div element not found: ' + divObject_id);
  }
}

function toggleDivs(divObject_id) {
  var theDiv = document.getElementById(divObject_id);
  var imgClicked = document.getElementById("img" + divObject_id);
  if (theDiv) {
    var theStyle = theDiv.style.display;
    if (theStyle == "none") {
      theDiv.style.display = "block";
      if (imgClicked) {
        imgClicked.src = "image/icons/icon_openNavItem.gif";
      } else {
        alert("image not found: " + "img" + divObject_id);
      }
    } else {
      theDiv.style.display = "none";
      imgClicked.src = "image/icons/icon_closeNavItem.gif";
    }
  } else {
    alert('Div element not found: ' + divObject_id);
  }
}

function expandDiv(divObject_id) {
  var theDiv = document.getElementById(divObject_id);
  if (theDiv) {
    var theStyle = theDiv.style.display;
    theDiv.style.display = "block";
    var imgClicked = document.getElementById("img" + divObject_id);
    imgClicked.src = "image/icons/icon_openNavItem.gif";
  } else {
    alert('Div element not found: ' + divObject_id);
  }
}

function setOption(id, value) {
  document.getElementById(id).value = value;
}

function getOption(id) {
  var theElement = document.getElementById(id);
  if (theElement) {
    return theElement.value;
  }
  return "";
}

function setOptionIndex(optionId) {
  var elSelSrc = document.getElementById('sel' + optionId);
  var i;
  for (i = elSelSrc.options.length - 1; i >= 0; i--) {
    if (elSelSrc.options[i].selected) {
      document.getElementById('hid' + optionId).value = elSelSrc.options[i].value;
    }
  }
}

//Check password and save if all ok (password's length not zero, password contains at least non space symbol,
//password and confirm field values are same.
//Returns true, if verification passed and password chenges sent to form handler.
function saveUserPassword()
{
  var passNewElem = document.getElementById('passwordNew');
  var passConfirmElem = document.getElementById('passwordConfirm');
  var password = document.getElementById('password');

  if (!passNewElem || !passConfirmElem) {
    return false;
  }

  var passNew = passNewElem.value;
  var passConfirm = passConfirmElem.value;

  if ((passNew == "") && (passConfirm == ""))
    return true;

  //Password which contains only space(s) symbols - don't save.
  if (passNew.match(/^(\s)*$/))
  {
    var allSpacesErrorMessage = getResource("personalization.password.allspaceserror");
    dijit.byId('messageBar').addMessage( {type:"error", summary:allSpacesErrorMessage});
    return false;
  }

  //Password which contains no symbols or not confirmed with confirm value - don't save.
  if (passNew == '' || passConfirm == '' || passNew != passConfirm ) {

    var confirmErrorMessage = getResource("personalization.password.confirmerror");
    dijit.byId('messageBar').addMessage( {type:"error", summary:confirmErrorMessage });

    return false;
  }

  saveUserPasswordAction();
  return true;
}

function saveWindowPreferences() {
  var mTryLogout = document.getElementById("hidTrylogOut");

  if (mTryLogout) {
    window.confirmLogout = (mTryLogout.value == "true");
  }
  else {
    window.confirmLogout = false;
  }

}

function prefGeneralPanelOk() {
  restoreGeneralPanel();
  showPreferences();
}

function isOptionDisabled(value, message) {
  if (value == 'true') {
    alert(message);
    return false;
  } else {
    showPreferences();
    return true;
  }
}

function warnClient(message, disabled, disabledMessage) {
  var contributeTab = document.getElementById("contributeTab");
  if (contributeTab && contributeTab.className && contributeTab.className == "current")
  {
    var cancelCallback = function() {};
    var okCallback = function() {
      isOptionDisabled(disabled, disabledMessage);
    };
    dojoConfirmDialog('',message,okCallback,cancelCallback);
  } else {
    isOptionDisabled(disabled, disabledMessage);
  }
}


