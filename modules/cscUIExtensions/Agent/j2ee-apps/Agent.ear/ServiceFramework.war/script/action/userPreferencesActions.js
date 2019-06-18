function restoreGeneralPanel(){
var theForm = document.getElementById("restoreGeneralPanelForm");
atgSubmitAction({
  form: theForm
});
}

function saveUserPasswordAction(){
  var theForm = document.getElementById("saveUserPasswordForm");
  theForm.newPassword.value=getOption('passwordNew');
  theForm.oldPassword.value=getOption('password');
  theForm.confirmPassword.value=getOption('passwordConfirm');
  atgSubmitAction({
    form: theForm,
    url: window.contextPath + "/include/passwordChanged.jsp"
  });
  theForm.newPassword.value="";
}

var currPrefHolder = new currentPreferencesHolder();

function currentPreferencesHolder(){
  //hot solution classes is't listed
  //it is a bit redundant to store 2 array of properties names. Some properties are a bit different (one has prefix 'hid' another hasn't).
  //Prefix could be removed with regex but we have property with name differs from form property name.
  var propertyNames = 
    new Array('hidTrylogOut','hidAgentUserDefaultHomeTab');
  var correspondFormProps = 
    new Array('TrylogOut','AgentUserDefaultHomeTab');
  var _holder = new Object;
  this.saveCurrentPreferences = 
    function(){
      var len = propertyNames.length;
      for (var i = 0; i < len; ++i){
        _holder[propertyNames[i]] = getOption(propertyNames[i]);
      }
    };
  this.arePreferencesChanged = 
    function(lastProperties){
      var isSame = true;
      var len = propertyNames.length;
      for (var i = 0; (i < len) && isSame; ++i){
        isSame = 
          ((_holder[propertyNames[i]] == null && lastProperties[correspondFormProps[i]].value == "") ||
           (_holder[propertyNames[i]] == lastProperties[correspondFormProps[i]].value));
      }
      return !isSame;
    };
};

function saveUserPreferences(theFormId){
  var theForm = document.getElementById(theFormId);
  theForm.TrylogOut.value=getOption('hidTrylogOut');
  theForm.AgentUserDefaultHomeTab.value=getOption('hidAgentUserDefaultHomeTab');

  if (currPrefHolder.arePreferencesChanged(theForm)){
    atgShowLoadingIcon();
    atgSubmitAction({
      form: theForm,
      nextSteps: "userPreferencesNextSteps",
      panels: ["nextStepsPanel"],
      panelStack: ["preferencesPanels","helpfulPanels"],
      showLoadingCurtain: false,
      sync: true
    }).addCallback(currPrefHolder.saveCurrentPreferences);
    atgHideLoadingIcon();
  }
}

function radioAllClassesOnClick() {
  //Enable button 'OK' when All classes selected
  var theOkButton = document.getElementById("okSolutionClasses");
  var theRadioAllClasses = document.getElementById('allSolutionClasses');
  if (theOkButton && theRadioAllClasses && theRadioAllClasses.checked) theOkButton.disabled = false;
}

