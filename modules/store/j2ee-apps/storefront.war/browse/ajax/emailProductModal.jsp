<%--
  - File Name: emailProductModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows a user to email a product's information to a friend
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/com/mff/browse/MFFEmailFormHandler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="productImageRoot" bean="/mff/MFFEnvironment.productImageRoot" />
	<dsp:getvalueof var="productId" param="productId" />

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">emailProductModal</jsp:attribute>
		<jsp:body>

			<dsp:droplet name="ProductLookup">
				<dsp:param name="id" param="productId"/>
				<dsp:param name="elementName" value="product"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="product" var="product"/>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:param name="productItem" value="${product}"/>

			<div class="email-product-modal">

				<div class="modal-header">
					<h2>SHARE ITEM VIA EMAIL</h2>
				</div>

				<dsp:form formid="email-product-form" id="email-product-form" method="post" data-validate>

					<div class="modal-body">
						<%-- product details --%>
						<div class="product-details">
							<h1 class="product-name">
								<dsp:valueof param="productItem.description" valueishtml="true"/>
							</h1>
							<div class="product-brand">
								<dsp:valueof param="productItem.brand" valueishtml="true"/>
							</div>
							<div class="product-number">
								<span class="label">Product #:</span>
								<span><dsp:valueof param="productItem.id" /></span>
							</div>
							
							<dsp:droplet name="/com/mff/browse/droplet/IsInactiveProduct">
								<dsp:param name="productId" value="${productId}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="isInactiveProd" param="isInActiveProduct"/>
									<dsp:getvalueof var="isActiveTeaser" param="isActiveTeaser"/>
									<c:choose>
										<c:when test="${isActiveTeaser}">
											<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
												<dsp:param name="value" param="productItem.teaserPDPMessage"/>
												<dsp:oparam name="true">
													<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
		  												<dsp:param name="queryRQL" value="infoKey=\"EVENT_ITEM_PDP\""/>
		  												<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
		  												<dsp:param name="itemDescriptor" value="infoMessage"/>
		  												<dsp:oparam name="output">
															<div class="regular-price">
																<span itemprop="price" content="content">
																	<dsp:valueof param="element.infoMsg" valueishtml="true"/>
																</span>
															</div>
		  												</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
												<dsp:oparam name="false">
														<div class="regular-price">
															<span itemprop="price" content="content">
																<dsp:valueof param="productItem.teaserPDPMessage" valueishtml="true"/>
															</span>
														</div>
												</dsp:oparam>												
											</dsp:droplet>
										</c:when>
										<c:otherwise>
											<div class="price" itemprop="offers" itemscope itemtype="http://schema.org/Offer">
												<span itemprop="priceCurrency" content="USD" />
												<dsp:include page="/browse/includes/productPrice.jsp" >
													<dsp:param name="productItem" param="productItem"/>
												</dsp:include>
											</div>
										</c:otherwise>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
														


							<%-- minimum age --%>
							<dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" param="productItem.minimumAge"/>
								<dsp:oparam name="18">
									<div class="product-message">
										<span class="icon icon-error"></span> You must be at least 18 years old to purchase this item.
									</div>
								</dsp:oparam>
								<dsp:oparam name="21">
									<div class="product-message">
										<span class="icon icon-error"></span> You must be at least 21 years old to purchase this item
									</div>
								</dsp:oparam>
							</dsp:droplet>

						</div>

						<%-- product image --%>
						<div class="product-image">
							<dsp:droplet name="/com/mff/browse/droplet/ProductImageDroplet">
								<dsp:param name="productId" param="productId" />
								<dsp:param name="imageSize" value="xl" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="defaultImage" param="productImages[0]" />
									<picture>
										<!--[if IE 9]><video style="display: none;"><![endif]-->
										<source srcset="${productImageRoot}/${productId}/l/${defaultImage}" media="(min-width: 980px)">
										<source srcset="${productImageRoot}/${productId}/m/${defaultImage}" media="(min-width: 768px)">
										<!--[if IE 9]></video><![endif]-->
										<img src="${productImageRoot}/${productId}/x/${defaultImage}" alt="${productName}" />
									</picture>
								</dsp:oparam>
								<dsp:oparam name="empty">
									<picture>
										<!--[if IE 9]><video style="display: none;"><![endif]-->
										<source srcset="${productImageRoot}/unavailable/l.jpg" media="(min-width: 980px)">
										<source srcset="${productImageRoot}/unavailable/m.jpg" media="(min-width: 768px)">
										<!--[if IE 9]></video><![endif]-->
										<img src="${productImageRoot}/unavailable/xl.jpg" alt="Image Unavailable" />
									</picture>
								</dsp:oparam>
							</dsp:droplet>
						</div>

						<%-- email form --%>
						<div class="email-form">
							<div class="field-group">
								<label for="friend-email">To</label>
								<dsp:input bean="MFFEmailFormHandler.friendEmail" id="friend-email" name="friend-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Friend's Email" />
							</div>
							<div class="field-group">
								<label for="your-name">Your Name</label>
								<dsp:input bean="MFFEmailFormHandler.yourName" id="your-name" name="your-name" type="text" autocapitalize="off" data-validation="required" data-fieldname="Your Name" />
							</div>
							<div class="field-group">
								<label for="your-email">Your Email Address</label>
								<dsp:input bean="MFFEmailFormHandler.yourEmail" id="your-email" name="your-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Your Email Address" />
							</div>
							<div class="field-group">
								<label for="message">Message</label>
								<dsp:textarea bean="MFFEmailFormHandler.message" id="message" name="message" autocapitalize="off">I thought you might be interested in this product from Fleet Farm.</dsp:textarea>
							</div>
						</div>

					</div>

					<div class="modal-footer">
						<div class="legal-links">
							<a href="${contextPath}/static/respect-for-privacy" target="_blank">Privacy Policy</a> | <a href="${contextPath}/static/visitor-info-collection" target="_blank">Terms of Use</a>
						</div>
						<div class="email-product-submit">
							<dsp:input bean="MFFEmailFormHandler.sendPDPEmil" type="submit" iclass="button primary expand" value="Send" />
							<dsp:input bean="MFFEmailFormHandler.productId" type="hidden" paramvalue="productId"/>
							<dsp:input bean="MFFEmailFormHandler.sendPDPEmailErrorURL" type="hidden" value="${contextPath}/browse/json/emailError.jsp"/>
							<dsp:input bean="MFFEmailFormHandler.sendPDPEmailSuccessURL" type="hidden" value="${contextPath}/browse/json/emailSuccess.jsp"/>
							<!--   <input type="submit" id="email-product-submit" name="email-product-submit" class="button primary expand" value="Send" />-->
						</div>
					</div>

				</dsp:form>

			</div>

		</jsp:body>
	</layout:ajax>
</dsp:page>
