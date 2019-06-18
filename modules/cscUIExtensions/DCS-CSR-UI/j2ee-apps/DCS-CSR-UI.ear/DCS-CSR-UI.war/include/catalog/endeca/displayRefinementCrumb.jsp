<%--
 A page fragment that displays an endeca refinement crumb

 @param searchCrumb - The Endeca refinement crumb
 @param breadCrumb - The Endeca breadcrumb content item
 @param contentItemMap - The map of content items
 @param contentItemResult - The Endeca search result content item
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayRefinementCrumb.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:getvalueof param="refinementCrumb" var="refinementCrumb"/>
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>

  <c:set var="ancestors" value="${refinementCrumb['ancestors']}"/>
  <li class="refinement">
    <span>
    <!-- div -->
      <c:forEach items="${ancestors}" var="ancestor">
        <dsp:droplet name="ContentRequestURLDroplet">
        <dsp:param name="navigationAction" value="${ancestor}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
          <!-- <span style="white-space: nowrap;"> -->
            <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><c:out value="${ancestor.label}"/></a> >
          <!-- </span> -->
        </dsp:oparam>
        </dsp:droplet>
      </c:forEach>
      <!-- <span style="white-space: nowrap;"> -->
        <c:out value="${refinementCrumb.label}"/>
      <!-- </span> -->
    </span>
    <!-- /div -->
    <dsp:droplet name="ContentRequestURLDroplet">
      <dsp:param name="navigationAction" value="${refinementCrumb.removeAction}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
        <div class="icon-delete" onclick="atgSubmitAction({url: '${contentURL}'});"></div>
      </dsp:oparam>
    </dsp:droplet>
  </li>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayRefinementCrumb.jsp#1 $$Change: 946917 $--%>
