<%--
This page defines the address view
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/shippingGroupReadView.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:getvalueof var="sgType" param="shippingGroup.shippingGroupClassType"/>
  <dsp:getvalueof var="sgTypeConfig" bean="CSRConfigurator.shippingGroupTypeConfigurationsAsMap.${sgType}"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <c:if test="${sgTypeConfig != null && sgTypeConfig.displayPageFragment != null}">
      <c:choose>
        <c:when test="${sgType == 'inStorePickupShippingGroup'}">
          <fmt:message key="shippingSummary.inStorePickup.header" />
          <br />
          <br />
          <fmt:message key="shippingSummary.inStorePickup.storeAddress" />
          <br />
          <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                           otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
            <dsp:param name="propertyName" value="value1"/>
            <dsp:param name="displayHeading" value="${true}"/>
            <dsp:param name="displaySelectButton" value="${false}"/>
            <dsp:param name="displayAuthorizedReceiver" value="${true}"/>
            <dsp:param name="displayStatus" value="${true}"/>
          </dsp:include>
        </c:when>
        <c:otherwise>
          <div class="atg_commerce_csr_addressView">
            <h4>
              <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                           otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
              <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                           otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayValue" value="${true}"/>
              </dsp:include>
            </ul>
          </div>

          <div class="atg_commerce_csr_shippingMethod">
            <h4>
              <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                           otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul>
              <li>
                <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                             otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                  <dsp:param name="propertyName" value="value2"/>
                  <dsp:param name="displayValue" value="${true}"/>
                </dsp:include>
              </li>
            </ul>
          </div>

          <c:if test="${isExistingOrderView}">
            <div class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
              <h4>
                <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                             otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                  <dsp:param name="propertyName" value="status"/>
                  <dsp:param name="displayHeading" value="${true}"/>
                </dsp:include>
              </h4>
              <ul>
                <li>
                  <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                               otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                    <dsp:param name="propertyName" value="status"/>
                    <dsp:param name="displayValue" value="${true}"/>
                  </dsp:include>
                </li>
              </ul>
            </div>

          </c:if>
        </c:otherwise>
      </c:choose>
    </c:if>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/shippingGroupReadView.jsp#1 $$Change: 946917 $--%>
