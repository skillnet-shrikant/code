<%--
  - File Name: backInStockEmailModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows a user to email their Wish List to a friend
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/com/mff/browse/MFFEmailFormHandler"/>

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">backInStockModal</jsp:attribute>

		<jsp:body>

			<div class="back-in-stock-modal">

				<div class="modal-header">
					<h2>Please Notify Me</h2>
				</div>

				<dsp:form id="back-in-stock-form" formid="back-in-stock-form" action="${originatingRequest.requestURI}" method="post" data-validate>

					<div class="modal-body">
						<p>
							Enter your email below and we'll send a notification when this item becomes available.
						</p>
						<p>
							<dsp:droplet name="ProductLookup">
								<dsp:param name="id" param="productId"/>
								<dsp:param name="elementName" value="product"/>
								<dsp:oparam name="output">
									<strong><dsp:valueof param="product.description" valueishtml="true" /></strong>
								</dsp:oparam>
							</dsp:droplet>
						</p>

						<%-- email form --%>
						<div class="email-form">
							<div class="field-group">
								<label for="email">* Email Address</label>
								<dsp:input bean="MFFEmailFormHandler.yourEmail" id="email" name="email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email Address" placeholder="Email Address"/>
							</div>
						</div>

					</div>

					<div class="modal-footer">
						<div class="back-in-stock-submit">
							<dsp:input bean="MFFEmailFormHandler.sendBackInStockEmail" type="submit" iclass="button primary expand" value="Submit" />
							<dsp:input bean="MFFEmailFormHandler.productId" type="hidden" paramvalue="productId"/>
							<dsp:input bean="MFFEmailFormHandler.skuId" type="hidden" paramvalue="skuId"/>
							<dsp:input bean="MFFEmailFormHandler.sendBackInStockEmailErrorURL" type="hidden" value="${contextPath}/browse/json/emailError.jsp"/>
							<dsp:input bean="MFFEmailFormHandler.sendBackInStockEmailSuccessURL" type="hidden" value="${contextPath}/browse/json/emailSuccess.jsp"/>
						</div>
					</div>

				</dsp:form>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
