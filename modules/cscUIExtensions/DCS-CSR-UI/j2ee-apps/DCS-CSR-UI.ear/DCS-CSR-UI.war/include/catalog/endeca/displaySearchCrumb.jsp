<%--
 A page fragment that displays an endeca searchcrumb

 @param searchCrumb - The Endeca search crumb
 @param breadCrumb - The Endeca breadcrumb content item
 @param contentItemMap - The map of content items
 @param contentItemResult - The Endeca search result content item
  @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySearchCrumb.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">


<dsp:getvalueof param="searchCrumb" var="searchCrumb"/>
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
    <c:choose>
    <c:when test="${! empty searchCrumb.correctedTerms}">
      <span> 
        <div class="corrected-term">
          <p><c:out value="\"${searchCrumb.terms}\""/></p>
        </div>
        <c:out value=">>"/>
        <div class="selected-term"><c:out value="\"${searchCrumb.correctedTerms}\""/></div>
      </span>
    </c:when>
    <c:otherwise>
      <span><c:out value="\"${searchCrumb.terms}\""/></span>
    </c:otherwise>
    </c:choose>
    <dsp:droplet name="ContentRequestURLDroplet">
    <dsp:param name="navigationAction" value="${searchCrumb.removeAction}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
       <div class="icon-delete" onclick="atgSubmitAction({url: '${contentURL}'});"></div>
    </dsp:oparam>
    </dsp:droplet>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySearchCrumb.jsp#1 $$Change: 946917 $--%>
