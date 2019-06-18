<%--
This tag is used to display the cancel button in the returns panels.

cancelIconOnclickURL - Required - This is used to cancel the return request in case of Return process.
cancelActionErrorURL - Required -- This URL is to stay on the same page and display the error message.
order -- Required -- This parameter is used in case of the exchange process. Exchange order is involved in the cancel
                     process.
--%>

<%@ tag language="java" %>
<%@ attribute name="cancelActionErrorURL" required="true" %>
<%@ attribute name="order" required="true" type="atg.commerce.order.Order"%>
<%@ attribute name="cancelIconOnclickURL" required="true" %>

<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="svc-ui"    uri="http://www.atg.com/taglibs/svc/svc-uiTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<dsp:page xml="true">
<dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>

<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnExchange">
      <dsp:oparam name="true">
        <dsp:getvalueof var="processName" param="returnProcessName" />
        <c:choose>
          <c:when test="${processName == 'Return'}">
            <input name="Cancel" type="button"
              value="<fmt:message key="common.cancel" />"
              onclick="${cancelIconOnclickURL}" />
          </c:when>
          <c:otherwise>
            <%-- Create success and error urls for Cancel Order --%>
            <c:choose>
            <c:when test="${framework.tabId == 'commerceTab'}">
              <svc-ui:frameworkUrl var="cancelSuccessURL" panelStacks="cmcOrderSearchPS" />
            </c:when>
            <c:otherwise>
              <svc-ui:frameworkUrl var="cancelSuccessURL" />
            </c:otherwise>
            </c:choose>
            <dsp:include page="/include/order/cancelOrderButton.jsp">
              <dsp:param name="orderId" value="${order.id}" />
              <dsp:param name="successUrl" value="${cancelSuccessURL}" />
              <dsp:param name="errorUrl" value="${cancelActionErrorURL}" />
            </dsp:include>
          </c:otherwise>
        </c:choose>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/displayReturnPanelsCancelButton.tag#1 $$Change: 946917 $--%>
