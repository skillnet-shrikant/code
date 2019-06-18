<%--
 A page fragment that display the paging controls for the results

 @param contentItemMap - The map of content items
 @param resultsListContentItem - The Endeca result list content item
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultsPaging.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true" >

<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>

<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>
<dsp:getvalueof param="resultsListContentItem" var="resultsListContentItem"/>


<c:set var="recsPerPage" value="${resultsListContentItem['recsPerPage']}"/>

<dsp:droplet name="/atg/commerce/custsvc/catalog/endeca/PagingDroplet">
<dsp:param name="recordsPerPage" value="${recsPerPage}"/>
<dsp:param name="currentRecordIndex" value="${resultsListContentItem['firstRecNum'] - 1}"/>
<dsp:param name="totalRecords" value="${resultsListContentItem['totalNumRecs']}"/>
<dsp:oparam name="prevPageGroup">
  
  <dsp:getvalueof var="prevPageGroupRecordIndex" param="prevPageGroupRecordIndex"/>
  <dsp:droplet name="ContentRequestURLDroplet">
  <dsp:param name="navigationAction" value="${resultsListContentItem['pagingActionTemplate']}"/>
  <dsp:param name="recordOffset" value="${prevPageGroupRecordIndex}"/>
  <dsp:param name="recordsPerPage" value="${recsPerPage}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
    <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><c:out value="<"/></a>
  </dsp:oparam>
  </dsp:droplet>
  
  

</dsp:oparam>
<dsp:oparam name="page">
  
  <dsp:getvalueof var="pageNumber" param="pageNumber"/>
  <dsp:getvalueof var="pageRecordIndex" param="pageRecordIndex"/>
  <dsp:droplet name="ContentRequestURLDroplet">
  <dsp:param name="navigationAction" value="${resultsListContentItem['pagingActionTemplate']}"/>
  <dsp:param name="recordOffset" value="${pageRecordIndex}"/>
  <dsp:param name="recordsPerPage" value="${recsPerPage}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
    <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><c:out value="${pageNumber}"/></a>
  </dsp:oparam>
  </dsp:droplet>

</dsp:oparam>
<dsp:oparam name="currentPage">
  
  <dsp:getvalueof var="pageNumber" param="pageNumber"/>
  <c:out value="${pageNumber}"/>

</dsp:oparam>
<dsp:oparam name="nextPageGroup">

  <dsp:getvalueof var="nextPageGroupRecordIndex" param="nextPageGroupRecordIndex"/>
   <dsp:droplet name="ContentRequestURLDroplet">
   <dsp:param name="navigationAction" value="${resultsListContentItem['pagingActionTemplate']}"/>
   <dsp:param name="recordOffset" value="${nextPageGroupRecordIndex}"/>
   <dsp:param name="recordsPerPage" value="${recsPerPage}"/>
   <dsp:oparam name="output">
     <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
     <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><c:out value=">"/></a>
   </dsp:oparam>
   </dsp:droplet>

</dsp:oparam>
</dsp:droplet>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayResultsPaging.jsp#1 $$Change: 946917 $--%>
