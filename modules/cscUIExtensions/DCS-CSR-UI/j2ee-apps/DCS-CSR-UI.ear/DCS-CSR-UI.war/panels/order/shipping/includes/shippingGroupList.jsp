<%--
This page is used to display the select dropdown in the multiple shipping and split qty pages.

This page walks through the available shipping groups in the ShippingGroupContainerService and displays only permitted
shipping groups for the SKU.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingGroupList.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf"%>

<dsp:page xml="true">

  <dsp:importbean
    bean="/atg/commerce/custsvc/order/ShippingGroupContainerService"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>

  <dsp:getvalueof var="beanString" param="beanString"/>
  <dsp:getvalueof var="cisiItem" param="cisiItem"/>
  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <dsp:select bean="${beanString}">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="itemShippingGroups"/>
      <dsp:param name="elementName" value="shippingGroup"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="sg" param="shippingGroup"/>
        <dsp:getvalueof var="key" param="key"/>
        <dsp:getvalueof var="sgTypeConfig"
                        bean="CSRConfigurator.shippingGroupTypeConfigurationsAsMap.${sg.shippingGroupClassType}"/>
        <c:if test="${sgTypeConfig != null && sgTypeConfig.displayPageFragment != null}">
          <c:choose>
            <c:when test="${cisiItem.shippingGroupName == key}">
              <dsp:option selected="${true}" value="${key}">
                <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                             otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                  <dsp:param name="shippingGroup" param="shippingGroup"/>
                  <dsp:param name="propertyName" value="selectOptionText"/>
                  <dsp:param name="displayValue" value="${true }"/>
                  <dsp:param name="shortDisplay" value="${true}"/>
                </dsp:include>
              </dsp:option>
            </c:when>
            <c:otherwise>
              <dsp:option value="${key}">
                <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                             otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                  <dsp:param name="shippingGroup" param="shippingGroup"/>
                  <dsp:param name="propertyName" value="selectOptionText"/>
                  <dsp:param name="displayValue" value="${true }"/>
                  <dsp:param name="shortDisplay" value="${true}"/>
                </dsp:include>
              </dsp:option>
            </c:otherwise>
          </c:choose>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:select>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingGroupList.jsp#1 $$Change: 946917 $--%>
