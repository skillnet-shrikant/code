<%--
This page determines whether the order is modifiable
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/orderIsModifiable.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%
/* 
 * As of Bright, the use of this page has been deprecated and should not be used by CSC.
 * It has been left here for backward compatibilty reasons. 
*/
%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:getvalueof bean="/atg/commerce/custsvc/order/ShoppingCart.current" var="currentOrder"/>
  <c:set value="cmcExistingOrderPS" var="orderPanelStack"/>
  <dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
    <dsp:param name="order" value="${currentOrder}"/>
    <dsp:oparam name="true">
      <c:set value="cmcShoppingCartPS" var="orderPanelStack"/>
    </dsp:oparam>
    <dsp:oparam name="false">
      <c:set value="cmcExistingOrderPS" var="orderPanelStack"/>
      <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
      <c:set target="${envTools}"
             property="viewOrder"
             value="${currentOrder}"/>
    </dsp:oparam>
  </dsp:droplet>
  <c:choose>
  <c:when test="${framework.hasTabId}">
    <c:set var="tabInstance" value="${framework.tabInstances[framework.tabId]}"/>
    <c:forEach items="${tabInstance.currentPanelStacks}"
               var="entry">
      <c:if test="${entry.value eq 'contentColumn'}">
        <c:set target="${tabInstance.currentPanelStacks}"
               property="${entry.key}"
               value=""/>
      </c:if>
    </c:forEach>
    <c:set target="${tabInstance.currentPanelStacks}"
           property="${orderPanelStack}"
           value="contentColumn"/>
    <dsp:include src="/framework.jsp" otherContext="${param.frameworkContext}">
      <dsp:param name="p" value="customerMainPanel"/>
    </dsp:include>
  </c:when>
  <c:otherwise>
    <%-- Include cmcHelpfulPanels in case we're on preferences, which has different side panels --%>
    <dsp:include src="/framework.jsp?ps=cmcHelpfulPanels&ps=${orderPanelStack}" 
                 otherContext="${param.frameworkContext}">
      <dsp:param name="p" value="customerMainPanel"/>
    </dsp:include>
  </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/orderIsModifiable.jsp#1 $$Change: 946917 $--%>
