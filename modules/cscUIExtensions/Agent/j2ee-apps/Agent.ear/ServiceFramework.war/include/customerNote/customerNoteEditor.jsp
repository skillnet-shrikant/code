<%--
 This page defines the popup for adding new customer note
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customerNote/customerNoteEditor.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <div class="atg_svc_popupPanel atg_svc_agent_addCustomerNotePanel">
      <div class="atg_svc_popupPanelCloseButton" >
      </div>

      <dspel:form id="addNewCustomerNoteForm" formid="addNewCustomerNoteForm" name="addNewCustomerNoteForm" action="#">
        <dspel:input bean="CustomerProfileFormHandler.update" id="updateCustomerNote" name="updateCustomerNote" type="hidden" value="1" priority="-10"/>
        <fmt:message key="customer.notes.addNote.required" var="requiredMessage"/>

        <dl class="addNewNote">
          <dd class="field">
            <dspel:textarea id="comments" bean="CustomerProfileFormHandler.editvalue.comments" name="note" 
            	required="true" cols="50" rows="5" style="width:97%;height:70px;" maxlength="2500" />
          </dd>
        </dl>

        <div class="atg_svc_panelFooter">
          <input type="button" name="addButton" value="<fmt:message key='customer.notes.addNote.save'/>" onclick="createNewCustomerNote();"/>
          <input type="button" value="<fmt:message key='customer.notes.addNote.cancel'/>"
            id="cancelChoice"
            onClick="hidePopupWithResults('addCustomerNotePopup', {result:'cancel'});
            return false;"/>
        </div>
      </dspel:form>
    </div>
    <script type="text/javascript">
      dojo.provide("atg.service.customer.addNewCustomerNote");
      atg.service.customer.addNewCustomerNote.validate = function () {
        var addNewCustomerNoteForm = dojo.byId('addNewCustomerNoteForm');
        atg.service.form.checkMaxLength(addNewCustomerNoteForm.note);
        if (atg.service.form.isFormEmpty('addNewCustomerNoteForm')
            || addNewCustomerNoteForm.note.value == '') {
          addNewCustomerNoteForm.addButton.disabled = true;
        }
        else {
          addNewCustomerNoteForm.addButton.disabled = false;
        }
      }
      _container_.onLoadDeferred.addCallback(function () {
        if (atg.service.form.isFormEmpty('addNewCustomerNoteForm')) {
          dojo.byId('addNewCustomerNoteForm').addButton.disabled = true;
        }
        atg.service.form.watchInputs('addNewCustomerNoteForm', atg.service.customer.addNewCustomerNote.validate);
      });
      _container_.onUnloadDeferred.addCallback(function () {
        atg.service.form.unWatchInputs('addNewCustomerNoteForm');
      });
      </script>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customerNote/customerNoteEditor.jsp#1 $$Change: 946917 $--%>
