<%--
 Initializes the order search results table using the following input parameters:
 tableConfig - the table configuration component
 searchResponse - the response from the search engine
  
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderSearchTable.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:getvalueof var="tableConfig" param="tableConfig" scope="request" />
    <dsp:getvalueof var="searchResponse" param="searchResponse" />
    <dsp:getvalueof var="items" value="${searchResponse.items}" />
    <c:if test="${!empty tableConfig.imageClosed}">
      <c:set var="imageClosed" value="${tableConfig.imageClosed}" scope="request"/>
    </c:if>
    <c:if test="${!empty tableConfig.imageOpen}">
      <c:set var="imageOpen" value="${tableConfig.imageOpen}" scope="request"/>
    </c:if>
    <c:if test="${!empty tableConfig.imagePath}">
      <c:set var="imagePath" value="${tableConfig.imagePath}" scope="request"/>
    </c:if> 
      
      <table class="atg_dataTable" summary="Summary" cellspacing="0"
        cellpadding="0">
        <thead>
          <c:forEach var="column" items="${tableConfig.columns}">
            <c:if test="${column.isVisible == 'true'}">
              <c:set var="columnWidth" value="${column.width}" />
                <c:if test="${empty columnWidth}">    
                  <c:set var="columnWidth" value="auto" />
                </c:if>  
               <th scope="col" style="width:${columnWidth}"> 
                <dsp:include src="${column.dataRendererPage.URL}"
                    otherContext="${column.dataRendererPage.servletContext}">
                  <dsp:param name="field" value="${column.field}" />
                  <dsp:param name="sortField" value="${column.sortField}" />
                  <dsp:param name="resourceBundle" value="${column.resourceBundle}" />
                  <dsp:param name="resourceKey" value="${column.resourceKey}" />
                  <dsp:param name="isHeading" value="true" />
                </dsp:include>
              </th>
            </c:if>
          </c:forEach>
        </thead>

        <c:forEach var="orderItem" items="${items}">
          <dsp:droplet name="/atg/targeting/RepositoryLookup">
            <dsp:param name="url" value="${orderItem.id}" />
            <dsp:oparam name="output">
                <dsp:tomap var="orderItemMap" param="element" />
                <dsp:getvalueof var="orderItem" param="element" />
                <dsp:droplet name="/atg/commerce/custsvc/order/GetOrderProfile">
                <dsp:param name="orderItem" value="${orderItem}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="profileItem" param="orderProfile" />
                </dsp:oparam>
                </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
          <tr>
            <c:forEach var="column" items="${tableConfig.columns}">
              <c:if test="${column.isVisible == 'true'}">
               <td>
                <c:if test="${column.dataRendererPage != ''}">
                  <dsp:include src="${column.dataRendererPage.URL}"
                    otherContext="${column.dataRendererPage.servletContext}">
                    <dsp:param name="field" value="${column.field}" />
                    <dsp:param name="profileItem" value="${profileItem}" />
                    <dsp:param name="orderItemMap" value="${orderItemMap}" />
                    <dsp:param name="orderItem" value="${orderItem}" />
                    <dsp:param name="imageClosed" value="${imageClosed}" />
                    <dsp:param name="imageOpen" value="${imageOpen}" />
                    <dsp:param name="imagePath" value="${CSRConfigurator.contextRoot}${imagePath}" />
                  </dsp:include>
                </c:if>
                </td>
              </c:if>
            </c:forEach>
          </tr> 
        </c:forEach>
      </table>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderSearchTable.jsp#1 $$Change: 946917 $--%>
