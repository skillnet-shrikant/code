<%--
  User picker contents
  
  This file is based on userConstraintBase.jsp and provides a view of users that is specific to the User picker on the 
  Contribute tab. Most notably, the view does not include the user filter and provides a slightly different UI from
  the original. It is invoked from userPicker.jsp

  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/userPickerContents.jsp#1 $ $Change: 946917 $
  @author: sshulman
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
<dspel:getvalueof var="editorField" param="editorField"/>
<dspel:getvalueof var="userListIds" param="userListIds"/>
<dspel:getvalueof var="userListNames" param="userListNames"/>

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<%-- Temporarily display current user values in picker --%>
<%-- TODO: Remove the following two lines --%>
<%--
User List IDs: <c:out value="${userListIds}" /><br />
User List Names: <c:out value="${userListNames}" />
--%>
<input type="hidden" id='baseList<c:out value="${editorField}"/>' 
       name='baseList<c:out value="${editorField}"/>'
       value='<c:out value="${userListIds}"/>' />

<input type="hidden" id='baseListExp<c:out value="${editorField}"/>'
       name='baseListExp<c:out value="${editorField}"/>'
       value='' />

<input type="hidden" id='baseSelect<c:out value="${editorField}"/>'
       name='baseSelect<c:out value="${editorField}"/>'
       value='allOf' />

<span id="spanBaseListSpan<c:out value='${editorField}' />" style="display:none">
  <c:out value="${userListNames}" escapeXml='false'/>
</span>

<script type="text/javascript">
//<![CDATA[
  function chooseUserConstraintMode<c:out value="${editorField}"/>() {
    var attrName = '<c:out value="${editorField}"/>';
    var radio = document.getElementsByName('userConstraintViewUsersMode' + attrName);
    var radioValue = getRadioValue(radio);

    if(radioValue == 'byOrg') {
      setUserEditorMode( attrName, false, false, true);
      <svc-ui:executeOperation treeTableId="userTree${editorField}" operationName="refresh"/>
    } else
    if (radioValue == 'usersList') {
      setUserEditorMode( attrName, false, true, false);
      var filterKeywordSpan = document.getElementById('userConstraintFilterKeywordSpan' + attrName);
      innerText(filterKeywordSpan, getUserListFilterKeyword('<c:out value="${editorField}"/>'));
      <svc-ui:executeOperation treeTableId="userListTree${editorField}" operationName="refresh"/>
    }
  }

  function showModeChooserFor<c:out value="${editorField}"/>() {
    setUserEditorMode( '<c:out value="${editorField}"/>', true, false, false);
  }

  window.chooseUserConstraintMode<c:out value="${editorField}"/> = chooseUserConstraintMode<c:out value="${editorField}"/>;
  window.showModeChooserFor<c:out value="${editorField}"/> = showModeChooserFor<c:out value="${editorField}"/>;
//]]>

//<![CDATA[
  registerControl( '<c:out value="${editorField}"/>', "List" );
  registerControl( '<c:out value="${editorField}"/>', "Select" );
  <c:if test="${empty editorValue}">
    if (isAdHoc) {
      updateAdHocBase( '<c:out value="${editorField}"/>' );
      updateAdHocBaseSpanList( '<c:out value="${editorField}"/>' );
    }
  </c:if>
  if (hasValueList('<c:out value="${editorField}"/>') )
    showSpanList( '<c:out value="${editorField}"/>' );
//]]>    
</script>

</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/userPickerContents.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/userPickerContents.jsp#1 $$Change: 946917 $--%>
