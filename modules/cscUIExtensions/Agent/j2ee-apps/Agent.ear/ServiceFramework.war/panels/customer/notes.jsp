<%--
 This page defines the Customer Notes Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/notes.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">  
  <dspel:importbean bean="/atg/userprofiling/ServiceCustomerProfile"/>

<script type="text/javascript">
  if (!dijit.byId("addCustomerNotePopup")) {
    new dojox.Dialog({ id: "addCustomerNotePopup",
                       cacheContent:"false",
                       executeScripts:"true",
                       scriptHasHooks: "true",
                       style: "display:none;"
                    });
  }
</script>

  <svc-ui:frameworkPopupUrl var="customerNoteAdd"
    value="/include/customerNote/customerNoteEditor.jsp"
    context="/agent"
    windowId="${windowId}"/>
  <fmt:message key="customer.notes.addNote.label" var="addNoteLabel"/>
  <c:set var="addCustomerNotePopupURL" value="showPopupWithResults({
            popupPaneId: 'addCustomerNotePopup',
            title: '${addNoteLabel}',
            url: '${customerNoteAdd}',
            onClose: function( args ) {
              if ( args.result == 'save' ) {
                atgSubmitAction({
                  panels : ['customerInformationPanel'],
                  panelStack : 'customerPanels',
                  form : document.getElementById('transformForm')
                });
              }
            }});
            return false;">
  </c:set>            
  
  <fmt:message key="customer.notes.noNotes.label" var="noNotesMessage"/>
  <dspel:include src="/include/viewNote.jsp" otherContext="${UIConfig.contextRoot}">
    <dspel:param name="notes" bean="ServiceCustomerProfile.comments"/>
    <dspel:param name="popupURL" value="${addCustomerNotePopupURL}"/>
    <dspel:param name="emptyMessage" value="${noNotesMessage}"/>
    <dspel:param name="mode" param="mode"/>
  </dspel:include>
  
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/notes.jsp#1 $$Change: 946917 $--%>
