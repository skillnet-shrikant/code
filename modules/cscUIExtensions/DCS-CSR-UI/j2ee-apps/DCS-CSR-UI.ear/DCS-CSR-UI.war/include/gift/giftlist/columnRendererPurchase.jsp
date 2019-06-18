<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/columnRendererPurchase.jsp#3 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $

--%>
<%@ include file="/include/top.jspf"%>
<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
  <dsp:getvalueof var="cartShareableTypeId" bean="/atg/commerce/custsvc/util/CSRConfigurator.cartShareableTypeId"/>

  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
    <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
    <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />

    <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
    <dsp:getvalueof var="formHandler" param="formHandler" />
    <dsp:getvalueof var="rowCounter" param="rowCounter"/>
    <dsp:getvalueof var="field" param="field" />
    <dsp:getvalueof var="giftlistId" param="giftlistId" />
    <dsp:getvalueof var="giftItem" param="giftItem" />
    <dsp:getvalueof var="giftSku" param="giftSku" />
    <dsp:getvalueof var="resourceBundle" param="resourceBundle" />
    <dsp:getvalueof var="resourceKey" param="resourceKey" />
    <dsp:getvalueof var="isHeading" param="isHeading" />
    <c:if test="${empty isHeading}">
      <c:set var="isHeading" value="false" />
    </c:if>
    <c:choose>

      <c:when test="${field=='buy' and isHeading=='true'}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}" />
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='buy' and isHeading=='false'}">
        <fmt:message key="giftlists.giftlist.invalidQuantityMessage" var="invalidQuantityMessage"/>
        <!-- Site Access Controls to only allow items to be added from sites the agent has access to -->
        <c:choose>
          <c:when test ="${envTools.siteAccessControlOn == 'true' }">
            <dsp:getvalueof var="siteId" param="giftItem.siteId"/>
            <dsp:droplet name="IsSiteAccessibleDroplet">
              <dsp:param name="siteId" value="${siteId}"/>
              <dsp:oparam name="true">
                <c:if test="${isMultiSiteEnabled == true}">
                  <dsp:getvalueof var="giftSiteId" param="giftItem.siteId" />
                  <dsp:droplet name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
                    <dsp:param name="siteId" value="${currentSiteId}" />
                    <dsp:param name="otherSiteId" value="${giftSiteId}" />
                    <dsp:param name="shareableTypeId" value="${cartShareableTypeId}" />
                    <dsp:oparam name="true">
                      <dsp:getvalueof var="siteId" param="giftItem.siteId" />
                      <dsp:getvalueof var="giftlistItemId" param="giftItem.id" />
                      <dsp:getvalueof var="productId" param="giftItem.productId" />
                      <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
                      <dsp:input bean="${formHandler}.items[${rowCounter.index}].siteId" type="hidden" value="${siteId}" />
                      <dsp:input bean="${formHandler}.items[${rowCounter.index}].giftlistId" type="hidden" value="${giftlistId}" />
                      <dsp:input bean="${formHandler}.items[${rowCounter.index}].giftlistItemId" type="hidden" value="${giftlistItemId}" />
                      <dsp:input bean="${formHandler}.items[${rowCounter.index}].productId" type="hidden" value="${productId}" />
                      <dsp:input bean="${formHandler}.items[${rowCounter.index}].catalogRefId" type="hidden" value="${catalogRefId}" />
                      <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                        <dsp:param name="product" value="${productId}"/>
                        <dsp:param name="sku" value="${catalogRefId}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="isFractional" param="fractional"/>
                        </dsp:oparam>
                      </dsp:droplet>
                      <c:if test="${isFractional == true}">
                        <dsp:input bean="${formHandler}.items[${rowCounter.index}].quantityWithFraction" type="text" value="" id="${catalogRefId}" size="5" maxlength="9">
                          <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                          <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                          <dsp:tagAttribute name="trim" value="true" />
                          <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces},pattern:${fractionalUnitPattern}}"/>
                        </dsp:input>
                      </c:if>
                      <c:if test="${isFractional == false}">
                        <dsp:input bean="${formHandler}.items[${rowCounter.index}].quantity" type="text" value=""  id="${catalogRefId}" size="5" maxlength="5">
                          <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                          <dsp:tagAttribute name="invalidMessage" value="${invalidQuantityMessage}"/>
                          <dsp:tagAttribute name="trim" value="true" />
                          <dsp:tagAttribute name="constraints" value="{places:0}"/>
                        </dsp:input>
                      </c:if>
                    </dsp:oparam>
                    <c:set var="siteId" value="${fn:escapeXml(siteId)}"/>
                    <dsp:oparam name="false">
                      <dsp:getvalueof var="siteId" param="giftItem.siteId" />
                      <input type="button" name="submit" value="<fmt:message key="giftlists.giftlist.ChangeSite.label"/>" onclick="atg.commerce.csr.common.changeSite('${siteId}','atg_commerce_csr_giftlistChangeSiteForm');" />
                    </dsp:oparam>
                  </dsp:droplet>
                </c:if>
              </dsp:oparam>
              <dsp:oparam name="false">
                &nbsp;
              </dsp:oparam>
            </dsp:droplet>
          </c:when>
          <c:otherwise>
            <c:if test="${isMultiSiteEnabled == true}">
              <dsp:getvalueof var="giftSiteId" param="giftItem.siteId" />
              <dsp:droplet name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
                <dsp:param name="siteId" value="${currentSiteId}" />
                <dsp:param name="otherSiteId" value="${giftSiteId}" />
                <dsp:param name="shareableTypeId" value="${cartShareableTypeId}" />
                <dsp:oparam name="true">
                  <dsp:getvalueof var="siteId" param="giftItem.siteId" />
                  <dsp:getvalueof var="giftlistItemId" param="giftItem.id" />
                  <dsp:getvalueof var="productId" param="giftItem.productId" />
                  <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
                  <dsp:input bean="${formHandler}.items[${rowCounter.index}].siteId" type="hidden" value="${siteId}" />
                  <dsp:input bean="${formHandler}.items[${rowCounter.index}].giftlistId" type="hidden" value="${giftlistId}" />
                  <dsp:input bean="${formHandler}.items[${rowCounter.index}].giftlistItemId" type="hidden" value="${giftlistItemId}" />
                  <dsp:input bean="${formHandler}.items[${rowCounter.index}].productId" type="hidden" value="${productId}" />
                  <dsp:input bean="${formHandler}.items[${rowCounter.index}].catalogRefId" type="hidden" value="${catalogRefId}" />
                  <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                    <dsp:param name="product" value="${productId}"/>
                    <dsp:param name="sku" value="${catalogRefId}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="isFractional" param="fractional"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <c:if test="${isFractional == true}">
                    <dsp:input bean="${formHandler}.items[${rowCounter.index}].quantityWithFraction" type="text" value="" id="${catalogRefId}" size="5" maxlength="9">
                      <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                      <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                      <dsp:tagAttribute name="trim" value="true" />
                      <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces},pattern:${fractionalUnitPattern}}"/>
                    </dsp:input>
                  </c:if>
                  <c:if test="${isFractional == false}">
                    <dsp:input bean="${formHandler}.items[${rowCounter.index}].quantity" type="text" value=""  id="${catalogRefId}" size="5" maxlength="5">
                      <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                      <dsp:tagAttribute name="invalidMessage" value="${invalidQuantityMessage}"/>
                      <dsp:tagAttribute name="trim" value="true" />
                      <dsp:tagAttribute name="constraints" value="{places:0}"/>
                    </dsp:input>
                  </c:if>
                </dsp:oparam>
                <c:set var="siteId" value="${fn:escapeXml(siteId)}"/>
                <dsp:oparam name="false">
                  <dsp:getvalueof var="siteId" param="giftItem.siteId" />
                  <input type="button" name="submit" value="<fmt:message key="giftlists.giftlist.ChangeSite.label"/>" onclick="atg.commerce.csr.common.changeSite('${siteId}','atg_commerce_csr_giftlistChangeSiteForm');" />
                </dsp:oparam>
              </dsp:droplet>
            </c:if>
          </c:otherwise>
        </c:choose>

        <c:if test="${isMultiSiteEnabled == false}">
          <dsp:getvalueof var="giftlistItemId" param="giftItem.id" />
          <dsp:getvalueof var="productId" param="giftItem.productId" />
          <dsp:getvalueof var="catalogRefId" param="giftItem.catalogRefId" />
          <dsp:input bean="${formHandler}.items[${rowCounter.index}].giftlistId" type="hidden" value="${giftlistId}" />
          <dsp:input bean="${formHandler}.items[${rowCounter.index}].giftlistItemId" type="hidden" value="${giftlistItemId}" />
          <dsp:input bean="${formHandler}.items[${rowCounter.index}].productId" type="hidden" value="${productId}" />
          <dsp:input bean="${formHandler}.items[${rowCounter.index}].catalogRefId" type="hidden" value="${catalogRefId}" />

          <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
            <dsp:param name="product" value="${productId}"/>
            <dsp:param name="sku" value="${catalogRefId}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="isFractional" param="fractional"/>
            </dsp:oparam>
          </dsp:droplet>
          <c:if test="${isFractional eq true}">
            <dsp:input bean="${formHandler}.items[${rowCounter.index}].quantityWithFraction" type="text" value="" id="${catalogRefId}" size="5" maxlength="9">
              <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
              <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
              <dsp:tagAttribute name="trim" value="true" />
              <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces},pattern:${fractionalUnitPattern}}"/>
            </dsp:input>
          </c:if>
          <c:if test="${isFractional eq false}">
            <dsp:input bean="${formHandler}.items[${rowCounter.index}].quantity" type="text" value="" id="${catalogRefId}" size="5" maxlength="5">
              <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
              <dsp:tagAttribute name="invalidMessage" value="${invalidQuantityMessage}"/>
              <dsp:tagAttribute name="trim" value="true" />
              <dsp:tagAttribute name="constraints" value="{places:0}"/>
            </dsp:input>
          </c:if>
        </c:if>
      </c:when>
    </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/columnRendererPurchase.jsp#3 $$Change: 1179550 $--%>