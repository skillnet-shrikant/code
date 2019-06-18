<%--
 This page defines the popup for adding new customer note
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/note/orderNoteEditor.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"
    var="cart" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/ViewOrderHolder" var="viewOrderHolder"/>
  <dsp:getvalueof var="successErrorURL" param="successErrorURL"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>
  
  <%-- Set the correct Form Handler --%> 
  <c:choose>
    <c:when test="${isExistingOrderView == 'false'}">
      <c:set var="formHandlerPath" value="/atg/commerce/custsvc/order/OrderNoteFormHandler"/>      
    </c:when>
    <c:otherwise>
      <c:set var="formHandlerPath" value="/atg/commerce/custsvc/order/OriginalOrderNoteFormHandler"/>      
    </c:otherwise>
  </c:choose>   
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <div class="atg_commerce_csr_popup">
      <div class="atg_commerce_csr_popupPanelCloseButton" >
      </div>      
      <dsp:form id="atg_commerce_csr_order_note_addNewOrderNoteForm">
        <dsp:input bean="${formHandlerPath}.successURL" value="${successErrorURL}" type="hidden"/>
        <dsp:input bean="${formHandlerPath}.errorURL" value="${successErrorURL}" type="hidden"/>        
        <dsp:input  bean="${formHandlerPath}.addComment" type="hidden" value="" priority="-10"/>                        
          
        <dl class="addNewNote">
          <dd class="field">
            <dsp:textarea id="atg_commerce_csr_order_note_comments" name="atg_commerce_csr_order_note_comments" 
                bean="${formHandlerPath}.comment"
            	required="true" cols="50" rows="5" style="width:97%;height:70px;" maxlength="2500" />
            <div style="display: none" id="atg_commerce_csr_order_note_hiddenDivWithInput">
              <dsp:input type="hidden" value="" bean="${formHandlerPath}.comment"/>
            </div>
          </dd>
        </dl>

        <div class="atg_commerce_csr_panelFooter">
          <input  type="button" name="addButton" 
                  value="<fmt:message key='common.save'/>" 
                  onclick="atg.commerce.csr.order.copyNote();atg.commerce.csr.order.finish.createNewOrderNote();"/>
          <input type="button" value="<fmt:message key='common.cancel'/>"
            id="atg_commerce_csr_order_note_cancelChoice"
            onClick="atg.commerce.csr.common.hidePopupWithReturn('addOrderNotePopup', {result:'cancel'});;
            return false;"/>
        </div>
      </dsp:form>
    </div>
    <script type="text/javascript">
      atg.commerce.csr.order.noteValidate = function () {
        var addNewOrderNoteForm = dojo.byId('atg_commerce_csr_order_note_addNewOrderNoteForm');
        var orderNote = addNewOrderNoteForm['atg_commerce_csr_order_note_comments'];
      	atg.service.form.checkMaxLength(orderNote);
        if (atg.service.form.isFormEmpty('atg_commerce_csr_order_note_addNewOrderNoteForm')
            || orderNote.value == '') {
          addNewOrderNoteForm.addButton.disabled = true;
        }
        else {
          addNewOrderNoteForm.addButton.disabled = false;
        }
      }

      atg.commerce.csr.order.copyNote = function () {
        var addNewOrderNoteForm = dojo.byId('atg_commerce_csr_order_note_addNewOrderNoteForm');
        var orderNote = addNewOrderNoteForm['atg_commerce_csr_order_note_comments'];
        var container = dojo.byId('atg_commerce_csr_order_note_hiddenDivWithInput')
        for(i=0;i<container.childNodes.length;i++){
          if(container.childNodes[i].type && container.childNodes[i].type == 'hidden') {
            container.childNodes[i].value = orderNote.value;
            break;
          }
        }
      }

      _container_.onLoadDeferred.addCallback(function () {
        if (atg.service.form.isFormEmpty('atg_commerce_csr_order_note_addNewOrderNoteForm')) {
          dojo.byId('atg_commerce_csr_order_note_addNewOrderNoteForm').addButton.disabled = true;
        }
        atg.service.form.watchInputs('atg_commerce_csr_order_note_addNewOrderNoteForm', atg.commerce.csr.order.noteValidate);
      });
      _container_.onUnloadDeferred.addCallback(function () {
        atg.service.form.unWatchInputs('atg_commerce_csr_order_note_addNewOrderNoteForm');
      });
      </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/note/orderNoteEditor.jsp#1 $$Change: 946917 $--%>
