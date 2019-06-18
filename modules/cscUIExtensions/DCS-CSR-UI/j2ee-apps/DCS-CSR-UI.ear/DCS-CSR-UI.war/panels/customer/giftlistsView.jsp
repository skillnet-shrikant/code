<%--
 Initializes the customer management gift list panel using the following input parameters:
 (optional) giftlistId - The id of the gift list to display, if giftlistId is present the gift list is not rendered
 
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/giftlistsView.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $


--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean var="GiftlistUIState" bean="/atg/commerce/custsvc/gifts/GiftlistUIState" />
  <dsp:importbean var="profile" bean="/atg/userprofiling/ServiceCustomerProfile" />
  <dsp:importbean var="gridConfig" bean="/atg/commerce/custsvc/ui/tables/gift/customer/GiftlistGrid" />
  <dsp:importbean var="wishlistViewTableConfig" bean="/atg/commerce/custsvc/ui/tables/gift/wishlist/WishlistViewResultsTable" />
  <dsp:importbean var="wishlistEditTableConfig" bean="/atg/commerce/custsvc/ui/tables/gift/wishlist/WishlistEditResultsTable" />
  <dsp:importbean var="giftlistViewTableConfig" bean="/atg/commerce/custsvc/ui/tables/gift/giftlist/GiftlistViewResultsTable" />
  <dsp:importbean var="giftlistEditTableConfig" bean="/atg/commerce/custsvc/ui/tables/gift/giftlist/GiftlistEditResultsTable" />  
  <dsp:getvalueof var="isProfileTransient" bean="/atg/userprofiling/ServiceCustomerProfile.transient" />
  <dsp:getvalueof var="viewprofileid" bean="/atg/userprofiling/ServiceCustomerProfile.repositoryId" />
  <dsp:getvalueof var="activeprofileid" bean="/atg/userprofiling/ActiveCustomerProfile.repositoryId" />
  <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request" />
  <c:if test="${empty giftlistId}">
    <dsp:getvalueof var="giftlistId" bean="GiftlistUIState.workingGiftlistId" />
    <%--Get the owner Id of the giftlist --%>
    <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
      <dsp:param name="id" value="${giftlistId}" />
      <dsp:oparam name="output">
        <dsp:setvalue paramvalue="element" param="giftlist" />
        <dsp:setvalue paramvalue="giftlist.id" param="giftlistId" />
        <dsp:setvalue paramvalue="giftlist.owner.id" param="ownerId" />
        <dsp:getvalueof var="ownerId" vartype="java.lang.Object" param="ownerId" />
        <%-- Check Gift list belongs to active customer, if not clear the giftlistId variable --%>
        <c:if test="${fn:trim(viewprofileid) != fn:trim(ownerId)}">
          <c:set var="giftlistId" value=""/>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
  
  <c:if test="${activeprofileid == viewprofileid}">
    <dsp:setvalue param="mode" value="edit" />
  </c:if>
  <c:if test="${activeprofileid != viewprofileid}">
    <dsp:setvalue param="mode" value="view" />
  </c:if>

  <script type="text/javascript">
    if (!dijit.byId("giftlistCreatePopup")) {
      new dojox.Dialog( {
        id :"giftlistCreatePopup",
        cacheContent :"false",
        executeScripts :"true",
        scriptHasHooks :"true"
      });
    }
    if (!dijit.byId("deleteGiftListPopup")) {
        new dojox.Dialog( {
          id :"deleteGiftListPopup",
          cacheContent :"false",
          executeScripts :"true",
          scriptHasHooks :"true"
        });
    }
  </script>

  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <%
      /* form used to display gift lists on the customer management pages */
    %>
    <dsp:importbean bean="/atg/commerce/custsvc/gifts/GiftlistTableFormHandler" />
    <dsp:setvalue bean="GiftlistTableFormHandler.profileId"
      value="${viewprofileid}" />
    <dsp:form id="atg_commerce_csr_giftlistsSearchForm"
      formid="atg_commerce_csr_giftlistsSearchForm">
      <dsp:input type="hidden" name="currentPage"
        bean="GiftlistTableFormHandler.currentPage" />
      <dsp:input type="hidden" name="sortProperty"
        bean="GiftlistTableFormHandler.sortField" />
      <dsp:input type="hidden" name="sortDirection"
        bean="GiftlistTableFormHandler.sortDirection" />
      <dsp:input type="hidden" bean="GiftlistTableFormHandler.search"
        value="Search" priority="-10" />
    </dsp:form>
    
  <script type="text/javascript">
    function customerGiftWishlistDropdownSelect() {
      var theForm = dojo
          .byId("atg_commerce_csr_customer_gift_giftWishlistDropdown");
      var giftlistSelect = document.getElementById("giftlistSelector");
      var selection = giftlistSelect.options[giftlistSelect.selectedIndex].value;
      theForm.showWishlist.value = selection;
      atgSubmitAction( {
        form :theForm,
        panels : [ "cmcGiftlistsViewP" ]
      });
    }
  </script>
    <ul
      class="atg_svc_panelToolBar atg_commerce_csr_giftwishListToolbar">
      <%-- Provide Gift/Wish list drop down --%>
      <dsp:form id="giftWishlistDropDownSelectForm">      
        <li class="atg_commcerce_csr_listOption">
        <label><fmt:message key="giftlists.view.label" /></label> 
          <dsp:select id="giftlistSelector"
            bean="/atg/commerce/custsvc/gifts/GiftlistUIState.showWishlist"
            onchange="customerGiftWishlistDropdownSelect()"
            nodefault="true" priority="10">
            <dsp:option value="true"
              selected="${GiftlistUIState.showWishlist}">
              <fmt:message key="giftlists.my-wishlist" />
            </dsp:option>
            <dsp:option value="false"
              selected="${!GiftlistUIState.showWishlist}">
              <fmt:message key="giftlists.my-giftlists" />
            </dsp:option>
          </dsp:select>
        </li>
      </dsp:form>
      <c:if test="${GiftlistUIState.showWishlist == 'true'}">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param param="mode" name="value" />
          <dsp:oparam name="edit">
          </dsp:oparam>
          <dsp:oparam name="view">
            <%-- Provide Select Customer link --%>
            <li class="atg_svc_last"><a href="#" onclick="viewCustomerSelect('${viewprofileid}');return false;"><fmt:message key="giftlists.customer.select.label" /></a></li>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <c:if test="${GiftlistUIState.showWishlist == 'false'}">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param param="mode" name="value" />
          <dsp:oparam name="edit">
            <li class="atg_svc_last"><svc-ui:frameworkPopupUrl
              var="giftlistCreate"
              value="/panels/gift/giftlistCreate.jsp"
              context="${CSRConfigurator.contextRoot}"
              windowId="${windowId}" /> <a href="#"
              class="atg_svc_popupLink"
              onClick="atg.commerce.csr.common.showPopupWithReturn({
                      popupPaneId: 'giftlistCreatePopup',
                      title: '<fmt:message key="giftlists.create.title"/>',
                      url: '${giftlistCreate}',
                      onClose: function( args ) {
                        if ( args.result == 'save' ) {
                          atgSubmitAction({
                            panels : ['cmcGiftlistsViewP'],
                            panelStack :[ 'customerPanels', 'globalPanels'],
                            form : document.getElementById('transformForm')
                          });
                        }
                      }});
                      return false;">
            <fmt:message key="giftlists.create.newGiftlistlabel" /> </a></li>
          </dsp:oparam>
          <dsp:oparam name="view">
            <%-- Provide Select Customer link --%>
            <li class="atg_svc_last"><a href="#" onclick="viewCustomerSelect('${viewprofileid}');return false;"><fmt:message key="giftlists.customer.select.label" /></a></li>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
    </ul>
    <c:choose>
      <c:when test="${GiftlistUIState.showWishlist == 'true'}">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param param="mode" name="value" />
          <dsp:oparam name="edit">
            <dsp:include src="${wishlistEditTableConfig.tablePage.URL}"
              otherContext="${wishlistEditTableConfig.tablePage.servletContext}">
              <dsp:param name="tableConfig"
                value="${wishlistEditTableConfig}" />
              <dsp:param name="isEdit" value="true" />
            </dsp:include>
          </dsp:oparam>
          <dsp:oparam name="view">
            <dsp:include src="${wishlistViewTableConfig.tablePage.URL}"
              otherContext="${wishlistViewTableConfig.tablePage.servletContext}">
              <dsp:param name="tableConfig"
                value="${wishlistViewTableConfig}" />
              <dsp:param name="isEdit" value="false" />
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>
      </c:when>
      <c:otherwise>
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param param="mode" name="value" />
          <dsp:oparam name="edit">
              <dsp:droplet name="IsEmpty">
                <dsp:param bean="ServiceCustomerProfile.giftlists" name="value"/>
                <dsp:oparam name="false">
                  <dsp:include
                    src="${gridConfig.gridPage.URL}"
                    otherContext="${gridConfig.gridPage.servletContext}">
                    <dsp:param name="gridConfig" value="${gridConfig}" />
                    <dsp:param name="isEdit" value="true" />
                  </dsp:include>
                </dsp:oparam>
                <dsp:oparam name="true">
                <div>
                  <fmt:message key="giftlists.profileHaveNoGiftlists" />
                </div>
                </dsp:oparam>
              </dsp:droplet>                
            <dsp:include src="${giftlistEditTableConfig.tablePage.URL}"
              otherContext="${giftlistEditTableConfig.tablePage.servletContext}">
              <dsp:param name="tableConfig"
                value="${giftlistEditTableConfig}" />
              <dsp:param name="isEdit" value="true" />
              <dsp:param name="giftlistId" value="${giftlistId}" />
            </dsp:include>
          </dsp:oparam>
          <dsp:oparam name="view">
            <%-- Only Display Customers Gift lists if they exist --%>
            <dsp:droplet name="IsEmpty">
              <dsp:param bean="ServiceCustomerProfile.giftlists" name="value"/>
              <dsp:oparam name="false">
                <dsp:include src="${gridConfig.gridPage.URL}"
                  otherContext="${gridConfig.gridPage.servletContext}">
                  <dsp:param name="gridConfig" value="${gridConfig}" />
                  <dsp:param name="isEdit" value="false" />
                </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
            <dsp:include src="${giftlistViewTableConfig.tablePage.URL}"
              otherContext="${giftlistViewTableConfig.tablePage.servletContext}">
              <dsp:param name="tableConfig"
                value="${giftlistViewTableConfig}" />
              <dsp:param name="isEdit" value="false" />
              <dsp:param name="giftlistId" value="${giftlistId}" />
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>
      </c:otherwise>
    </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/giftlistsView.jsp#1 $$Change: 946917 $--%>