<%--
 This page defines the notes panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/note/notes.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"
    var="cart" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/ViewOrderHolder" var="viewOrder"/>
  
  <dsp:getvalueof var="successErrorURL" param="successErrorURL"/>
  <dsp:getvalueof var="psToRefresh" param="psToRefresh"/>
  <dsp:getvalueof var="mode" param="mode"/>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>
  
  <c:if test="${empty isExistingOrderView}">
    <c:set var="isExistingOrderView" value="false" />
  </c:if>

<script type="text/javascript">
  if (!dijit.byId("addOrderNotePopup")) {    
    new dojox.Dialog({ id: "addOrderNotePopup",
                       cacheContent: "false",
                       executeScripts: "true",
                       scriptHasHooks: "true",
                       style: "display:none;"
                    });
  }
</script>

  <%-- Add note source --%>
  <c:url var="orderNoteAdd" context="${CSRConfigurator.contextRoot}"
    value="/include/order/note/orderNoteEditor.jsp">      
    <c:param name="${stateHolder.windowIdParameterName}"
      value="${windowId}" />
    <c:param name="successURL" value="${successErrorURL}" />
    <c:param name="isExistingOrderView" value="${isExistingOrderView}" />
  </c:url>
  <fmt:message key="order.notes.addNote.label" var="addNoteLabel"/>
  <c:set var="addOrderNotePopupURL" value="atg.commerce.csr.common.showPopupWithReturn({
                  popupPaneId: 'addOrderNotePopup',                  
                  url: '${orderNoteAdd}',
                  title: '${addNoteLabel}',
                  onClose: function( args ) {
                    if ( args.result == 'ok' ) {                                     
                      atgSubmitAction({                                        
                        panelStack : '${psToRefresh}',
                        form : document.getElementById('transformForm')
                      });
                    }
                  }
                });
                return false;">
  </c:set>                  

  <fmt:message key="order.notes.noNotes.label" var="noNotesMessage"/>
  <dsp:tomap var="orderRepositoryItem" value="${order.repositoryItem }"/>              
  <dsp:include src="/include/viewNote.jsp" otherContext="/agent">
    <dsp:param name="notes" value="${orderRepositoryItem.comments}"/>
    <dsp:param name="popupURL" value="${addOrderNotePopupURL}"/>
    <dsp:param name="emptyMessage" value="${noNotesMessage}"/>
    <dsp:param name="mode" value="${mode}"/>
  </dsp:include>
  
  </dsp:layeredBundle>  
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/note/notes.jsp#1 $$Change: 946917 $--%>
