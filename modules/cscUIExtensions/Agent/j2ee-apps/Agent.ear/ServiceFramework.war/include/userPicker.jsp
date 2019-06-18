<%--
  New user picker for Contribute tab
  
  This file is based on the QueryBuilder functionality and has been modified for the specific needs of the 
  Contribute tab. It is invoked from properties.jsp when solutionFormHandler.fieldType == 'UserType'

  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/userPicker.jsp#1 $ $Change: 946917 $
  @author: sshulman
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="name" param="name"/>
  <dspel:getvalueof var="type" param="type"/>
  <dspel:getvalueof var="consType" param="consType"/>
  <dspel:getvalueof var="title" param="title"/>
  <dspel:include src="/include/userPickerContents.jsp" otherContext="${UIConfig.contextRoot}">
    <dspel:param name="editorField" value="${name}" />
    <dspel:param name="userListIds" value="${userListIds}" />
    <dspel:param name="userListNames" value="${userListNames}" />
  </dspel:include>

  <dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />
  <c:set var="postfix" value="GenConstraintField_${name}"/>
  <c:set var="onClickScript" value="toggleDivClassCons('div${postfix}', '${name}', '${type}', ${!empty userListIds}, true);" />
  <c:set var="delImgOnClick" value="resetEditor('${name}');dojo.stopEvent(event);" />

  <span id='imgdiv<c:out value="${postfix}" />' /><span id='span<c:out value="${postfix}" />' />
  <div id='div<c:out value="${postfix}" />' style="display: none"></div>
  <span class="propertyrow">
  
  <script type="text/javascript">
  function toggleUserPickerDisplay(divObject_id, editorField, editorType, hasValue, bGeneralConsType ) {
    var isSimpleMode = true;
    var theDiv = document.getElementById(divObject_id);
    var imgClicked = document.getElementById( "img" + divObject_id );
    if (theDiv) {
      var theStyle = theDiv.style.display;
      if(theStyle == "none") {
        theDiv.style.display = "block";
        imgClicked.src = "image/iconcatalog/14x14/bullets/arrowdown.gif";
        if(prevEditorDiv) {
          prevEditorDiv.style.display = 'none';
          var prevDivImg = document.getElementById( "img" + prevEditorDiv.id );
          if(prevDivImg) {
            prevDivImg.src = "image/iconcatalog/14x14/bullets/arrowright.gif";
          }
        }
        prevEditorDiv = theDiv;
  
        if (theDiv.innerHTML.length < 1)
          renderEditor(editorField, editorType, hasValue, bGeneralConsType, isSimpleMode);
      } else {
        theDiv.style.display = "none";
        imgClicked.src = "image/iconcatalog/14x14/bullets/arrowright.gif";
  
        prevEditorDiv = null;
      }
    } else {
      alert('Div element not found: ' + divObject_id);
    }
    if ((typeof(event)!="undefined") && (event != null)) dojo.stopEvent(event);
  }

  toggleUserPickerDisplay('<c:out value="div${postfix}" />', '<c:out value="${name}" />', '<c:out value="${type}" />', <c:out value="${!empty userListIds}" />, true); 
  </script>
  
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/userPicker.jsp#1 $$Change: 946917 $--%>
