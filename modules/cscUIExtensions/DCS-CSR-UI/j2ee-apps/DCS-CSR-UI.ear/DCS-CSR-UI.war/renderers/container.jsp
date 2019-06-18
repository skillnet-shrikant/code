<%--

 @param panelId - ID of enclosing panel
 @param categoryId - The current product category (optional)
 @param productId - The ID of the product

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/container.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <c:set var="containerRenderInfo" value="${renderInfo}"/>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <c:forEach var="id" items="${containerRenderInfo.ids}">
        <csr:renderer name="${containerRenderInfo.baseNames[id]}">
          <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}"/>
        </csr:renderer>
      </c:forEach>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <% 
    Exception ee = (Exception) pageContext.getAttribute("exception"); 
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/container.jsp#1 $$Change: 946917 $--%>
