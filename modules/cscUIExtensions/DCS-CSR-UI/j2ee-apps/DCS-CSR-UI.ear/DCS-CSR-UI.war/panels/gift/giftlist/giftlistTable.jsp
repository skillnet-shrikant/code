<%--
 Initializes the gift list results table using the following input parameters:
 tableConfig - The table configuration component
 isEdit - Is the page to be rendered in edit more or view mode
 giftlistId - The Id of the gift list to be viewed

  
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlist/giftlistTable.jsp#2 $
 @updated $DateTime: 2015/02/26 10:47:28 $
--%>
<%@ include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/userprofiling/ServiceCustomerProfile"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup" />
    <dsp:importbean bean="/atg/commerce/custsvc/collections/filter/droplet/GiftlistSiteFilterDroplet"/>
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet" />
    <dsp:importbean bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler" />
    <dsp:importbean bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistDetailsViewDefault" var="giftlistDetailsViewDefault" />
    <dsp:importbean bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistDetailsViewExtended" var="giftlistDetailsViewExtended" />
    <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
    <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
    <dsp:getvalueof var="tableConfig" param="tableConfig" scope="request" />
    <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request" />
    <dsp:getvalueof var="isEdit" param="isEdit" scope="request"/>
    <%-- retrieve the giftlist and items --%>
    <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
      <dsp:param name="id" param="giftlistId" />
      <dsp:oparam name="output">
        <dsp:setvalue paramvalue="element" param="giftlist" />
        <dsp:getvalueof var="giftlist" vartype="java.lang.Object" param="giftlist" />
        <dsp:setvalue paramvalue="giftlist.id" param="giftlistId" />
        <dsp:setvalue paramvalue="giftlist.giftlistItems" param="giftlistAllItems" />
        <dsp:setvalue paramvalue="giftlist.siteId" param="giftlistSiteId" />
        <dsp:getvalueof var="giftlistItems" vartype="java.lang.Object" param="giftlistAllItems" />
        <dsp:getvalueof var="giftlistSiteId" param="giftlistSiteId" />
        <dsp:getvalueof var="isPublic" vartype="java.lang.String" param="giftlist.published" />
        <c:set var="ValidGiftlistId" value="true"/>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <c:set var="ValidGiftlistId" value="false"/>
      </dsp:oparam>
    </dsp:droplet>
    <c:if test="${isMultiSiteEnabled == true}">
      <dsp:droplet name="GiftlistSiteFilterDroplet">
        <dsp:param name="collection" value="${giftlistItems}"/>
        <dsp:param name="siteScope" value="all" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="giftlistItems" vartype="java.lang.Object" param="filteredCollection" />
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
      <c:if test="${not empty giftlistId and ValidGiftlistId}"> 
        <div id="atg_commerce_csr_giftRegistryEventsGrid" >
        <div class="atg_svc_subPanelHeader">       
          <ul class="atg_svc_panelToolBar">
            <li class="atg_svc_header">
              <h4 id="atg_commerce_csr_customerinfo_addresses"><fmt:message key="giftlists.giftlist.label"/></h4>            
            </li>
            <c:if test="${not empty giftlistItems and isPublic == 'true'}">
              <c:if test="${isMultiSiteEnabled != true}">
                <li>
                  <a href="#" class="blueU" onclick="atg.commerce.csr.order.gift.giftlistBuyFrom('${giftlistId}');"><fmt:message key="giftlists.buyFrom.label"/></a>               
                </li>
              </c:if>
              <c:if test="${isMultiSiteEnabled == true}">
	              <dsp:droplet name="GiftlistSiteFilterDroplet">
	                <dsp:param name="collection"  bean="ServiceCustomerProfile.giftlists"/>
	                <dsp:oparam name="output">
	                  <dsp:getvalueof var="collection" vartype="java.lang.Object" param="filteredCollection" />
	                </dsp:oparam>
	              </dsp:droplet>
	              <dsp:contains var="validSite" values="${collection}" object="${giftlist}"/>
                <c:choose>
                  <c:when test="${validSite}">
                    <li>
                      <a href="#" class="blueU" onclick="atg.commerce.csr.order.gift.giftlistBuyFrom('${giftlistId}');"><fmt:message key="giftlists.buyFrom.label"/></a>               
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li>
                      <!-- Site Access Controls to only allow the agent to change the site context 
                           only if they have access to the site on which the gift list was created -->
											<c:choose>
											  <c:when test="${envTools.siteAccessControlOn == 'true'}">
											    <dsp:getvalueof var="siteId" value="${giftlistSiteId}"/>
											    <dsp:droplet name="IsSiteAccessibleDroplet">
											      <dsp:param name="siteId" value="${siteId}"/>
											      <dsp:oparam name="true">
											        <a href="#" class="blueU" onclick="atg.commerce.csr.common.changeSite('${giftlistSiteId}','atg_commerce_csr_customerGiftlistChangeSiteForm');atg.commerce.csr.order.gift.giftlistSelect('${giftlistId}');"><fmt:message key="giftlists.giftlist.ChangeSite.label"/></a>
											      </dsp:oparam>
											      <dsp:oparam name="false">
											        &nbsp;
											      </dsp:oparam>
											    </dsp:droplet>
											  </c:when>
											  <c:otherwise>
											    <a href="#" class="blueU" onclick="atg.commerce.csr.common.changeSite('${giftlistSiteId}','atg_commerce_csr_customerGiftlistChangeSiteForm');atg.commerce.csr.order.gift.giftlistSelect('${giftlistId}');"><fmt:message key="giftlists.giftlist.ChangeSite.label"/></a>
											  </c:otherwise>
											</c:choose>
                    </li>
                  </c:otherwise>
                </c:choose>
	            </c:if>
            </c:if>
            <c:if test="${isEdit}">
              <li style="border-right:none">
              <fmt:message key="giftlists.create.edit.title" var="title"/>
                
               <svc-ui:frameworkPopupUrl
                  var="giftlistEdit"
                  value="/panels/gift/giftlistCreate.jsp"
                  context="${CSRConfigurator.contextRoot}"
                  giftlistId="${giftlistId}"
                  windowId="${windowId}" /> <a href="#"
                  class="atg_svc_popupLink"
                  onClick="editGiftListOnClick();
                  return false;">
                <c:out escapeXml="true" value="${title}"/> </a>  
                <script type="text/javascript">
                  if (!dijit.byId("giftlistCreatePopup")) {
                    new dojox.Dialog( {
                    id :"giftlistCreatePopup",
                    cacheContent :"false",
                    executeScripts :"true",
                    scriptHasHooks :"true"});}

                  var title="<c:out escapeXml="true" value="${title}"/>";

                  function editGiftListOnClick()
                  {
                    atg.commerce.csr.common.showPopupWithReturn({
                          popupPaneId: 'giftlistCreatePopup',
                          title: title,
                          url: '${giftlistEdit}',
                          onClose: function( args ) {
                            if ( args.result == 'save' ) {
                              atgSubmitAction({
                                panels : ['cmcGiftlistsViewP'],
                                panelStack : 'globalPanels',
                                form : document.getElementById('transformForm')
                              });
                            }
                          }});
                  }
                  
                  </script>               
              </li>        
            </c:if>  
          </ul>
        </div>
        <c:if test="${not empty giftlistDetailsViewDefault.URL}">
          <dsp:include src="${giftlistDetailsViewDefault.URL}"
            otherContext="${giftlistDetailsViewDefault.servletContext}">
            <dsp:param name="isEdit" value="${isEdit}" />
            <dsp:param name="giftlistId" value="${giftlistId}" />
          </dsp:include>
        </c:if>
        <c:if test="${not empty giftlistDetailsViewExtended.URL}">
          <dsp:include src="${giftlistDetailsViewExtended.URL}"
            otherContext="${giftlistDetailsViewExtended.servletContext}">
            <dsp:param name="isEdit" value="${isEdit}" />
            <dsp:param name="giftlistId" value="${giftlistId}" />
          </dsp:include>
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
                <th scope="col" style="width:${columnWidth}"><dsp:include
                  src="${column.dataRendererPage.URL}"
                  otherContext="${column.dataRendererPage.servletContext}">
                  <dsp:param name="field" value="${column.field}" />
                  <dsp:param name="resourceBundle"
                    value="${column.resourceBundle}" />
                  <dsp:param name="resourceKey"
                    value="${column.resourceKey}" />
                  <dsp:param name="isHeading" value="true" />
                  <dsp:param name="isEdit" value="${isEdit}" />
                </dsp:include></th>
              </c:if>
            </c:forEach>
          </thead>
          
          <%--  If no products to display show message --%>
          <c:if test="${isMultiSiteEnabled != true}">
            <c:set var="link">
              <a href="#" onclick="atg.commerce.csr.openPanelStackWithTabbedPanel('cmcCatalogPS','cmcProductCatalogBrowseP','commerceTab');">
            </c:set>
            <c:if test="${empty giftlistItems and isEdit=='true'}">              
              <fmt:message key="giftlists.noProducts.edit" var="noProductsMessage">
                <fmt:param value="${link}"/>
                <fmt:param value="</a>"/>
              </fmt:message>
            </c:if>
          </c:if>
          <c:if test="${isMultiSiteEnabled == true}">         
            <c:if test="${empty giftlistItems and isEdit=='true'}">
              <dsp:droplet name="GiftlistSiteFilterDroplet">
                <dsp:param name="collection"  bean="ServiceCustomerProfile.giftlists"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="collection" vartype="java.lang.Object" param="filteredCollection" />
                </dsp:oparam>
              </dsp:droplet>
              <dsp:contains var="validSite" values="${collection}" object="${giftlist}"/>
              <c:choose>
                <c:when test="${validSite}">
                  <c:set var="link">
                    <a href="#" onclick="atg.commerce.csr.openPanelStackWithTabbedPanel('cmcCatalogPS','cmcProductCatalogBrowseP','commerceTab');">
                  </c:set>
                  <fmt:message key="giftlists.noProducts.edit" var="noProductsMessage">
                    <fmt:param value="${link}"/>
                    <fmt:param value="</a>"/>
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <c:set var="link">
                    <a href="#" onclick="atg.commerce.csr.common.changeSite('${giftlistSiteId}','atg_commerce_csr_changeSiteProductCatalog');">
                  </c:set>
                  <fmt:message key="giftlists.noProductsChangeSite.edit" var="noProductsMessage">
                    <fmt:param value="${link}"/>
                    <fmt:param value="</a>"/>
                  </fmt:message>
                </c:otherwise>
              </c:choose>
            </c:if>
          </c:if>
          <c:if test="${empty giftlistItems and isEdit=='false'}">
            <fmt:message key="giftlists.noProducts.view" var="noProductsMessage"/>
          </c:if>
          <%--  If products exist in the list display them --%>
          <c:if test="${not empty giftlistItems}">
            <c:forEach var="giftItem" items="${giftlistItems}"
              varStatus="giftItemStatus">
              <dsp:param name="giftItem" value="${giftItem}" />
              <dsp:droplet name="CSRProductLookup">
                <dsp:param name="id" param="giftItem.productId" />
                <dsp:setvalue param="giftSku" paramvalue="element" />
                <dsp:oparam name="output">
                      <tr>
                        <c:forEach var="column"
                          items="${tableConfig.columns}">
                          <c:if test="${column.isVisible == 'true'}">
                            <td><c:if
                              test="${column.dataRendererPage != ''}">
                              <dsp:include
                                src="${column.dataRendererPage.URL}"
                                otherContext="${column.dataRendererPage.servletContext}">
                                <dsp:param name="field"
                                  value="${column.field}" />
                                <dsp:param name="giftlistId"
                                  value="${giftlistId}" />
                                <dsp:param name="giftItem"
                                  value="${giftItem}" />
                                <dsp:param name="giftSku"
                                  value="${giftSku}" />
                                <dsp:param name="isEdit"
                                  value="${isEdit}" />
                              </dsp:include>
                            </c:if></td>
                          </c:if>
                        </c:forEach>
                      </tr>
                </dsp:oparam>
              </dsp:droplet>
            </c:forEach>
          </c:if>         
        </table>
        <c:if test="${fn:length(noProductsMessage) > 0}">
          <div class="atg_resultTotal">
            <c:out value="${noProductsMessage}" escapeXml="false"/>
          </div>
        </c:if>
        <%-- Display the Update Gift list button if the gift list is to be viewed in edit mode --%>
        <c:if test="${isEdit==true}">
          <dsp:form style="display:none" action="#" id="atg_commerce_csr_updateGiftlist" formid="atg_commerce_csr_updateGiftlist">
            <input name="atg.successMessage" type="hidden" value=""/>
            <dsp:input type="hidden" id="giftlistId" bean="CSRGiftlistFormHandler.giftlistId"/>
            <dsp:input type="hidden" bean="CSRGiftlistFormHandler.updateGiftlistItems" priority="-10" value="" />
          </dsp:form>
          <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
            <dsp:param name="id" param="giftlistId" />
            <dsp:oparam name="output">
              <dsp:setvalue paramvalue="element" param="giftlist" />
              <%--  Display the update button only if there are gift items in the giftlist --%>
              <c:if test="${not empty giftlistItems}">
              <div class="atg_svc_formActions">
                <input type="button" name="submit" value="<fmt:message key='giftlists.giftlist.update.label'/>" onclick="atg.commerce.csr.order.gift.updateGiftlistItems('${giftlistId}');" />                
              </div>
              </c:if>
            </dsp:oparam>
          </dsp:droplet> 
        </c:if>        
      </c:if>
      </div>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlist/giftlistTable.jsp#2 $$Change: 953229 $--%>
