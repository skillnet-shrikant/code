<%--
 A page fragment that displays the search term input 

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySearchTermInput.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
<dsp:importbean var="autoSuggestJsonFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/AutoSuggestJson"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<li class="search-row" style="float:none;">
  <dsp:droplet name="ContentRequestURLDroplet">
    <dsp:param name="contentPath" value="${endecaConfig.defaultContentURI}?Dy=1&Nty=1&Ntt=SEARCHTERMINPUT"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
    </dsp:oparam>
  </dsp:droplet>

  <fmt:message var="searchTermPlaceHoler" key="endeca.searchTermTitle"/>

  <input type="text" class="search-box" maxlength="18" placeholder="${searchTermPlaceHoler}" id="searchTermInput" onkeydown="searchTermInputOnkeydown();" onkeyup="searchTermInputOnkeyup(); if(event.keyCode==13){searchTermInputOnkeydown(); atg.commerce.csr.catalog.endeca.search.submitSearchTermRequest('${contentURL}');}"/>
  <div class="icon-search" onclick="searchTermInputOnkeydown(); atg.commerce.csr.catalog.endeca.search.submitSearchTermRequest('${contentURL}');"></div>

</li>

<script type="text/javascript">
  function searchTermInputOnkeydown() {
    if(dojo.isIE){
      var input = dojo.byId("searchTermInput");
      if (input.value == input.getAttribute("placeholder")){
        input.value = "";
      }
    }
  }

  function searchTermInputOnkeyup() {
    if(dojo.isIE){
      var input = dojo.byId("searchTermInput");
      if (input.value == "" || input.value == input.getAttribute("placeholder")) {
        input.value = input.getAttribute("placeholder");
        if (input.textRange) {
          var range = input.textRange;
          range.collapse(true);  
          range.select();  
        }
      }
    }
  }

  if(dojo.isIE) {
    var element = dojo.byId("searchTermInput");
    if(element) { 
      element.focus();
      if (element.value == "" || element.value == element.getAttribute("placeholder")) {
        element.value = element.getAttribute("placeholder");
      }
    }
  } else {
    $(document).ready(function() {
      $("#searchTermInput").focus();
    });
  }
</script>

<c:if test="${not empty endecaConfig.autoSuggestURI}">
  <script type="text/javascript">
    $(document).ready(function() {
      atg.commerce.csr.catalog.endeca.search.setupAutoComplete(
        "/${autoSuggestJsonFragment.servletContext}${autoSuggestJsonFragment.URL}", 
        "${endecaConfig.autoSuggestURI}?Dy=1&Nty=1&Ntt=SEARCHTERMINPUT*", 
        ${endecaConfig.autoSuggestMinLength});
    });
  </script>
</c:if>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySearchTermInput.jsp#1 $$Change: 946917 $--%>
