<%--
	- File Name: giftCardBalance.jsp
	- Author(s): KnowledgePath Solutions
	- Copyright Notice:
	- Description: This is the Gift Card Check Balance page
	--%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/GiftCardBalanceFormHandler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="giftCardProductId" bean="/atg/commerce/catalog/CatalogTools.giftCardProductId" />

	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="gift-cards" />
		<dsp:param name="defaultPageTitle" value="Gift Card Balance" />
		<dsp:param name="defaultMetaDescription" value="" />
		<dsp:param name="defaultCanonicalURL" value="" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>

	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">home</jsp:attribute>
		<jsp:attribute name="pageType">giftCardBalance</jsp:attribute>
		<jsp:attribute name="bodyClass">gift-card-balance</jsp:attribute>
		<jsp:body>

			<div class="section-title">
				<h1>Gift Cards</h1>
			</div>

			<div class="section-row">
				<div class="gc-product-section">
					<h2>Purchase Gift Cards</h2>
					<img src="${contextPath}/resources/images/gift-card.png" alt="Fleet Farm gift card" />
					<p>
						A Gift that's sure to Please! : Fleet Farm Gift Cards - no guesswork, no
						exchanges, no hassles. Select an amount and a gift card will be mailed to the recipient
						which can be used at any Fleet Farm store or online at Fleetfarm.com.
					</p>
					<a href="${contextPath}/detail/gift-cards/${giftCardProductId}" class="button primary">View Gift Cards</a>
				</div>
				<div class="gc-balance-check-form">
					<h2>Check Balance</h2>
					<dsp:form id="check-balance-form" method="POST" formid="check-balance-form" action="${contextPath}/sitewide/giftCardBalance.jsp" data-validate>
						<div class="field-group">
							<div class="field gc-number">
								<label for="gift-card-number">Gift Card Number</label>
								<dsp:input bean="GiftCardBalanceFormHandler.giftCardNumber" id="gift-card-number" name="gift-card-number" type="tel" />
							</div>
							<div class="field access-number">
								<label for="gc-pin">
									Access Number
									<a href="${contextPath}/checkout/ajax/giftCardInfoModal.jsp" class="modal-trigger" data-target="gift-card-info-modal" data-size="small"><span class="icon icon-info"></span></a>
								</label>
								<dsp:input bean="GiftCardBalanceFormHandler.giftCardPin" type="tel" id="gc-pin" name="gc-pin" class="gc-pin" maxlength="8" />
							</div>
						</div>
						<%-- recaptcha --%>
						<div class="field-group captcha">
							<div id="gc-balance-captcha" class="g-recaptcha"></div>
						</div>
						<div class="field-group">
							<dsp:input bean="GiftCardBalanceFormHandler.giftCardBalanceErrorURL" type="hidden" id="gift-card-error-url" name="gift-card-error-url" value="${contextPath}/sitewide/json/gcBalanceError.jsp"/>
							<dsp:input bean="GiftCardBalanceFormHandler.giftCardBalanceSuccessURL" type="hidden" id="gift-card-success-url" name="gift-card-success-url" value="${contextPath}/sitewide/json/gcBalanceSuccess.jsp"/>
							<dsp:input bean="GiftCardBalanceFormHandler.CheckGiftCardBalance" type="submit" id="gc-balance-submit" name="gc-balance-submit" class="button primary expand" value="Check Balance" />
						</div>
					</dsp:form>
				</div>
			</div>

		</jsp:body>
	</layout:default>

</dsp:page>
