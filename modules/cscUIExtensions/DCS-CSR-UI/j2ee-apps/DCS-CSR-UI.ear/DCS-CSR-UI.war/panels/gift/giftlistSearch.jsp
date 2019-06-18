<%--

This page defines the gift list search panel. It includes the gift list search results page.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistSearch.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistSearch" var="giftlistSearchFormHandler" />
  <dsp:importbean var="defaultPageFragment" bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistSearchDefault" />
  <dsp:importbean var="extendedPageFragment" bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistSearchExtended" />
  <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/gift/search/GiftlistGrid" var="gridConfig"/> 
  <c:set var="formId" value="${gridConfig.searchFormId}"/>
  <dsp:form id="${formId}" method="post">
    <div id="atg_commerce_csr_searchGiftListForm">
      <c:if test="${not empty defaultPageFragment.URL}">
        <dsp:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}">
          <dsp:param name="formId" value="${formId}" />
        </dsp:include>
      </c:if>
  
      <c:if test="${not empty extendedPageFragment.URL}">
        <dsp:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}">
          <dsp:param name="formId" value="${formId}" />
        </dsp:include>
      </c:if>
      <ul class="atg_dataForm atg_commerce_csr_searchGiftListForm">
        <li class="atg_commerce_csr_giftListSearchSubmit">
          <dsp:input type="hidden" id="${formId}_sortProperty" name="sortProperty" bean="GiftlistSearch.sortField" /> 
          <dsp:input type="hidden" id="${formId}_sortDirection" name="sortDirection" bean="GiftlistSearch.sortDirection" /> 
          <dsp:input type="hidden" id="${formId}_currentPage" name="currentPage" bean="GiftlistSearch.currentPage" /> 
          <dsp:input type="hidden" bean="GiftlistSearch.search" value="" priority="-10" />
          <dsp:layeredBundle basename="atg.commerce.csr.Messages">
            <input type="button" name="${formId}_giftlistSearchButton"  id="${formId}_giftlistSearchButton"  dojoType="atg.widget.validation.SubmitButton" value="<fmt:message key="giftlists.search.searchButton.label"></fmt:message>" onclick=resetSortProperties();atg.commerce.csr.gift.refreshSearchResults(true);return false; />
          </dsp:layeredBundle>  
        </li>
      </ul>
    </div>
  </dsp:form>
  <dsp:include src="/panels/gift/giftlistSearchResults.jsp" otherContext="${defaultPageFragment.servletContext}" />
  <script type="text/javascript">
    function resetSortProperties() {
    	dojo.byId("${formId}_sortProperty").value = '${giftlistSearchFormHandler.sortField}';
    	dojo.byId("${formId}_sortDirection").value = '${giftlistSearchFormHandler.sortDirection}';
    }
  </script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistSearch.jsp#1 $$Change: 946917 $--%>