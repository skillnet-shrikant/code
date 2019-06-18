<%--
This page defines the shipping method picker
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/promotionsListing.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionViewDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:getvalueof var="order" param="order"/>
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
      
      <dsp:droplet name="PromotionViewDroplet">
        <dsp:param name="byType" value="${true}"/>
        <dsp:param name="order" value="${order}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="shippingPromotions" param="shippingPromotions"/>
            <c:if test="${!empty shippingPromotions}">
              <div class="atg_commerce_csr_promotionsBox" style="width:100%;">
                <div class="atg_commerce_csr_promotionsListing">
                <h4>
                  <fmt:message key="shipping.promotions"/>
                </h4>
             
                <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="shippingPromotions"/>
                    <dsp:oparam name="output">
                      <dsp:tomap var="promotion" param="element"/>
                      <li>
                        <fmt:message key="common.hyphen"/>
                        &nbsp;${fn:escapeXml(promotion.displayName)}
                      </li>
                    </dsp:oparam>
                  </dsp:droplet>
                </ul>
                </div>
              </div>
            </c:if>
          </dsp:oparam>
          <dsp:oparam name="error">
            <dsp:getvalueof var="msg" param="errorMessage"/>
            ${fn:escapeXml(msg)}
          </dsp:oparam>
        </dsp:droplet>

    </dsp:layeredBundle>
  </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/promotionsListing.jsp#1 $$Change: 946917 $--%>
