<%--
 A page to return autosuggests in a json format
 
 @param contentCollection - content collection and other endeca search parameters

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/autoSuggestJson.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
  <dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
  <dsp:importbean var="displayEndecaResourcedValueFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplayEndecaResourcedValue"/>
  <dsp:getvalueof var="searchResultPageURL" bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet.searchResultPageURL"/>
  <dsp:getvalueof var="contentCollection" param="contentCollection"/>

  <dsp:droplet name="/atg/commerce/custsvc/catalog/endeca/InvokeAssembler">
    <dsp:param name="contentCollection" value="${contentCollection}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="contentItemMap" param="contentItemMap"/>
    </dsp:oparam>
  </dsp:droplet>
  <json:object prettyPrint="${UIConfig.prettyPrintResponses}" escapeXml="false">
    <json:object name="dimensionSearchResults">
      <json:array name="dimensionSearchGroups" var="content" items="${contentItemMap['AutoSuggestPanel']}">
        <c:forEach var="autoSuggest" items="${content['autoSuggest']}">
          <c:forEach var="dimensionSearchGroup" items="${autoSuggest['dimensionSearchGroups']}">
            <json:object>
              <json:property name="displayName">
                <dsp:include src="${displayEndecaResourcedValueFragment.URL}" otherContext="${displayEndecaResourcedValueFragment.servletContext}">
                  <dsp:param name="key" value="dimensionSearchGroup.displayName"/>
                  <dsp:param name="contentItem" value="${dimensionSearchGroup}"/>
                </dsp:include>
              </json:property>
              <json:array name="dimensionSearchValues" items="${dimensionSearchGroup['dimensionSearchValues']}" var="dimensionSearchValue">
                <json:object>
                  <dsp:droplet name="ContentRequestURLDroplet">
                    <dsp:param name="url" value="${UIConfig.contextRoot}${searchResultPageURL}"/>
                    <dsp:param name="navigationAction" value="${dimensionSearchValue}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/> 
                    </dsp:oparam>
                  </dsp:droplet>
                  <json:property name="label">${dimensionSearchValue.label}</json:property>
                  <json:property name="contentPath">${dimensionSearchValue.contentPath}</json:property>
                  <json:property name="count">${dimensionSearchValue.count}</json:property>
                  <json:property name="multiSelect">${dimensionSearchValue.multiSelect}</json:property>
                  <json:property name="navigationState">${dimensionSearchValue.navigationState}</json:property>
                  <json:property name="contentURL">${contentURL}</json:property>
                  <json:array name="ancestors" items="${dimensionSearchValue['ancestors']}" var="ancestor">
                    <json:object>
                      <dsp:droplet name="ContentRequestURLDroplet">
                        <dsp:param name="url" value="${UIConfig.contextRoot}${searchResultPageURL}"/>
                        <dsp:param name="navigationAction" value="${ancestor}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/> 
                        </dsp:oparam>
                      </dsp:droplet>
                      <json:property name="label">${ancestor.label}</json:property>
                      <json:property name="contentURL">${contentURL}</json:property>
                      <json:property name="siteRootPath">${ancestor.siteRootPath}</json:property>
                      <json:property name="navigationState">${ancestor.navigationState}</json:property>
                      <json:property name="contentPath">${ancestor.contentPath}</json:property>
                    </json:object>
                  </json:array>
                </json:object>
              </json:array>
            </json:object>
          </c:forEach>
        </c:forEach>
      </json:array>
    </json:object>
  </json:object>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/autoSuggestJson.jsp#1 $$Change: 946917 $--%>
