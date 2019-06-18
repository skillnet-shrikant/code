<%--
 A page fragment that displays the sort options for the results

 @param contentItemMap - The map of content items
 @param contentItemResult - The Endeca search result content item
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultsSortOptions.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
<dsp:importbean var="displayEndecaResourcedValueFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplayEndecaResourcedValue"/>

<dsp:getvalueof param="resultsListContentItem" var="resultsListContentItem"/>
<select id="sortOptionSelect" onchange="atgSubmitAction({url: this.options[this.selectedIndex].value});return false;">
  <c:forEach items="${resultsListContentItem['sortOptions']}" var="sortOption">
    <dsp:getvalueof var="selected" value="${sortOption['selected']}"/>
    <dsp:droplet name="ContentRequestURLDroplet">
      <dsp:param name="navigationAction" value="${sortOption}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
        <c:set var="selectedAttribute">${selected? "selected='selected'": ""}</c:set>
        <option value="${contentURL}" ${selectedAttribute}>
          <dsp:include src="${displayEndecaResourcedValueFragment.URL}" otherContext="${displayEndecaResourcedValueFragment.servletContext}">
            <dsp:param name="key" value="sortOption.label"/>
            <dsp:param name="contentItem" value="${sortOption}"/>
          </dsp:include>
        </option>
      </dsp:oparam>
    </dsp:droplet>
  </c:forEach>
</select>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultsSortOptions.jsp#1 $$Change: 946917 $--%>
