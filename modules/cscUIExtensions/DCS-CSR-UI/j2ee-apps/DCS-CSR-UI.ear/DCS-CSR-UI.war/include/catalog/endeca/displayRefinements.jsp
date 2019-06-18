<%--
 A page fragment that displays the endeca refinements

 @param contentItemResult - The Endeca search result
 @param contentItemMap - The map of endeca content items by type
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayRefinements.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="contentItemResult" var="contentItemResult"/>
<dsp:getvalueof param="contentItemMap" var="contentItemMap"/>
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>

<dsp:importbean var="displayEndecaResourcedValueFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplayEndecaResourcedValue"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<c:set var="refinementMenus" value="${contentItemMap[endecaConfig.refinementMenuContentItemType]}"/>

<c:if test="${not empty refinementMenus}">
  <div class="dimensions">
    <ul>
      <c:forEach items="${refinementMenus}" var="refinementMenu">
         <li class="sub-heading">
          <dsp:include src="${displayEndecaResourcedValueFragment.URL}" otherContext="${displayEndecaResourcedValueFragment.servletContext}">
            <dsp:param name="key" value="refinementMenu.title"/>
            <dsp:param name="contentItem" value="${refinementMenu}"/>
          </dsp:include>
        </li>
        <c:forEach items="${refinementMenu.refinements}" var="refinement">
          <li>
            <dsp:droplet name="ContentRequestURLDroplet">
              <dsp:param name="navigationAction" value="${refinement}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
                <span>
                  <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><c:out value="${refinement['label']}"/></a>
                </span>
                <div><label>${refinement['count']}</label></div>
              </dsp:oparam>
            </dsp:droplet>
          </li>
        </c:forEach>
        
        <c:if test="${! empty refinementMenu['moreLink']}">
          <li class="right">
            <dsp:droplet name="ContentRequestURLDroplet">
              <dsp:param name="navigationAction" value="${refinementMenu['moreLink']}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
                <span>
                  <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><fmt:message key="endeca.showMore"/></a>
                </span>
              </dsp:oparam>
            </dsp:droplet>
          </li>
        </c:if>

        <c:if test="${! empty refinementMenu['lessLink']}">
          <li class="right">
            <dsp:droplet name="ContentRequestURLDroplet">
              <dsp:param name="navigationAction" value="${refinementMenu['lessLink']}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
                <span>
                  <a href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><fmt:message key="endeca.showFewer"/></a>
                </span>
              </dsp:oparam>
            </dsp:droplet>
          </li>
        </c:if>
      </c:forEach>
    </ul>
  </div>
</c:if>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayRefinements.jsp#1 $$Change: 946917 $--%>
